package com.f2.sharp;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.f2.frame.OleObjectManagerThread;
import com.f2.socket.MyByteArrayOutputStream;
import com.f2.tool.DrawTool;
import com.f2.tool.GeomTool;
import com.f2.tool.PaintBrush;
import com.f2.tool.PointF;
import com.f2.tool.constant.MenuAction;
import com.f2.tool.constant.SharpType;
import com.f2.tool.file.ByteReader;
import com.f2.tool.file.ByteWriter;

public class OleEmbed extends AbsMenuSharp {
	
	public static final int ACTION_NONE = 0;
	public static final int ACTION_RELEASE = 1;
	public static final int ACTION_RESET_BOUND = 2;
	public static final int ACTION_SHOW = 3;
	public static final int ACTION_HIDE = 4;

	private Shell myShell;
	private String filepath;
	private OleClientSite oleSite;
	private OleFrame oleFrame;
	
	private BufferedImage olePreviewImage;
	
	private OleObjectManagerThread oleObjectManagerThread;

	private int action = ACTION_NONE;
	private boolean isReleased = false;
	
	private static final int BOUND_EXT_WIDTH = 10; // FIXME 实际的bound.width和bound.height可能不足2*BOUND_EXT_WIDTH

	private PointF s = new PointF();
	private PointF e = new PointF();

	public OleEmbed(PaintBrush brush) {
		super(brush);
		resetMoreMenuActions();
	}

	public void setOleObjectManagerThread(OleObjectManagerThread thread) {
		oleObjectManagerThread = thread;
	}
	
	public void setStartPoint(PointF p)	{
		s = p;
		setBounds(s, e);
	}

	public void setEndPoint(PointF p)	{
		e = p;
		setBounds(s, e);
	}

	public Shell getShell() {
		return myShell;
	}
	
