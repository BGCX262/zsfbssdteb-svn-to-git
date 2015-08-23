package com.f2.tool;

import java.awt.Point;

public class PointF {
	public double x;
	public double y;
	public PointF() {
		this.x = 0.0;
		this.y = 0.0;
	}
	public PointF(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public PointF(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	public PointF(PointF p) {
		this.x = p.x;
		this.y = p.y;
	}
	public String toString() {
		return "P(" + x + "," + y + ")";
	}
	public Point toPoint() {
		return new Point((int)x, (int)y);
	}
	public boolean equals(PointF o) {
		if((int)x == (int)o.x && (int)y == (int)o.y) {
			return true;
		}
		return false;
	}
}
