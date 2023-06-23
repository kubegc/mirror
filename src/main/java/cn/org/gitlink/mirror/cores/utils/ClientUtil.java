/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.org.gitlink.mirror.cores.utils;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/1918
 *
 */
public class ClientUtil {

	private ClientUtil() {
		super();
	}

	/**
	 * 如果Client为空，后面业务都无法开展，则、直接退出
	 * 
	 * @param client     客户端
	 */
	public static void checkNull(Object client) {
		if (client == null) {
			System.exit(1);
		}
	}
}
