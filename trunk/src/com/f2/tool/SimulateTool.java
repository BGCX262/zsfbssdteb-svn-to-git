package com.f2.tool;

import java.util.LinkedList;
import java.util.List;

import com.f2.sharp.AbsMenuSharp;
import com.f2.sharp.Handwriting;
import com.f2.sharp.geometry.Circle;
import com.f2.sharp.geometry.Ellipse;
import com.f2.sharp.geometry.Line;
import com.f2.sharp.geometry.Polygon;

public class SimulateTool {
	
	private static final int DIFF_MIN_VALUE = 15;

	// �����ֻ��ߵ�����
	// �򵥵���������3����Բ��Բ�İ뾶�������ʰ뾶�����ĵ����������ʡ�
	// ��ȷһ�㣬������������ϣ��������ʡ�
	@SuppressWarnings("unused")
	private static double[] calCurvature(List<PointF> ps) {
		if(ps == null || ps.size() <= 1) {
			return new double[0];
		}
		if(ps.size() == 2) {
			return new double[]{0};
		}
		double[] curv = new double[ps.size() - 2];
		for(int i = 0; i < ps.size() - 2; i++) {
			try {
				PointF p1 = ps.get(i);
				PointF p2 = ps.get(i+1);
				PointF p3 = ps.get(i+2);
				double a = GeomTool.calcDist(p1, p2); //�߳�
				double b = GeomTool.calcDist(p1, p3); //�߳�
				double c = GeomTool.calcDist(p2, p3); //�߳�
				double S = Math.sqrt((a+b+c)*(a+b-c)*(a-b+c)*(-a+b+c))/4; //���
				double R = (a*b*c)/(S*4); //���Բ�뾶
				curv[i] = (1/R); //���ʰ뾶=R, ����=1/R
			} catch (Exception e) {
				e.printStackTrace();
				curv[i] = 0;
			}
		}
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < curv.length; i++) {
			sb.append(curv[i]).append(",");
		}
		//System.out.println("curv="+sb.toString());
		return curv;
	}
	
	/**
	 * �������,�����֪�Ķ�������,ȡƽ�������С��һ�����
	 * ƽ�����:�ȼ����������������ߵľ���֮��,Ȼ����Ե����ó���ƽ��ֵ����Բ�ǽ��ƾ��룩
	 * @param brush
	 * @param ps
	 * @return
	 */
	public static AbsMenuSharp simulate(PaintBrush brush, List<PointF> ps) {
		List<SimulateResult> simuList = new LinkedList<SimulateTool.SimulateResult>();
		long t0 = System.currentTimeMillis();
		simuList.add(simulateCircle(brush, ps));
		long t1 = System.currentTimeMillis();
		simuList.add(simulateLine(brush, ps));
		long t2 = System.currentTimeMillis();
		simuList.add(simulateRetangle(brush, ps));
		long t3 = System.currentTimeMillis();
		simuList.add(simulateTriangle(brush, ps));
		long t4 = System.currentTimeMillis();
		simuList.add(simulateEllipse(brush, ps));
		long t5 = System.currentTimeMillis();
		
		String str = "simulate timecost:";
		str = str + "\tt1="+(t1-t0)+"\tt2="+(t2-t1)+"\tt3="+(t3-t2)
				+"\tt4="+(t4-t3)+"\tt5="+(t5-t4)+"\ttotal="+(t5-t0);
//		System.out.println(str);

		double diff = Double.MAX_VALUE;
		AbsMenuSharp sharp = null;
		for(int i = 0; i < simuList.size(); i++) {
			SimulateResult simu = simuList.get(i);
			//System.out.println("simulate["+i+"]:"+simu);
			if(simu.diff < diff) {
				diff = simu.diff;
				sharp = simu.sharp;
			}
		}

		//System.out.println("simulate result:\tdiff="+diff+"\tsharp="+sharp+"\n");

		if(diff < DIFF_MIN_VALUE && sharp != null) {
			return sharp;
		}
		return new Handwriting(brush, ps);
	}
	
	/**
	 * ���Բ:
	 * 1,�ҵ�Բ��
	 * 2,�ҵ��������е����Բ�ĵ���С�뾶�����뾶
	 * 3,�뾶����С�����,�����������е�,�ҵ������С�İ뾶
	 * @param brush
	 * @param ps
	 * @return
	 */
	private static SimulateResult simulateCircle(PaintBrush brush, List<PointF> ps) {
		if(ps == null || ps.size() <= 2) {
			return new SimulateResult(Double.MAX_VALUE, new Circle(brush));
		}
		
		if(GeomTool.calcDist(ps.get(0), ps.get(ps.size() - 1)) > brush.getStrokeLineWidth() * 2) {
			//���պϵ����߲���Բ�����
			return new SimulateResult(Double.MAX_VALUE, new Circle(brush));
		}
		
		//1,�ҵ�Բ��
		PointF centre = GeomTool.getCentre(ps);
		
		//2,�ҵ��������е����Բ�ĵ���С�뾶�����뾶
		double rmin = Double.MAX_VALUE;
		double rmax = 1;
		for(PointF p : ps) {
			double rc = GeomTool.calcDist(p, centre);
			if(rc < rmin) rmin = rc;
			if(rc > rmax) rmax = rc;
		}
		
		//System.out.println("simulateCircle, rmin=" + rmin + ", rmax=" + rmax + ", centre=" + centre);

		//3,�뾶����С�����,�����������е�,�ҵ������С�İ뾶
		double radius = 1;
		double diff = Double.MAX_VALUE;
		for(int r = (int)rmin; r < (int)rmax + 1; r++) {
			double df = 0;
			for(PointF p : ps) {
				double rc = GeomTool.calcDist(p, centre);
				df += Math.abs(rc - r);
			}
			if(df < diff) {
				diff = df;
				radius = r;
			}
		}
		
		double diff2 = diff / ps.size();
		
		//System.out.println("simulateCircle, diff=" + diff + ", diff2=" + diff2 + ", ps.size()=" + ps.size() + ", radius=" + radius + ", center=" + centre);
		
		Circle circle = new Circle(brush);
		circle.setCentre(centre);
		circle.setRadius(radius);
		return new SimulateResult(diff2, circle);
	}
	
	/**
	 * ���ֱ��:
	 * 1,������ͷβ��Ϊֱ�ߵ�ͷβ
	 * 2,�������
	 * @param brush
	 * @param ps
	 * @return
	 */
	private static SimulateResult simulateLine(PaintBrush brush, List<PointF> ps) {
		if(ps == null || ps.size() <= 1) {
			return new SimulateResult(Double.MAX_VALUE, new Line(brush));
		}
		
		PointF p0 = ps.get(0);
		PointF p1 = ps.get(ps.size() - 1);
		
		double diff = 0;
		for(PointF p : ps) {
			double rd = GeomTool.calcAltitude(p, p0, p1);
			diff += rd;
		}
		double diff2 = diff / ps.size();
		
		//System.out.println("simulateLine, diff=" + diff + ", diff2=" + diff2 + ", ps.size()=" + ps.size() + ", p0=" + p0 + ", p1=" + p1);
		
		return new SimulateResult(diff2, new Line(brush, p0, p1));
	}

	/**
	 * ��Ͼ���:
	 * 1,�ҵ�Բ��
	 * 2,�ҵ��������е����Բ�ĵ���С�뾶�����뾶,��СX��ƫ�ƺ����X��ƫ��,��СY��ƫ�ƺ����Y��ƫ��
	 * 3,�ֱ��ҵ������С��X��ƫ�ƺ�Y��ƫ��
	 * 4,�����ۺ����
	 * @param brush
	 * @param ps
	 * @return
	 */
	private static SimulateResult simulateRetangle(PaintBrush brush, List<PointF> ps) {
		if(ps == null || ps.size() <= 2) {
			return new SimulateResult(Double.MAX_VALUE, new Polygon(brush));
		}
		
		if(GeomTool.calcDist(ps.get(0), ps.get(ps.size() - 1)) > brush.getStrokeLineWidth() * 2) {
			//���պϵ����߲����������
			return new SimulateResult(Double.MAX_VALUE, new Polygon(brush));
		}
		
		//1,�ҵ�Բ��
		PointF centre = GeomTool.getCentre(ps);
		
		//2,�ҵ��������е����Բ�ĵ���С�뾶�����뾶,��СX��ƫ�ƺ����X��ƫ��,��СY��ƫ�ƺ����Y��ƫ��
		double xmin = Double.MAX_VALUE;
		double xmax = 1;
		double ymin = Double.MAX_VALUE;
		double ymax = 1;
		double rmin = Double.MAX_VALUE;
		double rmax = 1;
		for(PointF p : ps) {
			double rc = GeomTool.calcDist(p, centre);
			if(rc < rmin) rmin = rc;
			if(rc > rmax) rmax = rc;
			double dx = Math.abs(p.x - centre.x);
			if(dx < xmin) xmin = dx;
			if(dx > xmax) xmax = dx;
			double dy = Math.abs(p.y - centre.y);
			if(dy < ymin) ymin = dy;
			if(dy > ymax) ymax = dy;
		}
		
		//System.out.println("simulateRetangle, rmin=" + rmin + ", rmax=" + rmax + ", xmin=" + xmin + ", xmax=" + xmax + ", ymin=" + ymin + ", ymax=" + ymax + ", centre=" + centre);

		//3.1,�ҵ������С��X��ƫ��
		double diffY = Double.MAX_VALUE;
		double rectDy = 1;
		for(int dy = (int)ymin; dy < (int)ymax + 1; dy++) {
			int dx = (int)(xmax); //(int)(xmax+xmin)/2;
			double df = 0;
			double rc = Math.sqrt(dx*dx + dy*dy);
			if(rc < rmin || rc > rmax) {
				continue;
			}
			for(PointF p : ps) {
				df += Math.abs(Math.abs(p.y - centre.y) - dy);
			}
			if(df < diffY) {
				diffY = df;
				rectDy = dy;
			}
		}
		
		//3.2,�ҵ������С��Y��ƫ��
		double diffX = Double.MAX_VALUE;
		double rectDx = 1;
		for(int dx = (int)xmin; dx < (int)xmax + 1; dx++) {
			int dy = (int)(ymax); //(int)(ymax+ymin)/2;
			double df = 0;
			double rc = Math.sqrt(dx*dx + dy*dy);
			if(rc < rmin || rc > rmax) {
				continue;
			}
			for(PointF p : ps) {
				df += Math.abs(Math.abs(p.x - centre.x) - dx);
			}
			if(df < diffX) {
				diffX = df;
				rectDx = dx;
			}
		}

		//4,�����ۺ����
		double diff = (Math.sqrt((diffX*diffY)));
		//double diff = ((diffX + diffY) / 2); 
		//double diff = Math.min(diffX, diffY);
		double diff2 = diff / ps.size(); 
		
		//System.out.println("simulateRetangle, diff=" + diff + ", diff2=" + diff2 + ", diffX=" + diffX + ", diffY=" + diffY);
		
		Polygon polygon = new Polygon(brush);
		polygon.addPoint(new PointF(centre.x - rectDx, centre.y - rectDy));
		polygon.addPoint(new PointF(centre.x + rectDx, centre.y - rectDy));
		polygon.addPoint(new PointF(centre.x + rectDx, centre.y + rectDy));
		polygon.addPoint(new PointF(centre.x - rectDx, centre.y + rectDy));
		return new SimulateResult(diff2, polygon);
	}
	
	/**
	 * ���������:
	 * 1,�ҵ�Բ��
	 * 2,�ҵ��������е����Բ�ĵ����뾶(����A)
	 * 3,�ҵ����������A��Զ�ĵڶ�����B
	 * 4,�ҵ�����ֱ��AB��Զ�ĵ�������C
	 * 5,�������
	 * 6,���������̫��,��ֱ�����,������
	 * @param brush
	 * @param ps
	 * @return
	 */
	private static SimulateResult simulateTriangle(PaintBrush brush, List<PointF> ps) {
		if(ps == null || ps.size() <= 2) {
			return new SimulateResult(Double.MAX_VALUE, new Polygon(brush));
		}
		
		if(GeomTool.calcDist(ps.get(0), ps.get(ps.size() - 1)) > brush.getStrokeLineWidth() * 2) {
			//���պϵ����߲������������
			return new SimulateResult(Double.MAX_VALUE, new Polygon(brush));
		}
		
		//1,�ҵ�Բ��
		PointF centre = GeomTool.getCentre(ps);
		
		//2,�ҵ��������е����Բ�ĵ����뾶(����A)
		PointF pa = centre;
		double rmax = 1;
		for(PointF p : ps) {
			double rc = GeomTool.calcDist(p, centre);
			if(rc > rmax) {
				rmax = rc;
				pa = p;
			}
		}
		
		//System.out.println("simulateTriangle, rmax=" + rmax + ", pa=" + pa + ", centre=" + centre);

		//3,�ҵ����������A��Զ�ĵڶ�����B
		PointF pb = pa;
		double pamax = 1;
		for(PointF p : ps) {
			double rc = GeomTool.calcDist(p, pa);
			if(rc > pamax) {
				pamax = rc;
				pb = p;
			}
		}
		
		//System.out.println("simulateTriangle, pamax=" + pamax + ", pb=" + pb + ", centre=" + centre);
		
		//4,�ҵ�����ֱ��AB��Զ�ĵ�������C
		PointF pc = pb;
		double pbmax = 1;
		for(PointF p : ps) {
			double rc = GeomTool.calcAltitude(p, pa, pb);
			if(rc > pbmax) {
				pbmax = rc;
				pc = p;
			}
		}
		
		//System.out.println("simulateTriangle, pbmax=" + pbmax + ", pc=" + pc + ", centre=" + centre);

		//5,�������
		double diff = 0;
		for(PointF p : ps) {
			double d1 = GeomTool.calcAltitude(p, pa, pb);
			double d2 = GeomTool.calcAltitude(p, pb, pc);
			double d3 = GeomTool.calcAltitude(p, pa, pc);
			diff += Math.min(Math.min(d1, d2), d3);
		}
		double diff2 = diff / ps.size(); 
		
		//System.out.println("simulateTriangle, diff=" + diff + ", diff2=" + diff2 + ", pa=" + pa + ", pb=" + pb + ", pc=" + pc);
		
		Polygon polygon = new Polygon(brush);
		polygon.addPoint(pa);
		polygon.addPoint(pb);
		polygon.addPoint(pc);
		return new SimulateResult(diff2, polygon);
	}
	
	/**
	 * �����Բ��:
	 * 1,�ҵ������������е�ľ���
	 * 2,�ҵ��˾��ζ�Ӧ����Բ
	 * 3,�������
	 * @param brush
	 * @param ps
	 * @return
	 */
	private static SimulateResult simulateEllipse(PaintBrush brush, List<PointF> ps) {
		if(ps == null || ps.size() <= 2) {
			return new SimulateResult(Double.MAX_VALUE, new Polygon(brush));
		}
		
		if(GeomTool.calcDist(ps.get(0), ps.get(ps.size() - 1)) > brush.getStrokeLineWidth() * 2) {
			//���պϵ����߲����������
			return new SimulateResult(Double.MAX_VALUE, new Ellipse(brush));
		}
		
		//1,�ҵ������������е�ľ���
		PointF min = new PointF();
		PointF max = new PointF();
		GeomTool.getMinMaxPoint(ps, min, max);
		
		//System.out.println("simulateEllipse, min=" + min + ", max=" + max);

		//2,�ҵ��˾��ζ�Ӧ����Բ
		PointF centre = new PointF((min.x + max.x) / 2, (min.y + max.y) / 2);
		double a = (max.x - min.x) / 2;
		double b = (max.y - min.y) / 2;
		Ellipse ellipse = new Ellipse(brush);
		ellipse.setCentre(centre);
		ellipse.setAB(a, b);
		
		//System.out.println("simulateEllipse, a=" + a + ", b=" + b + ", centre=" + centre);
		
		//3,�������
		double diff = 0;
		for(PointF p : ps) {
			double px = Math.abs(p.x-centre.x);
			double py = Math.abs(p.y-centre.y);
			double x2 = px*px; //=x^2
			double a2 = a*a; //=a^2
			double b2 = b*b; //=b^2
			double y2 = Math.abs(b2 - b2*(x2/a2)); //=y^2
			double rd = Math.abs(py - Math.sqrt(y2));
			diff += rd;
		}
		double diff2 = diff / ps.size(); 
		
		//System.out.println("simulateEllipse, diff=" + diff + ", diff2=" + diff2 + ", ps.size()=" + ps.size());
	
		return new SimulateResult(diff2, ellipse);
	}
	
	private static class SimulateResult {
		private double diff;
		private AbsMenuSharp sharp;
		public SimulateResult(double diff, AbsMenuSharp sharp) {
			this.diff = diff;
			this.sharp = sharp;
		}
		@Override
		public String toString() {
			return "SimulateResult[d=" + diff + ",s=" + sharp + "]";
		}
	}
}
