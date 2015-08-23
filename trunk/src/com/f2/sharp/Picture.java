package com.f2.sharp;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.f2.socket.MyByteArrayOutputStream;
import com.f2.tool.DrawTool;
import com.f2.tool.GeomTool;
import com.f2.tool.PaintBrush;
import com.f2.tool.PointF;
import com.f2.tool.constant.MenuAction;
import com.f2.tool.constant.SharpType;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;

public class Picture extends AbsMenuSharp {

	PointF s = new PointF();
	PointF e = new PointF();
	BufferedImage image = null;
	
	public Picture(PaintBrush brush) {
		super(brush);
		resetMoreMenuActions();
	}

	public void setStartPoint(PointF p)	{
		s = p;
		setBounds(s, e);
	}

	public void setEndPoint(PointF p)	{
		e = p;
		setBounds(s, e);
	}

	public void setFilepath(String filepath) {
		try {
			image = ImageIO.read(new File(filepath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Graphics g) {
//		super.paint(g);
		DrawTool.drawPicture(g, getBounds(), getImage(), getPaintBrush());
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	private BufferedImage getImage() {
		return image;
	}

	@Override
	public boolean isInSharp(PointF p) {
		return GeomTool.isInBounds(getBounds(), p);
	}
	 	
	@Override
	public void rotate(PointF basic, double arc) {
//		TODO
	}
	
	@Override
	public void move(double dx, double dy) {
		GeomTool.movePoint(dx, dy, s);
		GeomTool.movePoint(dx, dy, e);
		setBounds(s, e);
	}
	
	@Override
	public void zoom(PointF basic, double scale) {
		GeomTool.zoomLine(scale, basic, s);
		GeomTool.zoomLine(scale, basic, e);
		setBounds(s, e);
	}		

	@Override
	public void stretch(PointF basic, double sx, double sy) {
		GeomTool.stretchLine(sx, sy, basic, s);
		GeomTool.stretchLine(sx, sy, basic, e);
		setBounds(s, e);
	}
	
	@Override
	public int getSharpType() {
		return SharpType.PICTURE;
	}

	@Override
	public void setBytes(byte[] bs) throws IOException {
		ByteReader br = new ByteReader(bs);
		s = br.readPointF();	
		e = br.readPointF();
		byte[] imageContent = br.readBytes();
		if (imageContent != null && imageContent.length > 0) {
			image = ImageIO.read(new ByteArrayInputStream(imageContent));
		}
		// filepath = br.readUTF();
		setBounds(s, e);
	}

	@Override
	public byte[] getBytes() throws IOException {
		ByteWriter bw = new ByteWriter();
		bw.writePointF(s);
		bw.writePointF(e);
		/*bw.writeUTF(filepath);*/
		MyByteArrayOutputStream out = new MyByteArrayOutputStream();
		if (ImageIO.write(image, "JPEG", out)) {
			bw.writeBytes(out.getByteArray());
		}
		out.close();
		return bw.toByteArray();
	}
	
	@Override
	public Object clone() {
		Picture o = (Picture) super.clone();
		// TODO 
		return o;
	}
	
	private void resetMoreMenuActions() {
		mMoreMenuActions = new int[]{
				MenuAction.MENU_COPY, //"¸´ÖÆ"
				MenuAction.MENU_CLONE, //"¿ËÂ¡"
				MenuAction.MENU_CUT, //"¼ôÇÐ"
				MenuAction.MENU_DELETE, //"É¾³ý"
				MenuAction.MENU_MOVE_UP, //"ÉÏÒÆ"
				MenuAction.MENU_MOVE_DOWN, //"ÏÂÒÆ"
				MenuAction.MENU_MOVE_UP_TO_FIRST, //"ÖÃ¶¥"
				MenuAction.MENU_MOVE_DOWN_TO_LAST, //"ÖÃµ×"
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
				};
	}
}
