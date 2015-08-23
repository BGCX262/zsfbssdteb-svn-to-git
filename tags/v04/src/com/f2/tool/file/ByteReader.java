package com.f2.tool.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.f2.tool.PointF;

public class ByteReader {
	
	ByteArrayInputStream bais = null;
	
	public ByteReader(byte[] bs) {
		bais = new ByteArrayInputStream(bs);
	}
	
	public int read() {
		return bais.read();
	}

	public int readInt() {
		int b1 = bais.read();
		int b2 = bais.read();
		int b3 = bais.read();
		int b4 = bais.read();
		return (b1 << 24) + (b2 << 16) + (b3 << 8) + (b4 << 0);
	}

	public long readLong() {
		int b1 = bais.read();
		int b2 = bais.read();
		int b3 = bais.read();
		int b4 = bais.read();
		int b5 = bais.read();
		int b6 = bais.read();
		int b7 = bais.read();
		int b8 = bais.read();
		return (b1 << 56) + (b2 << 48) + (b3 << 40) + (b4 << 32) + (b5 << 24) + (b6 << 16) + (b7 << 8) + (b8 << 0);
	}

	public int readChar() {
		int b1 = bais.read();
		int b2 = bais.read();
		return (b1 << 8) + (b2 << 0);
	}

	public String readUTF() throws IOException {
		byte[] bs = readBytes();
		if(bs != null) {
			return new String(bs, "UTF-8");
		}
		return null;
	}

	public byte[] readBytes() throws IOException {
		int len = readInt();
		if(len >= 0) {
			byte[] bs = new byte[len];
			bais.read(bs);
			return bs;
		}
		return null;
	}
	
	public boolean readBoolean() {
		return bais.read() == 1;
	}

	public double readDouble() {
		// 精度算百万万分之一0.000001
		long a = readLong();
		return ((double) a) / 1000000;
	}

	public PointF readPointF() {
		return new PointF((int)readInt(), (int)readInt());
	}
	
}
