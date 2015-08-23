package com.f2.tool.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
	
	public static boolean saveObj(String filename, FbsFileObj obj) {
		// TODO
		return true;
	}
	
	public static FbsFileObj loadObj(String filename) {
		// TODO
		return null;
	}

	public static boolean save(String filepath, byte[] bs) {
		//System.out.println("save..." + filepath);
		if(filepath == null || filepath.length() == 0) {
			return false;
		}
		FileOutputStream fos = null;
		try {
			File file = new File(filepath);
			if(file.exists()) {
				file.delete();
			} else {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			fos.write(bs);
			fos.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(fos != null) {
				try {fos.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
	public static byte[] load(String filepath) {
		//System.out.println("load..." + filepath);
		if(filepath == null || filepath.length() == 0) {
			return null;
		}
		FileInputStream fis = null;
		try {
			File file = new File(filepath);
			if(!file.exists()) {
				return null;
			}
			fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bs = new byte[10240];
			int read = -1;
			while((read = fis.read(bs)) > 0) {
				baos.write(bs, 0, read);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(fis != null) {
				try {fis.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
	}

}
