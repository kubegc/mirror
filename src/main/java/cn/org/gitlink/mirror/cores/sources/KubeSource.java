/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.org.gitlink.mirror.cores.sources;

import java.net.MalformedURLException;
import java.net.http.HttpConnectTimeoutException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.hc.client5.http.HttpHostConnectException;

import cn.org.gitlink.mirror.cores.Env;
import cn.org.gitlink.mirror.cores.Source;
import cn.org.gitlink.mirror.cores.Target;
import cn.org.gitlink.mirror.cores.datas.KubeData;
import cn.org.gitlink.mirror.cores.datas.KubeData.Meta;
import cn.org.gitlink.mirror.cores.utils.ClientUtil;
import io.github.kubesys.client.KubernetesClient;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/18
 *
 */
public abstract class KubeSource extends Source<KubeData> {

	static final Logger m_logger = Logger.getLogger(KubeSource.class.getName());
	
	static final String VALID_KUBE_URL_PATTERN   = "请检查参数kubeUrl符合格式https://IP:port，必须是https协议，且通常默认port是6443";
	
	static final String VALID_KUBE_TOKEN_PATTERN = "请检查参数kubeToken符合要求，参见https://github.com/kubesys/client-java中创建和获取TOoken";

	/**
	 * 已经监听的Kinds
	 */
	static final Set<String> collectedKinds = new HashSet<>();
	
	/**
	 * fullKind与元数据描述映射关系
	 */
	protected static Map<String, Meta> kindToMetaMapper = new HashMap<>();
	
	/**
	 * Kubernetes的客户端
	 */
	protected final KubernetesClient kubeClient;
	
	/**
	 * @param metaTarget          处理器，如创建数据库表
	 * @param dataTarget          处理器，数据处理
	 */
	public KubeSource(Target<KubeData> metaTarget, Target<KubeData> dataTarget) {
		super(metaTarget, dataTarget);
		this.kubeClient = initKubeClient();
		// 缺少环境变量，直接异常退出
		ClientUtil.checkNull(kubeClient);
	}

	/**
	 * 读取环境变量'ENV_KUBE_URL'和'ENV_KUBE_TOKEN'，实例化Kubernetes客户端实例.
	 * 
	 * @return Kubernetes客户端实例
	 * @throws Exception 无法连接Kubernetes
	 */
	private static KubernetesClient initKubeClient() {
		try {
			return new KubernetesClient(
					System.getenv(Env.ENV_KUBE_URL), 
					System.getenv(Env.ENV_KUBE_TOKEN));
		} catch (NullPointerException ex) {
			m_logger.severe("缺少环境变量'" + Env.ENV_KUBE_URL + "'或者'" + Env.ENV_KUBE_TOKEN + "':" + ex);
		} catch (MalformedURLException | HttpHostConnectException | HttpConnectTimeoutException ex) {
			m_logger.severe(VALID_KUBE_URL_PATTERN + ":" + ex);
		} catch (Exception ex) {
			m_logger.severe(VALID_KUBE_TOKEN_PATTERN  + ":" + ex);
		}
		return null;
	}

	/**
	 * @return Kubernetes的客户端
	 */
	public KubernetesClient getKubeClient() {
		return kubeClient;
	}

}
