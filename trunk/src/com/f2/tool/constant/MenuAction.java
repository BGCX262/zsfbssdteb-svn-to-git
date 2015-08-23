package com.f2.tool.constant;

import com.f2.tool.GeomTool;
import com.f2.tool.PointF;

public class MenuAction {
	public static final int NONE = 0; //��
	public static final int ROTATE = 1; //��ת
	public static final int MOVE = 2; //ƽ��
	public static final int STRETCH  = 3; //����
	public static final int STRETCH_X = 4; //����X����
	public static final int STRETCH_Y = 5; //����Y����
	public static final int MENU = 100; //��ʾ����˵�
	public static final int MENU_PROPERTY = 101; //����(���������ε����߳��Ⱥ������Ǵ�С,Բ��Բ�ĺͰ뾶)
	public static final int MENU_MOVE_UP = 102; //����
	public static final int MENU_MOVE_DOWN = 103; //����
	public static final int MENU_MOVE_UP_TO_FIRST = 104; //�ö�
	public static final int MENU_MOVE_DOWN_TO_LAST = 105; //�õ�
	public static final int MENU_BRUSH_COLOR = 106; //��ɫ
	public static final int MENU_BRUSH_WIDTH = 107; //�ʿ�
	public static final int MENU_COPY = 108; //����
	public static final int MENU_CLONE = 109; //��¡
	public static final int MENU_CUT = 110; //����
	public static final int MENU_DELETE = 111; //ɾ��
	public static final int MENU_EDIT = 112; //���Ա༭
	
	protected PointF actionStartPoint;
	protected PointF actionEndPoint;

	public void setActionStartPoint(PointF actionStartPoint) {
		this.actionStartPoint = actionStartPoint;
	}

	public PointF getActionStartPoint() {
		return actionStartPoint;
	}

	public void setActionEndPoint(PointF actionEndPoint) {
		this.actionEndPoint = actionEndPoint;
	}

	public PointF getActionEndPoint() {
		return actionEndPoint;
	}

	public double getRotateArc(PointF basic) {
		PointF ps = getActionStartPoint();
		PointF pe = getActionEndPoint();
		double as = GeomTool.calcArc(basic, ps);
		double ae = GeomTool.calcArc(basic, pe);
		return - (ae - as);
	}
	
	public double getMoveDx() {
		PointF ps = getActionStartPoint();
		PointF pe = getActionEndPoint();
		return pe.x - ps.x;
	}

	public double getMoveDy() {
		PointF ps = getActionStartPoint();
		PointF pe = getActionEndPoint();
		return pe.y - ps.y;
	}

	public double getStretchScale(PointF basic) {
		PointF ps = getActionStartPoint();
		PointF pe = getActionEndPoint();
		double ds = GeomTool.calcDist(basic, ps);
		double de = GeomTool.calcDist(basic, pe);
		if(ds == 0) {
			return 10; //Integer.MAX_VALUE;
		}
		return de / ds;
	}
	
	public double getStretchXScale(PointF basic) {
		PointF ps = getActionStartPoint();
		PointF pe = getActionEndPoint();
		double dsx = Math.abs(basic.x - ps.x);
		double dex = Math.abs(basic.x - pe.x);
		if(dsx == 0) {
			return 10; //Integer.MAX_VALUE;
		}
		return dex / dsx;
	}

	public double getStretchYScale(PointF basic) {
		PointF ps = getActionStartPoint();
		PointF pe = getActionEndPoint();
		double dsy = Math.abs(basic.y - ps.y);
		double dey = Math.abs(basic.y - pe.y);
		if(dsy == 0) {
			return 10; //Integer.MAX_VALUE;
		}
		return dey / dsy;
	}

}
