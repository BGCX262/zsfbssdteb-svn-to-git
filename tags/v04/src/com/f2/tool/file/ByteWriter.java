package com.f2.tool.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.f2.tool.PointF;

public class ByteWriter {
	
	ByteArrayOutputStream baos = null;
	
	public ByteWriter() {
		baos = new ByteArrayOutputStream();
	}
	
	public void write(byte b) {
		baos.write(b);
	}

	public void writeInt(int i) {
		int b1 = (i >>> 24) & 0xff;
		int b2 = (i >>> 16) & 0xff;
		int b3 = (i >>> 8) & 0xff;
		int b4 = (i >>> 0) & 0xff;
		baos.write(b1);
		baos.write(b2);
		baos.write(b3);
		baos.write(b4);
	}

	public void writeLong(long i) {
		int b1 = (byte) ((i >>> 56) & 0xff);
		int b2 = (byte) ((i >>> 48) & 0xff);
		int b3 = (byte) ((i >>> 40) & 0xff);
		int b4 = (byte) ((i >>> 32) & 0xff);
		int b5 = (byte) ((i >>> 24) & 0xff);
		int b6 = (byte) ((i >>> 16) & 0xff);
		int b7 = (byte) ((i >>> 8) & 0xff);
		int b8 = (byte) ((i >>> 0) & 0xff);
		baos.write(b1);
		baos.write(b2);
		baos.write(b3);
		baos.write(b4);
		baos.write(b5);
		baos.write(b6);
		baos.write(b7);
		baos.write(b8);
	}

	public void writeChar(char c) {
		int b1 = (c >>> 8) & 0xff;
		int b2 = (c >>> 0) & 0xff;
		baos.write(b1);
		baos.write(b2);
	}

	public void writeUTF(String s) throws IOException {
		if(s == null) {
			return;
		}
		byte[] bs = s.getBytes("UTF-8");
		writeBytes(bs);
	}

	public void writeBytes(byte[] bs) throws IOException {
		if(bs == null) {
			return;
		}
		int len = bs.length;
		writeInt(len);
		baos.write(bs);
	}

	public void writeDouble(double d) {
		// 精度算百万万分之一0.000001
		long a = (long) (d * 1000000);
		writeLong(a);
	}
	
	public void writeBoolean(boolean b) {
		write((byte)(b ? 1 : 0));
	}
	
	public byte[] toByteArray() {
		return baos.toByteArray();
	}

	public void writePointF(PointF p) {
		if(p == null) {
			return;
		}
		writeInt((int)p.x);
		writeInt((int)p.y);
	}
}
