package org.leolo.jmodbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.collections4.map.UnmodifiableMap;

public class IRCConnectionConfiguration {
	
	private String hostname;
	private int port;
	
	private boolean useSSL;
	private boolean acceptAllCertificate;
	
	private String nickname;
	private String fullname;
	private String serverPassword;
	
	private List<Class<? extends CAPModule>> capModules;
	
	private Map<String, String> customConfig;
	
	public IRCConnectionConfiguration() {
		capModules = new Vector<>();
		customConfig = new Hashtable<>();
	}
	
	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		if(port <=0 || port > 65535) {
			throw new IllegalArgumentException("Port number must between 0 and 65535");
		}
		this.port = port;
	}

	/**
	 * @return the useSSL
	 */
	public boolean isUseSSL() {
		return useSSL;
	}

	/**
	 * @param useSSL the useSSL to set
	 */
	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	/**
	 * @return the acceptAllCertificate
	 */
	public boolean isAcceptAllCertificate() {
		return acceptAllCertificate;
	}

	/**
	 * @param acceptAllCertificate the acceptAllCertificate to set
	 */
	public void setAcceptAllCertificate(boolean acceptAllCertificate) {
		this.acceptAllCertificate = acceptAllCertificate;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public void addCAPModule(Class<? extends CAPModule> module) {
		capModules.add(module);
	}
	
	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	public void addCustomConfig(String key, String value) {
		this.customConfig.put(key, value);
	}
	
	public ConfigurationSnapshot build() {
		return new ConfigurationSnapshot();
	}
	
	public String getServerPassword() {
		return serverPassword;
	}

	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}

	public class ConfigurationSnapshot{
		private ConfigurationSnapshot() {
			this.hostname = IRCConnectionConfiguration.this.hostname;
			this.port = IRCConnectionConfiguration.this.port;
			this.useSSL = IRCConnectionConfiguration.this.useSSL;
			this.acceptAllCertificate = IRCConnectionConfiguration.this.acceptAllCertificate;
			this.nickname = IRCConnectionConfiguration.this.nickname;
			this.fullname = IRCConnectionConfiguration.this.fullname;
			List<Class<? extends CAPModule>> list = new ArrayList<>();
			list.addAll(IRCConnectionConfiguration.this.capModules);
			this.capModules = new UnmodifiableList<>(list);
			Map<String, String> map = new HashMap<>();
			map.putAll(IRCConnectionConfiguration.this.customConfig);
			this.customConfig = UnmodifiableMap.unmodifiableMap(map);
			this.serverPassword=IRCConnectionConfiguration.this.serverPassword;
		}

		private String hostname;
		private int port;
		
		private boolean useSSL;
		private boolean acceptAllCertificate;
		
		private String nickname;
		private String fullname;
		private String serverPassword;
		
		private List<Class<? extends CAPModule>> capModules;
		private Map<String, String> customConfig;
		/**
		 * @return the hostname
		 */
		public String getHostname() {
			return hostname;
		}

		/**
		 * @return the port
		 */
		public int getPort() {
			return port;
		}

		/**
		 * @return the useSSL
		 */
		public boolean isUseSSL() {
			return useSSL;
		}

		/**
		 * @return the acceptAllCertificate
		 */
		public boolean isAcceptAllCertificate() {
			return acceptAllCertificate;
		}

		/**
		 * @return the nickname
		 */
		public String getNickname() {
			return nickname;
		}

		/**
		 * @return the fullname
		 */
		public String getFullname() {
			return fullname;
		}

		/**
		 * @return the capModules
		 */
		public List<Class<? extends CAPModule>> getCapModules() {
			return capModules;
		}

		/**
		 * @return the customConfig
		 */
		public Map<String, String> getCustomConfig() {
			return customConfig;
		}

		public String getServerPassword() {
			return serverPassword;
		}
	}
}
