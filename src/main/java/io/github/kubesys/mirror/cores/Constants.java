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

	public static final String KUBE_APIVERSION          = "apiVersion";
	
	public static final String KUBE_APIVERSION_SPLIT    = "/";
	
	public static final String KUBE_VALUE_SPLIT         = ".";
	
	public static final String KUBE_DEFAULT_GROUP       = "";
	
	public static final String KUBE_KIND                = "kind";
	
	public static final String KUBE_PLURAL              = "plural";
	
	public static final String KUBE_METADATA            = "metadata";
	
	public static final String KUBE_METADATA_NAME       = "name";
	
	public static final String KUBE_METADATA_CREATED    = "creationTimestamp";
	
	public static final String KUBE_METADATA_NAMESPACE  = "namespace";
	
	public static final String KUBE_DEFAULT_NAMESPACE    = "default";
	
	public static final String TABLE_REPLACED_SOURCE    = "[-/]";
	
	public static final String TABLE_REPLACED_TARGET    = "";
	
	public static final String JSON_DATABASE_ITEM       = "data";
	
	public static final String JSON_INPUT_SPLIT         = "##";
	
	public static final String JSON_MIDDLE_CONNECTOR    = " -> ";
	
	public static final String JSON_LAST_CONNECTOR      = " ->> ";
	
	public static final String JSON_SINGLE_QUOTES       = "'";
}
