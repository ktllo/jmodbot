package org.leolo.jmodbot;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.leolo.jmodbot.manager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	
	
	Logger log = LoggerFactory.getLogger(Configuration.class);
	
	private Map<String, Identity> identityMap;
	private Set<Class<? extends CAPModule>> capModules;
	private Set<Class<?>> entityClass;
	
	
	class Identity{
		String nickname;
		String username;
		String password;
		String type;
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Identity [nickname=" + nickname + ", username=" + username + ", password=" + password + ", type="
					+ type + "]";
		}
	}
	
	private Configuration() {
		identityMap = new HashMap<>();
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
		String db_dialect = database.elementText("dialect");
		String db_driver = database.elementText("driver");
		String db_uri = database.element("server").elementText("connection-string");
		String db_user = database.element("server").elementText("username");
		String db_pass = database.element("server").elementText("password");
		entityClass.add(org.leolo.jmodbot.model.DataPair.class);
		DatabaseManager.setSessionFactory(db_uri, db_user, db_pass, db_dialect, db_driver, entityClass);
	}

	public static Configuration getConfiguration() throws Exception {
		return getConfiguration(new File("bot.xml"));
	}


}
