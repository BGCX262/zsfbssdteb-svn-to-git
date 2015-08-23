package com.f2.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import com.f2.frame.dada.CanvasItem;
import com.f2.listener.CanvasRepaintListener;
import com.f2.sharp.AbsMenuSharp;

@SuppressWarnings("serial")
public class PreviewPanel extends JPanel implements CanvasRepaintListener {
	
	private BufferedImage bgTempImage;
	private CanvasItem canvasItem = null;
	private int pageNo;
	
	private boolean isSelected = false;
	
	public PreviewPanel(CanvasItem canvasItem, int width, int height, int pageNo) {
		super();
		this.canvasItem = canvasItem;
		this.pageNo = pageNo;
		setSize(124, 98);
		setPreferredSize(getSize());
		bgTempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
	
	@Override
	public void paint(Graphics g) {
		//System.out.println("mCanvas.paint");
		if(bgTempImage == null) {
			return;
		}
		
		//»­±³¾°
		Graphics g1 = bgTempImage.getGraphics();
		canvasItem.getBackground().draw(g1, 0, 0, bgTempImage.getWidth(), bgTempImage.getHeight());

		//»­Í¼ÐÎ
		List<AbsMenuSharp> sharps = canvasItem.getSharps();
		for(int i = sharps.size() - 1; i >= 0; i--) {
			sharps.get(i).draw(g1);
		}
		
		g1.dispose();				

		//»­±ß¿ò		
		g.setColor(isSelected ? Color.BLUE : Color.YELLOW);
		g.drawRect(0, 2, this.getWidth() - 1, this.getHeight() - 5);

		//Ó³ÉäÁÙÊ±bgImageµ½Êµ¼Ê»­²¼
		g.drawImage(bgTempImage, 2, 4, this.getWidth() - 4, this.getHeight() - 8, null);
		
		//»­Ò³Âë		
		g.setColor(Color.GRAY);
		g.drawString("µÚ" + pageNo + "Ò³", 4, 16);		
	}
	
	@Override
	public void repaintCanvasPanel() {
		this.repaint();
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public CanvasItem getCanvasItem() {
		return canvasItem;
	}

	public void setCanvasItem(CanvasItem canvasItem) {
		this.canvasItem = canvasItem;
		repaintCanvasPanel();
	}

}
