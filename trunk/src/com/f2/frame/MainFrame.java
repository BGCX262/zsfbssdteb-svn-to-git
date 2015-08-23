package com.f2.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.f2.frame.data.Background;
import com.f2.frame.data.CanvasItem;
import com.f2.frame.data.CanvasPage;
import com.f2.frame.selector.BackgroundSelector;
import com.f2.frame.selector.ColorSelector;
import com.f2.frame.selector.EraserSelector;
import com.f2.frame.selector.GeometrySelector;
import com.f2.frame.selector.StrokeLineSelector;
import com.f2.listener.CanvasResetListener;
import com.f2.listener.EraserListener;
import com.f2.sharp.AbsMenuSharp;
import com.f2.socket.TCPSocketServer;
import com.f2.tool.ClipBoard;
import com.f2.tool.CursorTool;
import com.f2.tool.ImageTool;
import com.f2.tool.PaintBrush;
import com.f2.tool.constant.SharpType;
import com.f2.tool.file.FileHelper;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener, 
	CanvasResetListener, ComponentListener,
	ColorSelector.ColorSelectionListener, 
	StrokeLineSelector.StrokeSelectionListener,
	BackgroundSelector.BackgroundSelectionListener,
	EraserListener {

	private static final int MAIN_FRAME_HEIGHT = 162;
	private static final int MAIN_FRAME_FULL_WIDTH = 963; //9����ť=789; //10����ť=876; //11����ť=963;
	private static final int MAIN_FRAME_SMALL_WIDTH = 176;
	
	private static final int SOCKET_PORT = 9999;

	private PaintBrush mPaintBrush;
	private CanvasFrame mCanvas = null;
	private JPanel mContentPane;
	
	
	private TCPSocketServer socketServer;
	
	private PageManager pageManager = null;
	private Wander wander = null;
	private About about = null;
	private EraserSelector eraserSelector = null;
	private StrokeLineSelector strokeLineSelector = null;
	private GeometrySelector geometrySelector = null;
	private BackgroundSelector backgroundSelector = null;
	private ColorSelector colorSelector = null;

	private JButton btnSwitch = null;
	private JButton btnHandwriting = null;
	private JButton btnSimulator = null;
	private JButton btnGeometry = null;
	private JButton btnBackground = null;
	private JButton btnPicture = null;
	private JButton btnColor = null;
	private JButton btnWidth = null;

	private JButton btnUndoRedo = null;
	private JButton btnEraser = null;
	private JButton btnSharpSelect = null;
		
	private JFileChooser fcImageImport = null;
	private JFileChooser fcImageExport = null;
	
	private JMenuBar menuBar = new JMenuBar();

	private JMenu menuFile;
	private JMenuItem miNew;
	private JMenuItem miOpen;
	private JMenuItem miSave;
	private JMenuItem miSaveAs;
	private JMenuItem miExit;
	private JMenuItem miPrint;
	private JMenuItem miPrintAll;
	private JMenuItem miExportImage;
	private JMenuItem miExportAllImage;

	private JMenu menuFileMini;
	private JMenuItem miNewMini;
	private JMenuItem miOpenMini;
	private JMenuItem miExitMini;
	private JMenuItem miSaveMini;
	private JMenuItem miSaveAsMini;
	
	private JMenu menuView;
	private JCheckBoxMenuItem miPageManager;
	private JCheckBoxMenuItem miWander;
	private JMenuItem miPaste;
	private JMenuItem miScreenShot;
	
	private JMenu menuHelp;
	private JMenuItem miIpInfo;
	private JMenuItem miAbout;
	
	private boolean isShowUndoBtn = true;
	
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
					final MainFrame frame = new MainFrame();
					frame.addWindowListener(new WindowAdapter(){
			            public void windowClosing(WindowEvent e){            
			            	//���ڹر�ʱ����Ӧ������� - ѯ���Ƿ�ر�
			        		frame.exit();
			            }
			            public void windowIconified(WindowEvent e){
			                //������С��ʱ����Ӧ������� - ��������С��,ʹ֮�ָ�������״̬
			            	frame.setExtendedState(NORMAL);	
			            }
			        });
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
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//�޸ı�����ͼ��
		//setIconImage(Toolkit.getDefaultToolkit().createImage("icon.png"));
		mPaintBrush = new PaintBrush(null); //ȫ�ֵĻ���
		mCanvas = new CanvasFrame(mPaintBrush);
		pageManager = new PageManager(mCanvas, this);
		wander = new Wander(this);

		updateTitle(pageManager.getDataFilePath());
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(24, (int)(dim.getHeight()-MAIN_FRAME_HEIGHT-28), MAIN_FRAME_FULL_WIDTH, MAIN_FRAME_HEIGHT);
		mContentPane = new JPanel();
		mContentPane.setBorder(null);
		setContentPane(mContentPane);
		mContentPane.setLayout(new GridLayout(1, 0, 0, 0));
			
		addMainButton(); //�����Ҫ�İ�ť
		addMainMenu(); // ������˵�
		switchPaintMode(true); // ����ʱ���л����滭ģʽ
		
		this.addComponentListener(this); // ��Ӵ����¼�	
		
		try {
			socketServer = new TCPSocketServer(SOCKET_PORT);
			socketServer.setSocketStateListener(mCanvas);
			socketServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void addMainButton() 
	{
		btnSwitch = new JButton(""); //��ʼ
		btnSwitch.setIcon(ImageTool.getResImageIcon("/res/images/computer.png"));
		btnSwitch.setToolTipText("��ʼ����");
		btnSwitch.addActionListener(this);
		mContentPane.add(btnSwitch);
		
		btnHandwriting = new JButton(""); //��д��
		btnHandwriting.setIcon(ImageTool.getResImageIcon("/res/images/handwriting.png"));
		btnHandwriting.setToolTipText("��д��");
		btnHandwriting.addActionListener(this);
		mContentPane.add(btnHandwriting);
		
		btnSimulator = new JButton(""); //���ܱ�,����д����ϳ���֪����ͼ��
		btnSimulator.setIcon(ImageTool.getResImageIcon("/res/images/simulator.png"));
		btnSimulator.setToolTipText("���ܱ�,����д����ϳ���֪����ͼ��");
		btnSimulator.addActionListener(this);
		mContentPane.add(btnSimulator);
		
		btnGeometry = new JButton(""); //����ͼ��
		btnGeometry.setIcon(ImageTool.getResImageIcon("/res/images/geometry.png"));
		btnGeometry.setToolTipText("���Ƽ���ͼ��");
		btnGeometry.addActionListener(this);
		mContentPane.add(btnGeometry);

		btnBackground = new JButton(""); //����ѡ��
		btnBackground.setIcon(ImageTool.getResImageIcon("/res/images/background.png"));
		btnBackground.setToolTipText("����ѡ��");
		btnBackground.addActionListener(this);
		mContentPane.add(btnBackground);
		
		btnPicture = new JButton(""); //ͼƬ
		btnPicture.setIcon(ImageTool.getResImageIcon("/res/images/picture.png"));
		btnPicture.setToolTipText("����ͼƬ��Office�ĵ�");
		btnPicture.addActionListener(this);
		mContentPane.add(btnPicture);
		
		btnColor = new JButton(""); //��ɫ
		btnColor.setIcon(ImageTool.getResImageIcon("/res/images/color.png"));
		btnColor.setToolTipText("ѡ�񻭱�������ɫ");
		btnColor.addActionListener(this);
		mContentPane.add(btnColor);
		
		btnWidth = new JButton(""); //�ʿ�
		btnWidth.setIcon(ImageTool.getResImageIcon("/res/images/width.png"));
		btnWidth.setToolTipText("���û��������ʿ�");
		btnWidth.addActionListener(this);
		mContentPane.add(btnWidth);
		
		btnEraser = new JButton(""); //����
		btnEraser.setIcon(ImageTool.getResImageIcon("/res/images/eraser.png"));
		btnEraser.setToolTipText("����");
		btnEraser.addActionListener(this);
		mContentPane.add(btnEraser);

		btnUndoRedo = new JButton(""); //����
		btnUndoRedo.setIcon(ImageTool.getResImageIcon("/res/images/undo.png"));
		btnUndoRedo.setToolTipText("������һ�β����ͼ��");
		btnUndoRedo.addActionListener(this);
		mContentPane.add(btnUndoRedo);

		btnSharpSelect = new JButton(""); //ѡ��
		btnSharpSelect.setIcon(ImageTool.getResImageIcon("/res/images/select.png"));
		btnSharpSelect.setToolTipText("ͼ��ʰȡ");
		btnSharpSelect.addActionListener(this);
		mContentPane.add(btnSharpSelect);
	}

	private void addMainMenu() {
		
		this.setJMenuBar(menuBar); 
		
		menuFile = new JMenu("�ļ�");
		addMenu(menuFile);
		miNew = new JMenuItem("�½�", ImageTool.getResImageIcon("/res/images/menu/new.png"));
		addMenuItem(menuFile, miNew);
		miOpen = new JMenuItem("��", ImageTool.getResImageIcon("/res/images/menu/open.png"));
		addMenuItem(menuFile, miOpen);
		miSave = new JMenuItem("����", ImageTool.getResImageIcon("/res/images/menu/save.png"));
		addMenuItem(menuFile, miSave);
		miSaveAs = new JMenuItem("���Ϊ...", ImageTool.getResImageIcon("/res/images/menu/saveas.png"));
		addMenuItem(menuFile, miSaveAs);
		
		menuFile.addSeparator();
		miPrint = new JMenuItem("��ҳ��ӡ", ImageTool.getResImageIcon("/res/images/menu/print.png"));
		addMenuItem(menuFile, miPrint);
		miPrintAll = new JMenuItem("ȫ����ӡ", ImageTool.getResImageIcon("/res/images/menu/printall.png"));
		addMenuItem(menuFile, miPrintAll);
			
		menuFile.addSeparator();
		miExportImage = new JMenuItem("��ҳ����ͼƬ", ImageTool.getResImageIcon("/res/images/menu/imageexport.png"));
		addMenuItem(menuFile, miExportImage);
		miExportAllImage = new JMenuItem("ȫ������ͼƬ", ImageTool.getResImageIcon("/res/images/menu/imageexportall.png"));
		addMenuItem(menuFile, miExportAllImage);
		
		menuFile.addSeparator();
		miExit = new JMenuItem("�˳�", ImageTool.getResImageIcon("/res/images/menu/exit.png"));
		addMenuItem(menuFile, miExit);
		
		menuView = new JMenu("��ͼ");
		addMenu(menuView);
		miPageManager = new JCheckBoxMenuItem("ҳ�����", ImageTool.getResImageIcon("/res/images/menu/pages.png"));
		miPageManager.setSelected(true);
		addMenuItem(menuView, miPageManager);		
		miWander = new JCheckBoxMenuItem("ҳ������", ImageTool.getResImageIcon("/res/images/menu/wander.png"));
		miWander.setSelected(false);
		addMenuItem(menuView, miWander);	
		
		menuView.addSeparator();
		miPaste = new JMenuItem("ճ��ͼ��", ImageTool.getResImageIcon("/res/images/menu/paste.png"));
		addMenuItem(menuView, miPaste);		
				
		menuView.addSeparator();
		miScreenShot = new JMenuItem("��Ļ��ͼ", ImageTool.getResImageIcon("/res/images/menu/screenshot.png"));
		addMenuItem(menuView, miScreenShot);		
		
		menuHelp = new JMenu("����");
		addMenu(menuHelp);
		miIpInfo = new JMenuItem("IP��Ϣ", ImageTool.getResImageIcon("/res/images/menu/about.png"));
		addMenuItem(menuHelp, miIpInfo);	
		miAbout = new JMenuItem("����", ImageTool.getResImageIcon("/res/images/menu/about.png"));
		addMenuItem(menuHelp, miAbout);		
		
		menuFileMini = new JMenu("�ļ�");
		miNewMini = new JMenuItem("�½�", ImageTool.getResImageIcon("/res/images/menu/new.png"));
		addMenuItem(menuFileMini, miNewMini);
		miOpenMini = new JMenuItem("��", ImageTool.getResImageIcon("/res/images/menu/open.png"));
		addMenuItem(menuFileMini, miOpenMini);
		miSaveMini = new JMenuItem("����", ImageTool.getResImageIcon("/res/images/menu/save.png"));
		addMenuItem(menuFileMini, miSaveMini);
		miSaveAsMini = new JMenuItem("���Ϊ...", ImageTool.getResImageIcon("/res/images/menu/saveas.png"));
		addMenuItem(menuFileMini, miSaveAsMini);
		menuFileMini.addSeparator();
		miExitMini = new JMenuItem("�˳�", ImageTool.getResImageIcon("/res/images/menu/exit.png"));
		addMenuItem(menuFileMini, miExitMini);
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
		if(mCanvas != null) {
			mCanvas.cancelSelected(); // ȡ��ѡ��״̬
		}
		if(event.getSource() == btnSwitch) {
			switchPaintMode(!mCanvas.isVisible());
		} else if(event.getSource() == btnHandwriting) {
			startHandwriting();
		} else if(event.getSource() == btnSimulator) {
			startSimulate();
		} else if(event.getSource() == btnGeometry) {
			switchGeometrySelector();
		} else if(event.getSource() == btnBackground) {
			switchBackgroundSelector();
		} else if(event.getSource() == btnPicture) {
			switchPictureSelector();
		} else if(event.getSource() == btnColor) {
			switchColorSelector();
		} else if(event.getSource() == btnWidth) {
			switchStrokeLineSelector();
		} else if(event.getSource() == btnEraser) {
			switchEraserSelector();
		} else if(event.getSource() == btnUndoRedo) {
			undoRedo();
		} else if(event.getSource() == btnSharpSelect) {
			startSelectSharp();
		} else if(event.getSource() == miNew || event.getSource() == miNewMini) {
			fileNew();
		} else if(event.getSource() == miOpen || event.getSource() == miOpenMini) {
			fileOpen();
		} else if(event.getSource() == miSave || event.getSource() == miSaveMini) {
			fileSave();
		} else if(event.getSource() == miSaveAs || event.getSource() == miSaveAsMini) {
			fileSaveAs();
		} else if(event.getSource() == miExportImage) {
			fileExportImage();
		} else if(event.getSource() == miExportAllImage) {
			fileExportImageAll();			
		} else if(event.getSource() == miExit || event.getSource() == miExitMini) {
			exit();
		} else if(event.getSource() == miPageManager) {
			switchPageManager();
		} else if(event.getSource() == miWander) {
			switchWander();
		} else if(event.getSource() == miPaste) {
			sharpPaste();
		} else if(event.getSource() == miScreenShot) {
			takeScreenShot();
		} else if(event.getSource() == miPrint) {
			print();
		} else if(event.getSource() == miPrintAll) {
			printAll();
		} else if(event.getSource() == miIpInfo) {
			showIpInfo();
		} else if(event.getSource() == miAbout) {
			switchAbout();
		} 

		repaintCanvas();
	}

	private void sharpPaste() {
		AbsMenuSharp sharp = (AbsMenuSharp) ClipBoard.get(ClipBoard.SHARP_COPY);
		if(sharp == null) {
			// TODO ��ʾ
		} else {
			mCanvas.sharpPaste(sharp);
		}
	}

	private void takeScreenShot() {
		new Thread(new Runnable() {
			public void run() {
				try {
					//Thread.sleep(500L);
					switchPaintMode(false);
					setLocation(-2048, -2048);
					pageManager.pageNew();
					Thread.sleep(800L);
					BufferedImage image = ImageTool.getScreenShot();
					mCanvas.addScreenShot(image);
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					setLocation(24, (int)(dim.getHeight()-MAIN_FRAME_HEIGHT-28));
					switchPaintMode(true);
					validate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void printImage(PrintJob job, BufferedImage image) {
		Graphics pg = job.getGraphics();
		if (pg != null && image != null) {
			Dimension pdim = job.getPageDimension();
			double sx = image.getWidth() * 1.0f / pdim.width;
			double sy = image.getHeight() * 1.0f / pdim.height;
			double s = sx < sy ? sy : sx;
			int w = (int) (image.getWidth() / s);
			int h = (int) (image.getHeight() / s);
			int x = (pdim.width - w) / 2;
			int y = (pdim.height - h) / 2;					
			pg.drawImage(image, x, y, w, h, null);
			pg.dispose();
		}
	}

	private void print() {
		PrintJob job = null;
		try {
			Properties prop = new Properties();
			job = Toolkit.getDefaultToolkit().getPrintJob(this, "best", prop);
			if (job != null) {
				BufferedImage image = ImageTool.getFrameImage(mCanvas);
				printImage(job, image);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(job != null) {
				job.end();
				job = null;
			}
		}
	}
	
	private void printAll() {
		PrintJob job = null;
		try {
			Properties prop = new Properties();
			job = Toolkit.getDefaultToolkit().getPrintJob(this, "best", prop);
			if (job != null) {
				CanvasPage page = pageManager.getCanvasPageData();
				int cur = page.getSelectedIndex();
				for(int i = 0; i < page.size(); i++) {
					mCanvas.setCanvasItem(page.get(i));
					BufferedImage image = ImageTool.getFrameImage(mCanvas);
					printImage(job, image);
				}
				mCanvas.setCanvasItem(page.get(cur));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(job != null) {
				job.end();
				job = null;
			}
		}
	}
	
	private JFileChooser getImageExportFileChooser() {
		if(fcImageExport == null) {
			String[] formats = new String[]{"png", "bmp", "jpg"};				   
			fcImageExport = new JFileChooser(".");
			fcImageExport.setDialogTitle("ѡ��һ��ͼƬ�ļ�...");
			fcImageExport.setAcceptAllFileFilterUsed(false);
			//����ļ���չ���Ĺ�����
			fcImageExport.addChoosableFileFilter(new FileNameExtensionFilter("ͼƬ(*.png|*.bmp|*.jpg)", formats));
		}
		return fcImageExport;
	}

	private void fileExportImage() {
		try {
			JFileChooser fc = getImageExportFileChooser();
			int result = fc.showSaveDialog(null);
			File file = fc.getSelectedFile();
			if(result == 0 && file != null) {
				String filepath = file.getPath();
				String format = "";
				int pos = filepath.lastIndexOf('.');
				if(pos > 0) {
					format = filepath.substring(pos + 1);
				} else {
					format = "png";
					filepath += "." + format;
				}
				BufferedImage image = ImageTool.getFrameImage(mCanvas);
				FileHelper.saveImage(image, format, file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fileExportImageAll() {
		try {
			JFileChooser fc = getImageExportFileChooser();
			int result = fc.showSaveDialog(null);
			File file = fc.getSelectedFile();
			if(result == 0 && file != null) {
				String filepath = file.getPath();
				String format = "";
				int pos = filepath.lastIndexOf('.');
				if(pos >= 0) {
					format = filepath.substring(pos + 1);
				} else {
					format = "png";
					filepath += "." + format;
				}
				CanvasPage page = pageManager.getCanvasPageData();
				int cur = page.getSelectedIndex();
				for(int i = 0; i < page.size(); i++) {
					mCanvas.setCanvasItem(page.get(i));
					BufferedImage image = ImageTool.getFrameImage(mCanvas);
					String fp = filepath.replace("." + format, "_" + (i+1) + "." + format);
					FileHelper.saveImage(image, format, fp);
				}
				mCanvas.setCanvasItem(page.get(cur));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fileNew() {
		pageManager.fileNew();
		updateTitle(pageManager.getDataFilePath());
		switchPaintMode(true);
	}

	private void fileOpen() {
		pageManager.fileOpen();
		updateTitle(pageManager.getDataFilePath());
		switchPaintMode(true);
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
		Object[] options = {"ȷ��", "ȡ��"};
		int result = JOptionPane.showOptionDialog(this, "�Ƿ��˳�����", "ȷ���˳�", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); 
		if(result == 0) {
			this.dispose();
			System.exit(0);
		}
	}
	
	@Override
	public void dispose() {
		if (socketServer != null) {
			socketServer.stop();
			socketServer = null;
		}
		super.dispose();
	}
	
	private void switchPaintMode(boolean visible) {
		if(!visible) {
			this.setSize(MAIN_FRAME_SMALL_WIDTH, this.getHeight());
			btnSwitch.setText("");
			btnSwitch.setIcon(ImageTool.getResImageIcon("/res/images/paintting.png"));
			btnSwitch.setToolTipText("�л�������ģʽ");
			
			mContentPane.removeAll();
			mContentPane.add(btnSwitch);
			
			mCanvas.setVisible(false);
			
			pageManager.setVisible(false);
			
			menuBar.removeAll();
			addMenu(menuFileMini);
			addMenu(menuHelp);
		} else {
			this.setSize(MAIN_FRAME_FULL_WIDTH, this.getHeight());
			btnSwitch.setText("");
			btnSwitch.setIcon(ImageTool.getResImageIcon("/res/images/computer.png"));
			btnSwitch.setToolTipText("�л���PC��ʾ��");
			
			mContentPane.add(btnHandwriting);
			mContentPane.add(btnSimulator);
			mContentPane.add(btnGeometry);
			mContentPane.add(btnBackground);
			mContentPane.add(btnPicture);
			mContentPane.add(btnColor);
			mContentPane.add(btnWidth);
			mContentPane.add(btnEraser);
			mContentPane.add(btnUndoRedo);
			mContentPane.add(btnSharpSelect);

			mCanvas.setVisible(true);
			mCanvas.initBufferImage();
			
			pageManager.setVisible(true);

			menuBar.removeAll();
			addMenu(menuFile);
			addMenu(menuView);
			addMenu(menuHelp);

			CursorTool.updateCursor(mPaintBrush.getType());
		}
		
		hideExtFrames();
	}

	private void hideExtFrames() {
		if(strokeLineSelector != null && strokeLineSelector.isVisible()) {
			strokeLineSelector.setVisible(false);
		}
		if(geometrySelector != null && geometrySelector.isVisible()) {
			geometrySelector.setVisible(false);
		}
		if(backgroundSelector != null && backgroundSelector.isVisible()) {
			backgroundSelector.setVisible(false);
		}
		if(colorSelector != null && colorSelector.isVisible()) {
			colorSelector.setVisible(false);
		}
		if(about != null && about.isVisible()) {
			about.setVisible(false);
		}
		if(wander != null && wander.isVisible()) {
			wander.setVisible(false);
		}
		if(mCanvas != null) {
			mCanvas.hideExtFrames();
		}
	}

	private void startHandwriting() {
		mPaintBrush.setType(SharpType.HANDWRITING);
	}

	private void startSimulate() {
		mPaintBrush.setType(SharpType.SIMULATE);
	}

	private void switchGeometrySelector() {
		if(geometrySelector == null) {
			geometrySelector = new GeometrySelector(this);
		}
		geometrySelector.setPaintBrush(mPaintBrush);
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

	private void switchBackgroundSelector() {
		if(backgroundSelector == null) {
			backgroundSelector = new BackgroundSelector(this);
		}
		Point loc0 = MainFrame.this.getLocation();
		Point loc1 = mContentPane.getLocation();
		Point loc2 = btnBackground.getLocation();
		if(mCanvas != null && mCanvas.getCanvasItem() != null) {
			Color color = mCanvas.getCanvasItem().getBackgroundColor();
			backgroundSelector.setBackgroundSelectionListener(this, color);
		}
		backgroundSelector.setLocation(loc0.x + loc2.x, loc0.y + loc1.y - backgroundSelector.getHeight());
		if(backgroundSelector.isVisible()) {
			backgroundSelector.setVisible(false);
		} else {
			backgroundSelector.setVisible(true);
		}
	}

	private JFileChooser getImageImportFileChooser() {
		if(fcImageImport == null) {				
			String [] imageFormats = ImageIO.getReaderFormatNames();
			String[] openFormats = new String[imageFormats.length + 5];
			System.arraycopy(imageFormats, 0, openFormats, 0, imageFormats.length);
			
			int pos = imageFormats.length;
			openFormats[pos++] = "ppt";
			openFormats[pos++] = "doc";
			openFormats[pos++] = "xls";
			openFormats[pos++] = "txt";
			openFormats[pos++] = "docx";
			
			HashSet<String> formatSet = new HashSet<String>();
			for (String str : openFormats) {
				formatSet.add(str.toLowerCase());
			}
			StringBuffer formatHint = new StringBuffer();
			formatHint.append("���ĵ�");
			formatHint.append(formatSet.toString());
			formatHint.append("");
			
			fcImageImport = new JFileChooser(".");
			fcImageImport.setDialogTitle("ѡ��һ���ⲿ�ļ�...");
			fcImageImport.setAcceptAllFileFilterUsed(false);
			//����ļ���չ���Ĺ�����
			fcImageImport.addChoosableFileFilter(new FileNameExtensionFilter(formatHint.toString(), openFormats));
		}
		return fcImageImport;
	}
	
	private void switchPictureSelector() {
		JFileChooser fc = getImageImportFileChooser();
		int result = fc.showOpenDialog(this);
		File file = fc.getSelectedFile();
		if(result == 0 && file != null) {
			String filepath = file.getPath();
			if(mCanvas != null) {
				mCanvas.setFilePath(filepath);
			}
			if (isImage(filepath)) {
				mPaintBrush.setType(SharpType.PICTURE);
			} else {
				mPaintBrush.setType(SharpType.OLE_EMBED);
			}
		}
	}
	
	private boolean isImage(String fileName) {
		fileName = fileName.toLowerCase(Locale.US);
		String [] formats = ImageIO.getReaderFormatNames();
		for (String foramt : formats) {
			if (fileName.endsWith(foramt.toLowerCase(Locale.US))) {
				return true;
			}
		}
		return false;
	}

	private void switchColorSelector() {
		if(colorSelector == null) {
			colorSelector = new ColorSelector(this);
		}
		colorSelector.setColorSelectionListener(this, mPaintBrush.getColor());
		Point loc0 = MainFrame.this.getLocation();
		Point loc1 = mContentPane.getLocation();
		Point loc2 = btnColor.getLocation();
		colorSelector.setLocation(loc0.x + loc2.x, loc0.y + loc1.y - colorSelector.getHeight());
		if(colorSelector.isVisible()) {
			colorSelector.setVisible(false);
		} else {
			colorSelector.setVisible(true);
		}
	}

	private void switchStrokeLineSelector() {
		if(strokeLineSelector == null) {
			strokeLineSelector = new StrokeLineSelector(this);
		}
		strokeLineSelector.setStrokeSelectionListener(this, mPaintBrush.getStrokeLineWidth(), mPaintBrush.getStrokeDash());
		Point loc0 = MainFrame.this.getLocation();
		Point loc1 = mContentPane.getLocation();
		Point loc2 = btnWidth.getLocation();
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

	private void switchEraserSelector() {
		if(eraserSelector == null) {
			eraserSelector = new EraserSelector(this);
		}
		eraserSelector.setEraserListener(this);
		Point loc0 = MainFrame.this.getLocation();
		Point loc1 = mContentPane.getLocation();
		Point loc2 = btnEraser.getLocation();
		eraserSelector.setLocation(loc0.x + loc2.x, loc0.y + loc1.y - eraserSelector.getHeight());
		if(eraserSelector.isVisible()) {
			eraserSelector.setVisible(false);
		} else {
			eraserSelector.setVisible(true);
		}
	}

	private void undoRedo() {
//		if(mCanvas != null) {
//			mCanvas.undoRedo();
//		}
		if(pageManager != null) {
			pageManager.undoRedo();
		}
		if(isShowUndoBtn) {
			btnUndoRedo.setIcon(ImageTool.getResImageIcon("/res/images/undo.png"));
			btnUndoRedo.setToolTipText("������һ�β���");
			isShowUndoBtn = false;
		} else {
			btnUndoRedo.setIcon(ImageTool.getResImageIcon("/res/images/redo.png"));
			btnUndoRedo.setToolTipText("������һ�β���");
			isShowUndoBtn = true;
		}		
	}

	private void showIpInfo() {
		String message = "";
		try {
			InetAddress host = InetAddress.getLocalHost();
			message = "������:" + host.getHostName() + " ����IP:" + host.getHostAddress();
		} catch (UnknownHostException e) {
			message = "��ʱ��ȡ����������������IP";
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(this, message);
	}
	
	private void switchAbout() {
		if(about == null) {
			about = new About(this);
		}
		about.setLocationRelativeTo(this);
		if(about.isVisible()) {
			about.setVisible(false);
		} else {
			about.setVisible(true);
		}
	}
	
	private void switchPageManager() {
		if(pageManager.isVisible() && pageManager.getExtendedState() == NORMAL) {
			pageManager.setExtendedState(ICONIFIED);	
			pageManager.setVisible(false);
		} else {
			pageManager.setExtendedState(NORMAL);	
			pageManager.setVisible(true);
		}
		updateCheckBoxMenuItem();
	}

	private void switchWander() {
		wander.setWanderListener(mCanvas);
		wander.setZoomListener(mCanvas);
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
	
	private void repaintCanvas() {
		if(mCanvas != null) {
			mCanvas.repaintCanvas();
		}		
		CursorTool.updateCursor(mPaintBrush.getType());
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
		if(wander != null) {
			wander.resetZoomValue();
		}
	}

	@Override
	public void onEraserAction() {
		mPaintBrush.setType(SharpType.ERASE);
	}

	@Override
	public void onEraserRemoveLastAction() {
		if(mCanvas != null) {
			mCanvas.sharpRemoveLast();
		}
	}

	@Override
	public void onEraserClearAction() {
		Object[] options = {"ȷ��", "ȡ��"};
		int result = JOptionPane.showOptionDialog(eraserSelector, "ȷ��ɾ����ǰҳ��", "ȷ��ɾ��", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); 
		if(result == 0) {
			if(mCanvas != null) {
				mCanvas.sharpClear();
			}
		} else {
			// nothing here
		}
	}

	@Override
	public void onColorSelection(Color color) {
		if(color != null) {
			 mPaintBrush.setColor(color);
		}
	}

	@Override
	public void onStrokeLineWidthSelection(int strokeLineWidth) {
		mPaintBrush.setStrokeLineWidth(strokeLineWidth);	
	}

	@Override
	public void onStrokeDashSelection(float[] strokeDash) {
		mPaintBrush.setStrokeDash(strokeDash);
	}

	@Override
	public void onBgColorSelection(Color color) {
		if(mCanvas != null && mCanvas.getCanvasItem() != null && color != null) {
			CanvasItem canvasItem = mCanvas.getCanvasItem();
			canvasItem.setBackgroundType(Background.BgType.COLOR, true);
			canvasItem.setBackgroundColor(color);
		}
	}	

	@Override
	public void onBgPictureImageSelection(String filepath) {
		if(mCanvas != null && mCanvas.getCanvasItem() != null && filepath != null) {
			CanvasItem canvasItem = mCanvas.getCanvasItem();
			canvasItem.setBackgroundType(Background.BgType.PICTURE, true);
			canvasItem.setBackgroundPictureImageFilePath(filepath);
		}
	}

	@Override
	public void onBgMarbleImageSelection(String filepath) {
		if(mCanvas != null && mCanvas.getCanvasItem() != null && filepath != null) {
			CanvasItem canvasItem = mCanvas.getCanvasItem();
			canvasItem.setBackgroundType(Background.BgType.MARBLE, true);
			canvasItem.setBackgroundMarbleImageFilePath(filepath);
		}
	}

	@Override
	public void onBgTypeSelection(int type, boolean mask) {
		if(mCanvas != null && mCanvas.getCanvasItem() != null) {
			CanvasItem canvasItem = mCanvas.getCanvasItem();
			canvasItem.setBackgroundType(type, mask);
		}
	}
}
