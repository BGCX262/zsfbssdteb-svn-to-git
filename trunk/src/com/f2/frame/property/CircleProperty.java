package com.f2.frame.property;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.f2.sharp.geometry.Circle;
import com.f2.tool.PointF;

@SuppressWarnings("serial")
public class CircleProperty extends JFrame implements ActionListener {

	private JPanel contentPane;

	private ComponentListener componentListener;

	private Circle circle = null;
	
	private JButton btnOK = null;
	private JButton btnCancel = null;
				
	private SpinnerNumberModel jsXModel = null;
	private SpinnerNumberModel jsYModel = null;
	private SpinnerNumberModel jsRModel = null;
	private JSpinner.NumberEditor jsXEditor = null;
	private JSpinner.NumberEditor jsYEditor = null;
	private JSpinner.NumberEditor jsREditor = null;
	
	public CircleProperty(ComponentListener componentListener) {
		super();
		setTitle("圆属性");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 178); 
		
		if(componentListener != null) {
			this.addComponentListener(componentListener);
			this.componentListener = componentListener;
		}		

		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(4, 1, 0, 0));
	}
	
	public void setSharp(Circle circle) {
		if(circle == null) {
			return;
		}
		this.circle = circle;			

		contentPane.removeAll();

		JPanel pane0 = new JPanel();
		pane0.setBorder(new EmptyBorder(10, 18, 10, 18));
		JLabel label = new JLabel("圆心C、半径R(整数):");
		label.setFont(label.getFont().deriveFont(18.0f));
		pane0.add(label);
		contentPane.add(pane0);
		
		JPanel paneP1 = new JPanel();
		
		JLabel labelPX = new JLabel("C.X=");
		labelPX.setFont(labelPX.getFont().deriveFont(20.0f));
		paneP1.add(labelPX);
		jsXModel = new SpinnerNumberModel();
		JSpinner jsX = new JSpinner(jsXModel);
		jsXEditor = new JSpinner.NumberEditor(jsX, "#");
		jsX.setEditor(jsXEditor);
		paneP1.add(jsX);	
		
		JLabel labelPY = new JLabel("，C.Y=");
		labelPY.setFont(labelPY.getFont().deriveFont(20.0f));
		paneP1.add(labelPY);
		jsYModel = new SpinnerNumberModel();
		JSpinner jsY = new JSpinner(jsYModel);
		jsYEditor = new JSpinner.NumberEditor(jsY, "#");
		jsY.setEditor(jsYEditor);
		paneP1.add(jsY);
		
		contentPane.add(paneP1);

		JPanel paneP2 = new JPanel();

		JLabel labelR = new JLabel("R=");
		labelR.setFont(labelR.getFont().deriveFont(20.0f));
		paneP2.add(labelR);
		jsRModel = new SpinnerNumberModel();
		JSpinner jsR = new JSpinner(jsRModel);
		jsREditor = new JSpinner.NumberEditor(jsR, "#");
		jsR.setEditor(jsREditor);
		paneP2.add(jsR);

		contentPane.add(paneP2);

		JPanel pane3 = new JPanel();
		btnOK = new JButton("确定");
		btnOK.setFont(btnOK.getFont().deriveFont(18.0f));
		btnOK.addActionListener(this);
		pane3.add(btnOK);		
		btnCancel = new JButton("取消");
		btnCancel.setFont(btnCancel.getFont().deriveFont(18.0f));
		btnCancel.addActionListener(this);
		pane3.add(btnCancel);
		contentPane.add(pane3);

		PointF c = circle.getCentre();
		double r = circle.getRadius();
		jsXModel.setValue(Math.round(c.x));
		jsYModel.setValue(Math.round(c.y));
		jsRModel.setValue(Math.round(r));
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(circle == null) {
			return;
		}
		if(event.getSource() == btnOK) {
			try {
				double x = Double.parseDouble(jsXEditor.getTextField().getText());
				double y = Double.parseDouble(jsYEditor.getTextField().getText());
				double r = Double.parseDouble(jsREditor.getTextField().getText());
				circle.setCentre(new PointF(x, y));
				circle.setRadius(r);
				setVisible(false); 
				repaintComponent();
			} catch (Exception e) {
				e.printStackTrace();
				// 提示出错
				JOptionPane.showMessageDialog(this, "坐标输入有误，请重新输入！");
			}
		} else if(event.getSource() == btnCancel) {
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
