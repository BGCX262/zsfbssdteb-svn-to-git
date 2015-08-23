package com.f2.socket;


import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TCPSocketPacket {
	
	public final static int DATA_PACKAGE_LENGTH = 256;
	
	private final static byte[] NULL_DATA = new byte[0];

	public final static int PACKET_ID_HEART = -0x1;
	public final static int PACKET_ID_REQUEST_DATA = 0x12;
	public final static int PACKET_ID_DATA = 0x13;

	public final static int DATA_ALL_SCREEN = 0x100;
	public final static int DATA_ADD_COMPONENT = 0x1;
	public final static int DATA_UPDATE_COMPONENT = 0x2;
	public final static int DATA_REMOVE_COMPONENT = 0x3;
	public final static int DATA_KEY_LEFT = 0x11;
	public final static int DATA_KEY_RIGHT = 0x12;
	public final static int DATA_KEY_UP = 0x13;
	public final static int DATA_KEY_DOWN = 0x14;
	public final static int DATA_KEY_SWITCH = 0x15;
	public final static int DATA_KEY_OPEN = 0x16;
	public final static int DATA_KEY_CONFIRM = 0x17;
	public final static int DATA_KEY_CANCEL = 0x18;
	public final static int DATA_KEY_PAGE_DOWN = 0x19;
	public final static int DATA_KEY_PAGE_UP = 0x1a;

	boolean hasZipped;
	byte[] data;
	int packetId;
	int originalDataSize;

	public TCPSocketPacket(int packetId, byte[] data) {
		this.packetId = packetId;
		this.data = data == null ? NULL_DATA : data;
		this.originalDataSize = data.length;
	}
	
	public TCPSocketPacket(int packetId, byte[] data, int originalDataSize) {
		this(packetId, data);
		hasZipped = originalDataSize != data.length;
		this.originalDataSize = originalDataSize;
	}
	
	public void zip() throws IOException {
		MyByteArrayOutputStream byteArrayOutputStream = new MyByteArrayOutputStream(originalDataSize);
		GZIPOutputStream gzipos = new GZIPOutputStream(byteArrayOutputStream);
		gzipos.write(data);
		gzipos.close();
		data = byteArrayOutputStream.toByteArray();
		hasZipped = true;
	}
	
	public void unzip() throws IOException {
		data = getData();
		hasZipped = false;
	}
	
	public int getPacketId() {
		return packetId;
	}
	
	public int getOriginalDataSize() {
		return originalDataSize;
	}
	
	public byte[] getData() throws IOException {
		if (!hasZipped) {
			return data;
		} else {
			byte[] unzippedData = new byte[originalDataSize];
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
			GZIPInputStream gzipis = new GZIPInputStream(byteArrayInputStream);
			int readLength, readOffset = 0;
			while ((readLength = gzipis.read(unzippedData, readOffset, originalDataSize - readOffset)) > 0) {
				readOffset += readLength;
			}
			return unzippedData;
		}
	}
	
	public byte[] createSendData() {
		if (!hasZipped) {
			try {
				zip();
				MyByteArrayOutputStream myByteArrayOutputStream = new MyByteArrayOutputStream(data.length + 12);
				DataOutputStream dos = new DataOutputStream(myByteArrayOutputStream);
				dos.writeInt(packetId);
				dos.writeInt(originalDataSize);
				dos.writeInt(data.length);
				dos.write(data);
				dos.close();
				// System.out.println("create send data:" + Integer.toHexString(packetId) + "/" + originalDataSize + "/" + data.length + "/"  + myByteArrayOutputStream.getByteArray().length);
				return myByteArrayOutputStream.getByteArray();
			} catch (Exception ignore) {}
		}
		return null;
	}
	
	public boolean hasZipped() {
		return hasZipped;
	}
	
	public static TCPSocketPacket createHeartBeatPacket() {
		return new TCPSocketPacket(PACKET_ID_HEART, null);
	}
	
	public static TCPSocketPacket createDataPacket(int dataId, byte[] data) {
		if (data == null) {
			data = NULL_DATA;
		}
		MyByteArrayOutputStream mbaos = new MyByteArrayOutputStream(4 + data.length);
		DataOutputStream dos = new DataOutputStream(mbaos);
		try {
			dos.writeInt(dataId);
			dos.write(data);
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new TCPSocketPacket(PACKET_ID_DATA, mbaos.getByteArray());
	}
	
	public static TCPSocketPacket createRequestDataPacket(int dataId) {
		MyByteArrayOutputStream mbaos = new MyByteArrayOutputStream(4);
		DataOutputStream dos = new DataOutputStream(mbaos);
		try {
			dos.writeInt(dataId);
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new TCPSocketPacket(PACKET_ID_REQUEST_DATA, mbaos.getByteArray());
	}
}
