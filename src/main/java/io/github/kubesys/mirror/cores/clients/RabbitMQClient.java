/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.clients;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import io.github.kubesys.mirror.cores.Env;

/**
 * @author wuheng@iscas.ac.cn
 * @version 0.2.0
 * @since 2023/07/21
 * 
 *        面向企业基础设施管理，用户量不大，未来支持连接池 目前，为每一个Kubernetes的Kind分配一个客户端
 */
public class RabbitMQClient {

	/**
	 * 日志
	 */
	static final Logger m_logger = Logger.getLogger(RabbitMQClient.class.getName());

	/**
	 * 默认MQ地址
	 */
	static final String DEFAULT_URL = "kube-message:kube-system";

	/**
	 * 默认MQ端口
	 */
	static final String DEFAULT_PORT = "30304";

	/**
	 * 默认用户名
	 */
	static final String DEFAULT_USERNAME = "root";

	/**
	 * 默认密码
	 */
	static final String DEFAULT_PASSWORD = "onceas";

	/**
	 * 连接池
	 */
	protected Connection connection;

	/**
	 * 连接属性
	 */
	protected AMQP.BasicProperties properties;

	/**
	 * 连接通道
	 */
	protected static Map<String, Channel> channels = new HashMap<>();

	/**
	 * 
	 */
	public RabbitMQClient() {
		this(getEnv(Env.ENV_MQ_URL, DEFAULT_URL), getEnv(Env.ENV_MQ_PORT, DEFAULT_PORT),
				getEnv(Env.ENV_MQ_USER, DEFAULT_USERNAME), getEnv(Env.ENV_MQ_PWD, DEFAULT_PASSWORD));
	}

	/**
	 * @param url      url
	 * @param port     端口
	 * @param username 用户名
	 * @param password 密码
	 * 
	 */
	public RabbitMQClient(String url, String port, String username, String password) {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(url);
			factory.setPort(Integer.parseInt(port)); //
			factory.setUsername(username);
			factory.setPassword(password);
			factory.setAutomaticRecoveryEnabled(true);

			this.properties = new AMQP.BasicProperties.Builder().expiration("600000") // 10分钟
					.build();
			this.connection = factory.newConnection();

		} catch (Exception ex) {
			m_logger.severe("wrong rabbitmq parameters，or unavailable network." + ex);
			System.exit(1);
		}
	}

	public synchronized void publish(String queue, String data) {
		Channel channel = channels.get(queue);

		try {
			if (channel == null) {
				channel = connection.createChannel();
				channel.addShutdownListener(new ConnectionShutdownListener(connection, queue));
				channels.put(queue, channel);
			}

//			channel.basicPublish("", queue, properties, data.getBytes());
			channel.queueDeclare(queue, false, false, false, null);
			m_logger.warning("send message successful," + data);
		} catch (IOException e) {
			m_logger.warning("unable to send message:" + e);
		}
	}

	/**
	 * @param key 关键字
	 * @param def 默认值
	 * @return 环境变量
	 */
	private static String getEnv(String key, String def) {
		String val = System.getenv(key);
		return val == null ? def : val;
	}

	private static class ConnectionShutdownListener implements ShutdownListener {

		protected final Connection connection;

		protected final String queue;

		private ConnectionShutdownListener(Connection connection, String queue) {
			super();
			this.connection = connection;
			this.queue = queue;
		}

		@Override
		public void shutdownCompleted(ShutdownSignalException cause) {
			m_logger.warning("unable to connect to rabbitmq:" + cause);
			try {
				Channel channel = connection.createChannel();
				channel.addShutdownListener(new ConnectionShutdownListener(connection, queue));
				channels.put(queue, channel);
			} catch (IOException e) {
				m_logger.warning("unable to connect to rabbitmq:" + e);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
			}
		}
	}
}
