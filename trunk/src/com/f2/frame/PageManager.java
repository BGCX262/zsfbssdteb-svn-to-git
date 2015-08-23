package com.f2.frame;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import com.f2.frame.data.CanvasItem;
import com.f2.frame.data.CanvasPage;
import com.f2.panel.PreviewPanel;
import com.f2.tool.ClipBoard;
import com.f2.tool.ImageTool;
import com.f2.tool.file.FileHelper;

import javax.swing.BoxLayout;

@SuppressWarnings("serial")
public class PageManager extends JFrame implements ActionListener, MouseListener {

	private static final String FileNameExtension = "5best";
	private JPanel contentPane;
	private CanvasFrame mCanvas = null;
	
	private CanvasPage dataPage = null;
	
	private boolean isShowPreview = false;
	private JButton btnManager = null;
	private JButton btnNew = null;
	private JButton btnUp = null;
	private JButton btnDown = null;
	private JButton btnUpToFirst = null;
	private JButton btnDownToLast = null;
	private JButton btnDelete = null;
	private JButton btnCopy = null;
	private JButton btnCut = null;
	private JButton btnPaste = null;
		
	private JPanel btnPanel = new JPanel();

	private JPanel panelPage = new JPanel();
	private JFileChooser fileChooser;
	private String mDataFilePath;
	
	public PageManager(CanvasFrame canvas, ComponentListener componentListener) {
		super();
		this.mCanvas = canvas;
		setTitle("页面浏览");
		setAlwaysOnTop(true);
		//setUndecorated(true); 
		setResizable(false);
		//Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(24, 30); // TODO 居中?页面最左侧?
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(null); //new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		btnManager = new JButton();
		btnManager.addActionListener(this);

		isShowPreview = true;
		switchPreviewMode();
		isShowPreview = false;
		fileNew(); // 打开为空白的白板
		
		this.addComponentListener(componentListener);		
	}
	
