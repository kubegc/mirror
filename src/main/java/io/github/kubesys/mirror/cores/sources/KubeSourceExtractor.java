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
import io.github.kubesys.mirror.cores.Target;
import io.github.kubesys.mirror.cores.datas.KubeData;
import io.github.kubesys.mirror.cores.datas.KubeData.Meta;
import io.github.kubesys.mirror.cores.utils.KubeUtil;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/18
 *
 */
public class KubeSourceExtractor extends KubeSource {

	/**
	 * 已经监听的Kinds
	 */
	static final Set<String> collectedKinds = new HashSet<>();
	
	/**
	 * fullKind与元数据描述映射关系
	 * fullKind = group + "." + kind
	 */
	protected static Map<String, Meta> kindToMetaMapper = new HashMap<>();
	
	public KubeSourceExtractor(Target<KubeData> metaTarget, Target<KubeData> dataTarget) {
		super(metaTarget, dataTarget);
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
		Meta kubeData = KubeUtil.toKubeMeta(fullkind, value);
		kindToMetaMapper.put(fullkind, kubeData);
		metaTarget.doHandleAdded(new KubeData(kubeData, null));
	    
	    // 只有支持watch才进行监听,真正做数据处理
	    if (supportWatch(value)) {
	    	 //开始监听数据
		    kubeClient.watchResources(fullkind, new KubeCollector(kubeClient,fullkind, dataTarget));
	    }
	    
	    collectedKinds.add(fullkind);
	}

	@Override
	public void startCollect(String fullkind, Meta meta) throws Exception {
		//开始监听数据
	    kubeClient.watchResources(fullkind, new KubeCollector(kubeClient,fullkind, dataTarget));
	}
	
	
	/**
	 * @param value      目标KInd的元数据表述
	 * @return           是否支持被watch，像其见Kubernetes的Watch机制
	 */
	private boolean supportWatch(JsonNode value) {
		return value.get("verbs").toPrettyString().contains("watch");
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
		protected final Target<KubeData> dataTarget;
		
		protected KubeCollector(KubernetesClient client, String fullKind, Target<KubeData> target) {
			super(client);
			this.fullKind = fullKind;
			this.dataTarget = target;
		}


		@Override
		public void doAdded(JsonNode node) {
			try {
				dataTarget.handle(KubernetesConstants.JSON_TYPE_ADDED, 
						new KubeData(kindToMetaMapper.get(fullKind), node));
			} catch (Exception e) {
				m_logger.warning("未知错误：" + e + ":" + node.toPrettyString());
			}
		}

		@Override
		public void doModified(JsonNode node) {
			try {
				dataTarget.handle(KubernetesConstants.JSON_TYPE_MODIFIED, 
						new KubeData(kindToMetaMapper.get(fullKind), node));
			} catch (Exception e) {
				m_logger.warning("未知错误：" + e  + ":" + node.toPrettyString());
			}
		}

		@Override
		public void doDeleted(JsonNode node) {
			try {
				dataTarget.handle(KubernetesConstants.JSON_TYPE_DELETED, 
						new KubeData(kindToMetaMapper.get(fullKind), node));
			} catch (Exception e) {
				m_logger.warning("未知错误：" + e  + ":" + node.toPrettyString());
			}
		}

		@Override
		public void doClose() {
			m_logger.severe("由于网络断开，证书变更等原因无法再监听，等待3秒重新连接" + fullKind);
			try {
				Thread.sleep(3000);
				 client.watchResources(fullKind, new KubeCollector(client, fullKind, dataTarget));
			} catch (Exception e) {
				doClose();
			}
		}
	}

}
