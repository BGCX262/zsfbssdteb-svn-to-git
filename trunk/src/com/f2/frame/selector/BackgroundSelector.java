package com.f2.frame.selector;

import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.f2.frame.data.Background;

@SuppressWarnings("serial")
public class BackgroundSelector extends JFrame implements ActionListener {

	private JPanel contentPane;

	
	private JCheckBox chbColor = null;
	private JButton btnColor = null;
		
	private JCheckBox chbPicture = null;
	private JButton btnPicture = null;
	
	private JCheckBox chbMarble = null;
	private JButton btnMarble = null;
	
	private JFileChooser jfc = null;
	
	private Color color;
	private BackgroundSelectionListener backgroundSelectionListener;
	private ComponentListener componentListener;
	
	/**
	 * Create the frame.
	 */
	public BackgroundSelector(ComponentListener componentListener) {
		super();
		setTitle("����");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 177);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 1, 0, 0));
		
		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new BorderLayout(10, 10));		
		chbColor = new JCheckBox("������ɫ(��ɫ)");
		chbColor.setFont(chbColor.getFont().deriveFont(20.0f));
		chbColor.addActionListener(this);
		colorPanel.add(chbColor, BorderLayout.WEST);		
		btnColor = new JButton("ѡ����ɫ");
		btnColor.setFont(btnColor.getFont().deriveFont(20.0f));
		btnColor.addActionListener(this);
		colorPanel.add(btnColor, BorderLayout.EAST);
		contentPane.add(colorPanel);
		
		JPanel picturePanel = new JPanel();
		picturePanel.setLayout(new BorderLayout(10, 10));		
		chbPicture = new JCheckBox("����ͼƬ(����)");
		chbPicture.setFont(chbPicture.getFont().deriveFont(20.0f));
		chbPicture.addActionListener(this);
		picturePanel.add(chbPicture, BorderLayout.WEST);		
		btnPicture = new JButton("ѡ��ͼƬ");
		btnPicture.setFont(btnPicture.getFont().deriveFont(20.0f));
		btnPicture.addActionListener(this);
		picturePanel.add(btnPicture, BorderLayout.EAST);
		contentPane.add(picturePanel);

		JPanel marblePanel = new JPanel();
		marblePanel.setLayout(new BorderLayout(10, 10));		
		chbMarble = new JCheckBox("��������(ƽ��)");
		chbMarble.setFont(chbMarble.getFont().deriveFont(20.0f));
		chbMarble.addActionListener(this);
		marblePanel.add(chbMarble, BorderLayout.WEST);		
		btnMarble = new JButton("ѡ������");
		btnMarble.setFont(btnMarble.getFont().deriveFont(20.0f));
		btnMarble.addActionListener(this);
		marblePanel.add(btnMarble, BorderLayout.EAST);
		contentPane.add(marblePanel);
		
		if(componentListener != null) {
			this.addComponentListener(componentListener);
			this.componentListener = componentListener;
		}		
	}

	public void setBackgroundSelectionListener(BackgroundSelectionListener backgroundSelectionListener, Color color) {
		this.backgroundSelectionListener = backgroundSelectionListener;
		this.color = color;
	}
	
	public void actionPerformed(ActionEvent event) {
		if(backgroundSelectionListener == null) {
			setVisible(false); //dispose();
			return;
		}
		if(event.getSource() == btnColor) {
			Color c = JColorChooser.showDialog(BackgroundSelector.this, "ѡ�񱳾���ɫ", color);
			if(c != null) {
				chbColor.setSelected(true);
				backgroundSelectionListener.onBgColorSelection(c);
				repaintComponent();
			}
		}
		else if(event.getSource() == btnPicture) {
			if(jfc == null) {				
				String [] formats = javax.imageio.ImageIO.getReaderFormatNames();				   
				jfc = new JFileChooser(".");
				jfc.setDialogTitle("ѡ�񱳾�ͼƬ�ļ�...");
				jfc.setAcceptAllFileFilterUsed(false);
				//����ļ���չ���Ĺ�����
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("ͼƬ(*.gif|*.jpg|*.png|*.bmp|*.wbmp)", formats));
			}
			int result = jfc.showOpenDialog(null);
			File file = jfc.getSelectedFile();
			if(result == 0 && file != null) {
				String filepath = file.getPath();
				chbPicture.setSelected(true);
				backgroundSelectionListener.onBgPictureImageSelection(filepath);
			}
			repaintComponent();
		}
		else if(event.getSource() == btnMarble) {
			if(jfc == null) {				
				String [] formats = javax.imageio.ImageIO.getReaderFormatNames();				   
				jfc = new JFileChooser(".");
				jfc.setDialogTitle("ѡ������ͼƬ�ļ�...");
				jfc.setAcceptAllFileFilterUsed(false);
				//����ļ���չ���Ĺ�����
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("ͼƬ(*.gif|*.jpg|*.png|*.bmp|*.wbmp)", formats));
			}
			int result = jfc.showOpenDialog(null);
			File file = jfc.getSelectedFile();
			if(result == 0 && file != null) {
				String filepath = file.getPath();
				chbMarble.setSelected(true);
				backgroundSelectionListener.onBgMarbleImageSelection(filepath);
			}
			repaintComponent();
		}
		else if(event.getSource() == chbColor) {
			backgroundSelectionListener.onBgTypeSelection(Background.BgType.COLOR, chbColor.isSelected());
			repaintComponent();
		}
		else if(event.getSource() == chbPicture) {
			backgroundSelectionListener.onBgTypeSelection(Background.BgType.PICTURE, chbPicture.isSelected());
			repaintComponent();
		}
		else if(event.getSource() == chbMarble) {
			backgroundSelectionListener.onBgTypeSelection(Background.BgType.MARBLE, chbMarble.isSelected());
			repaintComponent();
		}
	}	

	private void repaintComponent() {
		if(componentListener != null) {
			componentListener.componentShown(null);
		}
	}	

	public interface BackgroundSelectionListener {
		public void onBgColorSelection(Color color);
		public void onBgPictureImageSelection(String filepath);
		public void onBgMarbleImageSelection(String filepath);
		public void onBgTypeSelection(int type, boolean mask);
	}
}
