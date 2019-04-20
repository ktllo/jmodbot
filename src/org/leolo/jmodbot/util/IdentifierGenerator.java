package org.leolo.jmodbot.util;

import java.util.Base64;
import java.util.Random;

public class IdentifierGenerator {
	
	private static IdentifierGenerator instance;
	
	private Random random;
	
	private IdentifierGenerator() {
		random = new Random();
	}
	
	public static IdentifierGenerator getInstance() {
		if(instance == null) {
			instance = new IdentifierGenerator();
		}
		return instance;
	}
	
	public String getConnectionIdentifier() {
		byte [] bytes = new byte[3];
		String id;
		while(true) {
			random.nextBytes(bytes);
			id =  new String(Base64.getEncoder().encode(bytes));
			if(id.contains("+")||id.contains("/"))
				continue;
			return id;
		}
	}
	
}
