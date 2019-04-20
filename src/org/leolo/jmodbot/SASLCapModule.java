package org.leolo.jmodbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SASLCapModule extends CAPModule {
	Logger log = LoggerFactory.getLogger(SASLCapModule.class);
	public SASLCapModule(IRCSocket socket) {
		super(socket);
		log.info("SASLCapModule Loaded");
	}
	
	@Override
	public String getCAPName() {
		return "sasl";
	}
	
	@Override
	public void performHookRegisteration() {
		getSocket().registerResponseListener("900", this);
		getSocket().registerResponseListener("901", this);
		getSocket().registerResponseListener("902", this);
		getSocket().registerResponseListener("903", this);
		getSocket().registerResponseListener("904", this);
		getSocket().registerResponseListener("905", this);
		getSocket().registerResponseListener("906", this);
		getSocket().registerResponseListener("907", this);
		getSocket().registerResponseListener("908", this);
		
	}
	
	@Override
	public void processGenericMessage(String [] tokens) {
		
	}

}
