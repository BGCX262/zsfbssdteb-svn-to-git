package com.f2.frame;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.f2.frame.selector.BackgroundSelector;
import com.f2.frame.selector.ColorSelector;
import com.f2.frame.selector.GeometrySelector;
import com.f2.frame.selector.StrokeLineSelector;
import com.f2.listener.CanvasResetListener;
import com.f2.tool.PaintBrush;
import com.f2.tool.constant.SharpType;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener, CanvasResetListener, ComponentListener {

	private static final int MAIN_FRAME_X = 10;
	private static final int MAIN_FRAME_Y = 610;
	private static final int MAIN_FRAME_HEIGHT = 162;
	private static final int MAIN_FRAME_WIDTH = 876;
	private static final int MAIN_FRAME_SMALL_WIDTH = 176;

	private PaintBrush mPaintBrush;
	private Canvas mCanvas = null;
	private JPanel mContentPane;
	
	private PageManager pageManager = null;
	private Wander wander = null;
	private About about = null;
	private StrokeLineSelector strokeLineSelector = null;
	private GeometrySelector geometrySelector = null;
	private BackgroundSelector backgroundSelector = null;
	private ColorSelector colorSelector = null;

	private JButton btnSwitch = null;
	private JButton btnHandwriting = null;
	private JButton btnGeometry = null;
	private JButton btnBackground = null;
	private JButton btnPicture = null;
	private JButton btnColor = null;
	private JButton btnWidth = null;

	private JButton btnSharpSelect = null;
	private JButton btnUndo = null;
	private JButton btnClear = null;
		
	private JFileChooser fileChooser = null;
	
	private JMenuBar menuBar = new JMenuBar();

	private JMenu menuFile;
	private JMenuItem miNew;
	private JMenuItem miOpen;
	private JMenuItem miSaveAs;
	private JMenuItem miSave;
	private JMenuItem miExit;
	
	private JMenu menuView;
	private JCheckBoxMenuItem miPageManager;
	private JCheckBoxMenuItem miWander;
	
	private JMenu menuHelp;
	private JMenuItem miAbout;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {		
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//�޸ı�����ͼ��
		//setIconImage(Toolkit.getDefaultToolkit().createImage("icon.png"));
		mPaintBrush = new PaintBrush(null); //ȫ�ֵĻ���
		mCanvas = new Canvas(mPaintBrush);
		pageManager = new PageManager(mCanvas, this);
		updateTitle(pageManager.getDataFilePath());
		
		setBounds(MAIN_FRAME_X, MAIN_FRAME_Y, MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);
		mContentPane = new JPanel();
		mContentPane.setBorder(null);
		setContentPane(mContentPane);
		mContentPane.setLayout(new GridLayout(1, 0, 0, 0));
			
		addMainButton(); //�����Ҫ�İ�ť
		addMainMenu(); // ������˵�
		switchPaintMode(); // ����ʱ���л����滭ģʽ
		
		this.addComponentListener(this); // ��Ӵ����¼�
	}
	
	private void addMainButton() 
	{
		btnSwitch = new JButton(""); //��ʼ
		btnSwitch.setIcon(new ImageIcon("images/computer.png"));
		btnSwitch.setToolTipText("��ʼ����");
		btnSwitch.addActionListener(this);
		mContentPane.add(btnSwitch);
		
		btnHandwriting = new JButton(""); //����
		btnHandwriting.setIcon(new ImageIcon("images/handwriting.png"));
		btnHandwriting.setToolTipText("��д��");
		btnHandwriting.addActionListener(this);
		mContentPane.add(btnHandwriting);
		
		btnGeometry = new JButton(""); //����ͼ��
		btnGeometry.setIcon(new ImageIcon("images/geometry.png"));
		btnGeometry.setToolTipText("���Ƽ���ͼ��");
		btnGeometry.addActionListener(this);
		mContentPane.add(btnGeometry);

		btnBackground = new JButton(""); //����ѡ��
		btnBackground.setIcon(new ImageIcon("images/background.png"));
		btnBackground.setToolTipText("����ѡ��");
		btnBackground.addActionListener(this);
		mContentPane.add(btnBackground);
		
		btnPicture = new JButton(""); //ͼƬ
		btnPicture.setIcon(new ImageIcon("images/picture.png"));
		btnPicture.setToolTipText("����ͼƬ");
		btnPicture.addActionListener(this);
		mContentPane.add(btnPicture);
		
		btnColor = new JButton(""); //��ɫ
		btnColor.setIcon(new ImageIcon("images/color.png"));
		btnColor.setToolTipText("ѡ�񻭱�������ɫ");
		btnColor.addActionListener(this);
		mContentPane.add(btnColor);
		
		btnWidth = new JButton(""); //�ʿ�
		btnWidth.setIcon(new ImageIcon("images/width.png"));
		btnWidth.setToolTipText("���û��������ʿ�");
		btnWidth.addActionListener(this);
		mContentPane.add(btnWidth);

		btnSharpSelect = new JButton(""); //ѡ��
		btnSharpSelect.setIcon(new ImageIcon("images/select.png"));
		btnSharpSelect.setToolTipText("ͼ��ʰȡ");
		btnSharpSelect.addActionListener(this);
		mContentPane.add(btnSharpSelect);
		
		btnUndo = new JButton(""); //����
		btnUndo.setIcon(new ImageIcon("images/undo.png"));
		btnUndo.setToolTipText("������һ�β����ͼ��");
		btnUndo.addActionListener(this);
		mContentPane.add(btnUndo);

		btnClear = new JButton(""); //����
		btnClear.setIcon(new ImageIcon("images/clear.png"));
		btnClear.setToolTipText("����");
		btnClear.addActionListener(this);
		mContentPane.add(btnClear);
	}

	private void addMainMenu() {
		
		this.setJMenuBar(menuBar); 
		
		menuFile = new JMenu("�ļ�");
		addMenu(menuFile);
		miNew = new JMenuItem("�½�", new ImageIcon("images/menu/new.png"));
		miOpen = new JMenuItem("��", new ImageIcon("images/menu/open.png"));
		miSave = new JMenuItem("����", new ImageIcon("images/menu/save.png"));
		miSaveAs = new JMenuItem("���Ϊ", new ImageIcon("images/menu/saveas.png"));
		miExit = new JMenuItem("�˳�", new ImageIcon("images/menu/exit.png"));
		addMenuItem(menuFile, miNew);
		addMenuItem(menuFile, miOpen);
		addMenuItem(menuFile, miSave);
		addMenuItem(menuFile, miSaveAs);
		menuFile.addSeparator();
		addMenuItem(menuFile, miExit);
		
		menuView = new JMenu("��ͼ");
		addMenu(menuView);
		miPageManager = new JCheckBoxMenuItem("ҳ�����", new ImageIcon("images/menu/pages.png"));
		miPageManager.setSelected(true);
		addMenuItem(menuView, miPageManager);		
		miWander = new JCheckBoxMenuItem("ҳ������", new ImageIcon("images/menu/wander.png"));
		miWander.setSelected(false);
		addMenuItem(menuView, miWander);	
		
		menuHelp = new JMenu("����");
		addMenu(menuHelp);
		miAbout = new JMenuItem("����", new ImageIcon("images/menu/about.png"));
		addMenuItem(menuHelp, miAbout);		
	}

	private void addMenu(JMenu menu) {
		menu.setFont(menu.getFont().deriveFont(22.0f));
		menuBar.add(menu);
	}
	
	private void addMenuItem(JMenu menu, JMenuItem mi) {
		mi.setFont(mi.getFont().deriveFont(18.0f));
		mi.addActionListener(this);
		menu.add(mi);
	}	
	
	public void actionPerformed(ActionEvent event) {
		//System.out.println("MainFrame.actionPerformed");
		
		if(event.getSource() == btnSwitch) {
			switchPaintMode();
		} else if(event.getSource() == btnHandwriting) {
			startHandwriting();
		} else if(event.getSource() == btnGeometry) {
			showGeometrySelector();
		} else if(event.getSource() == btnBackground) {
			showBackgroundSelector();
		} else if(event.getSource() == btnPicture) {
			showPictureSelector();
		} else if(event.getSource() == btnColor) {
			showColorSelector();
		} else if(event.getSource() == btnWidth) {
			showStrokeLineSelector();
		} else if(event.getSource() == btnSharpSelect) {
			startSelectSharp();
		} else if(event.getSource() == btnUndo) {
			undoSharpAdd();
		} else if(event.getSource() == btnClear) {
			clearSharps();
		} else if(event.getSource() == miNew) {
			fileNew();
		} else if(event.getSource() == miOpen) {
			fileOpen();
		} else if(event.getSource() == miSave) {
			fileSave();
		} else if(event.getSource() == miSaveAs) {
			fileSaveAs();
		} else if(event.getSource() == miExit) {
			exit();
		} else if(event.getSource() == miPageManager) {
			showPageManager();
		} else if(event.getSource() == miWander) {
			showWander();
		} else if(event.getSource() == miAbout) {
			showAbout();
		} 

		repaintCanvas();
	}

	private void fileNew() {
		pageManager.fileNew();
		updateTitle(pageManager.getDataFilePath());
	}

	private void fileOpen() {
		pageManager.fileOpen();
		updateTitle(pageManager.getDataFilePath());
	}

	private void fileSave() {
		pageManager.fileSave();
		updateTitle(pageManager.getDataFilePath());
	}

	private void fileSaveAs() {
		pageManager.fileSaveAs();
		updateTitle(pageManager.getDataFilePath());
	}

	private void updateTitle(String dataFilePath) {	
		if(dataFilePath != null && dataFilePath.length() > 0) {
			this.setTitle("��ѧ��.���ѧ�� - " + dataFilePath);
		} else {
			this.setTitle("��ѧ��.���ѧ�� - Five Best Student Board");
		}
	}
	
	private void exit() {
		dispose();
		System.exit(0);
	}

	private void switchPaintMode() {
		if(mCanvas.isVisible()) {
			this.setSize(MAIN_FRAME_SMALL_WIDTH, this.getHeight());
			btnSwitch.setText("");
			btnSwitch.setIcon(new ImageIcon("images/paintting.png"));
			btnSwitch.setToolTipText("�л�������ģʽ"); //btnStart.setText("����");
			mContentPane.removeAll();
			//contentPane.add(btnMainMenu);
			mContentPane.add(btnSwitch);
			//contentPane.validate();
			mCanvas.setVisible(false);
			if(strokeLineSelector != null && strokeLineSelector.isVisible()) {
				strokeLineSelector.setVisible(false);
			}
			if(about != null && about.isVisible()) {
				about.setVisible(false);
			}
		} else {
			this.setSize(MAIN_FRAME_WIDTH, this.getHeight());
			btnSwitch.setText("");
			btnSwitch.setIcon(new ImageIcon("images/computer.png"));
			btnSwitch.setToolTipText("�л���PC��ʾ��"); //btnStart.setText("����");
			mContentPane.add(btnHandwriting);
			mContentPane.add(btnGeometry);
			mContentPane.add(btnBackground);
			mContentPane.add(btnPicture);
			mContentPane.add(btnColor);
			mContentPane.add(btnWidth);
			mContentPane.add(btnSharpSelect);
			mContentPane.add(btnUndo);
			mContentPane.add(btnClear);
			
			//contentPane.validate();
			mCanvas.setVisible(true);
			mCanvas.initBufferImage();
			//��������ʼͼ��
			mCanvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
		
		if(pageManager.isVisible()) {
			pageManager.setVisible(false);
		}
		else {
			pageManager.setVisible(true);
		}
	}

	private void startHandwriting() {
		mPaintBrush.setType(SharpType.HANDWRITING);
	}

	private void showGeometrySelector() {
		if(geometrySelector == null) {
			geometrySelector = new GeometrySelector(mPaintBrush, this);
		}
		Point loc0 = MainFrame.this.getLocation();
		Point loc1 = mContentPane.getLocation();
		Point loc2 = btnGeometry.getLocation();
		geometrySelector.setLocation(loc0.x + loc2.x, loc0.y + loc1.y - geometrySelector.getHeight());
		if(geometrySelector.isVisible()) {
			geometrySelector.setVisible(false);
		} else {
			geometrySelector.setVisible(true);
		}
	}

	private void showBackgroundSelector() {
		if(backgroundSelector == null) {
			backgroundSelector = new BackgroundSelector(this);
		}
		Point loc0 = MainFrame.this.getLocation();
		Point loc1 = mContentPane.getLocation();
		Point loc2 = btnBackground.getLocation();
		if(mCanvas != null && mCanvas.getCanvasItem() != null && mCanvas.getCanvasItem().getBackground() != null) {
			backgroundSelector.setBackground(mCanvas.getCanvasItem().getBackground());
		}
		backgroundSelector.setLocation(loc0.x + loc2.x, loc0.y + loc1.y - backgroundSelector.getHeight());
		if(backgroundSelector.isVisible()) {
			backgroundSelector.setVisible(false);
		} else {
			backgroundSelector.setVisible(true);
		}
	}

	private void showPictureSelector() {
		if(fileChooser == null) {				
			String [] formats = javax.imageio.ImageIO.getReaderFormatNames();
			StringBuffer formatHint = new StringBuffer(formats.length * 5);
			formatHint.append("�ĵ�(");
			for (String str : formats) {
				formatHint.append("*.");
				formatHint.append(str);
			}
			String[] openFormats = new String[formats.length + 1];
			System.arraycopy(formats, 0, openFormats, 0, formats.length);
			openFormats[formats.length] = "ppt";
			formatHint.append("*.");
			formatHint.append(openFormats[formats.length]);
			formatHint.append(")");
			
			fileChooser = new JFileChooser(".");
			fileChooser.setDialogTitle("ѡ��һ���ⲿ�ļ�...");
			fileChooser.setAcceptAllFileFilterUsed(false);
			//����ļ���չ���Ĺ�����
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(formatHint.toString(), openFormats));
			//fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("���л�(*.wmf)", new String[]{"wmf"}));
		}
		int result = fileChooser.showOpenDialog(null);
		//String fileName = fileChooser.getName();
		//System.out.println("JFileChooser, result=" + result + ", fileName=" + fileName);
		File file = fileChooser.getSelectedFile();
		if(result == 0 && file != null) {
			String filepath = file.getPath();
			//String filename = file.getName();
			if(mCanvas != null) {
				mCanvas.setFilePath(filepath); //sPaintBrush.setFilepath(filepath);
			}
			mPaintBrush.setType(SharpType.PICTURE);
		}
	}

	private void showColorSelector() {
		if(colorSelector == null) {
			colorSelector = new ColorSelector(this);
		}
		Point loc0 = MainFrame.this.getLocation();
		Point loc1 = mContentPane.getLocation();
		Point loc2 = btnColor.getLocation();
		colorSelector.setPaintBrush(mPaintBrush);
		colorSelector.setLocation(loc0.x + loc2.x, loc0.y + loc1.y - colorSelector.getHeight());
		if(colorSelector.isVisible()) {
			colorSelector.setVisible(false);
		} else {
			colorSelector.setVisible(true);
		}
	}

	private void showStrokeLineSelector() {
		if(strokeLineSelector == null) {
			strokeLineSelector = new StrokeLineSelector(this);
		}
		Point loc0 = MainFrame.this.getLocation();
		Point loc1 = mContentPane.getLocation();
		Point loc2 = btnWidth.getLocation();
		strokeLineSelector.setPaintBrush(mPaintBrush);
		strokeLineSelector.setLocation(loc0.x + loc2.x, loc0.y + loc1.y - strokeLineSelector.getHeight());
		if(strokeLineSelector.isVisible()) {
			strokeLineSelector.setVisible(false);
		} else {
			strokeLineSelector.setVisible(true);
		}
	}

	private void startSelectSharp() {
		mPaintBrush.setType(SharpType.SELECTION);
	}

	private void undoSharpAdd() {
		if(mCanvas != null) {
			mCanvas.removeLastSharp();
		}
	}

	private void clearSharps() {
		if(mCanvas != null) {
			mCanvas.clearSharps();
		}
	}

	private void showAbout() {
		if(about == null) {
			about = new About(this);
		}
		about.setLocationRelativeTo(null);
		if(about.isVisible()) {
			about.setVisible(false);
		} else {
			about.setVisible(true);
		}
	}
	
	private void showPageManager() {
		if(pageManager.isVisible() && pageManager.getExtendedState() == NORMAL) {
			pageManager.setExtendedState(ICONIFIED);	
			pageManager.setVisible(false);
		} else {
			pageManager.setExtendedState(NORMAL);	
			pageManager.setVisible(true);
		}
		updateCheckBoxMenuItem();
	}

	private void showWander() {
		if(wander == null) {
			wander = new Wander(this);
			wander.setWanderListener(mCanvas);
			wander.setZoomListener(mCanvas);
		}
		mCanvas.setCanvasResetListener(this);
		//wander.setLocationRelativeTo(null);
		if(wander.isVisible() && wander.getExtendedState() == NORMAL) {
			wander.setExtendedState(ICONIFIED);	
			wander.setVisible(false);
		} else {
			wander.setExtendedState(NORMAL);	
			wander.setVisible(true);
		}
		updateCheckBoxMenuItem();
	}
	
	private void updateCheckBoxMenuItem() {
		if(miPageManager != null && pageManager != null) {
			miPageManager.setSelected(pageManager.isVisible() && pageManager.getExtendedState() == NORMAL);
		}
		if(miWander != null && wander != null) {
			miWander.setSelected(wander.isVisible() && wander.getExtendedState() == NORMAL);
		}
	}
	
	private void updateCursor() {
		// ����ͼ�κͻ���״̬�л����ͼ��
		if(mCanvas != null && mPaintBrush.getType() == SharpType.SELECTION) {
			mCanvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if(mCanvas != null){
			mCanvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} 
	}

	private void repaintCanvas() {
		if(mCanvas != null && mCanvas.isVisible()) {
			mCanvas.repaint();
		}		
		updateCursor();	
		updateCheckBoxMenuItem();
	}
	
	@Override
	public void componentHidden(ComponentEvent event) {
//		System.out.println("componentHidden");
		repaintCanvas();
	}

	@Override
	public void componentMoved(ComponentEvent event) {
//		System.out.println("componentMoved");
		repaintCanvas();
	}

	@Override
	public void componentResized(ComponentEvent event) {
//		System.out.println("componentResized");
		repaintCanvas();
	}

	@Override
	public void componentShown(ComponentEvent event) {
//		System.out.println("componentShown");
		repaintCanvas();
	}

	@Override
	public void resetCanvasItem() {
		System.out.println("resetCanvasItem");
		if(wander != null) {
			// FIXME
			wander.resetZoomValue(mCanvas.getZoomValue());
		}
	}
}
