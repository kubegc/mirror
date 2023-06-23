/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.org.gitlink.mirror.cores.clients;

import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import cn.org.gitlink.mirror.cores.Env;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 * @author wuheng@iscas.ac.cn
 * @version  0.1.0
 * @since   2023/06/21
 * 
 * TODO 未来可以变成连接池
 * 面向企业基础设施管理，用户量不大
 * 目前，为每一个Kubernetes的Kind分配一个客户端
 */
public class PostgresClient   {

	/**
	 * 日志
	 */
	static final Logger m_logger = Logger.getLogger(PostgresClient.class.getName());
	
	/**
	 * 默认postgres地址
	 */
	static final String DEFAULT_JDBCURL    = "jdbc:postgresql://kube-database:kube-system/database";
	
	/**
	 * 默认用户名
	 */
	static final String DEFAULT_USERNAME   = "postgres";
	
	/**
	 * 默认密码
	 */
	static final String DEFAULT_PASSWORD   = "onceas";
	
	/**
	 * 默认驱动
	 */
	static final String DEFAULT_JDBCDRIVER = "org.postgresql.Driver";
	
	/**
	 * 默认数据库增删改查管理器
	 */
	protected EntityManager entityManager;

	/**
	 * 
	 */
	public PostgresClient() {
		this(getEnv(Env.ENV_JDBC_URL, DEFAULT_JDBCURL),
				getEnv(Env.ENV_JDBC_USER, DEFAULT_USERNAME),
				getEnv(Env.ENV_JDBC_PWD, DEFAULT_PASSWORD),
				getEnv(Env.ENV_JDBC_DRIVER, DEFAULT_JDBCDRIVER));
	}
	
	
	/**
	 * @param jdbcUrl        jdbc地址
	 * @param username       用户名
	 * @param password       密码
	 * @param jdbcDriver     jdbc驱动
	 */
	public PostgresClient(String jdbcUrl, String username, String password, String jdbcDriver) {
		try {
			Configuration configuration = new Configuration();
	        // 设置数据库连接信息
	        configuration.setProperty("hibernate.connection.url", jdbcUrl);
	        configuration.setProperty("hibernate.connection.username", username);
	        configuration.setProperty("hibernate.connection.password", password);
	        configuration.setProperty("hibernate.connection.driver_class", jdbcDriver);
	        // 构建SessionFactory
	        SessionFactory sessionFactory = configuration.buildSessionFactory();
	        // 创建Session对象
	        Session session = sessionFactory.openSession();
	        this.entityManager = session.getEntityManagerFactory().createEntityManager();
		} catch (Exception ex) {
			m_logger.severe("数据库连接参数错误，或者网络不好，无法实例化." + ex);
			System.exit(1);
		}
	}
	
	/**
	 * @param sql        SQL
	 * @return           查询结果
	 */
	public Object execWithSingleResult(String sql) {
		entityManager.getTransaction().begin();
		Query query = entityManager.createNativeQuery(sql);
		Object singleResult = query.getSingleResult();
		entityManager.getTransaction().commit();
		return singleResult;
	}
	
	/**
	 * @param sql        SQL
	 */
	public void execWithoutResult(String sql) {
		entityManager.getTransaction().begin();
		entityManager.createNativeQuery(sql).executeUpdate();
		entityManager.getTransaction().commit();
	}
	
	
	/**
	 * @return   数据库增删改查管理器
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * @param key    关键字
	 * @param def    默认值
	 * @return       环境变量
	 */
	private static String getEnv(String key, String def) {
		String val = System.getenv(key);
		return val == null ? def : val;
	}
	
}
