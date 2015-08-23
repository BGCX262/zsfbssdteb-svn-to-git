package com.f2.tool;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class DrawTool {

	public static void drawEllipse(Graphics g, PointF centre, double width, double height, PaintBrush brush) {
		Ellipse2D ellipse2d = new Ellipse2D.Double(centre.x - width/2, centre.y - height/2, width, height);
		drawSharp2D(g, ellipse2d, brush);
	}
	
	public static void drawLine(Graphics g, PointF p1, PointF p2, PaintBrush brush) {
		Line2D line2d = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
		drawSharp2D(g, line2d, brush);
	}
	
	public static void drawSharp2D(Graphics g, java.awt.Shape sharp2d, PaintBrush brush) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(brush.getColor());
		g2d.setStroke(brush.getStroke());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.draw(sharp2d);
	}
		
	public static void drawPicture(Graphics g, Rectangle bounds, Image image, PaintBrush brush) {
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
