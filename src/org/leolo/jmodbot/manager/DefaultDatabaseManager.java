package org.leolo.jmodbot.manager;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.metamodel.EntityType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.leolo.jmodbot.Configuration;
import org.leolo.jmodbot.model.DataPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDatabaseManager implements DatabaseManager {
	private Logger log = LoggerFactory.getLogger(Configuration.class);
	
	private static final String TOKEN_MAIN = new String();
	
	private SessionFactory sessionFactory=null;
	
	public DefaultDatabaseManager(String url, String username, String password, String dialect, String driver, Set<Class<?>> entityClass) {
		synchronized(TOKEN_MAIN) {
			if(sessionFactory!=null) {
				throw new RuntimeException("SessionFactory already created!");
			}
			Properties prop= new Properties();
			prop.setProperty("hibernate.connection.url", url);
			prop.setProperty("hibernate.connection.username", username);
			prop.setProperty("hibernate.connection.password", password);
			prop.setProperty("hibernate.dialect", dialect);
			prop.setProperty("hibernate.connection.driver_class", driver);
			prop.setProperty("hibernate.show_sql", "true");
			prop.setProperty("hibernate.format_sql", "false");
			prop.setProperty("hibernate.use_sql_comments", "false");
			prop.setProperty("connection.provider_class","org.hibernate.connection.C3P0ConnectionProvider");        
			prop.setProperty("hibernate.c3p0.acquire_increment","1");
			prop.setProperty("hibernate.c3p0.idle_test_period","60");
			prop.setProperty("hibernate.c3p0.min_size","10");
			prop.setProperty("hibernate.c3p0.max_size","200");
			prop.setProperty("hibernate.c3p0.max_statements","50");
			prop.setProperty("hibernate.c3p0.timeout","0");
			prop.setProperty("hibernate.c3p0.acquireRetryAttempts","1");
			prop.setProperty("hibernate.c3p0.acquireRetryDelay","250");
			
			org.hibernate.cfg.Configuration conf = new org.hibernate.cfg.Configuration();
			conf.addProperties(prop);
			conf.addPackage("org.leolo.jmodbot.model");
			conf.addResource("org/leolo/jmodbot/model/Users.hbm.xml");
			for(Class<?> clazz:entityClass) {
				conf.addAnnotatedClass(clazz);
			}
			sessionFactory = conf.buildSessionFactory();
			log.info("Listing mapped entities, {} expected.",sessionFactory.getMetamodel().getEntities().size());
			for(EntityType<?> et:sessionFactory.getMetamodel().getEntities()) {
				log.info(">{} mapped to class {}",et.getName(),et.getJavaType());
			}
			checkSchemaVersion();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.leolo.jmodbot.manager.DatabaseManager#getSession()
	 */
	@Override
	public Session getSession() {
		synchronized(TOKEN_MAIN) {
			if(sessionFactory==null) {
				throw new RuntimeException("SessionFactory not created yet");
			}
		}
		return sessionFactory.openSession();
	}
	
	public void checkSchemaVersion() {
		Session s = getSession();
		final String HQL = "from DataPair where keyName=:keyName";
		Query q = s.createQuery(HQL);
		q.setParameter("keyName", "CORE_DB_VER");
		List l = q.list();
		log.info("{}",l.get(0));
	}
	
}
