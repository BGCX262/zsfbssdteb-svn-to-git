package com.f2.socket;


import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSocketReceiver extends Thread {
	
	private final Socket client;
	
    private DataInputStream dataInputStream;
    
    private OnPacketReceiveListener onPacketReceiveListener;
    
	public TCPSocketReceiver(Socket client) {
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
	
	public void setOnPacketReceiveListener(OnPacketReceiveListener listener) {
		onPacketReceiveListener = listener;
	}
	
	@Override
	public void run() {
		
		if (client.isConnected()) {
			try {
				dataInputStream = new DataInputStream(client.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			// TODO 处理链接信息
			while (!client.isClosed()) {
				
				synchronized (this) {
					try {
						int packetId = dataInputStream.readInt();
						int originalDataSize = dataInputStream.readInt();
						int dataSize = dataInputStream.readInt();
						// System.out.println("data received, packetId/originalDataSize/DataSize:" + Integer.toHexString(packetId) + "/" + originalDataSize + "/" + dataSize);
						byte[] data = new byte[dataSize];
						int readLength = 0, readOffset = 0;
						while (readOffset < data.length && (readLength = dataInputStream.read(data, readOffset, data.length - readOffset)) >= 0) {
							readOffset += readLength;
							// System.out.println("data transfered:" + readLength);
						}
						if (onPacketReceiveListener != null) {
							onPacketReceiveListener.onPacketReceive(new TCPSocketPacket(packetId, data, originalDataSize));
						}
					} catch (Exception e) {
					}
				}
				
			}
		}
		
		try {
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			dataInputStream.close();
		} catch (Exception e) {}
	}
	
	public interface OnPacketReceiveListener {
		public void onPacketReceive(TCPSocketPacket packet);
	}
}
