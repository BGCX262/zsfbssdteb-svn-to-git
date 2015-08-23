package com.f2.sharp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.f2.tool.PaintBrush;
import com.f2.tool.PointF;
import com.f2.tool.constant.MenuAction;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;
import com.f2.tool.file.IFbsData;

public abstract class AbsMenuSharp extends AbsSharp implements IFbsData {
	
	protected boolean isSelected = false;
	protected boolean isShowMoreMenu = false;

	protected static final int MENU_ITEM_WIDTH = 28;
	protected static final int MENU_ITEM_HEIGHT = 28;
	protected static final int MENU_MORE_ITEM_WIDTH = 35;
	protected static final int MENU_MORE_ITEM_HEIGHT = 35;
	protected static BasicStroke sBroundStroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	private static int[] sActionMoreMenu = new int[]{
			MenuAction.MENU_DELETE, //"删除"
			MenuAction.MENU_MOVE_UP, //"上移"
			MenuAction.MENU_MOVE_DOWN, //"下移"
			MenuAction.MENU_MOVE_UP_TO_FIRST, //"置顶"
			MenuAction.MENU_MOVE_DOWN_TO_LAST, //"置底"
			MenuAction.MENU_BRUSH_COLOR, //"颜色"
			MenuAction.MENU_BRUSH_WIDTH, //"笔宽"
			//MenuAction.MENU_PROPERTY, //"属性"
			}; 

	private static Image imgRotate;
	private static Image imgMove;
	private static Image imgStretch;
	private static Image imgStretchX;
	private static Image imgStretchY;
	private static Image imgMore;
	
	private static Image imgDelete;
	private static Image imgUp;
	private static Image imgDown;
	private static Image imgUpToFirst;
	private static Image imgDownToLast;
	private static Image imgColor;
	private static Image imgPaintWidth;
	
	static {
		imgRotate = loadImage("images/btn/sharp_rotate.png");
		imgMove = loadImage("images/btn/sharp_move.png");
		imgStretch = loadImage("images/btn/sharp_stretch.png");
		imgStretchX = loadImage("images/btn/sharp_stretch_x.png");
		imgStretchY = loadImage("images/btn/sharp_stretch_y.png");
		imgMore = loadImage("images/btn/sharp_more.png");
		
		imgDelete = loadImage("images/btn/sharp_delete.png");
		imgUp = loadImage("images/btn/sharp_up.png");
		imgDown = loadImage("images/btn/sharp_down.png");
		imgUpToFirst = loadImage("images/btn/sharp_up_to_first.png");
		imgDownToLast = loadImage("images/btn/sharp_down_to_last.png");
		imgColor = loadImage("images/btn/sharp_color.png");
		imgPaintWidth = loadImage("images/btn/sharp_paint_width.png");
	}
	
	//private static String[] sLabelMoreMenu = new String[]{
	//	"删除", "上移", "下移", "置顶", "置底", "颜色", "笔宽"
	//	}; // ,"属性"};

	private static Image[] sImageMoreMenu = new Image[]{
		imgDelete, imgUp, imgDown, imgUpToFirst, imgDownToLast, imgColor, imgPaintWidth
		}; // ,"属性"};

