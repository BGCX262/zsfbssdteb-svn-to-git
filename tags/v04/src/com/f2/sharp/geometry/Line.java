package com.f2.sharp.geometry;

import java.awt.Graphics;
import java.io.IOException;

import com.f2.sharp.AbsMenuSharp;
import com.f2.tool.DrawTool;
import com.f2.tool.GeomTool;
import com.f2.tool.PointF;

import com.f2.tool.PaintBrush;
import com.f2.tool.constant.SharpType;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;

/**
 * Ö±Ïß
 * 
 * @author user
 *
 */
public class Line extends AbsMenuSharp {
	
	PointF s = new PointF();
	PointF e = new PointF();
	
	public Line(PaintBrush brush) {
		super(brush);
	}
	
	public void setStartPoint(PointF p)	{
		s = p;
		setBounds(s, e);
	}

	public void setEndPoint(PointF p)	{
		e = p;
		setBounds(s, e);
	}
	
	@Override
	public void draw(Graphics g) {
//		super.paint(g);
		DrawTool.drawLine(g, s, e, getPaintBrush());
	}
	
	@Override
	public boolean isInSharp(PointF p) {
		return GeomTool.isLineContain(s, e, p, getPaintBrush().getStrokeLineWidth());
	}
	 	
	@Override
	public void rotate(PointF basic, double arc) {
		GeomTool.rotateLine(arc, basic, s);
		GeomTool.rotateLine(arc, basic, e);
		setBounds(s, e);
	}
	
	@Override
	public void move(double dx, double dy) {
		GeomTool.movePoint(dx, dy, s);
		GeomTool.movePoint(dx, dy, e);
		setBounds(s, e);
	}
	
	@Override
	public void zoom(PointF basic, double scale) {
		GeomTool.zoomLine(scale, basic, s);
		GeomTool.zoomLine(scale, basic, e);
		setBounds(s, e);
	}		

	@Override
	public void stretch(PointF basic, double sx, double sy) {
		GeomTool.stretchLine(sx, sy, basic, s);
		GeomTool.stretchLine(sx, sy, basic, e);
		setBounds(s, e);
	}

	@Override
	public int getSharpType() {
		return SharpType.LINE;
	}

	@Override
	public void setBytes(byte[] bs) throws IOException {
		ByteReader br = new ByteReader(bs);
		s = br.readPointF();	
		e = br.readPointF();
		setBounds(s, e);
	}

	@Override
	public byte[] getBytes() throws IOException {
		ByteWriter bw = new ByteWriter();
		bw.writePointF(s);
		bw.writePointF(e);
		return bw.toByteArray();
	}
}
