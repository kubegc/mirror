/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.org.gitlink.mirror.cores;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/17
 *
 * 本项目的主要作用是确定数据源
 */
public abstract class Source<T> {
	
	/**
	 * 收集的数据发给谁
	 */
	protected Target<T> metaTarget;
	
	/**
	 * 收集的数据发给谁
	 */
	protected Target<T> dataTarget;

	/**
	 * 设置目标处理器
	 * 
	 * @param metaTarget  元处理器，如创建表
	 * @param dataTarget  目标处理器
	 */
	public Source(Target<T> metaTarget, Target<T> dataTarget) {
		super();
		this.metaTarget = metaTarget;
		this.dataTarget = dataTarget;
	}

	/**
	 * 开始收集数据
	 */
	public abstract void startCollect() throws Exception;
	
	/**
	 * 开始收集数据
	 * 
	 * @param fullKind 只监测某一种类型 
	 * @param json 对应kubeClient中getKindDesc
	 */
	public abstract void startCollect(String fullKind, JsonNode json) throws Exception;
}
