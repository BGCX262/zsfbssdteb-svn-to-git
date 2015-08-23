package com.f2.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
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

import javax.swing.JButton;
import javax.swing.JScrollPane;

import com.f2.frame.data.CanvasItem;
import com.f2.frame.data.CanvasPage;
import com.f2.panel.PreviewPanel;
import com.f2.tool.file.FileHelper;

import javax.swing.BoxLayout;

@SuppressWarnings("serial")
public class PageManager extends JFrame implements ActionListener, MouseListener {

	private static final String FileNameExtension = "5best";
	private JPanel contentPane;
	private Canvas mCanvas = null;
	
	private CanvasPage dataPage = null;
	
	private boolean isShowPreview = false;
	private JButton btnManager = null;
	private JButton btnNewPage = null;
	private JButton btnPageUp = null;
	private JButton btnPageDown = null;
	private JButton btnPageUpToFirst = null;
	private JButton btnPageDownToLast = null;
	private JButton btnPageDelete = null;
	private JButton btnPageCopy = null;
		
	private JPanel btnPanel = new JPanel();

	private JPanel panelPage = new JPanel();
	private JFileChooser fileChooser;
	private String mDataFilePath;
	
	public PageManager(Canvas canvas, ComponentListener componentListener) {
		super();
		this.mCanvas = canvas;
		setTitle("页面浏览");
		setAlwaysOnTop(true);
		//setUndecorated(true); 
		setResizable(false);
		setLocation(10, 60); // TODO 居中?页面最左侧?
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(null); //new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		btnManager = new JButton();
		btnManager.addActionListener(this);

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
		int th = 27;
		if(!isShowPreview) {
			setSize(120, 24 + th);
		} else {
			if(dataPage.size() >= 4) {
				setSize(166, 510 + th);
			} else if(dataPage.size() == 3) {
				setSize(166, 412 + th);
			} else if(dataPage.size() == 2) {
				setSize(166, 314 + th);
			} else if(dataPage.size() == 1) {
				setSize(166, 216 + th);
			}
		}
	}

	private void addPreviewButtons() {
		btnPanel.setLayout(new GridLayout(4, 2, 0, 0));

		btnManager.setText("隐藏");
		btnManager.setToolTipText("点击隐藏页面管理");
		btnPanel.add(btnManager);

		if(btnNewPage == null) {
			btnNewPage = new JButton("新一页");
			btnNewPage.setToolTipText("创建新的一页");
			btnNewPage.addActionListener(this);
		}
		btnPanel.add(btnNewPage);

		if(btnPageUp == null) {
			btnPageUp = new JButton("上移");
			btnPageUp.setToolTipText("将此页上移");
			btnPageUp.addActionListener(this);
		}
		btnPanel.add(btnPageUp);
		
		if(btnPageDown == null) {
			btnPageDown = new JButton("下移");
			btnPageDown.setToolTipText("将此页下移");
			btnPageDown.addActionListener(this);
		}
		btnPanel.add(btnPageDown);		
		
		if(btnPageUpToFirst == null) {
			btnPageUpToFirst = new JButton("置顶");
			btnPageUpToFirst.setToolTipText("将此页上移至第一页");
			btnPageUpToFirst.addActionListener(this);
		}
		btnPanel.add(btnPageUpToFirst);
		
		if(btnPageDownToLast == null) {
			btnPageDownToLast = new JButton("置底");
			btnPageDownToLast.setToolTipText("将此页下移至最末页");
			btnPageDownToLast.addActionListener(this);
		}
		btnPanel.add(btnPageDownToLast);
		
		if(btnPageDelete == null) {
			btnPageDelete = new JButton();
			btnPageDelete = new JButton("删除");
			btnPageDelete.setToolTipText("将此页删除");
			btnPageDelete.addActionListener(this);
		}
		btnPanel.add(btnPageDelete);
		
		if(btnPageCopy == null) {
			btnPageCopy = new JButton();
			btnPageCopy = new JButton("复制");
			btnPageCopy.setToolTipText("将此页复制");
			btnPageCopy.addActionListener(this);
		}
		btnPanel.add(btnPageCopy);
		
		contentPane.add(btnPanel, BorderLayout.NORTH);
	}

