package com.f2.tool;

import java.awt.Rectangle;
import java.util.List;

public class GeomTool {
	
	public static void rotateLine(double arc, PointF basic, PointF target) {
		if(basic.x == target.x && basic.y == target.y) {
			return;
		}		
		double d = calcDist(basic, target);
		double a = calcArc(basic, target);
		double c = a - arc;
		target.x = (basic.x + d*Math.cos(c));
		target.y = (basic.y - d*Math.sin(c));
	}	

	public static double calcArc(PointF p1, PointF p2) {
		double x1 = p1.x;
		double y1 = p1.y;
		double x2 = p2.x;
		double y2 = p2.y;
		double a0 = Math.PI/2;
		if(x1 == x2 && y1 - y2 < 0) {
			a0 = -Math.PI/2;
		}
		else if(x1 != x2) {
			double tga = ((y1 - y2) * 1.0) / (x2 - x1);
			a0 = Math.atan(tga);
			if(x1 - x2 > 0) {
				a0 += Math.PI;
			}
		} 
		return a0;
	}
	
	public static double calcDist(PointF p1, PointF p2) {
		return Math.sqrt((p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y));
	}
	
	public static void movePoint(double dx, double dy, PointF p) {
		p.x += dx;
		p.y += dy;
	}

	public static void zoomLine(double scale, PointF basic, PointF target) {
		stretchLine(scale, scale, basic, target);	
	}
	
	public static void stretchLine(double sx, double sy, PointF basic, PointF target) {
		if(sx > 0) {
			double dx = (target.x - basic.x);
			target.x = basic.x + (dx * sx);
		}
		if(sy > 0) {
			double dy = (target.y - basic.y);
			target.y = basic.y + (dy * sy);
		}
	}
	
	public static boolean isInBounds(Rectangle bounds, PointF p) {
		if(p.x >= bounds.x && p.x <= bounds.x + bounds.width
				&& p.y >= bounds.y && p.y <= bounds.y + bounds.height) {
			return true;
		}		
		return false;
	}

	/**
	 * 是否在直线区域内(直线有一定宽度)
	 * @param startPoint
	 * @param endPoint
	 * @param point
	 * @param lineWidth
	 * @return
	 */
	public static boolean isLineContain(PointF startPoint, PointF endPoint, PointF point, int lineWidth) {
		double d1 = GeomTool.calcDist(startPoint, point);
		double d2 = GeomTool.calcDist(startPoint, endPoint);
		double d3 = GeomTool.calcDist(point, endPoint);
		
		if(d1 < lineWidth/2 || d3 < lineWidth/2) {
			return true;
		}
		
		if(d1 + d3 == d2) {
			return true;
		}
		
		double cosa = (d1*d1 + d2*d2 - d3*d3) / (2*d1*d2);
		if(cosa <= 0) {
			return false;
		}

		double cosb = (d3*d3 + d2*d2 - d1*d1) / (2*d3*d2);
		if(cosb <= 0) {
			return false;
		}

		double sina = Math.sqrt((1-cosa*cosa));
		if(d1 * sina < lineWidth/2)
		{
			return true;
		}
		
		return false;
	}
	

	public static boolean isEllipseContain(PointF centre, double a, double b, double f, PointF p, int lineWidth) {
		double dx, dy;
		int w = lineWidth/2 + 1;
		if(a == b) {
			//圆
			dx = (p.x - centre.x);
			dy = (p.y - centre.y);
			double d = Math.sqrt(dx*dx + dy*dy);
			if(d >= a - w && d <= a + w) {
				return true;
			}
		}
		else if(a > b) {
			//焦点在X轴
			dx = (p.x - (centre.x-f));
			dy = (p.y - centre.y);
			double d1 = Math.sqrt(dx*dx + dy*dy);
			dx = (p.x - (centre.x+f));
			dy = (p.y - centre.y);
			double d2 = Math.sqrt(dx*dx + dy*dy);
			double d = (d1 + d2)/2;
			if(d >= a - w && d <= a + w) {
				return true;
			}
		}
		else if(a < b) {
			//焦点在Y轴
			dx = (p.x - centre.x);
			dy = (p.y - (centre.y-f));
			double d1 = Math.sqrt(dx*dx + dy*dy);
			dx = (p.x - centre.x);
			dy = (p.y - (centre.y+f));
			double d2 = Math.sqrt(dx*dx + dy*dy);
			double d = (d1 + d2)/2;
			if(d >= b - w && d <= b + w) {
				return true;
			}
		}
		return false;
	}

	public static void getMinPoint(PointF min, PointF p) {
		if(min.x > p.x) min.x = p.x;
		if(min.y > p.y) min.y = p.y;
	}

	public static void getMaxPoint(PointF max, PointF p) {
		if(max.x < p.x) max.x = p.x;
		if(max.y < p.y) max.y = p.y;
	}
	
	public static void getMinMaxPoint(PointF s, PointF m, PointF e, PointF min, PointF max) {
		min.x = min.y = Integer.MAX_VALUE;
		max.x = max.y = Integer.MIN_VALUE;
		getMinPoint(min, s);
		getMaxPoint(max, s);
		getMinPoint(min, m);
		getMaxPoint(max, m);		
		getMinPoint(min, e);
		getMaxPoint(max, e);		
	}
	
	public static void getMinMaxPoint(PointF s, PointF e, PointF min, PointF max) {
		min.x = min.y = Integer.MAX_VALUE;
		max.x = max.y = Integer.MIN_VALUE;
		getMinPoint(min, s);
		getMaxPoint(max, s);
		getMinPoint(min, e);
		getMaxPoint(max, e);		
	}
	
	public static void getMinMaxPoint(List<PointF> ps, PointF min, PointF max) {
		min.x = min.y = Integer.MAX_VALUE;
		max.x = max.y = Integer.MIN_VALUE;
		for(int i = 0; i < ps.size(); i++) {
			PointF p = ps.get(i);
			getMinPoint(min, p);
			getMaxPoint(max, p);
		}
	}
}
