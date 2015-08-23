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
		setTitle("ҳ�����");
		setAlwaysOnTop(true);
		//setUndecorated(true); 
		setResizable(false);
		setLocation(10, 60); // TODO ����?ҳ�������?
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(null); //new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		btnManager = new JButton();
		btnManager.addActionListener(this);

		fileNew(); // ��Ϊ�հ׵İװ�
		
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

		btnManager.setText("����");
		btnManager.setToolTipText("�������ҳ�����");
		btnPanel.add(btnManager);

		if(btnNewPage == null) {
			btnNewPage = new JButton("��һҳ");
			btnNewPage.setToolTipText("�����µ�һҳ");
			btnNewPage.addActionListener(this);
		}
		btnPanel.add(btnNewPage);

		if(btnPageUp == null) {
			btnPageUp = new JButton("����");
			btnPageUp.setToolTipText("����ҳ����");
			btnPageUp.addActionListener(this);
		}
		btnPanel.add(btnPageUp);
		
		if(btnPageDown == null) {
			btnPageDown = new JButton("����");
			btnPageDown.setToolTipText("����ҳ����");
			btnPageDown.addActionListener(this);
		}
		btnPanel.add(btnPageDown);		
		
		if(btnPageUpToFirst == null) {
			btnPageUpToFirst = new JButton("�ö�");
			btnPageUpToFirst.setToolTipText("����ҳ��������һҳ");
			btnPageUpToFirst.addActionListener(this);
		}
		btnPanel.add(btnPageUpToFirst);
		
		if(btnPageDownToLast == null) {
			btnPageDownToLast = new JButton("�õ�");
			btnPageDownToLast.setToolTipText("����ҳ��������ĩҳ");
			btnPageDownToLast.addActionListener(this);
		}
		btnPanel.add(btnPageDownToLast);
		
		if(btnPageDelete == null) {
			btnPageDelete = new JButton();
			btnPageDelete = new JButton("ɾ��");
			btnPageDelete.setToolTipText("����ҳɾ��");
			btnPageDelete.addActionListener(this);
		}
		btnPanel.add(btnPageDelete);
		
		if(btnPageCopy == null) {
			btnPageCopy = new JButton();
			btnPageCopy = new JButton("����");
			btnPageCopy.setToolTipText("����ҳ����");
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
		// ��������������
		for(int i = 0; i < dataPage.size(); i++) {
			PreviewPanel pp = (PreviewPanel) panelPage.getComponent(i);
			pp.setCanvasItem(dataPage.get(i));
		}
		// Ȼ���Ƴ������
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

			btnManager.setText((dataPage.getSelectedIndex()+1) + "/" + dataPage.size() + "ҳ");
			btnManager.setToolTipText("�����ҳ�����");
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
			//btnManager.setText((selectedIndex+1) + "/" + sharpsPage.size() + "ҳ");
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
			fileChooser.setDialogTitle("ѡ��һ���װ��ļ�...");
			fileChooser.setAcceptAllFileFilterUsed(false);
			//����ļ���չ���Ĺ�����
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("�װ��ļ�(*." + FileNameExtension + ")", formats));
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
			fileChooser.setDialogTitle("ѡ��һ���װ��ļ�...");
			fileChooser.setAcceptAllFileFilterUsed(false);
			//����ļ���չ���Ĺ�����
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("�װ��ļ�(*.dat)", formats));
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
