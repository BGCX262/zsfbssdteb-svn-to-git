package com.f2.tool;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class ImageTool {
	/**
	 * 获取屏幕截图
	 * @return
	 */
	public static BufferedImage getScreenShot() {
		try {
			Robot rbt = new Robot();
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension dim = tk.getScreenSize();
			Rectangle rect = new Rectangle(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
			BufferedImage image = rbt.createScreenCapture(rect);
			return image;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取image资源创建Image
	 * @param filepath 形如/res/images/btn/sharp_more.png
	 * @return
	 */
	public static BufferedImage getResImage(String filepath) {
		if(filepath == null || filepath.length() == 0) {
			return null;
		}
		try {
			InputStream is = "AAA".getClass().getResourceAsStream(filepath);
			return ImageIO.read(is);
		} catch (Exception ex) {
			System.out.println("getResourceImage error, filepath=" + filepath + ", ex=" + ex.toString());
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取image资源创建ImageIcon
	 * @param filepath 形如/res/images/btn/sharp_more.png
	 * @return
	 */
	public static ImageIcon getResImageIcon(String filepath) {
		BufferedImage image = getResImage(filepath);
		if(image == null) {
			return null;
		}
		return new ImageIcon(image);
	}
	
	/**
	 * 获取Frame截图
	 * @param frame
	 * @return
	 */
	public static BufferedImage getFrameImage(JFrame frame) {
		try {
			Rectangle rect = frame.getBounds();
			BufferedImage image = (BufferedImage) frame.createImage(rect.width, rect.height);
			Graphics g = image.getGraphics();
			frame.paint(g);
			g.dispose();
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
