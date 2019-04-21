package org.leolo.jmodbot;

import org.leolo.jmodbot.IRCConnectionConfiguration.ConfigurationSnapshot;

public abstract class Module {
	
	private IRCSocket socket;
	
	public Module(IRCSocket socket) {
		this.socket = socket;
	}

	protected final void setSocket(IRCSocket socket) {
		this.socket = socket;
	}
	
	protected final IRCSocket getSocket() {
		return socket;
	}
	
	public void processGenericMessage(String [] tokens) {
		
	}
	
	protected final ConfigurationSnapshot getConfig() {
		return socket.getConfig();
	}
}
