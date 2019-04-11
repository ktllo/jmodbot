package org.leolo.jmodbot;

import java.util.Set;

public class Channel implements MessageDestination{
	
	private String channelName;

	private IRCNetwork network;
	
	@Override
	public String getMessageDestination() {
		// TODO Auto-generated method stub
		return channelName;
	}
	
	class ChannelMember{
		Set<String> prefix;
		IRCUser user;
	}
}
