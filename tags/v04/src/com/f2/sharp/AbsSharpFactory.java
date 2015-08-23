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
		case SharpType.TRIANGLE_RIGHT: //ֱ��������
		case SharpType.TRIANGLE_EQUILATERAL: //�ȱ�������
		case SharpType.RECTANGLE: //������
		case SharpType.SQUARE: //������
		case SharpType.PENTAGON: //�����
		case SharpType.HEXAGON: //������
			return new Polygon(null);
		case SharpType.CIRCLE:
			return new Circle(null);
		case SharpType.PICTURE:
			return new Picture(null);
		case SharpType.ELLIPSE: //��Բ
			return new Ellipse(null);
		case SharpType.POLYLINE: //���߶�
			return new Polyline(null);
		}
		return new Handwriting(null);
	}
}
