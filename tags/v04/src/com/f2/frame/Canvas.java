package com.f2.frame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.f2.frame.data.Background;
import com.f2.frame.data.CanvasItem;
import com.f2.frame.selector.StrokeLineSelector;
import com.f2.listener.CanvasRepaintListener;
import com.f2.listener.CanvasResetListener;
import com.f2.listener.WanderListener;
import com.f2.listener.ZoomListener;
import com.f2.sharp.AbsMenuSharp;
import com.f2.sharp.Handwriting;
import com.f2.sharp.MSPPTDocument;
import com.f2.sharp.PagablePicture;
import com.f2.sharp.Picture;
import com.f2.sharp.geometry.Circle;
import com.f2.sharp.geometry.Ellipse;
import com.f2.sharp.geometry.Line;
import com.f2.sharp.geometry.Polygon;
import com.f2.sharp.geometry.Polyline;
import com.f2.tool.GeomTool;
import com.f2.tool.PaintBrush;
import com.f2.tool.PointF;
import com.f2.tool.constant.MenuAction;
import com.f2.tool.constant.SharpType;

@SuppressWarnings("serial")
public class Canvas extends JFrame implements 
	WanderListener, ZoomListener, 
	MouseListener, MouseMotionListener, ComponentListener {

	private CanvasRepaintListener repaintListener;
	private CanvasResetListener resetListener;

	private PaintBrush mPaintBrush = null;
	private String mFilePath = null;
	private boolean isSharpSelected = false;
	
	private AbsMenuSharp mCurSharp = null;
	private List<AbsMenuSharp> mSharpList = new LinkedList<AbsMenuSharp>();
	private MenuAction mMenuAction = new MenuAction();
	private Background mBackground = new Background();
	private int zoomValue = Wander.SLIDER_ZOOM_MID;
		
	private BufferedImage bgTempImage;
	private ImageObserver bgImageObserver;

	private StrokeLineSelector strokeLineSelector = null;
	
	private SharpProxy sharpProxy = new SharpProxy();
	
	//private int wanderX;
	//private int wanderY;
	
	/**
	 * Create the frame.
	 */
	public Canvas(PaintBrush brush) {
		super();
		this.mPaintBrush = brush;
		setUndecorated(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(null);
		contentPane.setLayout(null);
		setContentPane(contentPane);
				
		contentPane.addMouseListener(this);
		contentPane.addMouseMotionListener(this);
	}
	
	public void initBufferImage() {
		if(bgTempImage == null) {
			bgTempImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		}		
	}
	
	@Override
	public void paint(Graphics g) {
		//System.out.println("mCanvas.paint");
		//super.paint(g);
		
		if(bgTempImage == null) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			return;
		}

		//画背景
		Graphics g1 = bgTempImage.getGraphics();
		mBackground.draw(g1, 0, 0, getWidth(), getHeight());

		//画图形
		for(int i = mSharpList.size() - 1; i >= 0; i--) {
			mSharpList.get(i).draw(g1);
		}
		
		//画菜单
		for(int i = mSharpList.size() - 1; i >= 0; i--) {
			AbsMenuSharp sharp = mSharpList.get(i);
			sharp.drawMenu(g1);
		}
		
		g1.dispose();		
	
		//映射临时bgImage到实际画布
		g.drawImage(bgTempImage, 0, 0, this.getWidth(), this.getHeight(), bgImageObserver);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
//		System.out.println("mouseClicked, p = " + p);
//		System.out.println("e.getClickCount()=" + e.getClickCount());
		
		boolean hasAction = false;
		
		switch(mPaintBrush.getType()) {
		case SharpType.SELECTION:
			hasAction = checkAndDoMenuAction(p);
			break;
		default:
			if(e.getClickCount() == 1) {
				hasAction = sharpProxy.mouseClicked(mPaintBrush.getType(), p);
			} else {
				hasAction = sharpProxy.mouseMultiClicked(mPaintBrush.getType(), p, e.getClickCount());
			}
			break;
		}					
		
		if(!hasAction && mCurSharp != null){
			cancelSelected();
		}
		
		repaintCanvas();
	}

	private boolean checkAndDoMenuAction(PointF p) {
//		boolean isInSelectArea = false;
		boolean hasAction = false;
		//检测是否有Sharp的MoreMenu菜单被选中,并操作MoreMenu
		if(!hasAction) {
			for(AbsMenuSharp sharp : mSharpList) {
				if(sharp.isSelected() && sharp.isShowMoreMenu()) {
					hasAction = doMoreMenuAction(sharp, p);
//					isInSelectArea = true;
					break;					
				}
			}
		}
		//检测是否有Sharp的More菜单被选中,并显示More菜单
		if(!hasAction) {
			for(AbsMenuSharp sharp : mSharpList) {
				if(sharp.isSelected() && sharp.isInMoreMenuBounds(p)) {
					setShowMoreMenu(sharp);
//					isInSelectArea = true;
					hasAction = true;
					break;					
				}
			}
		}
		//检测是否有Sharp被选中,并显示四角菜单
		if(!hasAction) {
			for(AbsMenuSharp sharp : mSharpList) {
				if(sharp.isInSharp(p)) {
					if (!sharp.onClick(p)) {
						setSharpSelected(sharp);
						// isInSelectArea = true;
					}
					hasAction = true;
					break;
				}
			}
		}
		//取消其他Sharp的选中状态
		for(AbsMenuSharp sharp : mSharpList) {
			if(sharp != mCurSharp) {
				sharp.setSelected(false);
				sharp.setShowMoreMenu(false);
			}
		}
		return hasAction;
	}

	private boolean doMoreMenuAction(AbsMenuSharp sharp, PointF p) {
		int action = sharp.getMoreMenuAction(p);
//		System.out.println("doMoreMenuAction, action = " + action);
		switch(action) {
		case MenuAction.MENU_DELETE:
			sharpDelete(sharp);
			return true;
		case MenuAction.MENU_MOVE_UP:
			sharpUp(sharp);
			return true;
		case MenuAction.MENU_MOVE_DOWN:
			sharpDown(sharp);
			return true;
		case MenuAction.MENU_MOVE_UP_TO_FIRST:
			sharpUpToFirst(sharp);
			return true;
		case MenuAction.MENU_MOVE_DOWN_TO_LAST:
			sharpDownToLast(sharp);
			return true;
		case MenuAction.MENU_BRUSH_COLOR:
			Color c = JColorChooser.showDialog(Canvas.this, "选择画笔颜色", sharp.getPaintBrush().getColor());
			if(c != null) {
				sharp.getPaintBrush().setColor(c);
			}
			return true;
		case MenuAction.MENU_BRUSH_WIDTH:
			if(strokeLineSelector == null) {
				strokeLineSelector = new StrokeLineSelector(this);
			}
			strokeLineSelector.setPaintBrush(sharp.getPaintBrush());
			strokeLineSelector.setLocation((int)p.x - strokeLineSelector.getWidth() - 2, (int)p.y + 2);
			strokeLineSelector.setVisible(true);
			return true;
		case MenuAction.MENU_PROPERTY:
			// 
			return true;
		}		
		return false;
	}

	public boolean doAction(AbsMenuSharp sharp) {
		int action = sharp.getPaintBrush().getAction();
		PointF basic;
		switch(action) {
		case MenuAction.ROTATE:
			basic = sharp.getRotateBasicPoint();
			sharp.rotate(basic, mMenuAction.getRotateArc(basic));
			return true;
		case MenuAction.MOVE:
			sharp.move(mMenuAction.getMoveDx(), mMenuAction.getMoveDy());
			return true;
		case MenuAction.STRETCH:
			basic = sharp.getStretchBasicPoint();
			double scale = mMenuAction.getStretchScale(basic);
			sharp.stretch(basic, scale, scale);
			return true;
		case MenuAction.STRETCH_X:
			basic = sharp.getStretchBasicPoint();
			double sx = mMenuAction.getStretchXScale(basic);
			sharp.stretch(basic, sx, 0);
			return true;
		case MenuAction.STRETCH_Y:
			basic = sharp.getStretchBasicPoint();
			double sy = mMenuAction.getStretchYScale(basic);
			sharp.stretch(basic, 0, sy);
			return true;
		}		
		return false;
	}

	private void setSharpSelected(AbsMenuSharp sharp) {
		mCurSharp = sharp;
		mCurSharp.setSelected(true);
		mCurSharp.getPaintBrush().setAction(MenuAction.NONE);
		isSharpSelected = true;
	}
	
	private void setShowMoreMenu(AbsMenuSharp sharp) {
		mCurSharp = sharp;
		mCurSharp.setShowMoreMenu(true);
		mCurSharp.getPaintBrush().setAction(MenuAction.NONE);
		isSharpSelected = true;
	}
	
	private void cancelSelected() {
		mCurSharp.setSelected(false);
		mCurSharp.setShowMoreMenu(false);
		mCurSharp.getPaintBrush().setAction(MenuAction.NONE);
		mCurSharp = null;
		isSharpSelected = false;
		if(strokeLineSelector != null) {
			strokeLineSelector.setVisible(false);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
//		Point p = e.getPoint();
//		System.out.println("mouseEntered, p = " + p);
	}

	@Override
	public void mouseExited(MouseEvent e) {
//		Point p = e.getPoint();
//		System.out.println("mouseExited, p = " + p);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
//		System.out.println("mousePressed, p = " + p);
				
		if(!isSharpSelected) { 
			switch(mPaintBrush.getType()) {
			default:
				sharpProxy.mousePressed(mPaintBrush.getType(), p);
				break;
			}
		}

		if(isSharpSelected && mCurSharp != null) {
			int action = mCurSharp.getBasicMenuAction(p);
			if(action != MenuAction.NONE) {
				mMenuAction.setActionStartPoint(p);
				mCurSharp.getPaintBrush().setAction(action);
			}
		}

		repaintCanvas();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
//		System.out.println("mouseReleased, p = " + p);
		
		if(!isSharpSelected && mPaintBrush.getAction() == MenuAction.NONE) {
			switch(mPaintBrush.getType()) {
			default:
				sharpProxy.mouseReleased(mPaintBrush.getType(), p);
				break;
			}
		}
		
		if(isSharpSelected && mCurSharp != null) {
			mCurSharp.getPaintBrush().setAction(MenuAction.NONE);
		}
		
		repaintCanvas();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
//		System.out.println("mouseDragged, p = " + p);
		
		if(!isSharpSelected && mPaintBrush.getAction() == MenuAction.NONE) {
			switch(mPaintBrush.getType()) {
			default:
				sharpProxy.mouseDragged(mPaintBrush.getType(), p);
				break;
			}
		}

		if(isSharpSelected && mCurSharp != null) {
			mMenuAction.setActionEndPoint(p);
			doAction(mCurSharp);
			mMenuAction.setActionStartPoint(p);
		}
		
		repaintCanvas();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
//		System.out.println("mouseReleased, p = " + p);
		
		if(!isSharpSelected && mPaintBrush.getAction() == MenuAction.NONE) {
			switch(mPaintBrush.getType()) {
			default:
				boolean rp = sharpProxy.mouseMoved(mPaintBrush.getType(), p);
				if(rp) {
					repaintCanvas();
				}
				break;
			}
		}
	}
	
	private void addSharp(AbsMenuSharp sharp) {
//		System.out.println("addSharp, sharp=" + sharp);
		if(mSharpList == null) {
			return;
		}
//		sharp.setSize(getWidth(), getHeight());
//		sharp.setLocation(0, 0);
		mSharpList.add(0, sharp);
		mCurSharp = sharp;
		//contentPane.add(sharp, 0);
	}

	public void clearSharps() {
		if(mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		mSharpList.clear();
		//contentPane.removeAll();
		repaintCanvas();
	}
	
	public void removeLastSharp() {
		if(mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		AbsMenuSharp sharp = mSharpList.remove(0);
		//contentPane.remove(0);
		if(mSharpList.size() == 0) {
			mCurSharp = null;
		} else {
			mCurSharp = mSharpList.get(0);
		}		
		isSharpSelected = false;
		if (sharp != null) {
			sharp.release();
		}
		repaintCanvas();
	}
	
	private void sharpDelete(AbsMenuSharp sharp) {
		if(mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		mSharpList.remove(sharp);
		//contentPane.remove(sharp);
		if(mSharpList.size() == 0) {
			mCurSharp = null;
		} else {
			mCurSharp = mSharpList.get(0);
		}		
		isSharpSelected = false;
		if (sharp != null) {
			sharp.release();
		}
		repaintCanvas();
	}
	
	/**
	 * Sharp下移一层
	 * index=0是最后添加的,也就是最上层的一个
	 * @param sharp
	 */
	private void sharpDown(AbsMenuSharp sharp) {
		if(mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		int index = mSharpList.indexOf(sharp);
		if(index == -1 || index == mSharpList.size() - 1) {
			return;
		}
		AbsMenuSharp temp = mSharpList.get(index + 1);
		mSharpList.set(index + 1, sharp);
		mSharpList.set(index, temp);
		repaintCanvas();
	}

	/**
	 * Sharp上移一层
	 * index=0是最后添加的,也就是最上层的一个
	 * @param sharp
	 */
	private void sharpUp(AbsMenuSharp sharp) {
		if(mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		int index = mSharpList.indexOf(sharp);
		if(index == -1 || index == 0) {
			return;
		}
		AbsMenuSharp temp = mSharpList.get(index - 1);
		mSharpList.set(index - 1, sharp);
		mSharpList.set(index, temp);
		repaintCanvas();
	}

	/**
	 * Sharp下移到底层
	 * index=0是最后添加的,也就是最上层的一个
	 * @param sharp
	 */
	private void sharpDownToLast(AbsMenuSharp sharp) {
		if(mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		int index = mSharpList.indexOf(sharp);
		if(index == -1 || index == mSharpList.size() - 1) {
			return;
		}
		AbsMenuSharp temp = mSharpList.remove(index);
		mSharpList.add(temp);
		repaintCanvas();
	}

	/**
	 * Sharp上移到顶层
	 * index=0是最后添加的,也就是最上层的一个
	 * @param sharp
	 */
	private void sharpUpToFirst(AbsMenuSharp sharp) {
		if(mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		int index = mSharpList.indexOf(sharp);
		if(index == -1 || index == 0) {
			return;
		}
		AbsMenuSharp temp = mSharpList.remove(index);
		mSharpList.add(0, temp);
		repaintCanvas();
	}

	@Override
	public void componentHidden(ComponentEvent event) {
//		System.out.println("componentHidden, event = " + event);
		repaint();
	}

	@Override
	public void componentMoved(ComponentEvent event) {
//		System.out.println("componentMoved, event = " + event);
		repaint();
	}

	@Override
	public void componentResized(ComponentEvent event) {
//		System.out.println("componentResized, event = " + event);
		repaint();
	}

	@Override
	public void componentShown(ComponentEvent event) {
//		System.out.println("componentShown, event = " + event);
		repaint();
	}

	public CanvasItem getCanvasItem() {
		CanvasItem canvasItem = new CanvasItem();
		canvasItem.setSharps(mSharpList);
		canvasItem.setBackground(mBackground);
		canvasItem.setZoomValue(zoomValue);
		return canvasItem;
	}

	public void setCanvasItem(CanvasItem canvasItem) {
		this.mSharpList = canvasItem.getSharps();
		this.mBackground = canvasItem.getBackground();
		this.zoomValue = canvasItem.getZoomValue();
		if(resetListener != null) {
			resetListener.resetCanvasItem();
		}
		repaintCanvas();		
	}	

	private void repaintCanvas() {
		repaint();
		if(repaintListener != null) {
			repaintListener.repaintCanvasPanel();
		}
	}

	public void setCanvasRepaintListener(CanvasRepaintListener repaintListener) {
		this.repaintListener = repaintListener;
	}
	
	public void setCanvasResetListener(CanvasResetListener resetListener) {
		this.resetListener = resetListener;
	}
	
	public String getFilePath() {
		return mFilePath;
	}

	public void setFilePath(String filepath) {
		this.mFilePath = filepath;
	}
	
	public class SharpProxy {

		public boolean mouseClicked(int type, PointF p) {
//			System.out.println("mouseClicked,type=" + type + ",p=" + p);
			switch(type) {
			case SharpType.POLYLINE:
				addPolyline(p);
				return true;
			}				
			return false;
		}

		public boolean mouseMultiClicked(int type, PointF p, int clickCount) {
//			System.out.println("mouseMultiClicked,type=" + type + ",p=" + p + ",c=" + clickCount);
			switch(type) {
			case SharpType.POLYLINE:
				endPolyline(p);
				return false;
			}				
			return false;
		}
		
		public boolean mouseMoved(int type, PointF p) {
//			System.out.println("mouseMoved,type=" + type + ",p=" + p + ",(mCurSharp == null)=" + (mCurSharp == null));
			if(mCurSharp == null) {
				return false;
			}
			switch(type) {
			case SharpType.POLYLINE:
				setPolylineLastPoint(p);
				return true;
			}				
			return false;
		}

		public void mousePressed(int type, PointF p) {
			switch(type) {
			case SharpType.HANDWRITING:
				addHandwriting(p);
				break;
			case SharpType.LINE:
				addLine(p);
				break;
			case SharpType.TRIANGLE_RIGHT:
				addRightTriangle(p);
				break;
			case SharpType.TRIANGLE_EQUILATERAL:
				addEquilateralTriangle(p);
				break;
			case SharpType.RECTANGLE:
				addRectangle(p);
				break;
			case SharpType.SQUARE:
				addSquare(p);
				break;
			case SharpType.PENTAGON:
				addPentagon(p);
				break;
			case SharpType.HEXAGON:
				addHexagon(p);
				break;
			case SharpType.CIRCLE:
				addCircle(p);
				break;
			case SharpType.ELLIPSE:
				addEllipse(p);
				break;
			case SharpType.PICTURE:
				addPicture(p);
				break;
			}
		}
		
		public void mouseReleased(int type, PointF p) {
			if(mCurSharp == null) {
				return;
			}
			switch(type) {
			case SharpType.HANDWRITING:
				endHandwriting(p);
				break;
			case SharpType.LINE:
				endLine(p);
				break;
			case SharpType.TRIANGLE_RIGHT:
				endRightTriangle(p);
				break;
			case SharpType.TRIANGLE_EQUILATERAL:
				endEquilateralTriangle(p);
				break;
			case SharpType.RECTANGLE:
				endRectangle(p);
				break;
			case SharpType.SQUARE:
				endSquare(p);
				break;
			case SharpType.PENTAGON:
				endPentagon(p);
				break;
			case SharpType.HEXAGON:
				endHexagon(p);
				break;
			case SharpType.CIRCLE:
				endCircle(p);
				break;
			case SharpType.ELLIPSE:
				endEllipse(p);
				break;
			case SharpType.PICTURE:
				endPicture(p);
				break;
			}
		}
		
		public void mouseDragged(int type, PointF p) {
			if(mCurSharp == null) {
				return;
			}
			switch(type) {
			case SharpType.HANDWRITING:
				endHandwriting(p);
				break;
			case SharpType.LINE:
				endLine(p);
				break;
			case SharpType.TRIANGLE_RIGHT:
				endRightTriangle(p);
				break;
			case SharpType.TRIANGLE_EQUILATERAL:
				endEquilateralTriangle(p);
				break;
			case SharpType.RECTANGLE:
				endRectangle(p);
				break;
			case SharpType.SQUARE:
				endSquare(p);
				break;
			case SharpType.PENTAGON:
				endPentagon(p);
				break;
			case SharpType.HEXAGON:
				endHexagon(p);
				break;
			case SharpType.CIRCLE:
				endCircle(p);
				break;
			case SharpType.ELLIPSE:
				endEllipse(p);
				break;
			case SharpType.PICTURE:
				endPicture(p);
				break;
			}
		}
		
		/**
		 * 图片
		 * @param p
		 */
		private void addPicture(PointF p) {
			if (mFilePath == null || mFilePath.trim().length() == 0) {
				return;
			}
			Picture picture;
			String lowerCasePath = mFilePath.toLowerCase(Locale.US);
			if (lowerCasePath.endsWith(".ppt")) {
				picture = new MSPPTDocument(mPaintBrush);
			} else {
				picture = new Picture(mPaintBrush);
			}
			picture.setStartPoint(p);
			picture.setEndPoint(p);
			picture.setFilepath(mFilePath);
			addSharp(picture);
		}

		/**
		 * 直线
		 * @param p
		 */
		private void addLine(PointF p) {
			Line line = new Line(mPaintBrush);
			line.setStartPoint(p);
			line.setEndPoint(p);
			addSharp(line);
		}

		/**
		 * 圆形
		 * @param p
		 */
		private void addCircle(PointF p) {
			Circle circle = new Circle(mPaintBrush);
			circle.setRadius(0);
			circle.setCentre(p);
			addSharp(circle);
		}
		
		/**
		 * 椭圆
		 * @param p
		 */
		private void addEllipse(PointF p) {
			Ellipse ellipse = new Ellipse(mPaintBrush);
			ellipse.setAB(0, 0);
			ellipse.setCentre(p);
			addSharp(ellipse);
		}
		
		/**
		 * 手绘线
		 * @param p
		 */
		private void addHandwriting(PointF p) {
			Handwriting handwriting = new Handwriting(mPaintBrush);
			handwriting.addPoint(p);
			addSharp(handwriting);
		}

		/**
		 * 等腰直角三角形
		 * @param p
		 */
		private void addRightTriangle(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 3, p);
			addSharp(polygon);
		}

		/**
		 * 等边三角形
		 * @param p
		 */
		private void addEquilateralTriangle(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 3, p);
			addSharp(polygon);
		}
		
		/**
		 * 长方形
		 * @param p
		 */
		private void addRectangle(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 4, p);
			addSharp(polygon);
		}
		
		/**
		 * 正方形
		 * @param p
		 */
		private void addSquare(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 4, p);
			addSharp(polygon);
		}
		
		/**
		 * 五边形
		 * @param p
		 */
		private void addPentagon(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 5, p);
			addSharp(polygon);
		}
		
		/**
		 * 六边形
		 * @param p
		 */
		private void addHexagon(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 6, p);
			addSharp(polygon);
		}
		
		/**
		 * 手绘线
		 * @param p
		 */
		private void endHandwriting(PointF p) {
			Handwriting handwriting = (Handwriting) mCurSharp;
			handwriting.addPoint(p);
		}

		/**
		 * 直线
		 * @param p
		 */
		private void endLine(PointF p) {
			Line line = (Line) mCurSharp;
			line.setEndPoint(p);
		}

		/**
		 * 直角三角形
		 * @param p
		 */
		private void endRightTriangle(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			polygon.setPoint(1, new PointF(p.x, p0.y));
			polygon.setPoint(2, new PointF(p0.x, p.y));
		}

		/**
		 * 等边三角形
		 * @param p
		 */
		private void endEquilateralTriangle(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			PointF p1 = new PointF(p);
			PointF p2 = new PointF(p);
			GeomTool.rotateLine(-Math.PI/6, p0, p1);
			GeomTool.rotateLine(Math.PI/6, p0, p2);
			polygon.setPoint(1, p1);
			polygon.setPoint(2, p2);
		}

		/**
		 * 长方形
		 * @param p
		 */
		private void endRectangle(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			polygon.setPoint(1, new PointF(p.x, p0.y));
			polygon.setPoint(2, p);
			polygon.setPoint(3, new PointF(p0.x, p.y));
		}

		/**
		 * 正方形
		 * @param p
		 */
		private void endSquare(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			PointF p1 = new PointF(p);
			PointF p3 = new PointF(p);
			GeomTool.rotateLine(-Math.PI/4, p0, p1);
			GeomTool.rotateLine(Math.PI/4, p0, p3);
			double s2 = Math.sqrt(2)/2;
			GeomTool.zoomLine(s2, p0, p1);
			GeomTool.zoomLine(s2, p0, p3);
			polygon.setPoint(1, p1);
			polygon.setPoint(2, p);
			polygon.setPoint(3, p3);
		}

		/**
		 * 五边形
		 * @param p
		 */
		private void endPentagon(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			PointF p1 = new PointF(p);
			PointF p2 = new PointF(p);
			PointF p3 = new PointF(p);
			PointF p4 = new PointF(p);
			GeomTool.rotateLine(-Math.PI*3/10, p0, p1);
			GeomTool.rotateLine(-Math.PI/10, p0, p2);
			GeomTool.rotateLine(Math.PI/10, p0, p3);
			GeomTool.rotateLine(Math.PI*3/10, p0, p4);
			double s2 = 1.0/(2*Math.cos(Math.PI/5));
			GeomTool.zoomLine(s2, p0, p1);
			GeomTool.zoomLine(s2, p0, p4);
			polygon.setPoint(1, p1);
			polygon.setPoint(2, p2);
			polygon.setPoint(3, p3);
			polygon.setPoint(4, p4);
		}
		
		/**
		 * 六边形
		 * @param p
		 */
		private void endHexagon(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			PointF p1 = new PointF(p);
			PointF p2 = new PointF(p);
			PointF p4 = new PointF(p);
			PointF p5 = new PointF(p);
			GeomTool.rotateLine(-Math.PI/3, p0, p1);
			GeomTool.rotateLine(-Math.PI/6, p0, p2);
			GeomTool.rotateLine(Math.PI/6, p0, p4);
			GeomTool.rotateLine(Math.PI/3, p0, p5);
			double s1 = Math.sqrt(3)/2;
			GeomTool.zoomLine(s1, p0, p2);
			GeomTool.zoomLine(s1, p0, p4);
			double s2 = 1.0/2;
			GeomTool.zoomLine(s2, p0, p1);
			GeomTool.zoomLine(s2, p0, p5);
			polygon.setPoint(1, p1);
			polygon.setPoint(2, p2);
			polygon.setPoint(3, p);
			polygon.setPoint(4, p4);
			polygon.setPoint(5, p5);
		}

		/**
		 * 圆形
		 * @param p
		 */
		private void endCircle(PointF p) {
			Circle circle = (Circle) mCurSharp;
			PointF c = circle.getCentre();
			double r = Math.sqrt((p.x - c.x)*(p.x - c.x) + (p.y - c.y)*(p.y - c.y));
			circle.setRadius(r);
		}

		/**
		 * 椭圆
		 * @param p
		 */
		private void endEllipse(PointF p) {
			Ellipse ellipse = (Ellipse) mCurSharp;
			PointF c = ellipse.getCentre();
			ellipse.setAB(Math.abs((p.x - c.x)), Math.abs((p.y - c.y)));
		}

		/**
		 * 图片
		 * @param p
		 */
		private void endPicture(PointF p) {
			Picture picture = (Picture) mCurSharp;
			picture.setEndPoint(p);
		}

		/**
		 * 折线段
		 * @param p
		 */
		private void addPolyline(PointF p) {
			if(mCurSharp == null
					|| !(mCurSharp instanceof Polyline)
					|| ((Polyline) mCurSharp).isFinish()) {
				// 新增一个折线段
				Polyline polyline = new Polyline(mPaintBrush);
				polyline.addPoint(p);
				polyline.addPoint(p);
				polyline.setFinish(false);
				addSharp(polyline);
			} else if(mCurSharp instanceof Polyline) {
				// 继续上一个折线段
				Polyline polyline = (Polyline) mCurSharp;
				polyline.addPoint(p);
			}
		}

		/**
		 * 折线段
		 * @param p
		 */
		private void setPolylineLastPoint(PointF p) {
			if(mCurSharp instanceof Polyline) {
				Polyline polyline = (Polyline) mCurSharp;
				polyline.setLastPoint(p);
			}
		}

		/**
		 * 折线段
		 * @param p
		 */
		private void endPolyline(PointF p) {
			if(mCurSharp instanceof Polyline) {
				Polyline polyline = (Polyline) mCurSharp;
				polyline.setFinish(true);
			}
		}

	}

	public void wanderUp() {
		PointF sp = new PointF(0, 0);
		PointF ep = new PointF(0, 15);
		wander(sp, ep);
	}

	public void wanderDown() {
		PointF sp = new PointF(0, 0);
		PointF ep = new PointF(0, -15);
		wander(sp, ep);
	}

	public void wanderLeft() {
		PointF sp = new PointF(0, 0);
		PointF ep = new PointF(15, 0);
		wander(sp, ep);
	}

	public void wanderRight() {
		PointF sp = new PointF(0, 0);
		PointF ep = new PointF(-15, 0);
		wander(sp, ep);
	}

	private void wander(PointF sp, PointF ep) {
		//System.out.println("Canvas,wander,sp=" + sp + ",dy=" + ep);
		MenuAction ma = new MenuAction();
		ma.setActionStartPoint(sp);
		ma.setActionEndPoint(ep);
		double dx = ma.getMoveDx();
		double dy = ma.getMoveDy();
		for(AbsMenuSharp sharp : mSharpList) {
			sharp.move(dx, dy);
		}
		repaintCanvas();
	}
	
	public void switchWanderMode() {
		System.out.println("Canvas,switchWanderMode.");
	}

	@Override
	public void zoom(int value) {
		//System.out.println("Canvas,zoom,zoomValue=" + zoomValue);
		double scale = 1.0;
		if(value > 0 && value < zoomValue) {
			scale = Math.pow(1.0/1.05, zoomValue - value);
		} else if(value > zoomValue) {
			scale = Math.pow(1.05, value - zoomValue);
		}
		this.zoomValue = value;
		PointF basic = new PointF(this.getWidth()/2, this.getHeight()/2);
		for(AbsMenuSharp sharp : mSharpList) {
			sharp.zoom(basic , scale);
		}
		repaintCanvas();
	}

	public int getZoomValue() {
		return zoomValue;
	}

}
