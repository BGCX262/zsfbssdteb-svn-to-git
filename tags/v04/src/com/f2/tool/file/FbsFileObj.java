package com.f2.tool.file;

import java.util.HashMap;
import java.util.Map;

public class FbsFileObj {
	public static final int PAINT_BRUSH = 1;
	public static final int CANVAS_PAGE = 2;
//	public static final int CANVAS_ITEM = 3;
//	public static final int CANVAS_ITEM_SHARP = 4;
	
	private boolean isUpdated = false;
	private String filepath;
	private Map<Integer, IFbsData> maps = new HashMap<Integer, IFbsData>();

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

	public boolean isUpdated() {
		return isUpdated;
	}
	
	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public void setObj(int type, IFbsData obj) {
		maps.put(type, obj);
	}

	public IFbsData getObj(int type) {
		return maps.get(type);
	}

}
