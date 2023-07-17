/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.utils;

import java.sql.Timestamp;
import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.kubesys.mirror.cores.Constants;
import io.github.kubesys.mirror.cores.datas.KubeData.Meta;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/23
 *
 */
public class KubeUtil {

	private KubeUtil() {
		super();
	}

	/**
	 * 如果Client为空，后面业务都无法开展，则直接退出
	 * 
	 * @param client     客户端
	 */
	public static void checkNull(Object client) {
		if (client == null) {
			System.exit(1);
		}
	}
	
	/**
	 * 得到KubeData的元数据meta，不考虑raw为空或者不符合Kuberneres资源定义的的情况
	 * 否则抛出异常
	 * https://kubernetes.io/docs/concepts/overview/working-with-objects/
	 * 
	 * @param name    名称
	 * @param raw     原始数据
	 * @return 得到KubeData的元数据meta
	 */
	public static Meta toKubeMeta(String name, JsonNode raw) {
		Meta meta = new Meta();
		meta.setName(name);
		String apiVersion = raw.get(Constants.KUBE_APIVERSION).asText();
		int idx = apiVersion.indexOf(Constants.KUBE_APIVERSION_SPLIT);
		meta.setGroup(idx == -1 ? Constants.KUBE_DEFAULT_GROUP : apiVersion.substring(0, idx));
		meta.setKind(raw.get(Constants.KUBE_KIND).asText());
		meta.setPlural(raw.get(Constants.KUBE_PLURAL).asText());
		return meta;
	}
	
	/**
	 * 不考虑apiVersion为空或者不符合Kuberneres资源定义的的情况
	 * 否则抛出异常
	 * https://kubernetes.io/docs/concepts/overview/working-with-objects/
	 * 
	 * @param apiVersion Kubernetes对应kind的apiVersion
	 * @return Kubernetes对应kind的group
	 */
	public static String toGroup(String apiVersion) {
		int idx = apiVersion.indexOf(Constants.KUBE_APIVERSION_SPLIT);
		return idx == -1 ? Constants.KUBE_DEFAULT_GROUP : apiVersion.substring(0, idx);
	}
	
	
	/**
	 * 不考虑json为空或者不符合Kuberneres资源定义的的情况
	 * 否则抛出异常
	 * https://kubernetes.io/docs/concepts/overview/working-with-objects/
	 * 
	 * @param json   json
	 * @return      获得kubernetes的name
	 */
	public static String getName(JsonNode json) {
		return json.get(Constants.KUBE_METADATA).get(Constants.KUBE_METADATA_NAME).asText();
	}
	
	/**
	 * 不考虑json为空或者不符合Kuberneres资源定义的的情况
	 * 否则抛出异常
	 * https://kubernetes.io/docs/concepts/overview/working-with-objects/
	 * 
	 * @param json   json
	 * @return      获得kubernetes的namespace
	 */
	public static String getNamespace(JsonNode json) {
		if (json.get(Constants.KUBE_METADATA).has(Constants.KUBE_METADATA_NAMESPACE)) {
			return json.get(Constants.KUBE_METADATA).get(Constants.KUBE_METADATA_NAMESPACE).asText();
		}
		return Constants.KUBE_DEFAULT_NAMESPACE;
	}
	
	
	/**
	 * 不考虑json为空或者不符合Kuberneres资源定义的的情况
	 * 否则抛出异常
	 * https://kubernetes.io/docs/concepts/overview/working-with-objects/
	 * 
	 * @param json   json
	 * @return      获得kubernetes的namespace
	 */
	public static String getGroup(JsonNode json) {
		return toGroup(json.get(Constants.KUBE_APIVERSION).asText());
	}
	
	/**
	 * 不考虑fullkind为空或者不符合Kuberneres资源定义的的情况
	 * 否则抛出异常
	 * https://kubernetes.io/docs/concepts/overview/working-with-objects/
	 * 
	 * @param fullkind   fullkind = group + "." + kind
	 * @return      获得kubernetes的namespace
	 */
	public static String getGroup(String fullkind) {
		int idx = fullkind.lastIndexOf(Constants.KUBE_VALUE_SPLIT);
		return idx == -1 ? Constants.KUBE_DEFAULT_GROUP : fullkind.substring(0, idx);
	}
	
	/**
	 * @param json   json
	 * @return      获得kubernetes的创建时间
	 */
	public static Timestamp createdTime(JsonNode json) {
		String dateTimeStr = json.get(Constants.KUBE_METADATA)
					.get(Constants.KUBE_METADATA_CREATED).asText();
		// 使用DateTimeFormatter解析字符串为Instant对象
        Instant instant = Instant.parse(dateTimeStr);
        // 将Instant对象转换为java.sql.Timestamp对象
        return Timestamp.from(instant);
	}
	
	/**
	 * @return      获得kubernetes的更新时间
	 */
	public static Timestamp updatedTime() {
        return Timestamp.valueOf(java.time.LocalDateTime.now());
	}
}
