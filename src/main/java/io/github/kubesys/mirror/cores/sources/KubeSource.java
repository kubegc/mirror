/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.sources;

import io.github.kubesys.client.KubernetesClient;
import io.github.kubesys.mirror.cores.Env;
import io.github.kubesys.mirror.cores.Source;
import io.github.kubesys.mirror.cores.Target;
import io.github.kubesys.mirror.cores.datas.KubeData;
import io.github.kubesys.mirror.cores.utils.KubeUtil;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/18
 *
 */
public abstract class KubeSource extends Source<KubeData> {

	
	/**
	 * Kubernetes的客户端
	 */
	protected final KubernetesClient kubeClient;
	
	/**
	 * @param metaTarget          处理器，如创建数据库表
	 * @param dataTarget          处理器，数据处理
	 */
	KubeSource(Target<KubeData> metaTarget, Target<KubeData> dataTarget) {
		super(metaTarget, dataTarget);
		this.kubeClient = initKubeClient();
		// 缺少环境变量，直接异常退出
		KubeUtil.checkNull(kubeClient);
	}

	/**
	 * 读取环境变量'ENV_KUBE_URL'和'ENV_KUBE_TOKEN'，实例化Kubernetes客户端实例.
	 * 
	 * @return Kubernetes客户端实例
	 * @throws Exception 无法连接Kubernetes
	 */
	private static KubernetesClient initKubeClient() {
		return new KubernetesClient(
				System.getenv(Env.ENV_KUBE_URL), 
				System.getenv(Env.ENV_KUBE_TOKEN));
	}

	/**
	 * @return Kubernetes的客户端
	 */
	public KubernetesClient getKubeClient() {
		return kubeClient;
	}

}
