/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.sources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.kubesys.client.KubernetesClient;
import io.github.kubesys.client.KubernetesConstants;
import io.github.kubesys.client.KubernetesWatcher;
import io.github.kubesys.client.utils.KubeUtil;
import io.github.kubesys.mirror.cores.DataTarget;
import io.github.kubesys.mirror.cores.datas.KubeDataModel;
import io.github.kubesys.mirror.cores.datas.KubeDataModel.Meta;
import io.github.kubesys.mirror.cores.utils.MirrorUtil;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/18
 *
 */
public class KubeSourceExtractor extends AbstractKubeSource {

	/**
	 * 已经监听的Kinds
	 */
	public static final Set<String> collectedKinds = new HashSet<>();
	
	/**
	 * fullKind与元数据描述映射关系
	 * fullKind = group + "." + kind
	 */
	public static Map<String, Meta> kindToMetaMapper = new HashMap<>();
	
	/**
	 * @param dataTarget     dataTarget
	 * @throws Exception     Exception
	 */
	public KubeSourceExtractor(DataTarget<KubeDataModel> dataTarget) throws Exception {
		super(dataTarget);
	}

	@Override
	public void startCollect() throws Exception {
		
		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = kubeClient.getKindDesc().fields();
		
		while (fieldsIterator.hasNext()) {
		    Map.Entry<String, JsonNode> entry = fieldsIterator.next();
		    String fullkind = entry.getKey();
		    JsonNode value = entry.getValue();
		    startCollect(fullkind, value);
		}
	}
	
	private void startCollect(String fullkind, JsonNode value) throws Exception {
		// 已经监测过了，不再监测
	    if (collectedKinds.contains(fullkind)) {
	    	return;
	    }
	    
	    // 添加元数据描述信息
		Meta kubeData = MirrorUtil.toKubeMeta(fullkind, value);
		kindToMetaMapper.put(fullkind, kubeData);
	    
	    // 只有支持watch才进行监听,真正做数据处理
	    if (KubeUtil.supportWatch(value)) {
	    	 //开始监听数据
		    kubeClient.watchResources(fullkind, new KubeCollector(kubeClient, fullkind, dataTarget));
	    }
	    
	    collectedKinds.add(fullkind);
	}

	@Override
	public void startCollect(String fullkind, Meta meta) throws Exception {
		//开始监听数据
		kindToMetaMapper.put(fullkind, meta);
	    kubeClient.watchResources(fullkind, new KubeCollector(kubeClient,fullkind, dataTarget));
	}
	
	
	/**
	 * @author wuheng@iscas.ac.cn
	 * @version 0.1.0
	 * @since   2023/06/22
	 *
	 */
	static class KubeCollector extends KubernetesWatcher {
		
		/**
		 * 全称=group+kind
		 */
		protected final String fullKind;
		
		/**
		 * 目标处理器
		 */
		protected final DataTarget<KubeDataModel> dataTarget;
		
		protected KubeCollector(KubernetesClient client, String fullKind, DataTarget<KubeDataModel> target) {
			super(client);
			this.fullKind = fullKind;
			this.dataTarget = target;
		}


		@Override
		public void doAdded(JsonNode node) {
			try {
				dataTarget.handle(KubernetesConstants.JSON_TYPE_ADDED, 
						new KubeDataModel(kindToMetaMapper.get(fullKind), node));
			} catch (Exception e) {
				m_logger.warning("unknown error: " + e + ":" + node.toPrettyString());
			}
		}

		@Override
		public void doModified(JsonNode node) {
			try {
				dataTarget.handle(KubernetesConstants.JSON_TYPE_MODIFIED, 
						new KubeDataModel(kindToMetaMapper.get(fullKind), node));
			} catch (Exception e) {
				m_logger.warning("unknown error: " + e  + ":" + node.toPrettyString());
			}
		}

		@Override
		public void doDeleted(JsonNode node) {
			try {
				dataTarget.handle(KubernetesConstants.JSON_TYPE_DELETED, 
						new KubeDataModel(kindToMetaMapper.get(fullKind), node));
			} catch (Exception e) {
				m_logger.warning("unknown error: " + e  + ":" + node.toPrettyString());
			}
		}

		@Override
		public void doClose() {
			m_logger.severe("connection is close, wait for reconnect " + fullKind);
			try {
				Thread.sleep(3000);
				 client.watchResources(fullKind, new KubeCollector(client, fullKind, dataTarget));
			} catch (Exception e) {
				doClose();
			}
		}
	}

}
