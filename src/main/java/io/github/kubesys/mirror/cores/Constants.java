/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.1.1
 * @since    2023/07/17
 *
 */
public final class Constants {
	
	private Constants() {
		super();
	}

	/**
	 * 取值为apiVersion
	 */
	public static final String KUBE_APIVERSION          = "apiVersion";
	
	/**
	 *取值为\/
	 */
	public static final String KUBE_APIVERSION_SPLIT    = "/";
	
	/**
	 * 取值为.
	 */
	public static final String KUBE_VALUE_SPLIT         = ".";
	
	/**
	 * 取值为""
	 */
	public static final String KUBE_DEFAULT_GROUP       = "";
	
	/**
	 * 取值为kind
	 */
	public static final String KUBE_KIND                = "kind";
	
	/**
	 * 取值为plural
	 */
	public static final String KUBE_PLURAL              = "plural";
	
	/**
	 * 取值为metadata
	 */
	public static final String KUBE_METADATA            = "metadata";
	
	/**
	 * 取值为metadata.name
	 */
	public static final String KUBE_METADATA_NAME       = "name";
	
	/**
	 * 取值为creationTimestamp
	 */
	public static final String KUBE_METADATA_CREATED    = "creationTimestamp";
	
	/**
	 * 取值为metadata.namespace
	 */
	public static final String KUBE_METADATA_NAMESPACE  = "namespace";
	
	/**
	 * 取值为default
	 */
	public static final String KUBE_DEFAULT_NAMESPACE    = "default";
	
	/**
	 * 取值为-/
	 */
	public static final String TABLE_REPLACED_SOURCE    = "[-/]";
	
	/**
	 * 取值为
	 */
	public static final String TABLE_REPLACED_TARGET    = "";
	
	/**
	 * 取值为data
	 */
	public static final String JSON_DATABASE_ITEM       = "data";
	
	/**
	 * 取值为##
	 */
	public static final String JSON_INPUT_SPLIT         = "##";
	
	/**
	 * 取值为 ->
	 */
	public static final String JSON_MIDDLE_CONNECTOR    = " -> ";
	
	/**
	 * 取值为 ->>
	 */
	public static final String JSON_LAST_CONNECTOR      = " ->> ";
	
	/**
	 * 取值为'
	 */
	public static final String JSON_SINGLE_QUOTES       = "'";
}
