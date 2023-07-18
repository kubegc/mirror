/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores;

import io.github.kubesys.mirror.cores.datas.KubeData.Meta;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/17
 *
 * 本项目的主要作用是确定数据源
 */
public abstract class Source<T> {
	
	/**
	 * 数据来源是谁
	 */
	protected Target<T> metaTarget;
	
	/**
	 * 数据最终去处是哪
	 */
	protected Target<T> dataTarget;

	/**
	 * 设置目标处理器
	 * 
	 * @param metaTarget  元处理器，如创建表
	 * @param dataTarget  目标处理器
	 */
	protected Source(Target<T> metaTarget, Target<T> dataTarget) {
		super();
		this.metaTarget = metaTarget;
		this.dataTarget = dataTarget;
	}

	/**
	 * 开始收集全部数据
	 * 
	 * @throws Exception Exception
	 */
	public abstract void startCollect() throws Exception;
	
	/**
	 * 开始收集指定数据
	 * 
	 * @param fullKind 只监测某一种类型，见项目https://github.com/kubesys/client-java
	 * @param meta 对应kubeClient中getKindDesc
	 * 
	 * @throws Exception Exception
	 */
	public abstract void startCollect(String fullKind, Meta meta) throws Exception;
}
