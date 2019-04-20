package org.leolo.jmodbot;

public abstract class CAPModule extends Module {

	public CAPModule(IRCSocket socket) {
		super(socket);
	}

	public abstract String getCAPName();
	
	public void performHookRegisteration() {
		
	}
	
	public void perfromPreconnectionSequence() {
		
	}
	
}
