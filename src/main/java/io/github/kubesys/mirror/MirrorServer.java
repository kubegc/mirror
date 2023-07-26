/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.kubesys.mirror.cores.DataTarget;
import io.github.kubesys.mirror.datas.KubeDataModel;
import io.github.kubesys.mirror.sources.AbstractKubeSource;
import io.github.kubesys.mirror.sources.KubeSourceExtractor;
import io.github.kubesys.mirror.targets.PostgresTarget;
import io.github.kubesys.mirror.targets.RabbitMQTarget;
import io.github.kubesys.mirror.utils.MirrorUtil;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/17
 *
 * 本项目的主要作用是将Kubernetes的所有状态同步到Postgres数据库中
 * 
 * 具体文档见https://www.yuque.com/kubesys/backend/cbtrr8nshxowbgiy
 */
public class MirrorServer {
	
	/**
	 * @param args      系统参数
	 * @throws Exception  启动的异常
	 */
	public static void main(String[] args) throws Exception {
		
		DataTarget<KubeDataModel> dbTarget    = new PostgresTarget();
		DataTarget<KubeDataModel> msgTarget   = new RabbitMQTarget();
		dbTarget.setNext(msgTarget);
		
		AbstractKubeSource source = new KubeSourceExtractor(dbTarget);
		source.startCollect();
		
		// 调试的时候用
//		debug(source);
	}

	static void debug(AbstractKubeSource source) throws Exception {
		JsonNode json = source.getKubeClient().getKindDesc().get("Pod");
		source.startCollect("Pod", MirrorUtil.toKubeMeta("Pod", json));
	}
}
