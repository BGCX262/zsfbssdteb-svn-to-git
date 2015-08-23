package com.f2.sharp.geometry;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.f2.sharp.AbsMenuSharp;
import com.f2.tool.PaintBrush;
import com.f2.tool.DrawTool;
import com.f2.tool.GeomTool;
import com.f2.tool.PointF;
import com.f2.tool.constant.SharpType;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;

/**
 * 各点相连的闭合多边形
 * 
 * @author user
 *
 */
public class Polygon extends AbsMenuSharp {

	List<PointF> ps = new LinkedList<PointF>();
	
	public Polygon(PaintBrush brush) {
		super(brush);
		mPaintBrush.setStrokeEndCap(BasicStroke.CAP_SQUARE);
		mPaintBrush.setStrokeLineJoin(BasicStroke.JOIN_MITER);
	}
	
	public Polygon(PaintBrush brush, int count, PointF p) {
		super(brush);
		for(int i = 0; i < count; i++) {
			addPoint(p);
		}			
	}
	
	public PointF getPoint(int index) {
		if(index < 0 || index >= ps.size()) {
			return null;
		}
		return ps.get(index);
	}
	
	public void addPoint(PointF p) {
		ps.add(p);
		updateBounds(p);
	}

	public void setPoint(int index, PointF p) {
		if(index < 0 || index >= ps.size()) {
			return;
		}
		ps.set(index, p);
		updateBounds(p);
	}

	@Override
	public void draw(Graphics g) {
//		super.paint(g);
		for(int i = 0; i < ps.size() - 1; i++) {
			PointF s = ps.get(i);
			PointF e = ps.get(i + 1);
			DrawTool.drawLine(g, s, e, getPaintBrush());
		}		
		PointF s = ps.get(0);
		PointF e = ps.get(ps.size() - 1);
		DrawTool.drawLine(g, s, e, getPaintBrush());
	}
		
	@Override
	public boolean isInSharp(PointF p) {
		for(int i = 0; i < ps.size() - 1; i++) {
			PointF s = ps.get(i);
			PointF e = ps.get(i + 1);
			if(GeomTool.isLineContain(s, e, p, getPaintBrush().getStrokeLineWidth())) {
				return true;
			}
		}		
		PointF s = ps.get(0);
		PointF e = ps.get(ps.size() - 1);
		if(GeomTool.isLineContain(s, e, p, getPaintBrush().getStrokeLineWidth())) {
			return true;
		}
		return false;
	}
	 	
	@Override
	public void rotate(PointF basic, double arc) {
		for(int i = 0; i < ps.size(); i++) {
			PointF e = ps.get(i);
			GeomTool.rotateLine(arc, basic, e);
		}
		resetBounds(ps);
	}
	
	@Override
	public void move(double dx, double dy) {
		for(int i = 0; i < ps.size(); i++) {
			PointF s = ps.get(i);
			GeomTool.movePoint(dx, dy, s);
		}
		resetBounds(ps);
	}
	
	@Override
	public void zoom(PointF basic, double scale) {
		for(int i = 0; i < ps.size(); i++) {
			PointF e = ps.get(i);
			GeomTool.zoomLine(scale, basic, e);
		}
		resetBounds(ps);
	}

	@Override
	public void stretch(PointF basic, double sx, double sy) {
		for(int i = 0; i < ps.size(); i++) {
			PointF e = ps.get(i);
			GeomTool.stretchLine(sx, sy, basic, e);
		}
		resetBounds(ps);
	}

	@Override
	public int getSharpType() {
		return SharpType.POLYGON;
	}

	@Override
	public void setBytes(byte[] bs) throws IOException {
		ByteReader br = new ByteReader(bs);
		ps.clear();
		int size = br.readInt();
		for(int i = 0; i < size; i++) {
			PointF p = br.readPointF();	
			ps.add(p);
		}
		resetBounds(ps);
	}

	@Override
	public byte[] getBytes() throws IOException {
		ByteWriter bw = new ByteWriter();
		bw.writeInt(ps.size());
		for(int i = 0; i < ps.size(); i++) {
			bw.writePointF(ps.get(i));
		}
		return bw.toByteArray();
	}
}