	public boolean loadDataPage(String filepath) {
		try {
			dataPage = new CanvasPage();
			byte[] bs = null;
			if(filepath != null && filepath.length() > 0) {
				bs = FileHelper.load(filepath);
			}
			if(bs == null) {
				CanvasItem canvasItem = new CanvasItem();
				dataPage.add(canvasItem);
			} else {
				dataPage.initFromBytes(bs);
			}
			if(dataPage != null) {
				mCanvas.setCanvasItem(dataPage.getSelectedItem());
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean saveDataPage(String filepath) {
		try {
			return FileHelper.save(filepath, dataPage.toBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void resetFrameSize() {
		int th = 30;
		if(!isShowPreview) {
			setSize(120, 24 + th);
			btnManager.setToolTipText("点击打开页面管理");
			btnManager.setText((dataPage.getSelectedIndex()+1) + "/" + dataPage.size() + "页");
			btnManager.setIcon(null);
		} else if(dataPage != null) {
//			if(dataPage.size() >= 4) {
//				setSize(170, 634 + th);
//			} else 
			if(dataPage.size() >= 3) {
				setSize(170, 530 + th);
			} else if(dataPage.size() == 2) {
				setSize(170, 432 + th);
			} else if(dataPage.size() == 1) {
				setSize(170, 334 + th);
			}
			btnManager.setText("");
			btnManager.setIcon(ImageTool.getResImageIcon("/res/images/page/page_hide.png"));
			btnManager.setToolTipText("点击隐藏页面管理");
		}
	}

	private void addPreviewButtons() {
		btnPanel.setLayout(new GridLayout(5, 2, 0, 0));
		btnPanel.add(btnManager);

		if(btnNew == null) {
			btnNew = new JButton(ImageTool.getResImageIcon("/res/images/page/page_new.png"));
			btnNew.setToolTipText("创建新的一页");
			btnNew.addActionListener(this);
		}
		btnPanel.add(btnNew);

		if(btnUp == null) {
			btnUp = new JButton(ImageTool.getResImageIcon("/res/images/page/page_up.png"));
			btnUp.setToolTipText("将此页上移");
			btnUp.addActionListener(this);
		}
		btnPanel.add(btnUp);
		
		if(btnDown == null) {
			btnDown = new JButton(ImageTool.getResImageIcon("/res/images/page/page_down.png"));
			btnDown.setToolTipText("将此页下移");
			btnDown.addActionListener(this);
		}
		btnPanel.add(btnDown);		
		
		if(btnUpToFirst == null) {
			btnUpToFirst = new JButton(ImageTool.getResImageIcon("/res/images/page/page_up_to_first.png"));
			btnUpToFirst.setToolTipText("将此页上移至第一页");
			btnUpToFirst.addActionListener(this);
		}
		btnPanel.add(btnUpToFirst);
		
		if(btnDownToLast == null) {
			btnDownToLast = new JButton(ImageTool.getResImageIcon("/res/images/page/page_down_to_last.png"));
			btnDownToLast.setToolTipText("将此页下移至最末页");
			btnDownToLast.addActionListener(this);
		}
		btnPanel.add(btnDownToLast);
		
		if(btnDelete == null) {
			btnDelete = new JButton();
			btnDelete = new JButton(ImageTool.getResImageIcon("/res/images/page/page_delete.png"));
			btnDelete.setToolTipText("将此页删除");
			btnDelete.addActionListener(this);
		}
		btnPanel.add(btnDelete);
		
		if(btnCopy == null) {
			btnCopy = new JButton();
			btnCopy = new JButton(ImageTool.getResImageIcon("/res/images/page/page_copy.png"));
			btnCopy.setToolTipText("将此页复制");
			btnCopy.addActionListener(this);
		}
		btnPanel.add(btnCopy);
		
		if(btnCut == null) {
			btnCut = new JButton();
			btnCut = new JButton(ImageTool.getResImageIcon("/res/images/page/page_cut.png"));
			btnCut.setToolTipText("将此页剪切");
			btnCut.addActionListener(this);
		}
		btnPanel.add(btnCut);
		
		if(btnPaste == null) {
			btnPaste = new JButton();
			btnPaste = new JButton(ImageTool.getResImageIcon("/res/images/page/page_paste.png"));
			btnPaste.setToolTipText("粘贴到此页以下");
			btnPaste.addActionListener(this);
		}
		btnPanel.add(btnPaste);
		
		contentPane.add(btnPanel, BorderLayout.NORTH);
	}

	private void addPreviewSharps() {
		panelPage = new JPanel();
		panelPage.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelPage.setLayout(new BoxLayout(panelPage, BoxLayout.Y_AXIS));		
		for(int i = 0; dataPage != null && i < dataPage.size(); i++) {
			PreviewPanel pp = new PreviewPanel(dataPage.get(i), mCanvas.getWidth(), mCanvas.getHeight(), i+1);
			pp.addMouseListener(this);
			panelPage.add(pp);
		}
		
		JScrollPane scrollPane = new JScrollPane(panelPage);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}

	private void reloadPreviewData() {
		// 现重新设置数据
		for(int i = 0; dataPage != null && i < dataPage.size(); i++) {
			PreviewPanel pp = (PreviewPanel) panelPage.getComponent(i);
			pp.setCanvasItem(dataPage.get(i));
		}
		// 然后移除多余的
		for(int i = dataPage.size(); i < panelPage.getComponentCount(); i++) {
			panelPage.remove(i);
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		//System.out.println("PageManager.actionPerformed");
		
		if(event.getSource() == btnManager) {
			isShowPreview = !isShowPreview;
			switchPreviewMode();
		}
		else if(event.getSource() == btnNew) {
			pageNew();			
		}
		else if(event.getSource() == btnUp) {
			pageUp();
		}
		else if(event.getSource() == btnDown) {
			pageDown();
		}
		else if(event.getSource() == btnUpToFirst) {
			pageUpToFirst();
		}
		else if(event.getSource() == btnDownToLast) {
			pageDownToLast();
		}
		else if(event.getSource() == btnDelete) {
			delete();
		}
		else if(event.getSource() == btnCopy) {
			copy();
		}		
		else if(event.getSource() == btnCut) {
			cut();
		}		
		else if(event.getSource() == btnPaste) {
			CanvasItem item = (CanvasItem) ClipBoard.get(ClipBoard.CANVAS_COPY);
			if(item == null) {
				// TODO 提示
			} else {
				paste(item);
			}
		}		
		this.validate();
	}

	private void switchPreviewMode() {
		//System.out.println("switchPreviewMode...isShowPreview=" + isShowPreview);
		if(!isShowPreview) {
			contentPane.removeAll();
			
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1, 0, 0));
			contentPane.add(panel, BorderLayout.NORTH);

			panel.add(btnManager);
			
			resetFrameSize();
			this.validate();
		} 
		else {
			contentPane.removeAll();
			
			addPreviewButtons();
			addPreviewSharps();

			resetMainCanvas();
			resetButtonEnabled();
			resetFrameSize();			
			this.validate();
		}
	}

	public void pageNew() {
		CanvasItem item = new CanvasItem();
		int index = dataPage.getSelectedIndex() + 1;
		mCanvas.setCanvasItem(item);
		dataPage.add(index, item);
		dataPage.setSelectedIndex(index);
		PreviewPanel pp = new PreviewPanel(item, mCanvas.getWidth(), mCanvas.getHeight(), dataPage.size());
		pp.addMouseListener(this);
		panelPage.add(pp);
		resetAll();
	}
	
	private void resetAll() {
		reloadPreviewData();
		resetMainCanvas();
		resetButtonEnabled();
		resetFrameSize();
	}

	private void pageUp() {
		if(dataPage.getSelectedIndex() >= 1 && dataPage.getSelectedIndex() < panelPage.getComponentCount()) {			
			dataPage.itemUp();
			resetAll();
		}
	}

	private void pageDown() {
		if(dataPage.getSelectedIndex() >= 0 && dataPage.getSelectedIndex() < panelPage.getComponentCount() - 1) {
			dataPage.itemDown();
			resetAll();
		}
	}

	private void pageUpToFirst() {
		if(dataPage.getSelectedIndex() >= 1 && dataPage.getSelectedIndex() < panelPage.getComponentCount()) {
			dataPage.itemUpToFirst();
			resetAll();
		}
	}

	private void pageDownToLast() {
		if(dataPage.getSelectedIndex() >= 0 && dataPage.getSelectedIndex() < panelPage.getComponentCount() - 1) {
			dataPage.itemDownToLast();
			resetAll();
		}
	}

	private void deleteSelected(boolean showConfirm) {
		int result = 0;
		if(showConfirm) {
			Object[] options = {"确定", "取消"}; //定制可供选择按钮
			result = JOptionPane.showOptionDialog(this, "确认删除当前页面吗？", "确认删除", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); 
		}
		if(result == 0) {
			if(dataPage.getSelectedIndex() >= 0 && dataPage.getSelectedIndex() < panelPage.getComponentCount()) {
				if (dataPage.size() == 1) {
					return;
				}
				dataPage.deleteSelected();
				resetAll();
			}		
		} else {
			// nothing here
		}
	}

	private void delete() {
		deleteSelected(true);
	}
	
	private void copy() {
		CanvasItem item = (CanvasItem) dataPage.getSelectedItem().clone();		
		ClipBoard.put(ClipBoard.CANVAS_COPY, item);
	}
		
	private void cut() {
//		CanvasItem item = new CanvasItem(dataPage.getSelectedItem());
		CanvasItem item = (CanvasItem) dataPage.getSelectedItem().clone();		
		ClipBoard.put(ClipBoard.CANVAS_COPY, item);
		deleteSelected(false);
	}

	private void paste(CanvasItem item) {
		if(item == null) {
			return;
		}
		CanvasItem i2 = (CanvasItem) item.clone();
		int index = dataPage.getSelectedIndex() + 1;
		mCanvas.setCanvasItem(i2);
		dataPage.add(index, i2);
		//dataPage.setSelectedIndex(index);
		PreviewPanel pp = new PreviewPanel(i2, mCanvas.getWidth(), mCanvas.getHeight(), dataPage.size());
		pp.addMouseListener(this);
		panelPage.add(pp);
		resetAll();
	}

	private void resetMainCanvas() {
		if(dataPage == null) {
			return;
		}
		int selectedIndex = dataPage.getSelectedIndex();
		for(int i = 0; i < dataPage.size()&& i < panelPage.getComponentCount(); i++)
		{
			Component comp = panelPage.getComponent(i);
			PreviewPanel pp = ((PreviewPanel) comp);
			if(selectedIndex == i) {
				pp.setSelected(true);
				mCanvas.setCanvasRepaintListener(pp);
				mCanvas.initCanvasItem(dataPage.get(selectedIndex));
			} else {
				pp.setSelected(false);
			}
			pp.repaintCanvasPanel();
		}
	}
	
	private void resetButtonEnabled() {
		if(dataPage == null) {
			return;
		}
		btnUp.setEnabled(dataPage.getSelectedIndex() > 0);
		btnDown.setEnabled(dataPage.getSelectedIndex() < dataPage.size() - 1);
		btnUpToFirst.setEnabled(dataPage.getSelectedIndex() > 0);
		btnDownToLast.setEnabled(dataPage.getSelectedIndex() < dataPage.size() - 1);
		btnDelete.setEnabled(dataPage.size() > 1);
		btnCut.setEnabled(dataPage.size() > 1);
		if(!isShowPreview) {
			//btnManager.setText((selectedIndex+1) + "/" + sharpsPage.size() + "页");
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Object obj = e.getSource();
		if(obj == null || !(obj instanceof PreviewPanel)) {
			return;
		}
		PreviewPanel pp = (PreviewPanel) obj;
		int index = pp.getPageNo() - 1;
		dataPage.setSelectedIndex(index);
		resetMainCanvas();
		resetButtonEnabled();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public void fileNew() {
		//System.out.println("fileNew...");
		mDataFilePath = null;
		if(loadDataPage(mDataFilePath)) {
			switchPreviewMode();
		}
	}

	public void fileOpen() {
		//System.out.println("fileOpen...");
		if(fileChooser == null) {				
			String[] formats = new String[]{FileNameExtension};				   
			fileChooser = new JFileChooser(".");
			fileChooser.setDialogTitle("选择一个白板文件...");
			fileChooser.setAcceptAllFileFilterUsed(false);
			//添加文件扩展名的过滤器
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("白板文件(*." + FileNameExtension + ")", formats));
		}
		int result = fileChooser.showOpenDialog(null);
		File file = fileChooser.getSelectedFile();
		if(result == 0 && file != null) {
			mDataFilePath = file.getPath();
			if(loadDataPage(mDataFilePath)) {
				switchPreviewMode();
			}
		}
	}

	public void fileSave() {
		//System.out.println("fileSave...");
		if(mDataFilePath == null) {
			fileSaveAs();
		} else {
			saveDataPage(mDataFilePath);
		}
	}

	public void fileSaveAs() {
		//System.out.println("fileSaveAs...");
		if(fileChooser == null) {				
			String[] formats = new String[]{FileNameExtension};				   
			fileChooser = new JFileChooser(".");
			fileChooser.setDialogTitle("选择一个白板文件...");
			fileChooser.setAcceptAllFileFilterUsed(false);
			//添加文件扩展名的过滤器
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("白板文件(*." + FileNameExtension + ")", formats));
		}
		
		if(mDataFilePath == null || mDataFilePath.length() == 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			mDataFilePath = sdf.format(new Date(System.currentTimeMillis())) + "." + FileNameExtension;
		}
		fileChooser.setSelectedFile(new File(mDataFilePath));
		
		int result = fileChooser.showSaveDialog(null);
		File file = fileChooser.getSelectedFile();
		if(result == 0 && file != null) {
			mDataFilePath = file.getPath();
			if(!mDataFilePath.endsWith("." + FileNameExtension)) {
				mDataFilePath += "." + FileNameExtension;
			}
			saveDataPage(mDataFilePath);
		}
	}

	public String getDataFilePath() {
		return mDataFilePath;
	}

	public CanvasPage getCanvasPageData() {
		return dataPage;
	}

	public void undoRedo() {
		//System.out.println("Canvas,undoRedo");
		CanvasItem canvasItem = (CanvasItem) ClipBoard.get(ClipBoard.CANVAS_UNDO_REDO);
		if(canvasItem == null) {
			return;
		}
		CanvasItem clone = (CanvasItem) canvasItem.clone();
		dataPage.setSelectedItem(clone);
		if(dataPage.getSelectedIndex() < panelPage.getComponentCount()) {
			Component comp = panelPage.getComponent(dataPage.getSelectedIndex());
			((PreviewPanel) comp).setCanvasItem(clone);
		}
		mCanvas.undoRedo(clone);
	}
	

}
