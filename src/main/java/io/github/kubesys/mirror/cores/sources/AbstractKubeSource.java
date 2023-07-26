/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.sources;

import io.github.kubesys.client.KubernetesClient;
import io.github.kubesys.mirror.cores.Environment;
import io.github.kubesys.mirror.cores.DataSource;
import io.github.kubesys.mirror.cores.DataTarget;
import io.github.kubesys.mirror.cores.datas.KubeDataModel;
import io.github.kubesys.mirror.cores.utils.MirrorUtil;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/18
 *
 */
public abstract class AbstractKubeSource extends DataSource<KubeDataModel> {

	
	/**
	 * 默认Kubernetes的URL
	 */
	final static String DEFAULT_URL = "https://10.96.0.1:443";
	
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

}
