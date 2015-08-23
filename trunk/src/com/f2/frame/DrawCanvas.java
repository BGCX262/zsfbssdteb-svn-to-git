package com.f2.frame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.List;

import com.f2.frame.data.Background;
import com.f2.frame.data.CanvasItem;
import com.f2.frame.property.CircleProperty;
import com.f2.frame.property.EllipseProperty;
import com.f2.frame.property.LineProperty;
import com.f2.frame.property.PolygonProperty;
import com.f2.frame.selector.ColorSelector;
import com.f2.frame.selector.StrokeLineSelector;
import com.f2.listener.CanvasRepaintListener;
import com.f2.listener.CanvasResetListener;
import com.f2.sharp.AbsMenuSharp;
import com.f2.sharp.AbsSharp;
import com.f2.sharp.Handwriting;
import com.f2.sharp.OleEmbed;
import com.f2.sharp.Picture;
import com.f2.sharp.Simulator;
import com.f2.sharp.geometry.Circle;
import com.f2.sharp.geometry.Ellipse;
import com.f2.sharp.geometry.Line;
import com.f2.sharp.geometry.Polygon;
import com.f2.sharp.geometry.Polyline;
import com.f2.tool.ClipBoard;
import com.f2.tool.CursorTool;
import com.f2.tool.GeomTool;
import com.f2.tool.PaintBrush;
import com.f2.tool.PointF;
import com.f2.tool.constant.MenuAction;
import com.f2.tool.constant.SharpType;

