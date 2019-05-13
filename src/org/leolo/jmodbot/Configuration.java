package org.leolo.jmodbot;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.collections4.map.UnmodifiableMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.leolo.jmodbot.manager.DatabaseManager;
import org.leolo.jmodbot.manager.DefaultDatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	
	
	Logger log = LoggerFactory.getLogger(Configuration.class);
	
	private Map<String, Identity> identityMap;
	private Set<Class<? extends CAPModule>> capModules;
	private Set<Class<?>> entityClass;
	private Map<String, String> customConfigs;
	private Database database = new Database();
	private DatabaseManager databaseManager;
	
	public class Identity{
		private String nickname;
		private String username;
		private String password;
		private String type;
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Identity [nickname=" + nickname + ", username=" + username + ", password=" + password + ", type="
					+ type + "]";
		}
		/**
		 * @return the nickname
		 */
		public String getNickname() {
			return nickname;
		}
		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}
		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}
		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}
	}
	
	public class Database{
		private String dialect;
		private String driver;
		private String connectionString;
		private String user;
		private String password;
		/**
		 * @return the dialect
		 */
		public String getDialect() {
			return dialect;
		}
		/**
		 * @return the driver
		 */
		public String getDriver() {
			return driver;
		}
		/**
		 * @return the connectionString
		 */
		public String getConnectionString() {
			return connectionString;
		}
		/**
		 * @return the user
		 */
		public String getUser() {
			return user;
		}
		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}
	}
	
	private Configuration() {
		identityMap = new HashMap<>();
		customConfigs = new HashMap<>();
		capModules = new HashSet<>();
		entityClass = new HashSet<>();
	}
	
	public static Configuration getConfiguration(File file) throws Exception {
		Configuration conf = new Configuration();
		try {
			conf.parse(file);
		} catch (DocumentException e) {
			throw new Exception(e);
		}
		return conf;
	}
		
	@SuppressWarnings("unchecked")
	private void parse(File inputFile) throws DocumentException {
		log.info("Reading configuration file {}",inputFile.getAbsolutePath());
		SAXReader saxBuilder = new SAXReader();
		Document document;
		document = saxBuilder.read(inputFile);
		log.info("File size is {} bytes", inputFile.length());
		log.debug("Root element is {}",document.getRootElement().getName());
		log.debug("Docuemnt type is {}",document.getClass().getCanonicalName());
		Element rootElement = document.getRootElement();
		//Stage 1:parse identities
		List<Element> leIdentity = rootElement.elements("identity");
		log.info("{} identity found.", leIdentity.size());
		for(Element eIdentity:leIdentity) {
			String id = eIdentity.attributeValue("id");
			Identity identity = new Identity();
			identity.type = eIdentity.attributeValue("type");
			identity.nickname = eIdentity.elementText("nickname");
			identity.username = eIdentity.elementText("username");
			identity.password = eIdentity.elementText("password");
			log.debug("{}", identity);
			identityMap.put(id, identity);
		}
		//Stage 2: Load bot level CAP modules
		if(rootElement.element("caps")!=null) {
			List<Element> leCapMods = rootElement.element("caps").elements("cap");
			for(Element eCapMod:leCapMods) {
				String modName = eCapMod.getText().trim();
				Class<?> clazz;
				try {
					clazz = Class.forName(modName);
				} catch (ClassNotFoundException e) {
					log.error("Cannot load CAP module {} becasue {}",modName, e.getMessage());
					continue;
				}
				if(!CAPModule.class.isAssignableFrom(clazz)) {
					log.error("Cannot load CAP module {} becasue it is not a subclass of CAPModule",modName);
					continue;
				}
				if(!Modifier.isAbstract(clazz.getModifiers())) {
					log.error("Cannot load CAP module {} becasue it is abstract",modName);
					continue;
				}
				capModules.add((Class<? extends CAPModule>)clazz);
			}
		}
		log.debug("{} cap modules loaded", capModules.size());
		Element database = rootElement.element("database");
		this.database.dialect = database.elementText("dialect");
		this.database.driver = database.elementText("driver");
		this.database.connectionString = database.element("server").elementText("connection-string");
		this.database.user = database.element("server").elementText("username");
		this.database.password = database.element("server").elementText("password");
		databaseManager = new DatabaseManager(this);
		databaseManager.init();
	}
	
	private void addEntityClass(Class<?> clazz){
		Annotation [] annos = clazz.getAnnotations();
		boolean ok = false;
		for(Annotation anno:annos) {
			if(anno instanceof Entity) {
				ok = true;
				break;
			}
		}
		if(ok) {
			this.entityClass.add(clazz);
		}else {
			log.error("Class {} cannot be added to the list because it does not have correction annotation.",clazz.getCanonicalName());
		}
	}
	
	public static Configuration getConfiguration() throws Exception {
		return getConfiguration(new File("bot.xml"));
	}
	
	public Map<String, String> getBotScopeCustomConfig(){
		return UnmodifiableMap.unmodifiableMap(customConfigs);
	}

}
