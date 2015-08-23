package com.f2.sharp.geometry;

import java.awt.Color;
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
 * 直线
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
	
	public Line(PaintBrush brush, PointF s, PointF e) {
		super(brush);
		this.s = s;
		this.e = e;
		setBounds(s, e);
	}
	
	public void setStartPoint(PointF p)	{
		s = p;
		setBounds(s, e);
	}

	public void setEndPoint(PointF p)	{
		e = p;
		setBounds(s, e);
	}
	
	public PointF getStartPoint() {
		return s;
	}

	public PointF getEndPoint()	{
		return e;
	}
	
	@Override
	public void draw(Graphics g) {
//		super.paint(g);
		DrawTool.drawLine(g, s, e, getPaintBrush());
	}
	
	@Override
	protected void drawProperty(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(18.0f));
		String str1 = "P1(" + Math.round(s.x) + "," + Math.round(s.y) + ")";
		g.drawString(str1, (int)(s.x+10), (int)(s.y + 20));
		String str2 = "P2(" + Math.round(e.x) + "," + Math.round(e.y) + ")";
		g.drawString(str2, (int)(e.x-100), (int)(e.y - 5));
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
	
	@Override
	public Object clone() {
		Line o = (Line) super.clone();
		o.s = new PointF(s);
		o.e = new PointF(e);
		return o;
	}

	@Override
	public String toString() {
		return "Line[" + s + "," + e + "]";
	}
	
	/**
	 * 响应擦除操作
	 * @param point
	 * @return 擦除结果
	 * 整个清除的则直接返回空的list; 
	 * 若擦除后无变化,则返回自身
	 * 若擦除后分裂成多个,则返回分裂结果
	 */
//	@Override
//	public List<AbsSharp> erase(PointF point, int lineWidth) {
//		System.out.println("Line.erase, point = " + point);
//		List<AbsSharp> list = new LinkedList<AbsSharp>();
//		if(point.equals(s) && point.equals(e)) {
//			return list;
//		}
//
//		if(point.equals(s) || point.equals(e)) {
//			list.add(this);
//			return list;
//		} 
//
//		if(GeomTool.isLineContain(s, e, point, lineWidth)) {
//			PointF p;
//			double scale;
//			scale = 0.9; // TODO
//			p = fissionLine(s, e, point); // 分裂
//			GeomTool.zoomLine(scale, s, p);
//			list.add(new Line(mPaintBrush, s, p));			
//			p = fissionLine(s, e, point); // 分裂
//			scale = 0.9; // TODO
//			GeomTool.zoomLine(scale, e, p);
//			list.add(new Line(mPaintBrush, e, p));			
//			return list;
//		}
//
//		return null;
//	}
//
//	/**
//	 * 分裂直线
//	 * @param s
//	 * @param e
//	 * @param point
//	 * @return 分裂点
//	 */
//	public static PointF fissionLine(PointF s, PointF e, PointF p) {
//		// TODO
//		PointF p2 = new PointF(p);
//		if(Math.abs(e.x - s.x) > 0.001) {
//			p2.y = s.y + (e.y - s.y) * (p2.x - s.x) / (e.x - s.x);
//		} else {
//			p2.x = e.x;
//		}
//		return p2;
//	}

}
