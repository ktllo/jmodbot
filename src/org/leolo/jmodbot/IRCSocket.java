package org.leolo.jmodbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IRCSocket {
	
	Logger log = LoggerFactory.getLogger(IRCSocket.class);
	
	private Queue<String> sendQueue = new LinkedList<>();
	
	private final String TOKEN_SEND_Q = new String();
	private final String TOKEN_STATUS_CHG = new String();
	
	private ConnectionStatus status;
	
	private IRCConnectionConfiguration config;
	private Socket socket;
	
	private Receiver receiver;
	private Sender sender;
	
	@Deprecated
	public IRCSocket(Socket socket) {
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
	
	public void connect() throws IOException {
		synchronized(TOKEN_STATUS_CHG) {
			if(status!=ConnectionStatus.NOT_CONNECTED&&status!=ConnectionStatus.DISCONNECTED) {
				throw new IllegalStateException();
			}
			status = ConnectionStatus.CONNECTING;
		}
		try {
			socket = new Socket(config.getHostname(),config.getPort());
		}catch (IOException e) {
			log.error(e.getMessage(),e);
			status = ConnectionStatus.FAILED;
			throw e;
		}
		sender = new Sender();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		receiver = new Receiver(reader);
		
		sender.start();
		receiver.start();
	}
	
	
	public void disconnect() {
		sender.stop();
		receiver.stop();
		
		try {
			socket.close();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		status = ConnectionStatus.DISCONNECTED;
	}
	/**
	 * @param line
	 * @throws IOException
	 */
	private void doSendRaw(String line) throws IOException{
		try {
			sendQueue.add(line);
		}catch(IllegalStateException e) {
			throw new IOException("Send-Q exceeded", e);
		}
		synchronized(TOKEN_SEND_Q) {
			TOKEN_SEND_Q.notifyAll();
		}
	}
	
	public void sendRaw(String line) throws IOException{
		String [] lines = line.split("\n");
		for(String s:lines) {
			doSendRaw(s);
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
			sendRaw("PONG "+line.substring(5));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public ConnectionStatus getStatus() {
		return status;
	}

	public void setStatus(ConnectionStatus status) {
		this.status = status;
	}

	public IRCConnectionConfiguration getConfig() {
		return config;
	}

	public void setConfig(IRCConnectionConfiguration config) {
		this.config = config;
	}

	class Sender extends Thread{
		public void run() {
			while(true) {
				if(!sendQueue.isEmpty()) {
					String line = sendQueue.poll();
					if(!line.endsWith("\r\n")) {
						line = line.concat("\r\n");
					}
					try {
						log.debug(Constants.LOG_MARKER_RAW_IO,">{}",line);
						socket.getOutputStream().write(line.getBytes());
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
					try {
						Thread.sleep(Constants.SEND_SLEEP_TIME);
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
					}
				}else {
					synchronized(TOKEN_SEND_Q) {
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
	
	class Receiver extends Thread{
		
		BufferedReader reader = null;
		
		public Receiver(BufferedReader reader) {
			this.reader = reader;
		}
		
		public void run() {
			
			
			while(true) {
				String line;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					continue;
				}
				log.info(Constants.LOG_MARKER_RAW_IO,"<{}", line);
				String uline = line.toUpperCase();
				
				if(uline.startsWith("PING")) {
					processPingMessage(line);
				}
			}
		}
	}
}
