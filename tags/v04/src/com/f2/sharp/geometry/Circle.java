package com.f2.sharp.geometry;

import java.awt.Graphics;
import java.io.IOException;

import com.f2.sharp.AbsMenuSharp;
import com.f2.tool.PaintBrush;
import com.f2.tool.DrawTool;
import com.f2.tool.GeomTool;
import com.f2.tool.PointF;
import com.f2.tool.constant.SharpType;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;

/**
 * �����
 * 
 * @author user
 *
 */
public class Circle extends AbsMenuSharp {

	private PointF centre = new PointF();
	private double radius = 0;
	
	public Circle(PaintBrush brush) {
		super(brush);
	}

	public void setCentre(PointF centre) {
		this.centre = centre;
		updateBounds();
	}

	public PointF getCentre() {
		return centre;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		updateBounds();
	}

	private void updateBounds() {
		int bx = (int)(centre.x - radius);
		int by = (int)(centre.y - radius);
		int bw = (int)(2*radius);
		int bh = (int)(2*radius);
		setBounds(bx, by, bw, bh);
	}

	public double getRadius() {
		return radius;
	}

	@Override
	public void move(double dx, double dy) {
		GeomTool.movePoint(dx, dy, centre);
		updateBounds();
	}
	
	@Override
	public void draw(Graphics g) {
//		super.paint(g);
		DrawTool.drawEllipse(g, centre, 2*radius, 2*radius, getPaintBrush());
	}
	
	@Override
	public boolean isInSharp(PointF p) {
		return GeomTool.isEllipseContain(centre, radius, radius, 0, p, getPaintBrush().getStrokeLineWidth());
	}

	@Override
	public void rotate(PointF basic, double arc) {
		// nothing here		
	}

	@Override
	public void zoom(PointF basic, double scale) {
		radius = radius * scale;
		updateBounds();
	}

	@Override
	public void stretch(PointF basic, double sx, double sy) {
		radius = radius * Math.max(sx, sy);
		updateBounds();
	}

	@Override
	public int getSharpType() {
		return SharpType.CIRCLE;
	}

	@Override
	public void setBytes(byte[] bs) throws IOException {
		ByteReader br = new ByteReader(bs);
		centre = br.readPointF();
		setRadius(br.readDouble());
	}

	@Override
	public byte[] getBytes() throws IOException {
		ByteWriter bw = new ByteWriter();
		bw.writePointF(centre);
		bw.writeDouble(radius);
		return bw.toByteArray();
	}
}
