package org.leolo.jmodbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.leolo.jmodbot.util.IdentifierGenerator;
import org.leolo.jmodbot.util.PriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IRCSocket {

	Logger log = LoggerFactory.getLogger(IRCSocket.class);

	private PriorityQueue sendQueue = new PriorityQueue(Constants.SEND_Q_SIZE);
	private Queue<String> recvQueue = new ArrayBlockingQueue<>(Constants.RECV_Q_SIZE);

	private final String TOKEN_SEND_Q = new String();
	private final String TOKEN_RECV_Q = new String();
	private final String TOKEN_STATUS_CHG = new String();
	private final String TOKEN_CON_STATUS_CHG = new String();
	private final String TOKEN_POOL_ID = new String();
	
	
	
	private ExecutorService threadPool;
	
	public final String CONNECTION_ID;

	private ConnectionStatus status = ConnectionStatus.NOT_CONNECTED;

	ConnectionStage stage = ConnectionStage.SOCKET_NOT_CONNECTED;
	private IRCConnectionConfiguration.ConfigurationSnapshot config;
	private Socket socket;

	private Receiver receiver;
	private Sender sender;
	private ProcessMessageThread pmt;
	private Map<String, List<Module>> messageReceiver;
	
	private int threadCount = 0;
	
	public IRCSocket(IRCConnectionConfiguration config) {
		CONNECTION_ID = IdentifierGenerator.getInstance().getConnectionIdentifier();
		messageReceiver = new HashMap<>();
		threadPool = Executors.newFixedThreadPool(Constants.THREAD_COUNT, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				synchronized(TOKEN_POOL_ID) {
					t.setName(CONNECTION_ID+"_TP_"+Integer.toHexString(threadCount++));
				}
				return t;
			}
			
		});
		this.config = config.build();
	}

	@Deprecated
	public IRCSocket(Socket socket) {
		messageReceiver = new HashMap<>();
		CONNECTION_ID = IdentifierGenerator.getInstance().getConnectionIdentifier();
		this.socket = socket;
		new Sender().start();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return;
		}
		new Receiver(reader).start();
	}
	private List<CAPModule> capModules = new Vector<>();
	public void connect() throws IOException {
		synchronized (TOKEN_STATUS_CHG) {
			if (status != ConnectionStatus.NOT_CONNECTED && status != ConnectionStatus.DISCONNECTED) {
				throw new IllegalStateException();
			}
			status = ConnectionStatus.CONNECTING;
		}
		try {
			socket = new Socket(config.getHostname(), config.getPort());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			status = ConnectionStatus.FAILED;
			throw e;
		}
		stage = ConnectionStage.SOCKET_READY;
		sender = new Sender();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		receiver = new Receiver(reader);
		for(Class<? extends CAPModule> c:config.getCapModules()) {
			try {
				CAPModule m = c.getConstructor(this.getClass()).newInstance(this);
				m.performHookRegisteration();
				capModules.add(m);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				log.error(e.getMessage(),e);
			}
		}
		pmt = new ProcessMessageThread(0);
		sender.start();
		receiver.start();
		pmt.start();
		
		// Send CAP request, nickanme and user info
		StringBuffer sb = new StringBuffer();
		sb.append("CAP REQ :");
		Iterator<CAPModule> iCapMod = capModules.iterator();
		while(iCapMod.hasNext()) {
			sb.append(iCapMod.next().getCAPName());
			if(iCapMod.hasNext()) {
				sb.append(" ");
			}
		}
		stage = ConnectionStage.AWAIT_CAP_ACK;
		this.registerResponseListener("CAP", new CAPAckListenModule() );
		sendRawPriority(sb.toString());
		if(config.getServerPassword()!=null) {
			sendRawPriority("PASS "+config.getServerPassword());
		}
		sendRawPriority("NICK "+config.getNickname());
		sendRawPriority("USER "+config.getNickname()+" "+config.getNickname()+" - :"+config.getFullname());
		
		
		synchronized(TOKEN_CON_STATUS_CHG) {
			try {
				TOKEN_CON_STATUS_CHG.wait();
			} catch (InterruptedException e) {
				log.error(e.getMessage(),e);
			}
		}
		//TODO: Check ACKed CAP
		for(CAPModule cm:capModules) {
			cm.perfromPreconnectionSequence();
		}
		sendRaw("CAP END");
		stage = ConnectionStage.CONNECTED;
		synchronized (TOKEN_STATUS_CHG) {
			status = ConnectionStatus.CONNECTED;
		}
	}

	public void disconnect() {
		sender.stop();
		receiver.stop();

		try {
			socket.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		status = ConnectionStatus.DISCONNECTED;
		stage = ConnectionStage.SOCKET_NOT_CONNECTED;
	}

	/**
	 * @param line
	 * @throws IOException
	 */
	private void doSendRaw(String line) throws IOException {
		try {
			sendQueue.add(line);
		} catch (IllegalStateException e) {
			throw new IOException("Send-Q exceeded", e);
		}
		synchronized (TOKEN_SEND_Q) {
			TOKEN_SEND_Q.notifyAll();
		}
	}

	public void sendRaw(String line) throws IOException {
		String[] lines = line.split("\n");
		for (String s : lines) {
			doSendRaw(s.replace("\r", ""));
		}
	}

	/**
	 * @return
	 * @see java.net.Socket#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	/**
	 * @return
	 * @see java.net.Socket#getLocalAddress()
	 */
	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}

	/**
	 * @return
	 * @see java.net.Socket#getLocalPort()
	 */
	public int getLocalPort() {
		return socket.getLocalPort();
	}

	/**
	 * @return
	 * @see java.net.Socket#getLocalSocketAddress()
	 */
	public SocketAddress getLocalSocketAddress() {
		return socket.getLocalSocketAddress();
	}

	/**
	 * @return
	 * @see java.net.Socket#getPort()
	 */
	public int getPort() {
		return socket.getPort();
	}

	/**
	 * @return
	 * @see java.net.Socket#getRemoteSocketAddress()
	 */
	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getTcpNoDelay()
	 */
	public boolean getTcpNoDelay() throws SocketException {
		return socket.getTcpNoDelay();
	}

	/**
	 * @return
	 * @see java.net.Socket#isBound()
	 */
	public boolean isBound() {
		return socket.isBound();
	}

	/**
	 * @return
	 * @see java.net.Socket#isClosed()
	 */
	public boolean isClosed() {
		return socket.isClosed();
	}

	/**
	 * @return
	 * @see java.net.Socket#isConnected()
	 */
	public boolean isConnected() {
		return socket.isConnected();
	}

	/**
	 * @return
	 * @see java.net.Socket#isInputShutdown()
	 */
	public boolean isInputShutdown() {
		return socket.isInputShutdown();
	}

	/**
	 * @return
	 * @see java.net.Socket#isOutputShutdown()
	 */
	public boolean isOutputShutdown() {
		return socket.isOutputShutdown();
	}

	protected void processPingMessage(String line) {
		try {
			sendRawPriority("PONG " + line.substring(5));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void sendRawPriority(String string) throws IOException {
		try {
			sendQueue.addPriority(string);
		} catch (IllegalStateException e) {
			throw new IOException("Send-Q exceeded", e);
		}
		synchronized (TOKEN_SEND_Q) {
			TOKEN_SEND_Q.notifyAll();
		}
	}

	public ConnectionStatus getStatus() {
		return status;
	}

	public void setStatus(ConnectionStatus status) {
		this.status = status;
	}

	public void sendMessage(MessageDestination md, String message) {
		try {
			sendRaw("PRIVMSG " + md.getMessageDestination() + " :" + message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		}
	}

	public void sendNotice(MessageDestination md, String message) {
		try {
			sendRaw("NOTICE " + md.getMessageDestination() + " :" + message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		}
	}

	public void registerResponseListener(String numeric, Module module) {
		if (!messageReceiver.containsKey(numeric)) {
			messageReceiver.put(numeric, new Vector<>());
		}
		messageReceiver.get(numeric).add(module);
	}

	class Sender extends Thread {
		public Sender() {
			this.setName(CONNECTION_ID+"_ST");
		}
		public void run() {
			while (true) {
				if (!sendQueue.isEmpty()) {
					String line = sendQueue.poll();
					try {
						log.debug(Constants.LOG_MARKER_RAW_IO, ">{}", line);
						line = line.concat("\r\n");
						socket.getOutputStream().write(line.getBytes());
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
					try {
						Thread.sleep(Constants.SEND_SLEEP_TIME);
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
					}
				} else {
					synchronized (TOKEN_SEND_Q) {
						try {
							TOKEN_SEND_Q.wait();
						} catch (InterruptedException e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		}
	}

	class Receiver extends Thread {

		BufferedReader reader = null;

		public Receiver(BufferedReader reader) {
			this.setName(CONNECTION_ID+"_RT");
			this.reader = reader;
		}

		public void run() {
			while (true) {
				String line = null;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					disconnect();
				}
				if (line == null) {
					log.error("Cannot read from socket");
					disconnect();
				}
				log.info(Constants.LOG_MARKER_RAW_IO, "<{}", line);
				String uline = line.toUpperCase();

				if (uline.startsWith("PING")) {
					processPingMessage(line);
				}

				recvQueue.add(line);

				synchronized (TOKEN_RECV_Q) {
					TOKEN_RECV_Q.notifyAll();
				}

			}
		}
	}

	class ProcessMessageThread extends Thread {
		
		public ProcessMessageThread(int i) {
			setName(CONNECTION_ID+"_PST"+i);
		}
		
		public void run() {
			while (true) {
				String line = null;
				synchronized (TOKEN_RECV_Q) {
					if (recvQueue.isEmpty()) {
						try {
							TOKEN_RECV_Q.wait();
						} catch (InterruptedException e) {
							log.error(e.getMessage(), e);
							continue;
						}
					}
					line = recvQueue.poll();
				}
				if(line ==null) {
					continue;
				}
				String [] tokens = line.split(" ");
				if(messageReceiver.containsKey(tokens[1])) {
					for(Module m:messageReceiver.get(tokens[1])) {
						threadPool.execute(new Runnable() {
							@Override
							public void run() {
								m.processGenericMessage(tokens);
							}
						});
					}
				}
				if(messageReceiver.containsKey(tokens[0])) {
					for(Module m:messageReceiver.get(tokens[0])) {
						threadPool.execute(new Runnable() {
							@Override
							public void run() {
								m.processGenericMessage(tokens);
							}
						});
					}
				}
				
			}
		}
	}
	
	enum ConnectionStage{
		SOCKET_NOT_CONNECTED,
		SOCKET_READY,
		AWAIT_CAP_ACK,
		CAP_ACKED,
		FINISHING_SEQ,
		CONNECTED;
	}
	
	class CAPAckListenModule extends Module{

		public CAPAckListenModule() {
			super(IRCSocket.this);
		}
		
		@Override
		public void processGenericMessage(String [] args) {
			if("ACK".equals(args[3])) {
				log.info("CAP Ack received, token cnt = {}", args.length);
				synchronized(TOKEN_CON_STATUS_CHG) {
					stage = ConnectionStage.CAP_ACKED;
					TOKEN_CON_STATUS_CHG.notifyAll();
				}
			}
		}
		
	}

	/**
	 * @return the config
	 */
	public IRCConnectionConfiguration.ConfigurationSnapshot getConfig() {
		return config;
	}
}
