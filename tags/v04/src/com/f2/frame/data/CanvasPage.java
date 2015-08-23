package com.f2.frame.data;

import java.util.LinkedList;
import java.util.List;

import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;
import com.f2.tool.file.IFbsData;

public class CanvasPage implements IFbsData {

	private List<CanvasItem> items = new LinkedList<CanvasItem>();
	private int selectedIndex = 0;

	public CanvasPage() {

	}
	
	public void setItems(List<CanvasItem> items) {
		this.items = items;
	}

	public List<CanvasItem> getItems() {
		return items;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void add(CanvasItem item) {
		items.add(item);
	}

	public int size() {
		return items.size();
	}

	public CanvasItem get(int i) {
		return items.get(i);
	}
	
	public CanvasItem getSelectedItem() {
		return items.get(selectedIndex);
	}

	/**
	 * 上移一位
	 */
	public void itemUp() {
		if(selectedIndex < 1) {
			return;
		}
		CanvasItem tmp = items.get(selectedIndex);
		items.set(selectedIndex, items.get(selectedIndex - 1));
		items.set(selectedIndex - 1, tmp);
		selectedIndex = selectedIndex - 1;
	}

	/**
	 * 下移一位
	 */
	public void itemDown() {
		if(selectedIndex >= items.size() - 1) {
			return;
		}
		CanvasItem tmp = items.get(selectedIndex);
		items.set(selectedIndex, items.get(selectedIndex + 1));
		items.set(selectedIndex + 1, tmp);
		selectedIndex = selectedIndex + 1;
	}

	/**
	 * 上移到顶部
	 */
	public void itemUpToFirst() {
		if(selectedIndex < 1) {
			return;
		}
		CanvasItem tmp = items.remove(selectedIndex);
		items.add(0, tmp);
		selectedIndex = 0;
	}

	/**
	 * 下移到底部
	 */
	public void itemDownToLast() {
		if(selectedIndex >= items.size() - 1) {
			return;
		}
		CanvasItem tmp = items.remove(selectedIndex);
		items.add(tmp);
		selectedIndex = items.size() - 1;
	}

	/**
	 * 删除当前选中页
	 */
	public void deleteSelected() {
		items.remove(selectedIndex);
		selectedIndex = selectedIndex - 1;
		if (selectedIndex < 0) {			
			selectedIndex = 0;
		}
	}
	
	@Override
	public void initFromBytes(byte[] bs) {
		try {
			ByteReader br = new ByteReader(bs);
			int selectedIndex = br.readInt();
			int size = br.readInt();
			List<CanvasItem> list = new LinkedList<CanvasItem>();
			for(int i = 0; i < size; i++) {
				CanvasItem item = new CanvasItem();
				item.initFromBytes(br.readBytes());
				list.add(item);
			}
			this.selectedIndex = selectedIndex;
			this.items = list;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] toBytes() {
		try {
			ByteWriter bw = new ByteWriter();
			bw.writeInt(selectedIndex);
			bw.writeInt(items.size());
			for(CanvasItem item : items) {
				bw.writeBytes(item.toBytes());
			}
			return bw.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
