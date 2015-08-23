package com.f2.tool.constant;

import com.f2.tool.GeomTool;
import com.f2.tool.PointF;

public class MenuAction {
	public static final int NONE = 0; //无
	public static final int ROTATE = 1; //旋转
	public static final int MOVE = 2; //平移
	public static final int STRETCH  = 3; //伸缩
	public static final int STRETCH_X = 4; //伸缩X方向
	public static final int STRETCH_Y = 5; //伸缩Y方向
	public static final int MENU = 100; //显示更多菜单
	public static final int MENU_PROPERTY = 101; //属性(比如三角形的三边长度和三个角大小,圆的圆心和半径)
	public static final int MENU_MOVE_UP = 102; //上移
	public static final int MENU_MOVE_DOWN = 103; //下移
	public static final int MENU_MOVE_UP_TO_FIRST = 104; //置顶
	public static final int MENU_MOVE_DOWN_TO_LAST = 105; //置底
	public static final int MENU_BRUSH_COLOR = 106; //颜色
	public static final int MENU_BRUSH_WIDTH = 107; //笔宽
	public static final int MENU_COPY = 108; //复制
	public static final int MENU_CLONE = 109; //克隆
	public static final int MENU_CUT = 110; //剪切
	public static final int MENU_DELETE = 111; //删除
	public static final int MENU_EDIT = 112; //属性编辑
	
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
