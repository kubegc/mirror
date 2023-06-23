/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.org.gitlink.mirror;

import cn.org.gitlink.mirror.cores.Target;
import cn.org.gitlink.mirror.cores.datas.KubeData;
import cn.org.gitlink.mirror.cores.sources.KubeSource;
import cn.org.gitlink.mirror.cores.sources.KubeSourceExtractor;
import cn.org.gitlink.mirror.cores.targets.PostgresDataTarget;
import cn.org.gitlink.mirror.cores.targets.PostgresMetaTarget;

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
//		source.startCollect("Pod", source.getKubeClient().getKindDesc().get("Pod"));
	}

}
