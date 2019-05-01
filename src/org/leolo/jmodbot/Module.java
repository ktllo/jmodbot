package org.leolo.jmodbot;

import java.util.Set;

import org.leolo.jmodbot.IRCConnectionConfiguration.ConfigurationSnapshot;
import org.leolo.jmodbot.manager.DatabaseManager;

public abstract class Module {
	
	private IRCSocket socket;
	private DatabaseManager dbManager;
	
	
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
	
	public String getDefaultRuntimeName() {
		return "__module_"+this.getClass().getCanonicalName().replace('.', '_');
	}
	
	public Set<Class<?>> getEntityClasses(){
		return null;
	}
	
	public final DatabaseManager getDatabaseManager() {
		return this.dbManager;
	}
}
