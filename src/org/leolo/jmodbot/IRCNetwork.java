package org.leolo.jmodbot;

import java.util.List;

public class IRCNetwork {
	private String networkName;
	private String nickname;
	private String realName;
	private String password;
	private String username;
	private List<Channel> channel;
	private List<CAPModule> capModules;
	private List<Module> module;
	
	/**
	 * @return the networkName
	 */
	public String getNetworkName() {
		return networkName;
	}
	
	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @param networkName the networkName to set
	 */
	protected void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	/**
	 * @param nickname the nickname to set
	 */
	protected void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public List<Channel> getChannel() {
		return channel;
	}

	public void setChannel(List<Channel> channel) {
		this.channel = channel;
	}

	/**
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * @param realName the realName to set
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the capModules
	 */
	public List<CAPModule> getCapModules() {
		return capModules;
	}

	/**
	 * @param capModules the capModules to set
	 */
	public void setCapModules(List<CAPModule> capModules) {
		this.capModules = capModules;
	}

	/**
	 * @return the module
	 */
	public List<Module> getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	public void setModule(List<Module> module) {
		this.module = module;
	}
}
