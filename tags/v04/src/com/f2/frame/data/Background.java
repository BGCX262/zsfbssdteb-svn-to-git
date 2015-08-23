package com.f2.frame.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;
import com.f2.tool.file.IFbsData;

public class Background implements IFbsData {
	public class BgType { //背景类型,可叠加
		public static final int NONE = 0x00; //无背景(白色)
		public static final int COLOR = 0x01; //纯色
		public static final int PICTURE = 0x02; //图片,拉伸
		public static final int MARBLE = 0x04; //纹理,平铺
	}
	
	private int bgType;	
	private Color color;
	private String pictureImageFilePath;
	private BufferedImage pictureImage = null;
	private String marbleImageFilePath;
	private BufferedImage marbleImage = null;
	
	public Background() {
		bgType = BgType.NONE;
		color = Color.YELLOW;
		pictureImageFilePath = null;
		marbleImageFilePath = null;
	}
	
	public int getBgType() {
		return bgType;
	}

	public void setBgNone() {
		this.bgType = BgType.NONE;
	}
	
	public void setBgType(int type, boolean mask) {
		if(type == BgType.NONE || type == BgType.PICTURE) {
			pictureImage = null;
		}
		if(type == BgType.NONE || type == BgType.MARBLE) {
			marbleImage = null;
		}
		if(mask) {
			this.bgType = this.bgType | type;
		} else {
			this.bgType = this.bgType & (~type);
		}
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	
	public String getPictureImageFilePath() {
		return pictureImageFilePath;
	}

	public void setPictureImageFilePath(String filepath) {
		if(filepath != null && !filepath.equals(this.pictureImageFilePath)) {
			pictureImage = null;
		}
		this.pictureImageFilePath = filepath;
	}

	public String getMarbleImageFilePath() {
		return marbleImageFilePath;
	}

	public void setMarbleImageFilePath(String filepath) {
		if(filepath != null && !filepath.equals(this.marbleImageFilePath)) {
			marbleImage = null;
		}
		this.marbleImageFilePath = filepath;
	}

	public void draw(Graphics g, int x, int y, int width, int height) {
		
		if((bgType & BgType.COLOR) != 0) {
			g.setColor(color); //背景颜色
			g.fillRect(x, y, width, height);			
		} else {
			g.setColor(Color.WHITE); //白色
			g.fillRect(x, y, width, height);			
		}

		if((bgType & BgType.PICTURE) != 0) {
			if(pictureImage == null && pictureImageFilePath != null) {
				initPictureBgImage();
			}
			if(pictureImage != null) {
				g.drawImage(pictureImage, x, y, width, height, null);
			}
		}
		
		if((bgType & BgType.MARBLE) != 0) {
			if(marbleImage == null && marbleImageFilePath != null) {
				initMarbleBgImage(x, y, width, height);
			}
			if(marbleImage != null) {
				g.drawImage(marbleImage, x, y, width, height, null);
			}
		}
	}

	private void initPictureBgImage() {
		try {
			pictureImage = ImageIO.read(new File(pictureImageFilePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initMarbleBgImage(int x, int y, int width, int height) {
		try {
			marbleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g1 = marbleImage.getGraphics();
			BufferedImage img = ImageIO.read(new File(marbleImageFilePath));
			if(img != null) {
				int w = img.getWidth();
				int h = img.getHeight();
				for(int i = 0; i <= width / w; i++) {
					for(int j = 0; j <= height / h; j++) {
						g1.drawImage(img, x + i * w, y + j * h, w, h, null);
					}
				}
			}
			g1.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initFromBytes(byte[] bs) {
		try {
			ByteReader br = new ByteReader(bs);
			this.bgType = br.readInt();
			this.color = new Color(br.readInt());
			this.pictureImageFilePath = br.readUTF();
			this.marbleImageFilePath = br.readUTF();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] toBytes() {
		try {
			ByteWriter bw = new ByteWriter();
			bw.writeInt(bgType);
			bw.writeInt(color.getRGB());
			bw.writeUTF(pictureImageFilePath);
			bw.writeUTF(marbleImageFilePath);
			return bw.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
