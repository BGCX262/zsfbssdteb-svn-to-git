package com.f2.sharp.geometry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
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
		mPaintBrush.setStrokeEndCap(BasicStroke.JOIN_ROUND);
		mPaintBrush.setStrokeLineJoin(BasicStroke.JOIN_ROUND);
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
	protected void drawProperty(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(18.0f));
		for(int i = 0; i < ps.size(); i++) {
			PointF p = ps.get(i);
			double a = getAngle(i);
			String str = "P" + (i+1) + "(" + Math.round(p.x) + "," + Math.round(p.y) + "),∠" + Math.round(a) + "°";
			FontRenderContext frc = new FontRenderContext(null, true, true);
			double w = g.getFont().getStringBounds(str, frc).getWidth();
			g.drawString(str, (int)(p.x-w/2), (int)(p.y + 25));
		}		
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

	@Override
	public Object clone() {
		Polygon o = (Polygon) super.clone();
		o.ps = new LinkedList<PointF>();
		for(PointF p : ps) {
			o.ps.add(new PointF(p));
		}
		return o;
	}

	@Override
	public String toString() {
		return "Polygon[size=" + ps.size() + ",ps=" + ps + "]";
	}

	public void setPoints(List<PointF> ps) {
		if(ps != null) {
			this.ps = ps;
			resetBounds(ps);
		}
	}

	public List<PointF> getPoints() {
		return ps;
	}

	public boolean setAngles(List<Integer> as) {
		if(as == null || as.size() != ps.size()) {
			return false;
		}
		
		int total = 0;
		for(int a : as) {
			if(a <= 0) {
				return false;
			}
			total += a;
		}
		if(total != 180 * (ps.size()-2)) {
			return false;
		}
		
		// XXX 暂时只实现三角形的以角度确定边
		if(as.size() == 3) {
			// 三个角+一条边(P1,P2)=>另两条边(P3)
			double a1 = as.get(0);
			//double a2 = as.get(1);
			double a3 = as.get(2); // a3 = 180 - a1 - a2;
			//System.out.println("1 a1=" + a1 + ",a2=" + a2 + ",a3=" + a3);
			PointF p1 = ps.get(0);
			PointF p2 = ps.get(1);
			PointF p3 = new PointF(p2);
			//System.out.println("2 p1=" + p1 + ",p2=" + p2 + ",p3=" + p3);
			double d12 = GeomTool.calcDist(p1, p2); // P1,P2的线段长度
			if((int)d12 != 0) {				
				double h = d12 * Math.sin(Math.PI*(a1)/180); // P1,P2的高
//				double d13 = d12;
//				if(a1 == 90) {
//					// 直角
//					d13 = d12;
//				} else if(a1 < 90) {
//					// 锐角
//					d13 = d12 * Math.cos(Math.PI*(a1)/180) + h * Math.tan(Math.PI*(a2 - (90 - a1))/180);
//				} else {
//					// 钝角
//					d13 = d12 * Math.cos(Math.PI*(a1)/180) + h * Math.tan(Math.PI*(a2 - (90 - a1))/180);
//				}
				double d13 = d12 * Math.cos(Math.PI*(a1)/180) + h * Math.tan(Math.PI*(90 - a3)/180); // P1,P3的线段长度
				//System.out.println("3 d12=" + d12 + ",d13=" + d13);
				GeomTool.rotateLine(-Math.PI*(a1)/180, p1, p3); // 以P1为基点,旋转a1角度
				//System.out.println("4 p1=" + p1 + ",p2=" + p2 + ",p3=" + p3);
				GeomTool.stretchLine(d13/d12, d13/d12, p1, p3); // 以P1为基点,拉伸达到目的点P3
				//System.out.println("5 p1=" + p1 + ",p2=" + p2 + ",p3=" + p3);
			}
			ps.set(2, p3);
			resetBounds(ps);
		}
		
		return true;
	}
	
	public List<Integer> getAngles() {
		List<Integer> as = new LinkedList<Integer>();
		for(int i = 0; i < ps.size(); i++) {
			double a = getAngle(i);
			as.add((int)Math.round(a));
		}	
		return as;
	}
	
	public double getAngle(int i) {
		PointF p = ps.get(i);
		PointF p1 = ps.get((i + 1) % ps.size());
		PointF p2 = ps.get((i - 1 + ps.size()) % ps.size());
		double a = GeomTool.calcArc(p, p1, p2) / Math.PI * 180;
		return a <= 180.0 ? a : 360 - a;
	}
}
