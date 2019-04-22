package org.leolo.jmodbot;

public class IRCNetwork {
	private String networkName;
	private String nickname;
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
}
