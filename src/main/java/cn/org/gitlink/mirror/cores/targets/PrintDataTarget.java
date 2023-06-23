/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.org.gitlink.mirror.cores.targets;

import cn.org.gitlink.mirror.cores.Target;
import cn.org.gitlink.mirror.cores.datas.KubeData;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/18
 *
 */
public class PrintDataTarget extends Target<KubeData> {

	@Override
	public void doHandleAdded(KubeData data) throws Exception {
		System.out.println("ADDED: " + data.getData().toPrettyString());
	}

	@Override
	public void doHandleModified(KubeData data) throws Exception {
		System.out.println("MODIFIED: " + data.getData().toPrettyString());
	}

	@Override
	public void doHandleDeleted(KubeData data) throws Exception {
		System.out.println("DELETED: " + data.getData().toPrettyString());
	}

}
