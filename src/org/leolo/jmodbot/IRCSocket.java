package org.leolo.jmodbot;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IRCSocket {
	
	Logger log = LoggerFactory.getLogger(IRCSocket.class);
	
	private Socket socket;
	
	public IRCSocket(Socket socket) {
		this.socket = socket;
	}
	
	
}
