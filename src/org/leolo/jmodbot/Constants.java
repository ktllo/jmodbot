package org.leolo.jmodbot;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Constants {

	public static final Marker LOG_MARKER_RAW_IO = MarkerFactory.getMarker("RawIO");
	
	public static final int SEND_Q_SIZE = 2500;
	public static final int RECV_Q_SIZE = 2500;
	
	public static final int SEND_SLEEP_TIME = 100;
	
	public static final int THREAD_POOL_SIZE = 32;

	public static final int THREAD_COUNT = 200;
}
