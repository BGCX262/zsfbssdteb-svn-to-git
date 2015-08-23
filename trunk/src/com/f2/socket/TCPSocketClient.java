package com.f2.socket;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPSocketClient {

	private final Socket client;
	
    private final TCPSocketReceiver socketReceiver;
    
    private final TCPSocketSender socketSender;
    
    private MySocketStateListener mySocketStateListener;
    
    private InetAddress dstAddress;
    
    private int dstPort;
    
	public TCPSocketClient(Socket client) {
		this.client = client;
		dstAddress = client.getInetAddress();
		dstPort = client.getPort();
		socketReceiver = new TCPSocketReceiver(client);
		socketSender = new TCPSocketSender(client);
		socketReceiver.setOnPacketReceiveListener(new TCPSocketReceiver.OnPacketReceiveListener() {
			@Override
			public void onPacketReceive(TCPSocketPacket packet) {
				if (mySocketStateListener != null) {
					mySocketStateListener.onMessageFetched(TCPSocketClient.this, packet);
				}
			}
		});
	}
	
	public TCPSocketClient(String host, int port) throws UnknownHostException {
		this(new Socket());
		dstAddress = InetAddress.getByName(host);
		dstPort = port;
	}
	
	public TCPSocketClient(){
		this(new Socket());
	}
	
	public Socket getSocket() {
		return client;
	}
	
	public void setSocketStateListener(MySocketStateListener listener) {
		mySocketStateListener = listener;
	}
	
	public void send(TCPSocketPacket packet) {
		socketSender.send(packet);
	}
	
	public void connect(String host, int port) throws UnknownHostException {
		dstAddress = InetAddress.getByName(host);
		dstPort = port;
		start();
	}
	
	public void start() {
		if (!client.isConnected()) {
			new Thread() {
				@Override
				public void run() {
					try {
						client.connect(new InetSocketAddress(dstAddress, dstPort), 5000);
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					if (client.isConnected()) {
						onConnect();
					} else {
						onConnectFail();
					}
				}
			}.start();
		} else {
			onConnect();
		}
	}
	
	protected void onConnectFail() {
		if (mySocketStateListener != null) {
			mySocketStateListener.onConnectFailed(this);
		}
	}
	
	protected void onConnect() {
		socketReceiver.start();
		socketSender.start();
		if (mySocketStateListener != null) {
			mySocketStateListener.onConnected(this);
		}
	}
	
	public void stop() {
		socketReceiver.interrupt();
		socketSender.interrupt();
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		return client.isConnected();
	}
}
