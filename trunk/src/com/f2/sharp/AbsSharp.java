package com.f2.sharp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.f2.tool.PaintBrush;
import com.f2.tool.GeomTool;
import com.f2.tool.PointF;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;

public abstract class AbsSharp implements Cloneable {
	
	protected Rectangle mBounds = null; //图形边界
	protected PaintBrush mPaintBrush; //图形画笔
	protected final long mId;
	
	public AbsSharp() {
		mId = System.currentTimeMillis();
		this.mPaintBrush = new PaintBrush();
	}
	
	/**
	 * @param brush 图形画笔
	 */
	public AbsSharp(PaintBrush brush) {
		mId = System.currentTimeMillis();
		this.mPaintBrush = new PaintBrush(brush);
	}
	
	public long getId() {
		return mId;
	}

	/**
	 * 获取画笔
	 * @return
	 */
	protected PaintBrush getPaintBrush() {
		return mPaintBrush;
	}

	/**
	 * 设置新的边界
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setBounds(int x, int y, int width, int height) {
        if (mBounds == null) {
            mBounds = new Rectangle();
        }
        mBounds.setBounds(x, y, width, height);
    }
    
	/**
	 * 设置新的边界
	 * @param bounds
	 */
    public void setBounds(Rectangle bounds) {
        if (mBounds == null) {
        	mBounds = new Rectangle();
        }
        mBounds.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    /**
     * 更新边界,使之包含新的点
     * @param p
     */
    public void updateBounds(PointF p) {
        if (mBounds == null) {
        	mBounds = new Rectangle();
        	mBounds.setBounds((int)p.x, (int)p.y, 0, 0);
        }
    	PointF min = new PointF(mBounds.x, mBounds.y);
    	PointF max = new PointF(mBounds.x + mBounds.width, mBounds.y + mBounds.height);
    	GeomTool.getMinPoint(min, p);
    	GeomTool.getMaxPoint(max, p);
    	mBounds.setBounds((int)min.x, (int)min.y, (int)(max.x - min.x), (int)(max.y - min.y));
    }
    
    /**
     * 更新边界,使之仅包含点s和点e
     * @param s
     * @param e
     */
    public void setBounds(PointF s, PointF e) {
        if (mBounds == null) {
        	mBounds = new Rectangle();
        }
		PointF min = new PointF();
		PointF max = new PointF();
		GeomTool.getMinMaxPoint(s, e, min, max);
    	mBounds.setBounds((int)min.x, (int)min.y, (int)(max.x - min.x), (int)(max.y - min.y));
    }
    
    /**
     * 重设边界,使之包含一系列的点
     * @param ps
     */
    public void resetBounds(List<PointF> ps) {
        if (mBounds == null) {
        	mBounds = new Rectangle();
        }
		PointF min = new PointF();
		PointF max = new PointF();
		GeomTool.getMinMaxPoint(ps, min, max);
    	mBounds.setBounds((int)min.x, (int)min.y, (int)(max.x - min.x), (int)(max.y - min.y));
    }
    
    /**
     * 获取边界
     * @return
     */
	public Rectangle getBounds() {
		 if (mBounds == null) {
			 mBounds = new Rectangle();
		 }
		 return mBounds;
	}
	
	protected void initFromBytes(byte[] bs) {
		try {
			ByteReader br = new ByteReader(bs);
			setBounds(br.readInt(), br.readInt(), br.readInt(), br.readInt());		
			mPaintBrush.initFromBytes(br.readBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected byte[] toBytes() {
		try {
			ByteWriter bw = new ByteWriter();
			bw.writeInt(mBounds.x);
			bw.writeInt(mBounds.y);
			bw.writeInt(mBounds.width);
			bw.writeInt(mBounds.height);
			bw.writeBytes(mPaintBrush.toBytes());
			return bw.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 绘图
	 * @param g
	 */
	public abstract void draw(Graphics g);

	/**
	 * 旋转
	 * @param basic 旋转基点
	 * @param arc 旋转角度: >0顺时针,<0逆时针
	 */
	public abstract void rotate(PointF basic, double arc);

	/**
	 * 平移
	 * @param dx X轴方向偏移
	 * @param dy Y轴方向偏移
	 */
	public abstract void move(double dx, double dy);
	
	/**
	 * 缩放(宽高等比例)
	 * @param basic 缩放基点
	 * @param scale 缩放比例: >1放大,<1缩小
	 */
	public abstract void zoom(PointF basic, double scale);
	
	/**
	 * 伸缩(宽高非等比例)
	 * @param basic 缩放基点
	 * @param sx 缩放比例(X轴): >1放大,<1缩小
	 * @param sy 缩放比例(Y轴): >1放大,<1缩小
	 */
	public abstract void stretch(PointF basic, double sx, double sy);
	
	/**
	 * 判断某个点是否在图形内,或者是否在图形轨迹内,用于选中图形
	 * @param p
	 * @return
	 */
	public abstract boolean isInSharp(PointF p);	 
	
	/**
	 * 获取类型
	 * @return
	 */
	public abstract int getSharpType();	 
	
	/**
	 * 响应点击操作
	 * @param point
	 * @return
	 */
	public boolean onClick(PointF point) {
		return false;
	}
	
	/**
	 * 释放资源
	 */
	public void release() {
		// nothing here
	}
	
	public void show() {
		// nothing here
	}
	
	public void hide() {
		// nothing here
	}
	
	/**
	 * 响应擦除操作
	 * @param point
	 * @return 擦除结果
	 * 整个清除的则直接返回空的list; 
	 * 若擦除后无变化,则返回自身
	 * 若擦除后分裂成多个,则返回分裂结果
	 */
	public List<AbsSharp> erase(PointF point, int lineWidth) {
		return new LinkedList<AbsSharp>();
	}
	
	public PointF getRotateBasicPoint() {
		if(mBounds == null) {
			return new PointF();
		}
		return new PointF(mBounds.x + mBounds.width / 2, mBounds.y + mBounds.height / 2);
	}

	public PointF getZoomBasicPoint() {
		if(mBounds == null) {
			return new PointF();
		}
		return new PointF(mBounds.x + mBounds.width / 2, mBounds.y + mBounds.height / 2);
	}

	public PointF getStretchBasicPoint() {
		if(mBounds == null) {
			return new PointF();
		}
		return new PointF(mBounds.x + mBounds.width / 2, mBounds.y + mBounds.height / 2);
	}

	public Object clone() { 
		AbsSharp o = null;
		try {
			o = (AbsSharp) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		o.mBounds = new Rectangle(mBounds); //图形边界
		o.mPaintBrush = new PaintBrush(mPaintBrush); //图形画笔
		return o; 
	}

	public void setPaintColor(Color color) {
		mPaintBrush.setColor(color);
	}

	public void setPaintStrokeLineWidth(int strokeLineWidth) {
		mPaintBrush.setStrokeLineWidth(strokeLineWidth);
	}

	public void setPaintStrokeDash(float[] strokeDash) {
		mPaintBrush.setStrokeDash(strokeDash);
	}

	public Color getPaintColor() {
		return mPaintBrush.getColor();
	}

	public int getPaintStrokeLineWidth() {
		return mPaintBrush.getStrokeLineWidth();
	}

	public float[] getPaintStrokeDash() {
		return mPaintBrush.getStrokeDash();
	}
	
	public void setPaintAction(int action) {
		mPaintBrush.setAction(action);
	}

	public int getPaintAction() {
		return mPaintBrush.getAction();
	}
}
