/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.utils;

import io.github.kubesys.mirror.cores.Constants;

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
	 * @param plural    plural名称, plural可能出现-或者/情况，但实际数据库表不支持，需求替换成""
	 * @return 得到table名
	 */
	public static String table(String plural) {
		return plural.replaceAll(Constants.TABLE_REPLACED_SOURCE, 
								Constants.TABLE_REPLACED_TARGET);
	}
	
	/**
	 * 不考虑value为null情况，如果存在则直接抛出异常
	 * 
	 * @param value    JSON中需要查询的字段
	 * @return 得到满足SQL语法的标识，比如metadata##name，变成data -> 'metadata' ->> 'name'
	 */
	public static String jsonKey(String value) {

		StringBuilder sb = new StringBuilder();
		sb.append(Constants.JSON_DATABASE_ITEM);
		
		String[] parts = value.split(Constants.JSON_INPUT_SPLIT);
		
		for (int i = 0; i < parts.length - 1; i++) {
			sb.append(Constants.JSON_MIDDLE_CONNECTOR)
				.append(Constants.JSON_SINGLE_QUOTES).append(parts[i])
				.append(Constants.JSON_SINGLE_QUOTES);
		}
		
		sb.append(Constants.JSON_LAST_CONNECTOR)
				.append(Constants.JSON_SINGLE_QUOTES)
				.append(parts[parts.length - 1])
				.append(Constants.JSON_SINGLE_QUOTES);
		
		return sb.toString();
	}
}
