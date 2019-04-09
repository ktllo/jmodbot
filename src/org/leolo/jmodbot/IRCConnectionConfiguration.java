package org.leolo.jmodbot;

import java.util.Set;

public class IRCConnectionConfiguration {
	
	private String hostname;
	private int port;
	
	private boolean useSSL;
	private boolean acceptAllCertificate;
	
	private String nickname;
	
	private Set<String> capRequested;

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

	public Set<String> getCapRequested() {
		return capRequested;
	}

	public void setCapRequested(Set<String> capRequested) {
		this.capRequested = capRequested;
	}
	
}
