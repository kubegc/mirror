/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/17
 *
 */
public final class Env {
	
	private Env() {
		super();
	}

	/**
	 * JDBC的Url地址
	 */
	//@Description(type = "环境变量", value = "JDBC地址，如jdbc:postgresql://localhost:5432/mydatabase")
	public static final String ENV_JDBC_URL            = "jdbcUrl";
	
	/**
	 * JDBC的Username
	 */
	//@Description(type = "环境变量", value = "JDBC用户名，如postgres")
	public static final String ENV_JDBC_USER           = "jdbcUser";
	
	/**
	 * JDBC的Password
	 */
	//@Description(type = "环境变量", value = "JDBC密码，如onceas")
	public static final String ENV_JDBC_PWD             = "jdbcPwd";
	
	/**
	 * JDBC的Driver
	 */
	//@Description(type = "环境变量", value = "JDBC驱动，如org.postgresql.Driver")
	public static final String ENV_JDBC_DRIVER          = "jdbcDriver";
	
	/**
	 * Kubernetes的Url地址
	 */
	//@Description(type = "环境变量", value = "Kubernetes地址，如https://localhost:6443")
	public static final String ENV_KUBE_URL             = "kubeUrl";
	
	/**
	 * Kubernetes的Token
	 */
	//@Description(type = "环境变量", value = "Kubernetes的Bearer Token，如eyJhbGciOiJSUzI1NiIsImtpZCI6IkNhcVFxOHpmSHdRcTBpVFJvd2tacldzNzR2NElERHVzcG01eUM2ZmU0dHcifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJrdWJlcm5ldGVzLWNsaWVudC10b2tlbiIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJrdWJlcm5ldGVzLWNsaWVudCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjYyMjc5ZWFiLTBiZmQtNGU2NC1hYjU3LTA3OGZiODhkMTk4MSIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDprdWJlLXN5c3RlbTprdWJlcm5ldGVzLWNsaWVudCJ9.TI9mZC39ixMEq4h3cGfveKqLQSSh2y7BvvqLQixJBrFlKjsu9RJwlcGqEjh32UyshKDLtF4bM1J7w9HTMy7t74uGae9No-4Nm-R4kN3mXJA04MMdWZAV5gipDAuhR1J5R5wdoIIwYNyuUJNavWh61AqtXJkwC3uCAIYnClY9-Kx25Jif-XFlXyRkfETJxA2I9ZAbKZ3g_LOJgmVNfstjxSNTLJgRImYzQ65hrM2oZFul1_rZFPXM76rsNWwObvzPtDKPCT_yaqWt3dzAxxxOuP9EaQodVPSz7YNJb1ZHsGKgqAN9_I8MjQ2wJ0gLahyT4DFaU8rb2OvDhlDV66DoOw")
	public static final String ENV_KUBE_TOKEN           = "kubeToken";
	
	/**
	 * Kubernetes的Token
	 */
	//@Description(type = "环境变量", value = "Kubernetes集群标识")
	public static final String ENV_KUBE_REGION           = "kubeRegion";
	
	//@Description(type = "环境变量", value = "Kubernetes集群标识")
	public static final String ENV_MQ_URL                = "mqUrl";
	
	//@Description(type = "环境变量", value = "Kubernetes集群标识")
	public static final String ENV_MQ_PORT               = "mqPort";
	
	//@Description(type = "环境变量", value = "Kubernetes集群标识")
	public static final String ENV_MQ_USER               = "mqUser";
	
	//@Description(type = "环境变量", value = "Kubernetes集群标识")
	public static final String ENV_MQ_PWD                = "mqPwd";
}
