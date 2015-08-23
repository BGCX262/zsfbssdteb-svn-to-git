package com.f2.tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import com.f2.tool.constant.MenuAction;
import com.f2.tool.constant.SharpType;
import com.f2.tool.constant.StrokeDash;
import com.f2.tool.constant.StrokeWidth;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;
import com.f2.tool.file.IFbsData;

public class PaintBrush implements IFbsData {
	
	private int type = SharpType.HANDWRITING;
	private Color color = Color.RED;
	private int strokeLineWidth = StrokeWidth.W12;
	private int strokeEndCap = BasicStroke.CAP_ROUND;
	private int strokeLineJoin = BasicStroke.JOIN_ROUND;
	private float[] strokeDash = StrokeDash.D0; //ÊµÏß
	private int action = MenuAction.NONE;

	private BasicStroke stroke = createNewBasicStroke();
	
	public PaintBrush() {
		// use default paint brush
	}
	
	public PaintBrush(PaintBrush brush) {
		setPaintBrush(brush);
	}
	
	public void setPaintBrush(PaintBrush brush) {
		if(brush != null) {
			this.type = brush.type;
			this.color = new Color(brush.color.getRGB());
			setStrokeLineWidth(brush.strokeLineWidth);
			setStrokeEndCap(brush.strokeEndCap);
			setStrokeLineJoin(brush.strokeLineJoin);
			setStrokeDash(brush.strokeDash);
			this.action = brush.action;		
		}
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setStrokeLineWidth(int width) {
		if(strokeLineWidth != width) {
			strokeLineWidth = width;
			createNewBasicStroke();
		}
	}
	
	public int getStrokeLineWidth() {
		return strokeLineWidth;
	}

	public void setStrokeEndCap(int cap) {
		if(strokeEndCap != cap) {
			strokeEndCap = cap;
			createNewBasicStroke();
		}
	}
	
	public int getStrokeEndCap() {
		return strokeEndCap;
	}

	public void setStrokeLineJoin(int join) {
		if(strokeLineJoin != join) {
			strokeLineJoin = join;
			createNewBasicStroke();
		}
	}

	public int getStrokeLineJoin() {
		return strokeLineJoin;
	}

	public float[] getStrokeDash() {
		return strokeDash;
	}

	public void setStrokeDash(float[] dash) {		
		if(strokeDash != dash) {
			strokeDash = dash;
			createNewBasicStroke();
		}
	}

	private BasicStroke createNewBasicStroke() {
		stroke = new BasicStroke(strokeLineWidth, strokeEndCap, strokeLineJoin, 10.0f, strokeDash, 0.0f);
		return stroke;
	}

	public void setAction(int action) {
		this.action = action;
	}
	
	public int getAction() {
		return action;
	}

	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("[PaintBrush:");
		sb.append("type=").append(type);
		sb.append(",color=").append(color);
		sb.append(",strokeLineWidth=").append(strokeLineWidth);
		sb.append(",strokeEndCap=").append(strokeEndCap);
		sb.append(",strokeLineJoin=").append(strokeLineJoin);
		sb.append(",strokeDash=").append(strokeDash);
		sb.append(",action=").append(action);
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public void initFromBytes(byte[] bs) {
		try {
			ByteReader br = new ByteReader(bs);
			this.type = br.readInt();
			this.color = new Color(br.readInt());
			this.strokeLineWidth = br.readInt();
			this.strokeEndCap = br.readInt();
			this.strokeLineJoin = br.readInt();
			// TODO ¶Ádash
			this.action = br.readInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] toBytes() {
		try {
			ByteWriter bw = new ByteWriter();
			bw.writeInt(type);
			bw.writeInt(color.getRGB());
			bw.writeInt(strokeLineWidth);
			bw.writeInt(strokeEndCap);
			bw.writeInt(strokeLineJoin);
			bw.writeInt(action);
			// TODO Ð´dash
			return bw.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