	public Shell openShell(Display display, Canvas canvas) {
		if (myShell != null) {
			myShell.dispose();
		}
		
		myShell = SWT_AWT.new_Shell(display, canvas);
		myShell.setBounds(mBounds.x + BOUND_EXT_WIDTH, mBounds.y + BOUND_EXT_WIDTH, mBounds.width - 2*BOUND_EXT_WIDTH, mBounds.height - 2*BOUND_EXT_WIDTH);
		
		try {
			FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			myShell.setLayout(thisLayout);
			oleFrame = new OleFrame(myShell, SWT.NONE);
			// shell.setSize(229, 54);
			openFilepath(filepath);
			myShell.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		myShell.open();
		
		return myShell;
	}
		
	public void release() {
		action = ACTION_RELEASE;
	}
	
	public void show() {
		action = ACTION_SHOW;
	}
	
	public void hide() {
		action = ACTION_HIDE;
	}
	
	public void doRelease() {
		if(isReleased) {
			return;
		}
		
		if (oleSite != null) {
			oleSite.dispose();
			oleSite = null;
		}

		if (oleFrame != null) {
			oleFrame.dispose();
			oleFrame = null;
		}
		
		if (myShell != null) {
			myShell.close();
			myShell = null;
		}
		
		isReleased = true;
	}

	private void doShow() {
		if(isReleased) {
			return;
		}
		
		if (oleSite != null) {
			oleSite.setVisible(true);
		}

		if (oleFrame != null) {
			oleFrame.setVisible(true);
		}
		
		if (myShell != null) {
			myShell.setVisible(true);
		}
	}

	private void doHide() {
		if(isReleased) {
			return;
		}
		
		if (oleSite != null) {
			oleSite.setVisible(false);
		}

		if (oleFrame != null) {
			oleFrame.setVisible(false);
		}
		
		if (myShell != null) {
			myShell.setVisible(false);
		}
	}

	public void openFilepath(String filepath) {
		this.filepath = filepath;
		if (oleFrame != null) {
			if (oleSite != null) {
				oleSite.dispose();
			}
			try {
				File file = new File(filepath);// 创建文档的File对象
				oleSite = new org.eclipse.swt.ole.win32.OleClientSite(oleFrame, org.eclipse.swt.SWT.NONE, file);
				oleSite.setBounds(0, 0, mBounds.width, mBounds.height);
				oleSite.doVerb(org.eclipse.swt.ole.win32.OLE.OLEIVERB_SHOW);
			} catch (org.eclipse.swt.SWTException e) {
				String str = "Create OleClientSite Error" + e.toString();
				System.out.println(str);
			}
		}
	}

	@Override
	public void setBytes(byte[] bs) throws IOException {
		ByteReader br = new ByteReader(bs);
		s = br.readPointF();	
		e = br.readPointF();
		byte[] imageContent = br.readBytes();
		if (imageContent != null && imageContent.length > 0) {
			olePreviewImage = ImageIO.read(new ByteArrayInputStream(imageContent));
		}
		filepath = br.readUTF();
		openFilepath(filepath);
	}


	@Override
	public byte[] getBytes() throws IOException {
		ByteWriter bw = new ByteWriter();
		bw.writePointF(s);
		bw.writePointF(e);
		MyByteArrayOutputStream out = new MyByteArrayOutputStream();
		if (ImageIO.write(getOlePreviewImage(), "JPEG", out)) {
			bw.writeBytes(out.getByteArray());
		}
		out.close();
		bw.writeUTF(filepath);
		return bw.toByteArray();
	}
	
	public BufferedImage getOlePreviewImage() {
		if (myShell != null && oleObjectManagerThread != null) {
			olePreviewImage = null;
			oleObjectManagerThread.addOleEmbed(this);
			synchronized (this) {
				while (olePreviewImage == null) {
					try {
						wait();
					} catch (Exception e) {
					}
				}
			}
		}
		return olePreviewImage;
	}
	
	public final void buildOlePreviewImage() {
		GC gc = new GC(myShell);
		Rectangle rectangle = myShell.getBounds();
		Image image = new Image(myShell.getDisplay(), rectangle.width, rectangle.height);    
	    gc.copyArea(image, 0, 0);
	    synchronized (this) {
	    	olePreviewImage = convertToAWT(image.getImageData());
		    notifyAll();
		}
	}

	@Override
	public void draw(Graphics g) {
		if (myShell == null) {
			if (olePreviewImage != null) {
				DrawTool.drawPicture(g, getBounds(), olePreviewImage, getPaintBrush());
			} else {
				g.setColor(Color.YELLOW);
				g.fillRect(mBounds.x, mBounds.y, mBounds.width, mBounds.height);
			}
		} else {
			g.setColor(Color.YELLOW);
			for(int i = 0; i < BOUND_EXT_WIDTH; i++) {
				g.drawRect(mBounds.x + i, mBounds.y + i, mBounds.width - 2*i - 1, mBounds.height - 2*i - 1);
			}
//			g.fillRect(mBounds.x, mBounds.y, mBounds.width, mBounds.height);
//			g.setColor(Color.ORANGE);
//			g.setFont(g.getFont().deriveFont(mBounds.height/2.0f));
//			g.drawString(getFilepathExtension(), mBounds.x + BOUND_EXT_WIDTH*2, mBounds.y + BOUND_EXT_WIDTH*2 + mBounds.height*5/8);
		}
	}

//	private String getFilepathExtension() {
//		int pos = filepath.lastIndexOf('.');
//		if(pos >= 0) {
//			return filepath.substring(pos + 1);
//		}
//		return "";
//	}

	@Override
	public void rotate(PointF basic, double arc) {
//		TODO
	}

	public void resetOleBounds(PointF s, PointF e) {
		super.setBounds(s, e);
		action = ACTION_RESET_BOUND;		
	}
	
	public void doResetBound() {
		if(myShell != null && !myShell.isDisposed()) {
			myShell.setBounds(mBounds.x + BOUND_EXT_WIDTH, mBounds.y + BOUND_EXT_WIDTH, mBounds.width - 2*BOUND_EXT_WIDTH, mBounds.height - 2*BOUND_EXT_WIDTH);
		}
	}

	@Override
	public void move(double dx, double dy) {
		GeomTool.movePoint(dx, dy, s);
		GeomTool.movePoint(dx, dy, e);
		resetOleBounds(s, e);
	}
	
	@Override
	public void zoom(PointF basic, double scale) {
		GeomTool.zoomLine(scale, basic, s);
		GeomTool.zoomLine(scale, basic, e);
		resetOleBounds(s, e);
	}

	@Override
	public void stretch(PointF basic, double sx, double sy) {
		GeomTool.stretchLine(sx, sy, basic, s);
		GeomTool.stretchLine(sx, sy, basic, e);
		resetOleBounds(s, e);
	}

	@Override
	public boolean isInSharp(PointF p) {
		return GeomTool.isInBounds(getBounds(), p);
	}


	@Override
	public int getSharpType() {
		return SharpType.OLE_EMBED;
	}

	public int getOleAction() {
		return action;
	}

	public void doOleAction() {
		try {
			switch(action) {
			case ACTION_RELEASE:
				doRelease();
				break;
			case ACTION_RESET_BOUND:
				doResetBound();
				break;
			case ACTION_SHOW:
				doShow();
				break;
			case ACTION_HIDE:
				doHide();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		action = ACTION_NONE;
		
		if (olePreviewImage == null) {
			buildOlePreviewImage();
		}
	}

	public boolean isReleased() {
		return isReleased;
	}
	
	public static BufferedImage convertToAWT(ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					pixelArray[0] = rgb.red;
					pixelArray[1] = rgb.green;
					pixelArray[2] = rgb.blue;
					raster.setPixels(x, y, 1, 1, pixelArray);
				}
			}
			return bufferedImage;
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte) rgb.red;
				green[i] = (byte) rgb.green;
				blue[i] = (byte) rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
			}
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}
	
	private void resetMoreMenuActions() {
		mMoreMenuActions = new int[]{
				MenuAction.MENU_DELETE, //"删除"
				};
		mMoreMenuIcons = new BufferedImage[]{
				imgDelete,
				};
	}
}
