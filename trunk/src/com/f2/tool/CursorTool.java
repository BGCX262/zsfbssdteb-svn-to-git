package com.f2.tool;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import com.f2.tool.constant.SharpType;

public class CursorTool {
	private static Cursor cursorErase;
	private static Cursor cursorHandwriting;
	private static Cursor cursorSimulator;
	private static JFrame frame = null;
	
	public static void setCursorFrame(JFrame frame) {
		CursorTool.frame = frame;
	}

	public static void updateCursor(int brushType) {
		if(frame == null) {
			return;
		}
		// 根据图形和画笔状态切换鼠标图标
		if(brushType == SharpType.SELECTION) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if(brushType == SharpType.HANDWRITING) {
			if(cursorHandwriting == null) {
				BufferedImage img = ImageTool.getResImage("/res/images/menu/handwriting.png");
				cursorHandwriting = frame.getToolkit().createCustomCursor(img, new Point(16, 16), "handwriting");
			}
			frame.setCursor(cursorHandwriting);
		} else if(brushType == SharpType.SIMULATE) {
			if(cursorSimulator == null) {
				BufferedImage img = ImageTool.getResImage("/res/images/menu/simulator.png");
				cursorSimulator = frame.getToolkit().createCustomCursor(img, new Point(16, 16), "simulator");
			}
			frame.setCursor(cursorSimulator);
		} else if(brushType == SharpType.ERASE) {
			if(cursorErase == null) {
				BufferedImage img = ImageTool.getResImage("/res/images/menu/erase.png");
				cursorErase = frame.getToolkit().createCustomCursor(img, new Point(16, 16), "erase");
			}
			frame.setCursor(cursorErase);
		} else {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} 
	}
}
