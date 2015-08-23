package com.f2.socket;



public interface MySocketStateListener {
	public void onConnected(TCPSocketClient client);
	public void onDisconnected(TCPSocketClient client);
	public void onConnectFailed(TCPSocketClient client);
	public void onMessageFetched(TCPSocketClient client, TCPSocketPacket packet);
}
