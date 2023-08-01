/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.sources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.kubesys.client.KubernetesClient;
import io.github.kubesys.client.KubernetesConstants;
import io.github.kubesys.client.KubernetesWatcher;
import io.github.kubesys.client.utils.KubeUtil;
import io.github.kubesys.mirror.cores.Environment;
import io.github.kubesys.mirror.datas.KubeDataModel;
import io.github.kubesys.mirror.datas.KubeDataModel.Meta;
import io.github.kubesys.mirror.cores.DataSource;
import io.github.kubesys.mirror.cores.DataTarget;
import io.github.kubesys.mirror.utils.MirrorUtil;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/18
 *
 */
public abstract class AbstractKubeSource extends DataSource<KubeDataModel> {

	
	/**
	 * 已经监听的Kinds
	 */
	static final Set<String> watchedFullkinds = new HashSet<>();
	
	/**
	 * 忽略得Kinds
	 */
	static final Set<String> ignoredFullkinds = new HashSet<>();
	
	/**
	 * fullKind与元数据描述映射关系
	 * fullKind = group + "." + kind
	 */
	static final Map<String, Meta> fullkindToMeta = new HashMap<>();
	
	/**
	 * fullKind与元数据描述映射关系
	 * fullKind = group + "." + kind
	 */
	static final Map<String, Thread> fullkindToWatcher = new HashMap<>();
	
	/**
	 * 默认Kubernetes的URL
	 */
	static final String DEFAULT_URL = "https://10.96.0.1:443";
	
	/**
	 * Kubernetes的客户端
	 */
	protected final KubernetesClient kubeClient;
	
	
	/**
	 * @param metaTarget          处理器，如创建数据库表
	 * @param dataTarget          处理器，数据处理
	 * @throws Exception 
	 */
	AbstractKubeSource(DataTarget<KubeDataModel> dataTarget) throws Exception {
		super(dataTarget);
		this.kubeClient = initKubeClient();
		// 缺少环境变量，直接异常退出
		MirrorUtil.checkNull(kubeClient);
	}

	/**
	 * 读取环境变量'ENV_KUBE_URL'和'ENV_KUBE_TOKEN'，实例化Kubernetes客户端实例.
	 * 
	 * @return Kubernetes客户端实例
	 * @throws Exception 无法连接Kubernetes
	 */
	private static KubernetesClient initKubeClient() throws Exception {
		return new KubernetesClient(
				MirrorUtil.getEnv(Environment.ENV_KUBE_URL, DEFAULT_URL),
				System.getenv(Environment.ENV_KUBE_TOKEN));
	}

	/**
	 * @return Kubernetes的客户端
	 */
	public KubernetesClient getKubeClient() {
		return kubeClient;
	}
	
	
	@Override
	public void startCollect(String fullkind, Meta meta) throws Exception {
		//开始监听数据
		fullkindToMeta.put(fullkind, meta);
	    Thread thread = kubeClient.watchResources(fullkind, 
	    		new KubeCollector(kubeClient,fullkind, dataTarget));
	    fullkindToWatcher.put(fullkind, thread);
	}
	
	protected void doStartCollect(String fullkind, JsonNode value) throws Exception {
		
		// 已经监测过了，不再监测
	    if (watchedFullkinds.contains(fullkind)) {
	    	return;
	    }
	    
	    // 不支持监听就忽略退出
	    if (!KubeUtil.supportWatch(value)) {
	    	ignoredFullkinds.add(fullkind);
	    	return;
	    }
	    
	    // 添加元数据描述信息
		Meta kubeData = MirrorUtil.toKubeMeta(fullkind, value);
		fullkindToMeta.put(fullkind, kubeData);
	    
    	//开始监听数据
		kubeClient.watchResources(fullkind, new KubeCollector(kubeClient, fullkind, dataTarget));
	    watchedFullkinds.add(fullkind);
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
						new KubeDataModel(fullkindToMeta.get(fullKind), node));
			} catch (Exception e) {
				m_logger.warning("unknown error: " + e + ":" + node.toPrettyString());
			}
		}

		@Override
		public void doModified(JsonNode node) {
			try {
				dataTarget.handle(KubernetesConstants.JSON_TYPE_MODIFIED, 
						new KubeDataModel(fullkindToMeta.get(fullKind), node));
			} catch (Exception e) {
				m_logger.warning("unknown error: " + e  + ":" + node.toPrettyString());
			}
		}

		@Override
		public void doDeleted(JsonNode node) {
			try {
				dataTarget.handle(KubernetesConstants.JSON_TYPE_DELETED, 
						new KubeDataModel(fullkindToMeta.get(fullKind), node));
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