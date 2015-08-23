package com.f2.sharp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.f2.tool.ImageTool;
import com.f2.tool.PaintBrush;
import com.f2.tool.PointF;
import com.f2.tool.constant.MenuAction;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;
import com.f2.tool.file.IFbsData;

public abstract class AbsMenuSharp extends AbsSharp implements IFbsData {
	
	protected boolean isSelected = false;
	protected boolean isShowMoreMenu = false;
	protected boolean isShowProperty = false;

	protected static final int MENU_ITEM_WIDTH = 28;
	protected static final int MENU_ITEM_HEIGHT = 28;
	protected static final int MENU_MORE_ITEM_WIDTH = 35;
	protected static final int MENU_MORE_ITEM_HEIGHT = 35;
	protected static BasicStroke sBroundStroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	protected static BufferedImage imgRotate;
	protected static BufferedImage imgMove;
	protected static BufferedImage imgStretch;
	protected static BufferedImage imgStretchX;
	protected static BufferedImage imgStretchY;
	protected static BufferedImage imgMore;
	
	protected static BufferedImage imgCopy;
	protected static BufferedImage imgClone;
	protected static BufferedImage imgCut;
	protected static BufferedImage imgDelete;
	protected static BufferedImage imgUp;
	protected static BufferedImage imgDown;
	protected static BufferedImage imgUpToFirst;
	protected static BufferedImage imgDownToLast;
	protected static BufferedImage imgBrushColor;
	protected static BufferedImage imgBrushWidth;
	protected static BufferedImage imgProperty;
	protected static BufferedImage imgEdit;
	
	static {
		imgRotate = ImageTool.getResImage("/res/images/btn/sharp_rotate.png");
		imgMove = ImageTool.getResImage("/res/images/btn/sharp_move.png");
		imgStretch = ImageTool.getResImage("/res/images/btn/sharp_stretch.png");
		imgStretchX = ImageTool.getResImage("/res/images/btn/sharp_stretch_x.png");
		imgStretchY = ImageTool.getResImage("/res/images/btn/sharp_stretch_y.png");
		imgMore = ImageTool.getResImage("/res/images/btn/sharp_more.png");
		
		imgCopy = ImageTool.getResImage("/res/images/btn/sharp_copy.png");
		imgClone = ImageTool.getResImage("/res/images/btn/sharp_clone.png");
		imgCut = ImageTool.getResImage("/res/images/btn/sharp_cut.png");
		imgDelete = ImageTool.getResImage("/res/images/btn/sharp_delete.png");
		imgUp = ImageTool.getResImage("/res/images/btn/sharp_up.png");
		imgDown = ImageTool.getResImage("/res/images/btn/sharp_down.png");
		imgUpToFirst = ImageTool.getResImage("/res/images/btn/sharp_up_to_first.png");
		imgDownToLast = ImageTool.getResImage("/res/images/btn/sharp_down_to_last.png");
		imgBrushColor = ImageTool.getResImage("/res/images/btn/sharp_brush_color.png");
		imgBrushWidth = ImageTool.getResImage("/res/images/btn/sharp_brush_width.png");
		imgProperty = ImageTool.getResImage("/res/images/btn/sharp_property.png");
		imgEdit = ImageTool.getResImage("/res/images/btn/sharp_edit.png");
	}
	
	protected int[] mMoreMenuActions = new int[]{
			MenuAction.MENU_COPY, //"复制"
			MenuAction.MENU_CLONE, //"克隆"
			MenuAction.MENU_CUT, //"剪切"
			MenuAction.MENU_DELETE, //"删除"
			MenuAction.MENU_MOVE_UP, //"上移"
			MenuAction.MENU_MOVE_DOWN, //"下移"
			MenuAction.MENU_MOVE_UP_TO_FIRST, //"置顶"
			MenuAction.MENU_MOVE_DOWN_TO_LAST, //"置底"
			MenuAction.MENU_BRUSH_COLOR, //"颜色"
			MenuAction.MENU_BRUSH_WIDTH, //"笔宽"
			MenuAction.MENU_PROPERTY, //"属性"
			MenuAction.MENU_EDIT, //"编辑"
			};
	protected BufferedImage[] mMoreMenuIcons = new BufferedImage[]{
			imgCopy, 
			imgClone, 
			imgCut, 
			imgDelete,
			imgUp, 
			imgDown, 
			imgUpToFirst, 
			imgDownToLast, 
			imgBrushColor, 
			imgBrushWidth, 
			imgProperty, 
			imgEdit
			};
	
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

	public boolean isShowProperty() {
		return isShowProperty;
	}

	public void setShowProperty(boolean isShowProperty) {
		this.isShowProperty = isShowProperty;
	}

	public boolean hasPropertyAction() {
		return false;
	}
	
	public void drawMenu(Graphics g)
	{
		// 仅绘制是否被选中的边框+菜单+More菜单
		if(isShowProperty) {
			drawProperty(g);
		}
		if(isSelected) {
			drawBasicMenu(g);
			if(isShowMoreMenu) {
				drawMoreMenu(g);
			}
		}
	}
	
	protected void drawProperty(Graphics g) {
		
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
		
		int x = x2 + MENU_ITEM_WIDTH - MENU_MORE_ITEM_WIDTH * mMoreMenuIcons.length;
		int y = y2 + MENU_ITEM_HEIGHT + 1;

		int w = MENU_MORE_ITEM_WIDTH;
		int h = MENU_MORE_ITEM_HEIGHT;

		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(sBroundStroke);

		for(int i = 0; i < mMoreMenuIcons.length; i++) {
			g.setColor(Color.GRAY);
			g.drawRect(x, y, w, h);	
			g.drawImage(mMoreMenuIcons[i], x + 2, y + 2, null);
			x += w;
		}
	}
	public boolean isInBasicMenuBounds(PointF p) {
		return getBasicMenuAction(p) != MenuAction.NONE;
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
		} else if(isInSharp(p)) {
			return MenuAction.MOVE; //只要在图形内部,就算是拖拽
		}
		return MenuAction.NONE;
	}
	
	public int getMoreMenuAction(PointF p) {
		Rectangle bounds = getBounds();
		int x2 = (int) bounds.x + bounds.width;
		int y2 = (int) bounds.y + bounds.height;
		int w = MENU_MORE_ITEM_WIDTH;
		int h = MENU_MORE_ITEM_HEIGHT;
		int x = x2 - w * (mMoreMenuActions.length - 1);
		int y = y2 + w;
		for(int i = 0; i < mMoreMenuActions.length; i++) {
			if(p.x >= x && p.x <= x + w && p.y >= y && p.y <= y + h) {
				return mMoreMenuActions[i];
			}
			x += w;
		}		
		return MenuAction.NONE;
	}	

	@Override
	public void initFromBytes(byte[] bs) {
		try {
			ByteReader br = new ByteReader(bs);
			super.initFromBytes(br.readBytes());
			setBytes(br.readBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] toBytes() {
		try {
			ByteWriter bw = new ByteWriter();
			bw.writeBytes(super.toBytes());
			bw.writeBytes(getBytes());
			return bw.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public abstract void setBytes(byte[] bs) throws IOException;
	
	public abstract byte[] getBytes() throws IOException;
	
	public Object clone() { 
		AbsMenuSharp o = (AbsMenuSharp) super.clone();
		o.isSelected = isSelected;
		o.isShowMoreMenu = isShowMoreMenu;
		return o; 
	}
}
