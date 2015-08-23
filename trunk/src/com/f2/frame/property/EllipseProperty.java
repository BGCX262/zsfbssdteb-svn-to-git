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

import com.f2.sharp.geometry.Ellipse;
import com.f2.tool.PointF;

@SuppressWarnings("serial")
public class EllipseProperty extends JFrame implements ActionListener {

	private JPanel contentPane;

	private ComponentListener componentListener;

	private Ellipse ellipse = null;
	
	private JButton btnOK = null;
	private JButton btnCancel = null;
				
	private SpinnerNumberModel jsXModel = null;
	private SpinnerNumberModel jsYModel = null;
	private SpinnerNumberModel jsAModel = null;
	private SpinnerNumberModel jsBModel = null;
	private JSpinner.NumberEditor jsXEditor = null;
	private JSpinner.NumberEditor jsYEditor = null;
	private JSpinner.NumberEditor jsAEditor = null;
	private JSpinner.NumberEditor jsBEditor = null;
	
	public EllipseProperty(ComponentListener componentListener) {
		super();
		setTitle("椭圆属性");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 360, 178); 
		
		if(componentListener != null) {
			this.addComponentListener(componentListener);
			this.componentListener = componentListener;
		}		

		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(4, 1, 0, 0));
	}
	
	public void setSharp(Ellipse ellipse) {
		if(ellipse == null) {
			return;
		}
		this.ellipse = ellipse;			

		contentPane.removeAll();
		
		JPanel pane0 = new JPanel();
		pane0.setBorder(new EmptyBorder(10, 18, 10, 18));
		JLabel label = new JLabel("椭圆的圆心C、X轴半径A、Y轴半径B(整数):");
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

		JLabel labelA = new JLabel("A=");
		labelA.setFont(labelA.getFont().deriveFont(20.0f));
		paneP2.add(labelA);
		jsAModel = new SpinnerNumberModel();
		JSpinner jsA = new JSpinner(jsAModel);
		jsAEditor = new JSpinner.NumberEditor(jsA, "#");
		jsA.setEditor(jsAEditor);
		paneP2.add(jsA);

		JLabel labelB = new JLabel("，B=");
		labelB.setFont(labelB.getFont().deriveFont(20.0f));
		paneP2.add(labelB);
		jsBModel = new SpinnerNumberModel();
		JSpinner jsB = new JSpinner(jsBModel);
		jsBEditor = new JSpinner.NumberEditor(jsB, "#");
		jsB.setEditor(jsBEditor);
		paneP2.add(jsB);

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

		PointF c = ellipse.getCentre();
		double a = ellipse.getA();
		double b = ellipse.getB();
		jsXModel.setValue(Math.round(c.x));
		jsYModel.setValue(Math.round(c.y));
		jsAModel.setValue(Math.round(a));
		jsBModel.setValue(Math.round(b));
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(ellipse == null) {
			return;
		}
		if(event.getSource() == btnOK) {
			try {
				double x = Double.parseDouble(jsXEditor.getTextField().getText());
				double y = Double.parseDouble(jsYEditor.getTextField().getText());
				double a = Double.parseDouble(jsAEditor.getTextField().getText());
				double b = Double.parseDouble(jsBEditor.getTextField().getText());
				ellipse.setCentre(new PointF(x, y));
				ellipse.setAB(a, b);
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
