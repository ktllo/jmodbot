package org.leolo.jmodbot.poc;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import org.leolo.jmodbot.IRCSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConnect {
	public static void main(String [] args) throws IOException {
		Logger log = LoggerFactory.getLogger(TestConnect.class);
		Socket s = new Socket("192.168.56.102", 6667);
		IRCSocket ircs = new IRCSocket(s);
		ircs.sendRaw("CAP REQ :sasl");
		ircs.sendRaw("CAP END");
		ircs.sendRaw("NICK TestBot");
		ircs.sendRaw("USER user user 192.168.56.102 :realname");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			log.error(e.getMessage(),e);
		}
		ircs.sendRaw("JOIN #test");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error(e.getMessage(),e);
		}
		ircs.sendRaw("PRIVMSG #test :This is a message from the bot");
		while(true) {
			ircs.sendRaw("PRIVMSG #test :This is a message from the bot "+new Date());
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(),e);
			}
		}
	}
}
