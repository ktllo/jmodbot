package org.leolo.jmodbot;

import java.io.IOException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SASLCapModule extends CAPModule {
	Logger log = LoggerFactory.getLogger(SASLCapModule.class);
	
	public static final String CONFIG_USER_ID = "_CAP_SASL_UID";
	public static final String CONFIG_USER_PASSWORD = "_CAP_SASL_PWD";
	
	private final String TOKEN_AUTH_RESP = new String();
	
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
		getSocket().registerResponseListener("AUTHENTICATE", this);
	}
	
	@Override
	public void processGenericMessage(String [] tokens) {
		if("AUTHENTICATE".equals(tokens[0])) {
			synchronized(TOKEN_AUTH_RESP) {
				TOKEN_AUTH_RESP.notifyAll();
			}
		}
	}
	
	@Override
	public void perfromPreconnectionSequence() {
		try {
			String uid = getConfig().getCustomConfig().get(CONFIG_USER_ID);
			String pwd = getConfig().getCustomConfig().get(CONFIG_USER_PASSWORD);
			if(uid==null) {
				log.warn("No user id for SASL is given. Nickname {} is used instead.",getConfig().getNickname());
				uid=getConfig().getNickname();
			}
			if(pwd==null) {
				log.error("No password is given for SASL. Unable to identify with service.");
				return;
			}
			getSocket().sendRaw("AUTHENTICATE PLAIN");
			StringBuffer sb = new StringBuffer();
			sb.append(uid).append('\0').append(uid).append('\0').append(pwd);
			synchronized(TOKEN_AUTH_RESP) {
				try {
					TOKEN_AUTH_RESP.wait();
				}catch(InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
			getSocket().sendRaw("AUTHENTICATE "+Base64.getEncoder().encodeToString(sb.toString().getBytes()));
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}

}
