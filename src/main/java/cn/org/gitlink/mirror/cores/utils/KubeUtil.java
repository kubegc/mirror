/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.org.gitlink.mirror.cores.utils;

import java.sql.Timestamp;
import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;

import cn.org.gitlink.mirror.cores.datas.KubeData.Meta;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/1918
 *
 */
public class KubeUtil {

	private KubeUtil() {
		super();
	}

	/**
	 * 得到KubeData的元数据meta
	 * 
	 * @param name    名称
	 * @param raw     原始数据
	 * @return 得到KubeData的元数据meta
	 */
	public static Meta toKubeData(String name, JsonNode raw) {
		Meta meta = new Meta();
		meta.setName(name);
		String apiVersion = raw.get("apiVersion").asText();
		int idx = apiVersion.indexOf("/");
		meta.setGroup(idx == -1 ? "" : apiVersion.substring(0, idx));
		meta.setKind(raw.get("kind").asText());
		meta.setPlural(raw.get("plural").asText());
		return meta;
	}
	
	/**
	 * @param apiVersion Kubernetes对应kind的apiVersion
	 * @return Kubernetes对应kind的group
	 */
	public static String toGroup(String apiVersion) {
		int idx = apiVersion.indexOf("/");
		return idx == -1 ? "" : apiVersion.substring(0, idx);
	}
	
	/**
	 * @param json   json
	 * @return      获得kubernetes的name
	 */
	public static String getName(JsonNode json) {
		return json.get("metadata").get("name").asText();
	}
	
	/**
	 * @param json   json
	 * @return      获得kubernetes的namespace
	 */
	public static String getNamespace(JsonNode json) {
		if (json.get("metadata").has("namespace")) {
			return json.get("metadata").get("namespace").asText();
		}
		return "default";
	}
	
	/**
	 * @param json   json
	 * @return      获得kubernetes的namespace
	 */
	public static String getGroup(JsonNode json) {
		return toGroup(json.get("apiVersion").asText());
	}
	
	/**
	 * @param json   json
	 * @return      获得kubernetes的创建时间
	 */
	public static Timestamp createdTime(JsonNode json) {
		String dateTimeStr = json.get("metadata").get("creationTimestamp").asText();
		// 使用DateTimeFormatter解析字符串为Instant对象
        Instant instant = Instant.parse(dateTimeStr);
        // 将Instant对象转换为java.sql.Timestamp对象
        return Timestamp.from(instant);
	}
	
	/**
	 * @return      获得kubernetes的更新时间
	 */
	public static Timestamp updatedTime(JsonNode json) {
        return Timestamp.valueOf(java.time.LocalDateTime.now());
	}
}