	private static Image loadImage(String filepath) {
		try {
			return ImageIO.read(new File(filepath));
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public AbsMenuSharp(PaintBrush brush) {
		super(brush);
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isShowMoreMenu() {
		return isShowMoreMenu;
	}

	public void setShowMoreMenu(boolean isShowMoreMenu) {
		this.isShowMoreMenu = isShowMoreMenu;
	}

	public void drawMenu(Graphics g)
	{
		// 仅绘制是否被选中的边框+菜单+More菜单
		if(isSelected) {
			drawBasicMenu(g);
			if(isShowMoreMenu) {
				drawMoreMenu(g);
			}
		}
	}
	
	public void drawBasicMenu(Graphics g) {
		Rectangle bounds = getBounds();
		int x1 = (int) bounds.x;
		int y1 = (int) bounds.y;
		int x2 = (int) bounds.x + bounds.width;
		int y2 = (int) bounds.y + bounds.height;
		int w = MENU_ITEM_WIDTH;
		int h = MENU_ITEM_HEIGHT;
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(sBroundStroke);

		g.setColor(Color.GRAY);
		g.drawRect(x1 - w, y1 - h, x2 - x1 + 2*w, y2 - y1 + 2*h);
		
//		g.setColor(Color.YELLOW);
//		g.fillRect(x1 - w + 2, y1 - w + 2, w, h);
//		g.fillRect(x1 - w + 2, y2 - 1, w, h);
//		g.fillRect(x2 - 1, y1 - w + 2, w, h);
//		g.fillRect(x2 - 1, y2 - 1, w, h);
		
//		g.setColor(Color.BLACK);
//		g.drawString("旋转", x1 - w + 1, y1 - w*3/8);
//		g.drawString("平移", x1 - w + 1, y2 + w*5/8);
//		g.drawString("缩放", x2 + 1, y1 - w*3/8);
//		g.drawString("更多", x2 + 1, y2 + w*5/8);
		g.drawImage(imgRotate, x1 - w*3/4 + 1, y1 - h*3/4, null);
		g.drawImage(imgMove, x1 - w*3/4 + 1, y2 + h/4, null);
		g.drawImage(imgStretch, x2 + w/4 - 1, y1 - h*3/4, null);
		g.drawImage(imgStretchX, x2 + w/4 - 1, y1 - h/4 + bounds.height/2, null);
		g.drawImage(imgStretchY, x1 - w/4 + 1 + bounds.width/2, y1 - h*3/4, null);
		g.drawImage(imgMore, x2 + w/4 - 1, y2 + h/4, null);
	}
	
	public void drawMoreMenu(Graphics g) {
		Rectangle bounds = getBounds();
		int x2 = (int) bounds.x + bounds.width;
		int y2 = (int) bounds.y + bounds.height;
		
		int x = x2 + MENU_ITEM_WIDTH - MENU_MORE_ITEM_WIDTH * sImageMoreMenu.length;
		int y = y2 + MENU_ITEM_HEIGHT + 1;

		int w = MENU_MORE_ITEM_WIDTH;
		int h = MENU_MORE_ITEM_HEIGHT;

		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(sBroundStroke);

		for(int i = 0; i < sImageMoreMenu.length; i++) {
			g.setColor(Color.GRAY);
			g.drawRect(x, y, w, h);
			//g.setColor(Color.YELLOW);
			//g.fillRect(x + 1, y + 1, w - 1, h - 1);
			//g.setColor(Color.BLACK);
			//g.drawString(sLabelMoreMenu[i], x + 2, y + h*5/8);			
			g.drawImage(sImageMoreMenu[i], x + 2, y + 2, null);
			x += w;
		}
	}
	
	public boolean isInMoreMenuBounds(PointF p) {
		Rectangle bounds = getBounds();
		int w = MENU_MORE_ITEM_WIDTH;
		int h = MENU_MORE_ITEM_HEIGHT;
		if(p.x >= bounds.x + bounds.width && p.x <= bounds.x + bounds.width + w 
				&& p.y >= bounds.y + bounds.height && p.y <= bounds.y + bounds.height + h) {
			return true;
		}		
		return false;
	}
	
	public int getBasicMenuAction(PointF p) {
		Rectangle bounds = getBounds();
		int w = MENU_ITEM_WIDTH;
		int h = MENU_ITEM_HEIGHT;
		int x1 = (int) bounds.x;
		int y1 = (int) bounds.y;
		int x2 = (int) bounds.x + bounds.width;
		int y2 = (int) bounds.y + bounds.height;
		
		if(p.x >= x1 - w && p.x <= x1 && p.y >= y1 - h && p.y <= y1) {
			return MenuAction.ROTATE;
		}
		else if(p.x >= x1 - w && p.x <= x1 && p.y >= y2 && p.y <= y2 + h) {
			return MenuAction.MOVE;
		}
		else if(p.x >= x2 && p.x <= x2 + w && p.y >= y1 - h && p.y <= y1) {
			return MenuAction.STRETCH;
		}
		else if(p.x >= x2 && p.x <= x2 + w && p.y >= y1 - h/2 + bounds.height/2 && p.y <= y1 + h/2 + bounds.height/2) {
			return MenuAction.STRETCH_X;
		}
		else if(p.x >= x1 - w/2 + bounds.width/2 && p.x <= x1 + w/2 + bounds.width/2 && p.y >= y1 - h && p.y <= y1) {
			return MenuAction.STRETCH_Y;
		}
		else if(p.x >= x2 && p.x <= x2 + w && p.y >= y2 && p.y <= y2 + h) {
			return MenuAction.MENU;
		}
		return MenuAction.NONE;
	}
	
	public int getMoreMenuAction(PointF p) {
		Rectangle bounds = getBounds();
		int x2 = (int) bounds.x + bounds.width;
		int y2 = (int) bounds.y + bounds.height;
		int w = MENU_MORE_ITEM_WIDTH;
		int h = MENU_MORE_ITEM_HEIGHT;
		int x = x2 - w * (sActionMoreMenu.length - 1);
		int y = y2 + w;
		for(int i = 0; i < sActionMoreMenu.length; i++) {
			if(p.x >= x && p.x <= x + w && p.y >= y && p.y <= y + h) {
				return sActionMoreMenu[i];
			}
			x += w;
		}		
		return MenuAction.NONE;
	}	

	@Override
	public void initFromBytes(byte[] bs) throws IOException {
		ByteReader br = new ByteReader(bs);
		super.initFromBytes(br.readBytes());
//		isSelected = br.readBoolean();	
//		isShowMoreMenu = br.readBoolean();
		setBytes(br.readBytes());
	}

	@Override
	public byte[] toBytes() throws IOException {
		ByteWriter bw = new ByteWriter();
		bw.writeBytes(super.toBytes());
//		bw.writeBoolean(isSelected);
//		bw.writeBoolean(isShowMoreMenu);
		bw.writeBytes(getBytes());
		return bw.toByteArray();
	}

	public abstract void setBytes(byte[] bs) throws IOException;
	
	public abstract byte[] getBytes() throws IOException;
	

}
