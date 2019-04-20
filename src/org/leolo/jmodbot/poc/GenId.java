package org.leolo.jmodbot.poc;

import org.leolo.jmodbot.util.IdentifierGenerator;

public class GenId {
	public static void main(String [] args) {
		for(int i=0;i<1000;i++) {
			System.out.println(IdentifierGenerator.getInstance().getConnectionIdentifier());
		}
	}
}
