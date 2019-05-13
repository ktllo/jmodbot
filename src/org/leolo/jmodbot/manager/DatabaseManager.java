package org.leolo.jmodbot.manager;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.leolo.jmodbot.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
	private Logger log = LoggerFactory.getLogger(DatabaseManager.class);
	private final String TOKEN_STATUS = new String();
	
	private Set<Class<?>> annoationClasses;
	private Set<String> xmlResources;
	private Configuration config;
	private Status status = Status.NEW;
	private SessionFactory sessionFactory;
	
	public Session getSession() {
		synchronized(TOKEN_STATUS){
			if(status!=Status.READY) {
				throw new IllegalStateException();
			}
		}
		return sessionFactory.openSession();
	};
	
	public DatabaseManager(Configuration conf) {
		annoationClasses = new HashSet<>();
		xmlResources = new HashSet<>();
		this.config = conf;
	}
	
	public void addClass(Class<?> clazz) {
		log.debug("Adding class {}", clazz.getCanonicalName());
		
	}
	
	public void init() {
		synchronized(TOKEN_STATUS){
			if(status!=Status.NEW) {
				throw new IllegalStateException();
			}
			status = Status.INIT;
		}
		Map<String, String> customConfig = config.getBotScopeCustomConfig();
		Properties prop= new Properties();
//		prop.setProperty("hibernate.connection.url", conf);
//		prop.setProperty("hibernate.connection.username", username);
//		prop.setProperty("hibernate.connection.password", password);
//		prop.setProperty("hibernate.dialect", dialect);
//		prop.setProperty("hibernate.connection.driver_class", driver);
		prop.setProperty("hibernate.show_sql", customConfig.getOrDefault("hibernate.show_sql", "true"));
		prop.setProperty("hibernate.format_sql", customConfig.getOrDefault("hibernate.format_sql", "false"));
		prop.setProperty("hibernate.use_sql_comments", customConfig.getOrDefault("hibernate.use_sql_comments", "false"));
		prop.setProperty("connection.provider_class",customConfig.getOrDefault("connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider"));
		prop.setProperty("hibernate.c3p0.acquire_increment",customConfig.getOrDefault("hibernate.c3p0.acquire_increment", "1"));
		prop.setProperty("hibernate.c3p0.idle_test_period",customConfig.getOrDefault("hibernate.c3p0.idle_test_period", "60"));
		prop.setProperty("hibernate.c3p0.min_size",customConfig.getOrDefault("hibernate.c3p0.min_size", "10"));
		prop.setProperty("hibernate.c3p0.max_size",customConfig.getOrDefault("hibernate.c3p0.max_size", "200"));
		prop.setProperty("hibernate.c3p0.max_statements",customConfig.getOrDefault("hibernate.c3p0.max_statements", "50"));
		prop.setProperty("hibernate.c3p0.timeout",customConfig.getOrDefault("hibernate.c3p0.timeout", "0"));
		prop.setProperty("hibernate.c3p0.acquireRetryAttempts",customConfig.getOrDefault("hibernate.c3p0.acquireRetryAttempts", "1"));
		prop.setProperty("hibernate.c3p0.acquireRetryDelay",customConfig.getOrDefault("hibernate.c3p0.acquireRetryDelay", "250"));
		org.hibernate.cfg.Configuration conf = new org.hibernate.cfg.Configuration();
		conf.addProperties(prop);
		for(String res:xmlResources) {
			conf.addResource(res);
		}
		for(Class<?> clazz:annoationClasses) {
			conf.addAnnotatedClass(clazz);
		}
		sessionFactory = conf.buildSessionFactory();
		synchronized(TOKEN_STATUS){
			status = Status.READY;
		}
	}

	public void addXmlResource(String xmlResource) {
		synchronized(TOKEN_STATUS){
			if(status!=Status.NEW) {
				throw new IllegalStateException();
			}
		}
		this.xmlResources.add(xmlResource);
	}
	public void addAnnoatedClass(Class<?> clazz) {
		synchronized(TOKEN_STATUS){
			if(status!=Status.NEW) {
				throw new IllegalStateException();
			}
		}
		boolean ok = false;
		for(Annotation annotation:clazz.getAnnotations()) {
			if(annotation instanceof javax.persistence.Entity) {
				ok = true;
				break;
			}
		}
		if(ok) {
			this.annoationClasses.add(clazz);
		}else {
			log.warn("Class {} is not annotated", clazz.getCanonicalName());
		}
	}
	
	public enum Status{
		/** 
		 * This is a newly created DatabaseManager, and can add new resources to it
		 */
		NEW,
		/**
		 * The connection to database is in progress
		 */
		INIT,
		/**
		 * This manager is ready to use.
		 */
		READY;
	}
}