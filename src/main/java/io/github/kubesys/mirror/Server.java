/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.kubesys.mirror.cores.Target;
import io.github.kubesys.mirror.cores.datas.KubeData;
import io.github.kubesys.mirror.cores.sources.KubeSource;
import io.github.kubesys.mirror.cores.sources.KubeSourceExtractor;
import io.github.kubesys.mirror.cores.targets.PostgresDataTarget;
import io.github.kubesys.mirror.cores.targets.PostgresMetaTarget;
import io.github.kubesys.mirror.cores.targets.RabbitMQDataTarget;
import io.github.kubesys.mirror.cores.utils.KubeUtil;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/17
 *
 * 本项目的主要作用是将Kubernetes的所有状态同步到Postgres数据库中
 * 
 * 具体文档见https://www.yuque.com/kubesys/backend/cbtrr8nshxowbgiy
 */
public class Server {
	
	/**
	 * @param args      系统参数
	 * @throws Exception  启动的异常
	 */
	public static void main(String[] args) throws Exception {
		
		Target<KubeData> tableTarget = new PostgresMetaTarget();
		Target<KubeData> dbTarget    = new PostgresDataTarget();
		Target<KubeData> msgTarget   = new RabbitMQDataTarget();
		dbTarget.setNext(msgTarget);
		KubeSource source = new KubeSourceExtractor(tableTarget, dbTarget);
//		source.startCollect();
		
		JsonNode json = source.getKubeClient().getKindDesc().get("Pod");
		source.startCollect("Pod", KubeUtil.toKubeMeta("Pod", json));
	}
}
