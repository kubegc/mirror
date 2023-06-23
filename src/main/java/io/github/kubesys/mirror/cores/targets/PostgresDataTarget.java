/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.targets;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.kubesys.mirror.cores.Env;
import io.github.kubesys.mirror.cores.Target;
import io.github.kubesys.mirror.cores.clients.PostgresClient;
import io.github.kubesys.mirror.cores.datas.KubeData;
import io.github.kubesys.mirror.cores.utils.KubeUtil;
import io.github.kubesys.mirror.cores.utils.SQLUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/21
 *
 */
public class PostgresDataTarget extends Target<KubeData> {

	/**
	 * 日志
	 */
	static final Logger m_logger = Logger.getLogger(PostgresDataTarget.class.getName());
	
	/**
	 * 创建对象的SQL语法
	 */
	public static final String INSERT = "INSERT INTO #NAME# (\"name\", \"namespace\", \"apigroup\", \"region\", \"data\", \"created\", \"updated\") " +
            "VALUES (?, ?, ?, ?, CAST(? AS json), ?, ?)";
	
	/**
	 * 更新对象的SQL语法
	 */
	public static final String UPDATE = "UPDATE #NAME# SET data = CAST(? AS json), updated = ? WHERE name = ? AND namespace = ? AND apigroup = ? AND region = ?";
	
	/**
	 * 删除对象的SQL语法
	 */
	public static final String DELETE = "DELETE FROM #NAME# WHERE name = ? AND namespace = ? AND apigroup = ? AND region = ?";

	
	/**
	 * SQL中关键字Name
	 */
	public static final String TABLE_NAME = "#NAME#";
	
	/**
	 * PG的客户端
	 */
	private static final PostgresClient pgClient = new PostgresClient();
	
	@Override
	public synchronized void doHandleAdded(KubeData data) throws Exception {
		EntityManager entityManager = pgClient.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		try {
			JsonNode value = data.getData();
			entityManager.createNativeQuery(INSERT.replace(TABLE_NAME, 
							SQLUtil.table(data.getMeta().getPlural())))
		        .setParameter(1, KubeUtil.getName(value))
		        .setParameter(2, KubeUtil.getNamespace(value))
		        .setParameter(3, KubeUtil.getGroup(value))
		        .setParameter(4, System.getenv(Env.ENV_KUBE_REGION))
		        .setParameter(5, value.toPrettyString())
		        .setParameter(6, KubeUtil.createdTime(value))
		        .setParameter(7, KubeUtil.updatedTime(value))
		        .executeUpdate();
			transaction.commit();
			m_logger.info("完成对象插入：" + value.toPrettyString());
		} catch (Exception ex) {
			m_logger.warning("无法插入对象" + ex + ":" + data.getData().toPrettyString());
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
	}

	@Override
	public void doHandleModified(KubeData data) throws Exception {
		EntityManager entityManager = pgClient.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		try {
			JsonNode value = data.getData();
			entityManager.createNativeQuery(UPDATE.replace(TABLE_NAME, 
							SQLUtil.table(data.getMeta().getPlural())))
				.setParameter(1, value.toPrettyString())
				.setParameter(2, KubeUtil.updatedTime(value))
		        .setParameter(3, KubeUtil.getName(value))
		        .setParameter(4, KubeUtil.getNamespace(value))
		        .setParameter(5, KubeUtil.getGroup(value))
		        .setParameter(6, System.getenv(Env.ENV_KUBE_REGION))
		        .executeUpdate();
			transaction.commit();
			m_logger.info("完成对象更新：" + value.toPrettyString());
		} catch (Exception ex) {
			m_logger.warning("无法更新对象" + ex + ":" + data.getData().toPrettyString());
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
	}

	@Override
	public void doHandleDeleted(KubeData data) throws Exception {
		EntityManager entityManager = pgClient.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		try {
			JsonNode value = data.getData();
			entityManager.createNativeQuery(DELETE.replace(TABLE_NAME, 
							SQLUtil.table(data.getMeta().getPlural())))
		        .setParameter(1, KubeUtil.getName(value))
		        .setParameter(2, KubeUtil.getNamespace(value))
		        .setParameter(3, KubeUtil.getGroup(value))
		        .setParameter(4, System.getenv(Env.ENV_KUBE_REGION))
		        .executeUpdate();
			transaction.commit();
			m_logger.info("完成对象删除：" + value.toPrettyString());
		} catch (Exception ex) {
			m_logger.warning("无法删除对象" + ex + ":" + data.getData().toPrettyString());
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
	}
}
