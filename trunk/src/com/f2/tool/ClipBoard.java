package com.f2.tool;

import java.util.HashMap;
import java.util.Map;

public class ClipBoard {
	public static final int CANVAS_COPY = 1;
	public static final int SHARP_COPY = 2;
	public static final int CANVAS_UNDO_REDO = 3;
	
	private static Map<Integer, Object> objs = new HashMap<Integer, Object>();
	
	public static Object get(int type) {
		return objs.get(Integer.valueOf(type));
	}
	
	public static void put(int type, Object obj) {
		objs.put(Integer.valueOf(type), obj);
	}
	
	public static void clear() {
		objs.clear();
	}
}
