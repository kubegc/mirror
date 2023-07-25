/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.targets.metadata;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.hibernate.exception.SQLGrammarException;

import io.github.kubesys.mirror.cores.Environment;
import io.github.kubesys.mirror.cores.clients.PostgresClient;
import io.github.kubesys.mirror.cores.datas.KubeDataModel;
import io.github.kubesys.mirror.cores.utils.SQLUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * @author   wuheng@iscas.ac.cn
 * @version  0.0.1
 * @since    2023/06/21
 *
 */
public class PostgresTableCreator {

	/**
	 * 日志
	 */
	static final Logger m_logger = Logger.getLogger(PostgresTableCreator.class.getName());
	
	/**
	 * 创建表的SQL语法
	 */
	public static final String CREATE_TABLE = "CREATE TABLE #NAME# (\r\n"
			+ "    \"name\" character varying(256) NOT NULL,\r\n"
			+ "    \"namespace\" character varying(64) NOT NULL,\r\n"
			+ "    \"apigroup\" character varying(128) NOT NULL,\r\n"
			+ "    \"region\" character varying(128) NOT NULL,\r\n"
			+ "    \"data\" json NOT NULL,\r\n"
			+ "    \"created\" timestamp NOT NULL,\r\n"
			+ "    \"updated\" timestamp NOT NULL,\r\n"
			+ "    PRIMARY KEY (\"name\", \"namespace\", \"apigroup\", \"region\")\r\n"
			+ ");";
	
	/**
	 * 查询表是否存在SQL语法
	 */
	public static final String QUERY_TABLE = "SELECT EXISTS (\r\n"
			+ "   SELECT 1\r\n"
			+ "   FROM information_schema.tables\r\n"
			+ "   WHERE table_name = '#NAME#'\r\n"
			+ ");";
	
	/**
	 * 删除表的SQL语法
	 */
	public static final String DROP_TABLE = "DROP TABLE #NAME#; ";

	/**
	 * SQL中关键字Name
	 */
	public static final String TABLE_NAME = "#NAME#";
	
	/**
	 * 删除对象的SQL语法
	 */
	public static final String DELETE_DATA = "DELETE FROM #NAME# WHERE region = ?";

	
	/**
	 * 已经创建的表
	 */
	public static final Set<String> createdTables = new HashSet<>();
	
	/**
	 * PG的客户端
	 */
	private static final PostgresClient pgClient = new PostgresClient();
	
	public synchronized void createTableIfNeed(KubeDataModel data) throws Exception {
		String table = SQLUtil.table(data.getMeta().getPlural());
		createTableIfNeed(table);
		deleteDataIfExist(table);
	}

	/**
	 * @param table 表名，对应plural
	 */
	private void createTableIfNeed(String table) {
		if (!createdTables.contains(table)) {
			try {
				createTable(table);
				createdTables.add(table);
			} catch (SQLGrammarException ex) {
				m_logger.info("Table'" + table + "'已经创建.");
				EntityTransaction transaction = pgClient.getEntityManager().getTransaction();
				if (transaction.isActive()) {
					transaction.rollback();
				}
			} catch (Exception ex) {
				m_logger.severe("无法创建Table:" + table + "，原因是" + ex);
				System.exit(1);
			}
		}
	}
	
	/**
	 * @param table     表名，对应plural
	 */
	void createTable(String table) {
		pgClient.execWithoutResult(CREATE_TABLE.replace(TABLE_NAME, table));
		m_logger.info("成功创建Table: " + table);
	}

	/**
	 * @param table      表名，对应plural
	 */
	void deleteTableIfExist(String table) {
		Object result = pgClient.execWithSingleResult(QUERY_TABLE.replace(TABLE_NAME, table));
		if ((boolean) result) {
			pgClient.execWithoutResult(DROP_TABLE.replace(TABLE_NAME, table));
		}
	}


	void deleteDataIfExist(String table) {
		EntityManager entityManager = pgClient.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		try {
			entityManager.createNativeQuery(DELETE_DATA.replace(TABLE_NAME, 
							SQLUtil.table(table)))
		        .setParameter(1, System.getenv(Environment.ENV_KUBE_REGION))
		        .executeUpdate();
			transaction.commit();
			m_logger.info("完成'" + table + "'中region为'" + System.getenv(Environment.ENV_KUBE_REGION) + "'对象删除.");
		} catch (Exception ex) {
			m_logger.warning("无法删除对象" + ex);
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
	}
}
