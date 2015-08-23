package com.f2.socket;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class TCPSocketSender extends Thread {

	private final ArrayList<TCPSocketPacket> sendDataPackageList = new ArrayList<TCPSocketPacket>(10);
	
	private final Socket client;
	
	public TCPSocketSender(Socket client) {
		this.client = client;
	}
	
	@Override
	public void interrupt() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.interrupt();
	}
	
	public void send(TCPSocketPacket packet) {
		synchronized (sendDataPackageList) {
			sendDataPackageList.add(packet);
			sendDataPackageList.notifyAll();
		}
	}
	
	@Override
	public void run() {
		if (client.isConnected()) {
			DataOutputStream dataOutputStream = null;
			try {
				dataOutputStream = new DataOutputStream(client.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			// TODO 处理链接信息
			while (!client.isClosed()) {
				// 已数据包为结构单项收发，发�?�?��数据后，再接收一个数�?
				// 队列先进先出�?
				while (sendDataPackageList.size() > 0) {
					TCPSocketPacket packet = sendDataPackageList.remove(0);
					byte[] sendData = packet.createSendData();
					try {
						dataOutputStream.write(sendData);
					} catch (Exception ignore) {
					}
				}
				
				synchronized (sendDataPackageList) {
					if (sendDataPackageList.size() == 0) {
						try {
							sendDataPackageList.wait();
						} catch (Exception e) {
							break;
						}
					}
				}
				
			}
			try {
				dataOutputStream.close();
			} catch (Exception e) {}
		}
		try {
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
