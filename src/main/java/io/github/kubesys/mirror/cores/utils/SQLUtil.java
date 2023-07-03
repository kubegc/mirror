/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.utils;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/23
 *
 */
public class SQLUtil {

	private SQLUtil() {
		super();
	}

	/**
	 * 不考虑plural为null情况，如果存在则直接抛出异常
	 * 
	 * @param plural    plural名称
	 * @return 得到table名
	 */
	public static String table(String plural) {
		return plural.replaceAll("-", "").replaceAll("/", "");
	}
	
	/**
	 * 不考虑value为null情况，如果存在则直接抛出异常
	 * 
	 * @param value    JSON中需要查询的字段
	 * @return 得到满足SQL语法的标识，比如metadata##name，变成data - 'metadata' - 'name'
	 */
	public static String jsonKey(String value) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("data");
		
		String[] parts = value.split("##");
		for (int i = 0; i < parts.length - 1; i++) {
			sb.append(" -> '").append(parts[i]).append("'");
		}
		
		sb.append(" ->> '").append(parts[parts.length - 1]).append("'");
		return sb.toString();
	}
}
