/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror;

import io.github.kubesys.mirror.cores.Target;
import io.github.kubesys.mirror.cores.datas.KubeData;
import io.github.kubesys.mirror.cores.sources.KubeSource;
import io.github.kubesys.mirror.cores.sources.KubeSourceExtractor;
import io.github.kubesys.mirror.cores.targets.PostgresDataTarget;
import io.github.kubesys.mirror.cores.targets.PostgresMetaTarget;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/17
 *
 * 本项目的主要作用是将Kubernetes的所有状态同步到Postgres数据库中
 */
public class Server {
	
	/**
	 * @param args      系统参数
	 * @throws Exception  启动的异常
	 */
	public static void main(String[] args) throws Exception {
		
		Target<KubeData> metaTarget = new PostgresMetaTarget();
		Target<KubeData> dataTarget = new PostgresDataTarget();
		KubeSource source = new KubeSourceExtractor(metaTarget, dataTarget);
		source.startCollect();
	}

}
