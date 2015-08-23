package com.f2.frame.data;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.f2.frame.Wander;
import com.f2.sharp.AbsMenuSharp;
import com.f2.sharp.AbsSharpFactory;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;
import com.f2.tool.file.IFbsData;

public class CanvasItem implements IFbsData {
	private Background background;
	private List<AbsMenuSharp> sharps;
	private int zoomValue = Wander.SLIDER_ZOOM_MID;
	
	public CanvasItem() {
		sharps = new LinkedList<AbsMenuSharp>();
		background = new Background();
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

	public int getZoomValue() {
		return zoomValue;
	}

	public void setZoomValue(int zoomValue) {
		this.zoomValue = zoomValue;
	}

	@Override
	public void initFromBytes(byte[] bs) throws IOException {
		ByteReader br = new ByteReader(bs);
		Background bg = new Background();
		bg.initFromBytes(br.readBytes());		
		int size = br.readInt();
		List<AbsMenuSharp> list = new LinkedList<AbsMenuSharp>();
		for(int i = 0; i < size; i++) {
			AbsMenuSharp sharp = AbsSharpFactory.getSharp(br.readInt());
			sharp.initFromBytes(br.readBytes());
			list.add(sharp);
		}
		this.background = bg;
		this.sharps = list;
	}

	@Override
	public byte[] toBytes() throws IOException {
		ByteWriter bw = new ByteWriter();
		bw.writeBytes(background.toBytes());
		bw.writeInt(sharps.size());
		for(AbsMenuSharp sharp : sharps) {
			bw.writeInt(sharp.getSharpType());
			bw.writeBytes(sharp.toBytes());
		}
		return bw.toByteArray();
	}
}
