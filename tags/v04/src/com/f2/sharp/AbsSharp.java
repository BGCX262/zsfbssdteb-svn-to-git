package com.f2.sharp;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import com.f2.tool.PaintBrush;
import com.f2.tool.GeomTool;
import com.f2.tool.PointF;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;

public abstract class AbsSharp {
	
	protected Rectangle mBounds = null; //ͼ�α߽�
	protected PaintBrush mPaintBrush; //ͼ�λ���
	
	public AbsSharp() {
		super();
		this.mPaintBrush = new PaintBrush();
	}
	
	/**
	 * @param brush ͼ�λ���
	 */
	public AbsSharp(PaintBrush brush) {
		super();
		this.mPaintBrush = new PaintBrush(brush);
	}

	/**
	 * ��ȡ����
	 * @return
	 */
	public PaintBrush getPaintBrush() {
		return mPaintBrush;
	}

	/**
	 * �����µı߽�
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
	 * �����µı߽�
	 * @param bounds
	 */
    public void setBounds(Rectangle bounds) {
        if (mBounds == null) {
        	mBounds = new Rectangle();
        }
        mBounds.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    /**
     * ���±߽�,ʹ֮�����µĵ�
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
     * ���±߽�,ʹ֮��������s�͵�e
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
     * ����߽�,ʹ֮����һϵ�еĵ�
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
     * ��ȡ�߽�
     * @return
     */
	public Rectangle getBounds() {
		 if (mBounds == null) {
			 mBounds = new Rectangle();
		 }
		 return mBounds;
	}
	
	protected void initFromBytes(byte[] bs) throws IOException {
		ByteReader br = new ByteReader(bs);
		setBounds(br.readInt(), br.readInt(), br.readInt(), br.readInt());		
		mPaintBrush.initFromBytes(br.readBytes());
	}
	
	protected byte[] toBytes() throws IOException {
		ByteWriter bw = new ByteWriter();
		bw.writeInt(mBounds.x);
		bw.writeInt(mBounds.y);
		bw.writeInt(mBounds.width);
		bw.writeInt(mBounds.height);
		bw.writeBytes(mPaintBrush.toBytes());
		return bw.toByteArray();
	}
	
	/**
	 * ��ͼ
	 * @param g
	 */
	public abstract void draw(Graphics g);

	/**
	 * ��ת
	 * @param basic ��ת����
	 * @param arc ��ת�Ƕ�: >0˳ʱ��,<0��ʱ��
	 */
	public abstract void rotate(PointF basic, double arc);

	/**
	 * ƽ��
	 * @param dx X�᷽��ƫ��
	 * @param dy Y�᷽��ƫ��
	 */
	public abstract void move(double dx, double dy);
	
	/**
	 * ����(��ߵȱ���)
	 * @param basic ���Ż���
	 * @param scale ���ű���: >1�Ŵ�,<1��С
	 */
	public abstract void zoom(PointF basic, double scale);
	
	/**
	 * ����(��߷ǵȱ���)
	 * @param basic ���Ż���
	 * @param sx ���ű���(X��): >1�Ŵ�,<1��С
	 * @param sy ���ű���(Y��): >1�Ŵ�,<1��С
	 */
	public abstract void stretch(PointF basic, double sx, double sy);
	
	/**
	 * �ж�ĳ�����Ƿ���ͼ����,�����Ƿ���ͼ�ι켣��,����ѡ��ͼ��
	 * @param p
	 * @return
	 */
	public abstract boolean isInSharp(PointF p);	 
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public abstract int getSharpType();	 
	
	public boolean onClick(PointF point) {
		return false;
	}
	
	public void release() {}
	
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

}
