package com.f2.tool;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class DrawTool {

	public static void drawEllipse(Graphics g, PointF centre, double width, double height, PaintBrush brush) {
		float x = (float)(centre.x - width/2);
		float y = (float)(centre.y - height/2);
		float w = (float)(width);
		float h = (float)(height);
		Ellipse2D ellipse2d = new Ellipse2D.Float(x, y, w, h);
		drawSharp2D(g, ellipse2d, brush);
	}
	
	public static void drawLine(Graphics g, PointF p1, PointF p2, PaintBrush brush) {
		float x1 = (float)p1.x;
		float y1 = (float)p1.y;
		float x2 = (float)p2.x;
		float y2 = (float)p2.y;
		Line2D line2d = new Line2D.Float(x1, y1, x2, y2);
		drawSharp2D(g, line2d, brush);
	}
	
	public static void drawHandwriting(Graphics g, List<PointF> ps, PaintBrush brush) {
		if(ps.size() == 0) {
			return;
		}
		PointF s = ps.get(0);
		if(ps.size() == 1) {
			Line2D line2d = new Line2D.Float((float)s.x, (float)s.y, (float)s.x, (float)s.y);
			drawSharp2D(g, line2d, brush);
			return;
		}
		Path2D path2d = new Path2D.Float();
		path2d.moveTo(s.x, s.y);
		for(int i = 1; i < ps.size(); i++) {
			PointF e = ps.get(i);
			path2d.lineTo(e.x, e.y);
		}
		drawSharp2D(g, path2d, brush);
	}
	
	public static void drawSharp2D(Graphics g, java.awt.Shape sharp2d, PaintBrush brush) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(brush.getColor());
		g2d.setStroke(brush.getStroke());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.draw(sharp2d);
	}
		
	public static void drawPicture(Graphics g, Rectangle bounds, BufferedImage image, PaintBrush brush) {
		if(image == null) {
			return;
		}
		
		int x1 = (int) bounds.x;
		int y1 = (int) bounds.y;
		int x2 = (int) bounds.x + bounds.width;
		int y2 = (int) bounds.y + bounds.height;
		
		int w = Math.abs(x2 - x1);
		int h = Math.abs(y2 - y1);
		if(w <= 0 || h <= 0) {
			return;
		}
		g.drawImage(image, x1, y1, w, h, null);
	}


}
