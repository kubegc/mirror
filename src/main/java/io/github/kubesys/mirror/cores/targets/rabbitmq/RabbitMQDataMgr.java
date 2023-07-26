/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.targets.rabbitmq;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.Channel;

import io.github.kubesys.mirror.cores.clients.RabbitMQClient;
import io.github.kubesys.mirror.cores.datas.KubeDataModel;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.2.0
 * @since    2023/07/25
 *
 */
public class RabbitMQDataMgr {

	/**
	 * 日志
	 */
	static final Logger m_logger = Logger.getLogger(RabbitMQDataMgr.class.getName());
	
	public final RabbitMQClient mqClient;

	public RabbitMQDataMgr(RabbitMQClient mqClient) {
		this.mqClient = mqClient;
	}
	
	public void sendMsg(Channel channel, String queue, String type, KubeDataModel data) {
		
		ObjectNode msg = new ObjectMapper().createObjectNode();
		msg.put("type", type);
		msg.set("data", data.getData());
		
		try {
			mqClient.publish(channel, queue, msg.toPrettyString());
		} catch (Exception e) {
			// TODO
			m_logger.severe(e.toString());
		}
	}
}
