package com.f2.socket;


import java.io.ByteArrayOutputStream;

public class MyByteArrayOutputStream extends ByteArrayOutputStream {
	
	public MyByteArrayOutputStream() {}
	
	public MyByteArrayOutputStream(int size) {
		super(size);
	}
	
	public byte[] getByteArray() {
		return buf;
	}
	
}

