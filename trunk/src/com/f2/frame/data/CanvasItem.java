package com.f2.frame.data;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import com.f2.sharp.AbsMenuSharp;
import com.f2.sharp.AbsSharpFactory;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;
import com.f2.tool.file.IFbsData;

public class CanvasItem implements IFbsData, Cloneable {
	private Background background;
	private List<AbsMenuSharp> sharps;
	
	public CanvasItem() {
		sharps = new LinkedList<AbsMenuSharp>();
		background = new Background();
	}
	
	public Object clone() {
		CanvasItem o = null;
		try {
			o = (CanvasItem) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		o.sharps = new LinkedList<AbsMenuSharp>();
		for(AbsMenuSharp sharp : sharps) {
			o.sharps.add((AbsMenuSharp) sharp.clone());
		}
		o.background = (Background) background.clone();
		return o; 
	}

	public void setSharps(List<AbsMenuSharp> sharps) {
		this.sharps = sharps;
	}
	
	public List<AbsMenuSharp> getSharps() {
		return sharps;
	}

	public void setBackground(Background background) {
		this.background = background;
	}

	public Background getBackground() {
		return background;
	}

	@Override
	public void initFromBytes(byte[] bs) {
		try {
			ByteReader br = new ByteReader(bs);
			background = new Background();
			background.initFromBytes(br.readBytes());		
			sharps = new LinkedList<AbsMenuSharp>();
			int size = br.readInt();
			for(int i = 0; i < size; i++) {
				AbsMenuSharp sharp = AbsSharpFactory.getSharp(br.readInt());
				sharp.initFromBytes(br.readBytes());
				sharps.add(sharp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] toBytes() {
		try {
			ByteWriter bw = new ByteWriter();
			bw.writeBytes(background.toBytes());
			bw.writeInt(sharps.size());
			for(AbsMenuSharp sharp : sharps) {
				bw.writeInt(sharp.getSharpType());
				bw.writeBytes(sharp.toBytes());
			}
			return bw.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Color getBackgroundColor() {
		return background.getColor();
	}

	public void setBackgroundColor(Color color) {
		background.setColor(color);
	}

	public void setBackgroundType(int type, boolean mask) {
		background.setBgType(type, mask);
	}

	public void setBackgroundPictureImageFilePath(String filepath) {
		background.setPictureImageFilePath(filepath);
	}

	public void setBackgroundMarbleImageFilePath(String filepath) {
		background.setMarbleImageFilePath(filepath);
	}
}
