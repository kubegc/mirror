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
public class SQLUtil {

	private SQLUtil() {
		super();
	}

	/**
	 * 
	 * @param plural    plural名称
	 * @return 得到table名
	 */
	public static String table(String plural) {
		return plural.replaceAll("-", "").replaceAll("/", "");
	}
}
