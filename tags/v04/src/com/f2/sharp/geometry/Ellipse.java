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
 * ¶à±ßÐÎ
 * 
 * @author user
 *
 */
public class Ellipse extends AbsMenuSharp {

	private PointF centre = new PointF();
	private double a = 0; //xÖá°ë¾¶
	private double b = 0; //yÖá°ë¾¶
	private double f = 0; //³¤ÖáÉÏµÄ½¹¾à
	
	public Ellipse(PaintBrush brush) {
		super(brush);
	}

	public void setCentre(PointF centre) {
		this.centre = centre;
		updateBounds();
	}

	public PointF getCentre() {
		return centre;
	}

	public void setAB(double a, double b) {
		this.a = a;
		this.b = b;
		f = Math.sqrt(Math.abs(a*a - b*b));
		updateBounds();
	}

	private void updateBounds() {
		int bx = (int)(centre.x - a);
		int by = (int)(centre.y - b);
		int bw = (int)(2*a);
		int bh = (int)(2*b);
		setBounds(bx, by, bw, bh);
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}
	
	@Override
	public void move(double dx, double dy) {
		GeomTool.movePoint(dx, dy, centre);
		updateBounds();
	}
		
	@Override
	public void draw(Graphics g) {
//		super.paint(g);
		DrawTool.drawEllipse(g, centre, 2*a, 2*b, getPaintBrush());
	}

	@Override
	public boolean isInSharp(PointF p) {
		return GeomTool.isEllipseContain(centre, a, b, f, p, getPaintBrush().getStrokeLineWidth());
	}

	@Override
	public void rotate(PointF basic, double arc) {
		// TODO	
	}

	@Override
	public void zoom(PointF basic, double scale) {
		setAB(scale > 0 ? a * scale : a, scale > 0 ? b * scale : b);
	}

	@Override
	public void stretch(PointF basic, double sx, double sy) {
		setAB(sx > 0 ? a * sx : a, sy > 0 ? b * sy : b);
	}

	@Override
	public int getSharpType() {
		return SharpType.ELLIPSE;
	}

	@Override
	public void setBytes(byte[] bs) throws IOException {
		ByteReader br = new ByteReader(bs);
		centre = br.readPointF();
		setAB(br.readDouble(), br.readDouble());
	}

	@Override
	public byte[] getBytes() throws IOException {
		ByteWriter bw = new ByteWriter();
		bw.writePointF(centre);
		bw.writeDouble(a);
		bw.writeDouble(b);
		return bw.toByteArray();
	}
}
