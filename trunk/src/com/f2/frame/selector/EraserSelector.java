package com.f2.frame.selector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;

import javax.swing.JButton;

import com.f2.listener.EraserListener;
import com.f2.tool.ImageTool;

@SuppressWarnings("serial")
public class EraserSelector extends JFrame implements ActionListener {

	private JPanel contentPane;

	private EraserListener eraserListener = null;	
	private ComponentListener componentListener;

	private JButton btnEraser = null;
	private JButton btnRemoveLast = null;
	private JButton btnClear = null;
	
	/**
	 * Create the frame.
	 */
	public EraserSelector(ComponentListener componentListener) {
		super();
		setTitle("����");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 100, 328);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 1, 0, 0));
		
		btnEraser = new JButton(ImageTool.getResImageIcon("/res/images/eraser.png"));
		btnEraser.setToolTipText("��Ƥ��");
		btnEraser.addActionListener(this);
		contentPane.add(btnEraser);
		
		btnRemoveLast = new JButton(ImageTool.getResImageIcon("/res/images/remove_last.png"));
		btnRemoveLast.setToolTipText("�Ƴ����һ��ͼ��");
		btnRemoveLast.addActionListener(this);
		contentPane.add(btnRemoveLast);
		
		btnClear = new JButton(ImageTool.getResImageIcon("/res/images/clear.png"));
		btnClear.setToolTipText("�������ͼ��");
		btnClear.addActionListener(this);
		contentPane.add(btnClear);
		
		if(componentListener != null) {
			this.addComponentListener(componentListener);
			this.componentListener = componentListener;
		}		
	}

	public void setEraserListener(EraserListener listener) {
		this.eraserListener = listener;
	}

	public void actionPerformed(ActionEvent event) {
		if(eraserListener == null) {
			return;
		}
		if(event.getSource() == btnEraser) {
			eraserListener.onEraserAction();
			setVisible(false); 
			repaintComponent();
		}
		else if(event.getSource() == btnRemoveLast) {
			eraserListener.onEraserRemoveLastAction();
			//setVisible(false); 
			repaintComponent();
		}
		if(event.getSource() == btnClear) {
			eraserListener.onEraserClearAction();
			setVisible(false); 
			repaintComponent();
		}
	}	

	private void repaintComponent() {
		if(componentListener != null) {
			componentListener.componentShown(null);
		}
	}	
}
