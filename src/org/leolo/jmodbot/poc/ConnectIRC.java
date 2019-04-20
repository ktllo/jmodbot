package org.leolo.jmodbot.poc;

import java.io.IOException;

import org.leolo.jmodbot.IRCConnectionConfiguration;
import org.leolo.jmodbot.IRCSocket;
import org.leolo.jmodbot.SASLCapModule;

public class ConnectIRC {
	public static void main(String [] args) throws IOException {
		IRCConnectionConfiguration config = new IRCConnectionConfiguration();
		config.addCAPModule(SASLCapModule.class);
		config.setHostname("192.168.56.102");
		config.setPort(6667);
		config.setNickname("TestBot");
		config.setFullname("JModBot/alpha");
		IRCSocket socket = new IRCSocket(config);
		socket.connect();
	}
}
