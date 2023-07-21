/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.targets;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.kubesys.client.KubernetesConstants;
import io.github.kubesys.mirror.cores.Target;
import io.github.kubesys.mirror.cores.clients.RabbitMQClient;
import io.github.kubesys.mirror.cores.datas.KubeData;
import io.github.kubesys.mirror.cores.utils.SQLUtil;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.2.0
 * @since    2023/07/21
 *
 */
public class RabbitMQDataTarget extends Target<KubeData> {

	/**
	 * 日志
	 */
	static final Logger m_logger = Logger.getLogger(RabbitMQDataTarget.class.getName());
	
	
	/**
	 * PG的客户端
	 */
	private static final RabbitMQClient mqClient = new RabbitMQClient();
	
	@Override
	public synchronized void doHandleAdded(KubeData data) throws Exception {
		sendMsg(KubernetesConstants.JSON_TYPE_ADDED, data);
	}

	@Override
	public void doHandleModified(KubeData data) throws Exception {
		sendMsg(KubernetesConstants.JSON_TYPE_MODIFIED, data);
	}

	@Override
	public void doHandleDeleted(KubeData data) throws Exception {
		sendMsg(KubernetesConstants.JSON_TYPE_DELETED, data);
	}
	
	private void sendMsg(String type, KubeData data) {
		String queue = SQLUtil.table(data.getMeta().getPlural());
		ObjectNode msg = new ObjectMapper().createObjectNode();
		msg.put("type", type);
		msg.set("data", data.getData());
		mqClient.publish(queue, msg.toPrettyString());
	}
}
