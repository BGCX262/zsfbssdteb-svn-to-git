package com.f2.sharp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import com.f2.tool.PaintBrush;
import com.f2.tool.GeomTool;
import com.f2.tool.PointF;
import com.f2.tool.DrawTool;
import com.f2.tool.constant.MenuAction;
import com.f2.tool.constant.SharpType;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;

/**
 * 手绘线
 * 
 * @author user
 *
 */
public class Handwriting extends AbsMenuSharp {
	
	private BufferedImage tempImage;	
	protected List<PointF> ps = new LinkedList<PointF>();
	
	public Handwriting(PaintBrush brush) {
		super(brush);
		//mPaintBrush.setStrokeDash(StrokeDash.D0);
		//resetTempImage();
		resetMoreMenuActions();
	}

	public Handwriting(PaintBrush brush, List<PointF> ps) {
		super(brush);
		//mPaintBrush.setStrokeDash(StrokeDash.D0);
		this.ps.addAll(ps);
		resetBounds(ps);
		resetTempImage();
		resetMoreMenuActions();
	}

	public void addPoint(PointF p)	{
		ps.add(p);
		updateBounds(p);
	}

	public void endPoint(PointF p) {
		addPoint(p);
		resetTempImage();
	}

	private void resetTempImage() {
//		if(true) {
		Rectangle bound = getBounds();
		int lineWidth = getPaintBrush().getStrokeLineWidth();
		int w = bound.width + lineWidth*2;
		int h = bound.height + lineWidth*2;
		//System.out.println("Handwriting.resetTempImage, w = " + w + ", h = " + h);
		tempImage = null;
		tempImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = tempImage.getGraphics();
		int x = bound.x - lineWidth;
		int y = bound.y - lineWidth;
		g.translate(-x, -y);
		DrawTool.drawHandwriting(g, ps, getPaintBrush());
		g.dispose();
//		}
	}

	@Override
	public void draw(Graphics g) {
//		super.paint(g);
		if(tempImage == null) {
			DrawTool.drawHandwriting(g, ps, getPaintBrush());
		} else {
			Rectangle bound = getBounds();
			int lineWidth = getPaintBrush().getStrokeLineWidth();
			int x = bound.x - lineWidth;
			int y = bound.y - lineWidth;
			int w = tempImage.getWidth();
			int h = tempImage.getHeight();
			//System.out.println("Handwriting.draw, w = " + w + ", h = " + h);
			//((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.drawImage(tempImage, x, y, w, h, null);
		}
	}
	
	@Override
	public boolean isInSharp(PointF p) {
		for(int i = 0; i < ps.size() - 1; i++) {
			PointF s = ps.get(i);
			PointF e = ps.get(i + 1);
			if(GeomTool.isLineContain(s, e, p, getPaintBrush().getStrokeLineWidth())) {
				return true;
			}
		}		
		return false;
	}
	 		
	@Override	
	public void rotate(PointF basic, double arc) {
		for(int i = 0; i < ps.size(); i++) {
			PointF e = ps.get(i);
			GeomTool.rotateLine(arc, basic, e);
		}
		resetBounds(ps);
		resetTempImage();
	}

	@Override
	public void move(double dx, double dy) {
		for(int i = 0; i < ps.size(); i++) {
			PointF s = ps.get(i);
			GeomTool.movePoint(dx, dy, s);
		}
		resetBounds(ps);
		//resetTempImage();这里不需要
	}

	@Override
	public void zoom(PointF basic, double scale) {
		for(int i = 0; i < ps.size(); i++) {
			PointF e = ps.get(i);
			GeomTool.zoomLine(scale, basic, e);
		}
		resetBounds(ps);
		resetTempImage();
	}

	@Override
	public void stretch(PointF basic, double sx, double sy) {
		for(int i = 0; i < ps.size(); i++) {
			PointF e = ps.get(i);
			GeomTool.stretchLine(sx, sy, basic, e);
		}
		resetBounds(ps);
		resetTempImage();
	}
	
	@Override
	public int getSharpType() {
		return SharpType.HANDWRITING;
	}

	@Override
	public void setBytes(byte[] bs) {
		try {
			ByteReader br = new ByteReader(bs);
			ps.clear();
			int size = br.readInt();
			for(int i = 0; i < size; i++) {
				PointF p = br.readPointF();	
				ps.add(p);
			}
			resetBounds(ps);
			resetTempImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] getBytes() {
		try {
			ByteWriter bw = new ByteWriter();
			bw.writeInt(ps.size());
			for(int i = 0; i < ps.size(); i++) {
				bw.writePointF(ps.get(i));
			}
			return bw.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object clone() {
		Handwriting o = (Handwriting) super.clone();
		o.ps = new LinkedList<PointF>();
		for(PointF p : ps) {
			o.ps.add(new PointF(p));
		}
		return o;
	}
	
	/**
	 * 响应擦除操作
	 * @param point
	 * @return 擦除结果
	 * 整个清除的则直接返回空的list; 
	 * 若擦除后无变化,则返回自身
	 * 若擦除后分裂成多个,则返回分裂结果
	 */
	@Override
	public List<AbsSharp> erase(PointF point, int lineWidth) {
		//System.out.println("Handwriting.erase, point = " + point);

		List<AbsSharp> list = new LinkedList<AbsSharp>();
		if(ps.size() <= 2 && (point.equals(ps.get(0)) || point.equals(ps.get(ps.size() - 1)))) {
			return list;
		}

		int start = 0;
		for(int i = 0; i < ps.size() - 1; i++) {
			PointF s = ps.get(i);
			PointF e = ps.get(i + 1);			
			if(GeomTool.isLineContain(s, e, point, lineWidth)) {
				if(i > start) {
					list.add(new Handwriting(mPaintBrush, ps.subList(start, i)));
				}
				start = i + 1;
			}
		}

		if(ps.size() > start) {
			list.add(new Handwriting(mPaintBrush, ps.subList(start, ps.size())));
		}

		if(list.size() > 0)
			return list;
		return null;
	}
	
	public void setPaintColor(Color color) {
		super.setPaintColor(color);
		resetTempImage();
	}
	
	public void setPaintStrokeLineWidth(int strokeLineWidth) {
		super.setPaintStrokeLineWidth(strokeLineWidth);
		resetTempImage();
	}
	
	public void setPaintStrokeDash(float[] strokeDash) {
		super.setPaintStrokeDash(strokeDash);
		resetTempImage();
	}
	
	private void resetMoreMenuActions() {
		mMoreMenuActions = new int[]{
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
				//MenuAction.MENU_PROPERTY, //"属性"
				};
		mMoreMenuIcons = new BufferedImage[]{
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
				//imgProperty
				};
	}

	@Override
	public String toString() {
		return "Handwriting[size=" + ps.size() + ",ps=" + ps + "]";
	}
}
