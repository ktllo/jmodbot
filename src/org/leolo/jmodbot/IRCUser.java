package org.leolo.jmodbot;

import java.util.Set;

public class IRCUser implements MessageDestination{
	private String ident;
	private String nickname;
	private String hostname;
	private String username;
	private IRCNetwork network;
	
	private	Set<Channel> channels;

	/**
	 * @return the ident
	 */
	public String getIdent() {
		return ident;
	}

	/**
	 * @param ident the ident to set
	 */
	public void setIdent(String ident) {
		this.ident = ident;
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the channels
	 */
	public Set<Channel> getChannels() {
		return channels;
	}

	/**
	 * @param channels the channels to set
	 */
	public void setChannels(Set<Channel> channels) {
		this.channels = channels;
	}

	@Override
	public String getMessageDestination() {
		// TODO Auto-generated method stub
		return nickname;
	}
}
