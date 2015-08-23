package com.f2.sharp;

import com.f2.sharp.geometry.*;
import com.f2.tool.constant.SharpType;

public class AbsSharpFactory {
	public static AbsMenuSharp getSharp(int type) {
		switch(type) {
		case SharpType.HANDWRITING:
			return new Handwriting(null);
		case SharpType.LINE:
			return new Line(null);
		case SharpType.POLYGON:
		case SharpType.TRIANGLE_RIGHT: //直角三角形
		case SharpType.TRIANGLE_EQUILATERAL: //等边三角形
		case SharpType.RECTANGLE: //正方形
		case SharpType.SQUARE: //正方形
		case SharpType.PENTAGON: //五边形
		case SharpType.HEXAGON: //六边形
			return new Polygon(null);
		case SharpType.CIRCLE:
			return new Circle(null);
		case SharpType.PICTURE:
			return new Picture(null);
		case SharpType.ELLIPSE: //椭圆
			return new Ellipse(null);
		case SharpType.POLYLINE: //折线段
			return new Polyline(null);
		}
		return new Handwriting(null);
	}
}
