package com.f2.socket;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

public class TCPSocketServer {

	private final int DEFAULT_PORT = 9999;

	private boolean isWorking;

	private ServerSocket myServerSocket;

	private HashMap<String, TCPSocketClient> connectionPool = new HashMap<String, TCPSocketClient>();
	
	private MySocketStateListener mySocketStateListener;
	
	private final Thread socketConnectionRequestThread = new Thread() {
		@Override
		public void run() {
			System.out.println("tcp socket server has started at " + new Date(System.currentTimeMillis()).toString());
			while (isWorking) {
				try {
					Socket socketClient = myServerSocket.accept();
					String netAddressString = socketClient.getInetAddress().toString();
					TCPSocketClient client = new TCPSocketClient(socketClient);
					client.start();
					client.setSocketStateListener(mySocketStateListener);
					synchronized (socketConnectionRequestThread) {
						connectionPool.put(netAddressString, client);
					}
					System.out.println("tcp socket client " + netAddressString + " has connected at " + new Date(System.currentTimeMillis()).toString());
				} catch (Exception e) {
					System.out.println("tcp socket server has shut down at " + new Date(System.currentTimeMillis()).toString());
				}
			}
		};
		
		@Override
		public synchronized void start() {
			isWorking = true;
			super.start();
		};
		
		@Override
		public void interrupt() {
			isWorking = false;
			super.interrupt();
		};
	};

	public TCPSocketServer() throws IOException {
		myServerSocket = new ServerSocket(DEFAULT_PORT);
	}
	
	public TCPSocketServer(int port) throws IOException {
		myServerSocket = new ServerSocket(port);
	}
	
	public void setSocketStateListener(MySocketStateListener listener) {
		synchronized (socketConnectionRequestThread) {
			mySocketStateListener = listener;
			for (TCPSocketClient client : connectionPool.values()) {
				client.setSocketStateListener(mySocketStateListener);
			}
		}
	}

	public void start() {
		socketConnectionRequestThread.start();
	}

	public void stop() {
		isWorking = false;
		socketConnectionRequestThread.interrupt();
	}
}