@SuppressWarnings("serial")
public class DrawCanvas extends Canvas implements Wander.WanderListener, Wander.ZoomListener, MouseListener, MouseMotionListener, ColorSelector.ColorSelectionListener,
		StrokeLineSelector.StrokeSelectionListener, ComponentListener {
	private CanvasRepaintListener repaintListener;
	private CanvasResetListener resetListener;

	private PaintBrush mPaintBrush = null;
	private String mFilePath = null;
	private boolean isSharpSelected = false;

	private AbsMenuSharp mCurSharp = null;
	private List<AbsMenuSharp> mSharpList = new LinkedList<AbsMenuSharp>();
	private MenuAction mMenuAction = new MenuAction();
	private Background mBackground = new Background();

	private int wanderX = 0;
	private int wanderY = 0;
	private int mZoomValue = Wander.SLIDER_ZOOM_MID;

	private BufferedImage bgTempImage;
	private ImageObserver bgImageObserver;

	private ColorSelector colorSelector = null;
	private StrokeLineSelector strokeLineSelector = null;
	
	private LineProperty lineProperty = null;
	private PolygonProperty polygonProperty = null;
	private EllipseProperty ellipseProperty = null;
	private CircleProperty circleProperty = null;
	
	private SharpProxy sharpProxy = new SharpProxy();

	private boolean isDragging = false;
	private boolean hasMouseAction = false;
	
	private OleObjectManagerThread oleObjectManagerThread;

	/**
	 * Create the frame.
	 */
	public DrawCanvas(PaintBrush brush) {
		super();
		this.mPaintBrush = brush;
		addMouseListener(this);
		addMouseMotionListener(this);
		oleObjectManagerThread = new OleObjectManagerThread(this);
		oleObjectManagerThread.start();
	}
	
	public void initBufferImage() {
		if (bgTempImage == null) {
			bgTempImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		}
	}
	
	public OleObjectManagerThread getOleObjectManagerThread() {
		return oleObjectManagerThread;
	}
	
	@Override
	public void update(Graphics g) {
		
		if (bgTempImage != null) {
			Graphics bufferGraphics = bgTempImage.getGraphics();
			bufferGraphics.setColor(Color.WHITE);
			bufferGraphics.fillRect(0, 0, getWidth(), getHeight());
			paint(bufferGraphics);
			bufferGraphics.dispose();
			g.drawImage(bgTempImage, wanderX, wanderY, this.getWidth(), this.getHeight(), bgImageObserver);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		// long t0 = System.currentTimeMillis();
		// System.out.println("mCanvas.paint");
		// super.paint(g);


		// 画图形
		for (int i = mSharpList.size() - 1; i >= 0; i--) {
			mSharpList.get(i).draw(g);
		}

		// 画菜单
		for (int i = mSharpList.size() - 1; i >= 0; i--) {
			AbsMenuSharp sharp = mSharpList.get(i);
			sharp.drawMenu(g);
		}

		// 映射临时bgImage到实际画布
		

		// long t1 = System.currentTimeMillis();
		// System.out.println("mCanvas.paint,t="+(t1-t0));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
		// System.out.println("mouseClicked, p = " + p);
		// System.out.println("e.getClickCount()=" + e.getClickCount());

		boolean hasAction = false;

		switch (mPaintBrush.getType()) {
		case SharpType.SELECTION:
			hasAction = true; // doSelectionAndMenuAction(p);
			break;
		case SharpType.ERASE:
			backup();
			doEraseAction(p);
			hasAction = false;
			break;
		default:
			if (e.getClickCount() == 1) {
				hasAction = sharpProxy.mouseClicked(mPaintBrush.getType(), p);
			} else {
				hasAction = sharpProxy.mouseMultiClicked(mPaintBrush.getType(), p, e.getClickCount());
			}
			break;
		}

		if (!hasAction && mCurSharp != null) {
			cancelSelected();
		}

		repaintCanvas();
	}

	private boolean doSelectionAndMenuAction(PointF p) {
		boolean hasAction = false;
		// 先看下当前mCurSharp是否有动作,然后才看其他的Sharp
		if (mCurSharp != null && mCurSharp.isSelected()) {
			if(mCurSharp.isInBasicMenuBounds(p) 
					&& !mCurSharp.isInMoreMenuBounds(p)) {
				hasAction = true;
			}
		}		
		// 检测是否有Sharp的MoreMenu菜单被选中,并操作MoreMenu
		if (!hasAction) {
			for (AbsMenuSharp sharp : mSharpList) {
				if (sharp.isSelected() && sharp.isShowMoreMenu()) {
					hasAction = doMoreMenuAction(sharp, p);
					break;
				}
			}
		}
		// 检测是否有Sharp的More菜单被选中,并显示More菜单
		if (!hasAction) {
			for (AbsMenuSharp sharp : mSharpList) {
				if (sharp.isSelected() && sharp.isInMoreMenuBounds(p)) {
					setShowMoreMenu(sharp);
					hasAction = true;
					break;
				}
			}
		}
		// 检测是否有Sharp被选中,并显示四角菜单
		if (!hasAction) {
			for (AbsMenuSharp sharp : mSharpList) {
				if (sharp.isInSharp(p)) {
					if (!sharp.onClick(p)) {
						setSharpSelected(sharp);
					}
					hasAction = true;
					break;
				}
			}
		}
		// 取消其他Sharp的选中状态
		for (AbsMenuSharp sharp : mSharpList) {
			if (sharp != mCurSharp) {
				sharp.setSelected(false);
				sharp.setShowMoreMenu(false);
			}
		}
		return hasAction;
	}

	private boolean doMoreMenuAction(AbsMenuSharp sharp, PointF p) {
		int action = sharp.getMoreMenuAction(p);
		// System.out.println("doMoreMenuAction, action = " + action);
		switch (action) {
		case MenuAction.MENU_COPY:
			sharpCopy(sharp);
			return true;
		case MenuAction.MENU_CLONE:
			sharpClone(sharp);
			return true;
		case MenuAction.MENU_CUT:
			sharpCut(sharp);
			return true;
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
			showColorSelector(sharp, p);
			return true;
		case MenuAction.MENU_BRUSH_WIDTH:
			showStrokeLineSelector(sharp, p);
			return true;
		case MenuAction.MENU_PROPERTY:
			sharpShowProperty(sharp);
			return true;
		case MenuAction.MENU_EDIT:
			sharpShowEditProperty(sharp);
			return true;
		}
		return false;
	}

	private void showColorSelector(AbsMenuSharp sharp, PointF p) {
		if (colorSelector == null) {
			colorSelector = new ColorSelector(this);
		}
		colorSelector.setColorSelectionListener(this, sharp.getPaintColor());
		colorSelector.setLocation((int) p.x - colorSelector.getWidth() - 2, (int) p.y + 2);
		colorSelector.setVisible(true);
	}

	private void showStrokeLineSelector(AbsMenuSharp sharp, PointF p) {
		if (strokeLineSelector == null) {
			strokeLineSelector = new StrokeLineSelector(this);
		}
		strokeLineSelector.setStrokeSelectionListener(this, sharp.getPaintStrokeLineWidth(), sharp.getPaintStrokeDash());
		strokeLineSelector.setLocation((int) p.x - strokeLineSelector.getWidth() - 2, (int) p.y + 2);
		strokeLineSelector.setVisible(true);
	}

	private boolean doMenuAction(AbsMenuSharp sharp) {
		int action = sharp.getPaintAction();
		PointF basic;
		switch (action) {
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

	private void doEraseAction(PointF p) {
		// System.out.println("doEraseAction, p = " + p);
		List<AbsSharp> removeList = new LinkedList<AbsSharp>();
		List<AbsSharp> addList = new LinkedList<AbsSharp>();
		for (AbsMenuSharp sharp : mSharpList) {
			if (sharp.isInSharp(p)) {
				List<AbsSharp> result = sharp.erase(p, 32);
				// System.out.println("doEraseAction, result = " + result);
				if (result == null) {
					// nothing here
				} else if (result.size() == 0) {
					// 完全擦除
					removeList.add(sharp);
				} else if (result.size() == 1 && result.get(0) == sharp) {
					// nothing here
				} else if (result.size() >= 1) {
					removeList.add(sharp);
					addList.addAll(result);
				}
			}
		}
		for (AbsSharp s : removeList) {
			mSharpList.remove((AbsMenuSharp) s);
			((AbsMenuSharp) s).release();
		}
		for (AbsSharp s : addList) {
			mSharpList.add(0, (AbsMenuSharp) s);
		}
		isSharpSelected = false;
		mCurSharp = null;
		removeList.clear();
		addList.clear();
		repaintCanvas();
	}

	private void setSharpSelected(AbsMenuSharp sharp) {
		mCurSharp = sharp;
		mCurSharp.setSelected(true);
		mCurSharp.setPaintAction(MenuAction.NONE);
		isSharpSelected = true;
	}

	private void setShowMoreMenu(AbsMenuSharp sharp) {
		mCurSharp = sharp;
		mCurSharp.setShowMoreMenu(true);
		mCurSharp.setPaintAction(MenuAction.NONE);
		isSharpSelected = true;
	}

	public void cancelSelected() {
		if(mCurSharp != null) {
			mCurSharp.setSelected(false);
			mCurSharp.setShowMoreMenu(false);
			mCurSharp.setPaintAction(MenuAction.NONE);
			mCurSharp = null;
		}
		isSharpSelected = false;
		if (strokeLineSelector != null) {
			strokeLineSelector.setVisible(false);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Point p = e.getPoint();
		// System.out.println("mouseEntered, p = " + p);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Point p = e.getPoint();
		// System.out.println("mouseExited, p = " + p);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
		// System.out.println("mousePressed, p = " + p);

		isDragging = false;
		hasMouseAction = false;
		
		switch (mPaintBrush.getType()) {
		case SharpType.SELECTION:
			hasMouseAction = doSelectionAndMenuAction(p);
			break;
		}
		
		if (!isSharpSelected) {
			switch (mPaintBrush.getType()) {
			default:
				sharpProxy.mousePressed(mPaintBrush.getType(), p);
				break;
			}
		}

		if (isSharpSelected && mCurSharp != null) {
			int action = mCurSharp.getBasicMenuAction(p);
			if (action != MenuAction.NONE) {
				mMenuAction.setActionStartPoint(p);
				mCurSharp.setPaintAction(action);
			}
		}

		repaintCanvas();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
		// System.out.println("mouseReleased, p = " + p);

		isDragging = false;

		if (!isSharpSelected && mPaintBrush.getAction() == MenuAction.NONE) {
			switch (mPaintBrush.getType()) {
			default:
				sharpProxy.mouseReleased(mPaintBrush.getType(), p);
				break;
			}
			
			switch (mPaintBrush.getType()) {
			case SharpType.PICTURE:
			case SharpType.OLE_EMBED:
				if(mCurSharp != null) {
					//添加Picture和OleEmbed之后，默认为选中状态
					setSharpSelected(mCurSharp);
					mPaintBrush.setType(SharpType.SELECTION);
					CursorTool.updateCursor(mPaintBrush.getType());
				}
				break;
			}
		}

		if (isSharpSelected && mCurSharp != null) {
			mCurSharp.setPaintAction(MenuAction.NONE);
			if(!hasMouseAction) {
				cancelSelected();
			}
		}		
		
		repaintCanvas();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
		// System.out.println("mouseDragged, p = " + p);

		if (!isDragging) {
			backup();
			isDragging = true;
		}

		switch (mPaintBrush.getType()) {
		case SharpType.ERASE:
			doEraseAction(p);
			break;
		default:
			if (!isSharpSelected && mPaintBrush.getAction() == MenuAction.NONE) {
				switch (mPaintBrush.getType()) {
				default:
					sharpProxy.mouseDragged(mPaintBrush.getType(), p);
					break;
				}
			}
			if (isSharpSelected && mCurSharp != null) {
				mMenuAction.setActionEndPoint(p);
				doMenuAction(mCurSharp);
				mMenuAction.setActionStartPoint(p);
			}
			break;
		}

		repaintCanvas();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		PointF p = new PointF(e.getPoint());
		// System.out.println("mouseReleased, p = " + p);

		if (!isSharpSelected && mPaintBrush.getAction() == MenuAction.NONE) {
			switch (mPaintBrush.getType()) {
			default:
				boolean rp = sharpProxy.mouseMoved(mPaintBrush.getType(), p);
				if (rp) {
					repaintCanvas();
				}
				break;
			}
		}
	}

	public void sharpAdd(AbsMenuSharp sharp) {
		if (mSharpList == null) {
			return;
		}
		backup();
		mSharpList.add(0, sharp);
		mCurSharp = sharp;
	}
	
	public void sharpReplace(int index, AbsMenuSharp sharp) {
		if (mSharpList == null) {
			return;
		}
		if(index < 0 || index >= mSharpList.size()) {
			return;
		}
		//backup();
		AbsMenuSharp previously = mSharpList.set(index, sharp);
		if(previously != null) {
			previously.release();
		}
		mCurSharp = sharp;
	}
	
	public void sharpReplaceLast(AbsMenuSharp sharp) {
		sharpReplace(0, sharp); //index=0就是最新一个(the last one)
	}
	
	public void sharpRemove(AbsMenuSharp sharp) {
		if (mSharpList == null) {
			return;
		}
		backup();
		mSharpList.remove(sharp);
		if (mCurSharp == sharp) {
			mCurSharp = null;
		}
		if(sharp != null) {
			sharp.release();
		}
		repaintCanvas();
	}

	public void sharpClear() {
		if (mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		backup();
		for(AbsMenuSharp sharp : mSharpList) {
			sharp.release();
		}
		mSharpList.clear();
		repaintCanvas();
	}

	public void sharpRemoveLast() {
		if (mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		backup();
		AbsMenuSharp sharp = mSharpList.remove(0); //index=0就是最新一个(the last one)
		if (mSharpList.size() == 0) {
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

	private void sharpCopy(AbsMenuSharp sharp) {
		if (sharp == null) {
			return;
		}
		// backup();
		sharp.setSelected(false);
		sharp.setShowMoreMenu(false);
		AbsMenuSharp s2 = (AbsMenuSharp) sharp.clone();
		ClipBoard.put(ClipBoard.SHARP_COPY, s2);
		repaintCanvas();
	}

	private void sharpClone(AbsMenuSharp sharp) {
		if (sharp == null) {
			return;
		}
		backup();
		sharp.setSelected(false);
		sharp.setShowMoreMenu(false);
		AbsMenuSharp s2 = (AbsMenuSharp) sharp.clone();
		s2.move(20, 20);
		s2.setSelected(true);
		s2.setShowMoreMenu(false);
		sharpAdd(s2);
		repaintCanvas();
	}

	public void sharpPaste(AbsMenuSharp sharp) {
		if (sharp == null) {
			return;
		}
		backup();
		AbsMenuSharp s2 = (AbsMenuSharp) sharp.clone();
		s2.setSelected(true);
		s2.setShowMoreMenu(false);
		sharpAdd(s2);
		repaintCanvas();
	}

	private void sharpCut(AbsMenuSharp sharp) {
		if (sharp == null) {
			return;
		}
		backup();
		AbsMenuSharp s2 = (AbsMenuSharp) sharp.clone();
		s2.setSelected(false);
		s2.setShowMoreMenu(false);
		ClipBoard.put(ClipBoard.SHARP_COPY, s2);
		sharpDelete(sharp);
		repaintCanvas();
	}

	private void sharpDelete(AbsMenuSharp sharp) {
		if (mSharpList == null || mSharpList.size() == 0 || sharp == null) {
			return;
		}
		backup();
		mSharpList.remove(sharp);
		if (mSharpList.size() == 0) {
			mCurSharp = null;
		} else {
			mCurSharp = mSharpList.get(0);
		}
		isSharpSelected = false;
		sharp.release();
		repaintCanvas();
	}

	/**
	 * Sharp下移一层 index=0是最后添加的,也就是最上层的一个
	 * 
	 * @param sharp
	 */
	private void sharpDown(AbsMenuSharp sharp) {
		if (mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		int index = mSharpList.indexOf(sharp);
		if (index == -1 || index == mSharpList.size() - 1) {
			return;
		}
		backup();
		AbsMenuSharp temp = mSharpList.get(index + 1);
		mSharpList.set(index + 1, sharp);
		mSharpList.set(index, temp);
		repaintCanvas();
	}

	/**
	 * Sharp上移一层 index=0是最后添加的,也就是最上层的一个
	 * 
	 * @param sharp
	 */
	private void sharpUp(AbsMenuSharp sharp) {
		if (mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		int index = mSharpList.indexOf(sharp);
		if (index == -1 || index == 0) {
			return;
		}
		backup();
		AbsMenuSharp temp = mSharpList.get(index - 1);
		mSharpList.set(index - 1, sharp);
		mSharpList.set(index, temp);
		repaintCanvas();
	}

	/**
	 * Sharp下移到底层 index=0是最后添加的,也就是最上层的一个
	 * 
	 * @param sharp
	 */
	private void sharpDownToLast(AbsMenuSharp sharp) {
		if (mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		int index = mSharpList.indexOf(sharp);
		if (index == -1 || index == mSharpList.size() - 1) {
			return;
		}
		backup();
		AbsMenuSharp temp = mSharpList.remove(index);
		mSharpList.add(temp);
		repaintCanvas();
	}

	/**
	 * Sharp上移到顶层 index=0是最后添加的,也就是最上层的一个
	 * 
	 * @param sharp
	 */
	private void sharpUpToFirst(AbsMenuSharp sharp) {
		if (mSharpList == null || mSharpList.size() == 0) {
			return;
		}
		int index = mSharpList.indexOf(sharp);
		if (index == -1 || index == 0) {
			return;
		}
		backup();
		AbsMenuSharp temp = mSharpList.remove(index);
		mSharpList.add(0, temp);
		repaintCanvas();
	}

	private void sharpShowProperty(AbsMenuSharp sharp) {
		if(sharp != null) {
			sharp.setShowProperty(!sharp.isShowProperty());
		}
		repaintCanvas();
	}	

	private void sharpShowEditProperty(AbsMenuSharp sharp) {
		if(sharp == null) {
			return;
		}
		if(sharp instanceof Line) {
			if(lineProperty == null) {
				lineProperty = new LineProperty(this);
			}
			if(lineProperty.isVisible()) {
				lineProperty.setVisible(false);				
			} else {
				lineProperty.setSharp((Line) sharp);
				lineProperty.setVisible(true);
			}
		} else if(sharp instanceof Polygon) {
			if(polygonProperty == null) {
				polygonProperty = new PolygonProperty(this);
			}
			if(polygonProperty.isVisible()) {
				polygonProperty.setVisible(false);				
			} else {
				polygonProperty.setSharp((Polygon) sharp);
				polygonProperty.setVisible(true);
			}
		} else if(sharp instanceof Ellipse) {
			if(ellipseProperty == null) {
				ellipseProperty = new EllipseProperty(this);
			}
			if(ellipseProperty.isVisible()) {
				ellipseProperty.setVisible(false);				
			} else {
				ellipseProperty.setSharp((Ellipse) sharp);
				ellipseProperty.setVisible(true);
			}
		} else if(sharp instanceof Circle) {
			if(circleProperty == null) {
				circleProperty = new CircleProperty(this);
			}
			if(circleProperty.isVisible()) {
				circleProperty.setVisible(false);				
			} else {
				circleProperty.setSharp((Circle) sharp);
				circleProperty.setVisible(true);
			}
		}
	}
	
	@Override
	public void componentHidden(ComponentEvent event) {
		// System.out.println("componentHidden, event = " + event);
		repaintCanvas();
	}

	@Override
	public void componentMoved(ComponentEvent event) {
		// System.out.println("componentMoved, event = " + event);
		repaintCanvas();
	}

	@Override
	public void componentResized(ComponentEvent event) {
		// System.out.println("componentResized, event = " + event);
		repaintCanvas();
	}

	@Override
	public void componentShown(ComponentEvent event) {
		// System.out.println("componentShown, event = " + event);
		repaintCanvas();
	}

	public CanvasItem getCanvasItem() {
		CanvasItem canvasItem = new CanvasItem();
		canvasItem.setSharps(mSharpList);
		canvasItem.setBackground(mBackground);
		return canvasItem;
	}

	public void initCanvasItem(CanvasItem canvasItem) {
		setCanvasItem(canvasItem);
		backup();
	}

	public void setCanvasItem(CanvasItem canvasItem) {
		resetZoomValue();
		for(AbsMenuSharp sharp : mSharpList) {
			sharp.hide();
		}
		this.mSharpList = canvasItem.getSharps();
		for (AbsMenuSharp sharp : mSharpList) {
			sharp.setSelected(false);
			sharp.show();
		}
		this.mBackground = canvasItem.getBackground();
		repaintCanvas();
	}

	public void repaintCanvas() {
		if (isVisible()) {
			repaint();
		}
		if (repaintListener != null) {
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
			// System.out.println("mouseClicked,type=" + type + ",p=" + p);
			switch (type) {
			case SharpType.POLYLINE:
				addPolyline(p);
				return true;
			}
			return false;
		}

		public boolean mouseMultiClicked(int type, PointF p, int clickCount) {
			// System.out.println("mouseMultiClicked,type=" + type + ",p=" + p +
			// ",c=" + clickCount);
			switch (type) {
			case SharpType.POLYLINE:
				endPolyline(p);
				return false;
			}
			return false;
		}

		public boolean mouseMoved(int type, PointF p) {
			// System.out.println("mouseMoved,type=" + type + ",p=" + p +
			// ",(mCurSharp == null)=" + (mCurSharp == null));
			if (mCurSharp == null) {
				return false;
			}
			switch (type) {
			case SharpType.POLYLINE:
				setPolylineLastPoint(p);
				return true;
			}
			return false;
		}

		public void mousePressed(int type, PointF p) {
			switch (type) {
			case SharpType.HANDWRITING:
				addHandwriting(p);
				break;
			case SharpType.SIMULATE:
				addSimulate(p);
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
			case SharpType.OLE_EMBED:
				addOleEmbed(p);
				break;
			}
		}

		public void mouseReleased(int type, PointF p) {
			if (mCurSharp == null) {
				return;
			}
			switch (type) {
			case SharpType.HANDWRITING:
				endHandwriting(p);
				break;
			case SharpType.SIMULATE:
				endSimulate(p);
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
			case SharpType.OLE_EMBED:
				endOleEmbed(p);
				break;
			}
		}

		public void mouseDragged(int type, PointF p) {
			if (mCurSharp == null) {
				return;
			}
			switch (type) {
			case SharpType.HANDWRITING:
				updateHandwriting(p);
				break;
			case SharpType.SIMULATE:
				updateSimulate(p);
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
			case SharpType.OLE_EMBED:
				updateOleEmbed(p);
				break;
			}
		}

		/**
		 * 图片
		 * 
		 * @param p
		 */
		private void addPicture(PointF p) {
			if (mFilePath == null || mFilePath.trim().length() == 0) {
				return;
			}
			Picture picture = new Picture(mPaintBrush);
			picture.setStartPoint(p);
			picture.setEndPoint(p);
			picture.setFilepath(mFilePath);
			sharpAdd(picture);
		}
		
		/**
		 * Office
		 * 
		 * @param p
		 */
		private void addOleEmbed(PointF p) {
			if (mFilePath == null || mFilePath.trim().length() == 0) {
				return;
			}
			OleEmbed embed = new OleEmbed(mPaintBrush);
			embed.setStartPoint(p);
			embed.setEndPoint(p);
			embed.openFilepath(mFilePath);
			sharpAdd(embed);
		}
		

		/**
		 * 直线
		 * 
		 * @param p
		 */
		private void addLine(PointF p) {
			Line line = new Line(mPaintBrush);
			line.setStartPoint(p);
			line.setEndPoint(p);
			sharpAdd(line);
		}

		/**
		 * 圆形
		 * 
		 * @param p
		 */
		private void addCircle(PointF p) {
			Circle circle = new Circle(mPaintBrush);
			circle.setRadius(0);
			circle.setCentre(p);
			sharpAdd(circle);
		}

		/**
		 * 椭圆
		 * 
		 * @param p
		 */
		private void addEllipse(PointF p) {
			Ellipse ellipse = new Ellipse(mPaintBrush);
			ellipse.setAB(0, 0);
			ellipse.setCentre(p);
			sharpAdd(ellipse);
		}

		/**
		 * 手绘线
		 * 
		 * @param p
		 */
		private void addHandwriting(PointF p) {
			Handwriting handwriting = new Handwriting(mPaintBrush);
			handwriting.addPoint(p);
			sharpAdd(handwriting);
		}

		/**
		 * 手绘线拟合
		 * 
		 * @param p
		 */
		private void addSimulate(PointF p) {
			Simulator simulator = new Simulator(mPaintBrush);
			simulator.addPoint(p);
			sharpAdd(simulator);
		}

		/**
		 * 等腰直角三角形
		 * 
		 * @param p
		 */
		private void addRightTriangle(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 3, p);
			sharpAdd(polygon);
		}

		/**
		 * 等边三角形
		 * 
		 * @param p
		 */
		private void addEquilateralTriangle(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 3, p);
			sharpAdd(polygon);
		}

		/**
		 * 长方形
		 * 
		 * @param p
		 */
		private void addRectangle(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 4, p);
			sharpAdd(polygon);
		}

		/**
		 * 正方形
		 * 
		 * @param p
		 */
		private void addSquare(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 4, p);
			sharpAdd(polygon);
		}

		/**
		 * 五边形
		 * 
		 * @param p
		 */
		private void addPentagon(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 5, p);
			sharpAdd(polygon);
		}

		/**
		 * 六边形
		 * 
		 * @param p
		 */
		private void addHexagon(PointF p) {
			Polygon polygon = new Polygon(mPaintBrush, 6, p);
			sharpAdd(polygon);
		}

		/**
		 * 手绘线
		 * 
		 * @param p
		 */
		private void endHandwriting(PointF p) {
			Handwriting handwriting = (Handwriting) mCurSharp;
			handwriting.endPoint(p);
		}

		/**
		 * 手绘线拟合
		 * 
		 * @param p
		 */
		private void endSimulate(PointF p) {
			Simulator simulator = (Simulator) mCurSharp;
			simulator.endPoint(p);
			AbsMenuSharp simu = simulator.getSimulateSharp();
//			sharpAdd(simu); 
			sharpReplaceLast(simu);
		}

		private void updateHandwriting(PointF p) {
			Handwriting handwriting = (Handwriting) mCurSharp;
			handwriting.addPoint(p);
		}
		
		private void updateSimulate(PointF p) {
			Simulator simulator = (Simulator) mCurSharp;
			simulator.addPoint(p);
		}
		
		private void updateOleEmbed(PointF p) {
			OleEmbed embed = (OleEmbed) mCurSharp;
			embed.setEndPoint(p);
		}

		/**
		 * 直线
		 * 
		 * @param p
		 */
		private void endLine(PointF p) {
			Line line = (Line) mCurSharp;
			line.setEndPoint(p);
		}

		/**
		 * 直角三角形
		 * 
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
		 * 
		 * @param p
		 */
		private void endEquilateralTriangle(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			PointF p1 = new PointF(p);
			PointF p2 = new PointF(p);
			GeomTool.rotateLine(-Math.PI / 6, p0, p1);
			GeomTool.rotateLine(Math.PI / 6, p0, p2);
			polygon.setPoint(1, p1);
			polygon.setPoint(2, p2);
		}

		/**
		 * 长方形
		 * 
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
		 * 
		 * @param p
		 */
		private void endSquare(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			PointF p1 = new PointF(p);
			PointF p3 = new PointF(p);
			GeomTool.rotateLine(-Math.PI / 4, p0, p1);
			GeomTool.rotateLine(Math.PI / 4, p0, p3);
			double s2 = Math.sqrt(2) / 2;
			GeomTool.zoomLine(s2, p0, p1);
			GeomTool.zoomLine(s2, p0, p3);
			polygon.setPoint(1, p1);
			polygon.setPoint(2, p);
			polygon.setPoint(3, p3);
		}

		/**
		 * 五边形
		 * 
		 * @param p
		 */
		private void endPentagon(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			PointF p1 = new PointF(p);
			PointF p2 = new PointF(p);
			PointF p3 = new PointF(p);
			PointF p4 = new PointF(p);
			GeomTool.rotateLine(-Math.PI * 3 / 10, p0, p1);
			GeomTool.rotateLine(-Math.PI / 10, p0, p2);
			GeomTool.rotateLine(Math.PI / 10, p0, p3);
			GeomTool.rotateLine(Math.PI * 3 / 10, p0, p4);
			double s2 = 1.0 / (2 * Math.cos(Math.PI / 5));
			GeomTool.zoomLine(s2, p0, p1);
			GeomTool.zoomLine(s2, p0, p4);
			polygon.setPoint(1, p1);
			polygon.setPoint(2, p2);
			polygon.setPoint(3, p3);
			polygon.setPoint(4, p4);
		}

		/**
		 * 六边形
		 * 
		 * @param p
		 */
		private void endHexagon(PointF p) {
			Polygon polygon = (Polygon) mCurSharp;
			PointF p0 = polygon.getPoint(0);
			PointF p1 = new PointF(p);
			PointF p2 = new PointF(p);
			PointF p4 = new PointF(p);
			PointF p5 = new PointF(p);
			GeomTool.rotateLine(-Math.PI / 3, p0, p1);
			GeomTool.rotateLine(-Math.PI / 6, p0, p2);
			GeomTool.rotateLine(Math.PI / 6, p0, p4);
			GeomTool.rotateLine(Math.PI / 3, p0, p5);
			double s1 = Math.sqrt(3) / 2;
			GeomTool.zoomLine(s1, p0, p2);
			GeomTool.zoomLine(s1, p0, p4);
			double s2 = 1.0 / 2;
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
		 * 
		 * @param p
		 */
		private void endCircle(PointF p) {
			Circle circle = (Circle) mCurSharp;
			PointF c = circle.getCentre();
			double r = Math.sqrt((p.x - c.x) * (p.x - c.x) + (p.y - c.y) * (p.y - c.y));
			circle.setRadius(r);
		}

		/**
		 * 椭圆
		 * 
		 * @param p
		 */
		private void endEllipse(PointF p) {
			Ellipse ellipse = (Ellipse) mCurSharp;
			PointF c = ellipse.getCentre();
			ellipse.setAB(Math.abs((p.x - c.x)), Math.abs((p.y - c.y)));
		}

		/**
		 * 图片
		 * 
		 * @param p
		 */
		private void endPicture(PointF p) {
			Picture picture = (Picture) mCurSharp;
			picture.setEndPoint(p);
		}

		private void endOleEmbed(PointF p) {
			OleEmbed embed = (OleEmbed) mCurSharp;
			embed.setEndPoint(p);
			embed.setOleObjectManagerThread(oleObjectManagerThread);
			oleObjectManagerThread.addOleEmbed(embed);
		}
		/**
		 * 折线段
		 * 
		 * @param p
		 */
		private void addPolyline(PointF p) {
			if (mCurSharp == null || !(mCurSharp instanceof Polyline) || ((Polyline) mCurSharp).isFinish()) {
				// 新增一个折线段
				Polyline polyline = new Polyline(mPaintBrush);
				polyline.addPoint(p);
				polyline.addPoint(p);
				polyline.setFinish(false);
				sharpAdd(polyline);
			} else if (mCurSharp instanceof Polyline) {
				// 继续上一个折线段
				Polyline polyline = (Polyline) mCurSharp;
				polyline.addPoint(p);
			}
		}

		/**
		 * 折线段
		 * 
		 * @param p
		 */
		private void setPolylineLastPoint(PointF p) {
			if (mCurSharp instanceof Polyline) {
				Polyline polyline = (Polyline) mCurSharp;
				polyline.setLastPoint(p);
			}
		}

		/**
		 * 折线段
		 * 
		 * @param p
		 */
		private void endPolyline(PointF p) {
			if (mCurSharp instanceof Polyline) {
				Polyline polyline = (Polyline) mCurSharp;
				polyline.setFinish(true);
			}
		}

	}

	@Override
	public void onWander(double dx, double dy) {
		// System.out.println("Canvas,wander,dx=" + dx + ",dy=" + dy);
		wanderX += dx;
		wanderY += dy;
		repaintCanvas();
	}

	@Override
	public void onWanderReset() {
		// System.out.println("Canvas,wanderReset.");
		wanderX = 0;
		wanderY = 0;
		repaintCanvas();
	}

	private void resetZoomValue() {
		onZoomAction(Wander.SLIDER_ZOOM_MID);
		this.mZoomValue = Wander.SLIDER_ZOOM_MID;
		if (resetListener != null) {
			resetListener.resetCanvasItem();
		}
	}

	@Override
	public void onZoomAction(int value) {
		// System.out.println("Canvas,onZoomAction,mZoomValue=" + mZoomValue);
		double scale = 1.0;
		if (value > 0 && value < mZoomValue) {
			scale = Math.pow(1.0 / 1.05, mZoomValue - value);
		} else if (value > mZoomValue) {
			scale = Math.pow(1.05, value - mZoomValue);
		}
		this.mZoomValue = value;
		PointF basic = new PointF(this.getWidth() / 2, this.getHeight() / 2);
		for (AbsMenuSharp sharp : mSharpList) {
			sharp.zoom(basic, scale);
		}
		repaintCanvas();
	}

	public void addScreenShot(BufferedImage image) {
		Picture picture = new Picture(mPaintBrush);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// picture.saveFile("..."); // TODO
		picture.setStartPoint(new PointF(0, 0));
		picture.setEndPoint(new PointF(dim.getWidth(), dim.getHeight()));
		picture.setImage(image);
		sharpAdd(picture);
	}
	
	public void release() {
		oleObjectManagerThread.interrupt();
	}

	// public void undoRedo() {
	// //System.out.println("Canvas,undoRedo");
	// CanvasItem canvasItem = (CanvasItem)
	// ClipBoard.get(ClipBoard.CANVAS_UNDO_REDO);
	// if(canvasItem == null) {
	// return;
	// }
	// CanvasItem clone = (CanvasItem) canvasItem.clone();
	// undoRedo(clone);
	// }

	public void undoRedo(CanvasItem clone) {
		backup();
		setCanvasItem(clone);
	}

	private void backup() {
		// System.out.println("Canvas,backup");
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		CanvasItem canvasItem = getCanvasItem();
		ClipBoard.put(ClipBoard.CANVAS_UNDO_REDO, canvasItem.clone());
		// }
		// }).start();
	}

	@Override
	public void onColorSelection(Color color) {
		if (mCurSharp != null && color != null) {
			backup();
			mCurSharp.setPaintColor(color);
		}
	}

	@Override
	public void onStrokeLineWidthSelection(int strokeLineWidth) {
		if (mCurSharp != null) {
			backup();
			mCurSharp.setPaintStrokeLineWidth(strokeLineWidth);
		}
	}

	@Override
	public void onStrokeDashSelection(float[] strokeDash) {
		if (mCurSharp != null) {
			backup();
			mCurSharp.setPaintStrokeDash(strokeDash);
		}
	}

	public void hideExtFrames() {
		if(lineProperty != null && lineProperty.isVisible()) {
			lineProperty.setVisible(false);
		}
		if(polygonProperty != null && polygonProperty.isVisible()) {
			polygonProperty.setVisible(false);
		}
		if(ellipseProperty != null && ellipseProperty.isVisible()) {
			ellipseProperty.setVisible(false);
		}
		if(circleProperty != null && circleProperty.isVisible()) {
			circleProperty.setVisible(false);
		}
	}
}
