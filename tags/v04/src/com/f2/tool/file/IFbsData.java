package com.f2.tool.file;

import java.io.IOException;

public interface IFbsData {
	public byte[] toBytes() throws IOException;
	public void initFromBytes(byte[] bs) throws IOException;
}