	private void addPreviewSharps() {
		panelPage = new JPanel();
		panelPage.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelPage.setLayout(new BoxLayout(panelPage, BoxLayout.Y_AXIS));		
		for(int i = 0; i < dataPage.size(); i++) {
			PreviewPanel pp = new PreviewPanel(dataPage.get(i), mCanvas.getWidth(), mCanvas.getHeight(), i+1);
			pp.addMouseListener(this);
			panelPage.add(pp);
		}
		
		JScrollPane scrollPane = new JScrollPane(panelPage);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}

	private void reloadPreviewData() {
		// 现重新设置数据
		for(int i = 0; i < dataPage.size(); i++) {
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
		else if(event.getSource() == btnNewPage) {
			createNewPage();			
		}
		else if(event.getSource() == btnPageUp) {
			pageUp();
		}
		else if(event.getSource() == btnPageDown) {
			pageDown();
		}
		else if(event.getSource() == btnPageUpToFirst) {
			pageUpToFirst();
		}
		else if(event.getSource() == btnPageDownToLast) {
			pageDownToLast();
		}
		else if(event.getSource() == btnPageDelete) {
			deleteSelected();
		}
		else if(event.getSource() == btnPageCopy) {
			copySelected();
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

			btnManager.setText((dataPage.getSelectedIndex()+1) + "/" + dataPage.size() + "页");
			btnManager.setToolTipText("点击打开页面管理");
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

	private void createNewPage() {
		CanvasItem item = new CanvasItem();
		mCanvas.setCanvasItem(item);
		dataPage.add(item);
		dataPage.setSelectedIndex(dataPage.size() - 1);
		PreviewPanel pp = new PreviewPanel(item, mCanvas.getWidth(), mCanvas.getHeight(), dataPage.size());
		pp.addMouseListener(this);
		pp.setSelected(true);
		mCanvas.setCanvasRepaintListener(pp);
		panelPage.add(pp);
		panelPage.scrollRectToVisible(new Rectangle(0, 10000, 1, 1));
		resetMainCanvas();
		resetButtonEnabled();
		resetFrameSize();
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

	private void deleteSelected() {
		if(dataPage.getSelectedIndex() >= 0 && dataPage.getSelectedIndex() < panelPage.getComponentCount()) {
			if (dataPage.size() == 1) {
				return;
			}
			dataPage.deleteSelected();
			resetAll();
		}
	}
	
	private void copySelected() {
//		if(dataPage.getSelectedIndex() >= 0 && dataPage.getSelectedIndex() < panelPage.getComponentCount() - 1) {
//			// TODO
//		}
	}
		
	private void resetMainCanvas() {
		int selectedIndex = dataPage.getSelectedIndex();
		for(int i = 0; i < dataPage.size(); i++)
		{
			Component comp = panelPage.getComponent(i);
			PreviewPanel pp = ((PreviewPanel) comp);
			if(selectedIndex == i) {
				pp.setSelected(true);
				mCanvas.setCanvasRepaintListener(pp);
				mCanvas.setCanvasItem(dataPage.get(selectedIndex));
			} else {
				pp.setSelected(false);
			}
			pp.repaintCanvasPanel();
		}
	}
	
	private void resetButtonEnabled() {
		btnPageUp.setEnabled(dataPage.getSelectedIndex() > 0);
		btnPageDown.setEnabled(dataPage.getSelectedIndex() < dataPage.size() - 1);
		btnPageUpToFirst.setEnabled(dataPage.getSelectedIndex() > 0);
		btnPageDownToLast.setEnabled(dataPage.getSelectedIndex() < dataPage.size() - 1);
		btnPageDelete.setEnabled(dataPage.size() > 1);
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
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("白板文件(*.dat)", formats));
		}
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


}
