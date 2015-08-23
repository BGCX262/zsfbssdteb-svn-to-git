package com.f2.frame.property;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.f2.sharp.geometry.Polygon;
import com.f2.tool.PointF;

@SuppressWarnings("serial")
public class PolygonProperty extends JFrame implements ActionListener {

	private JPanel contentPane;

	private ComponentListener componentListener;

	private Polygon polygon = null;
	
	private JButton btnPointOK = null;
	private JButton btnAngleOK = null;
	private JButton btnCancel = null;
				
	private SpinnerNumberModel[] jsXModels = null;
	private SpinnerNumberModel[] jsYModels = null;
	private SpinnerNumberModel[] jsAModels = null;
	private JSpinner.NumberEditor[] jsXEditors = null;
	private JSpinner.NumberEditor[] jsYEditors = null;
	private JSpinner.NumberEditor[] jsAEditors = null;
	
	public PolygonProperty(ComponentListener componentListener) {
		super();
		setTitle("多边形属性");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		
		if(componentListener != null) {
			this.addComponentListener(componentListener);
			this.componentListener = componentListener;
		}		
	}
	
	public void setSharp(Polygon polygon) {
		
		if(polygon == null || polygon.getPoints() == null) {
			return;
		}
		
		this.polygon = polygon;		
		List<PointF> ps = polygon.getPoints();
		setBounds(100, 100, 420, ps.size() * 50 + 100); 

		contentPane.removeAll();
		contentPane.setLayout(new GridLayout(ps.size() + 2, 1, 0, 0));

		JPanel pane0 = new JPanel();
		pane0.setBorder(new EmptyBorder(10, 18, 10, 18));
		boolean isTriangle = ps.size() == 3;
		boolean isRetcangle = ps.size() == 4;		
		String ss = isTriangle ? "三角形" : isRetcangle ? "四边形" : "多边形";
		JLabel label = new JLabel(ss + "各顶点Pn的坐标(整数):");
		label.setFont(label.getFont().deriveFont(18.0f));
		pane0.add(label);
		contentPane.add(pane0);
		
		jsXModels = new SpinnerNumberModel[ps.size()];
		jsYModels = new SpinnerNumberModel[ps.size()];
		jsAModels = new SpinnerNumberModel[ps.size()];
		
		jsXEditors = new JSpinner.NumberEditor[ps.size()];
		jsYEditors = new JSpinner.NumberEditor[ps.size()];
		jsAEditors = new JSpinner.NumberEditor[ps.size()];
		
		for(int i = 0; i < ps.size(); i++) {
			PointF p = ps.get(i);
			double a = polygon.getAngle(i);

			JPanel panePn = new JPanel();
			
			JLabel labelPX = new JLabel("P" + (i+1) + ".X=");
			labelPX.setFont(labelPX.getFont().deriveFont(20.0f));
			panePn.add(labelPX);
			jsXModels[i] = new SpinnerNumberModel();
			jsXModels[i].setValue(Math.round(p.x));
			JSpinner jsX = new JSpinner(jsXModels[i]);
			jsXEditors[i] = new JSpinner.NumberEditor(jsX, "#");
			jsX.setEditor(jsXEditors[i]);
			panePn.add(jsX);	
			
			JLabel labelPY = new JLabel("，P" + (i+1) + ".Y=");
			labelPY.setFont(labelPY.getFont().deriveFont(20.0f));
			panePn.add(labelPY);
			jsYModels[i] = new SpinnerNumberModel();
			jsYModels[i].setValue(Math.round(p.y));
			JSpinner jsY = new JSpinner(jsYModels[i]);
			jsYEditors[i] = new JSpinner.NumberEditor(jsY, "#");
			jsY.setEditor(jsYEditors[i]);
			panePn.add(jsY);
			
			if(isTriangle) {
				JLabel labelPA = new JLabel("，∠" + (i+1) + "=");
				labelPA.setFont(labelPA.getFont().deriveFont(20.0f));
				panePn.add(labelPA);
				jsAModels[i] = new SpinnerNumberModel();
				jsAModels[i].setValue(Math.round(a));
				JSpinner jsA = new JSpinner(jsAModels[i]);
				jsAEditors[i] = new JSpinner.NumberEditor(jsA, "#");
				jsA.setEditor(jsAEditors[i]);
				panePn.add(jsA);
			}
			
			contentPane.add(panePn);
		}

		JPanel paneBtn = new JPanel();
		if(isTriangle) {
			btnPointOK = new JButton("按顶点算");
			btnPointOK.setFont(btnPointOK.getFont().deriveFont(18.0f));
			btnPointOK.addActionListener(this);
			paneBtn.add(btnPointOK);		
			btnAngleOK = new JButton("按角度算");
			btnAngleOK.setFont(btnAngleOK.getFont().deriveFont(18.0f));
			btnAngleOK.addActionListener(this);
			paneBtn.add(btnAngleOK);	
		} else {
			btnPointOK = new JButton("确定");
			btnPointOK.setFont(btnPointOK.getFont().deriveFont(18.0f));
			btnPointOK.addActionListener(this);
			paneBtn.add(btnPointOK);		
		}
		btnCancel = new JButton("取消");
		btnCancel.setFont(btnCancel.getFont().deriveFont(18.0f));
		btnCancel.addActionListener(this);
		paneBtn.add(btnCancel);
		contentPane.add(paneBtn);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(polygon == null) {
			return;
		}
		if(event.getSource() == btnPointOK) {
			try {
				List<PointF> ps = new LinkedList<PointF>();
				for(int i = 0; i < polygon.getPoints().size(); i++) {
					double x = Double.parseDouble(jsXEditors[i].getTextField().getText());
					double y = Double.parseDouble(jsYEditors[i].getTextField().getText());
					ps.add(new PointF(x, y));
				}
				polygon.setPoints(ps);
				setVisible(false); 
				repaintComponent();
			} catch (Exception e) {
				e.printStackTrace();
				// 提示出错
				JOptionPane.showMessageDialog(this, "坐标输入有误，请重新输入！");
			}
		} else if(event.getSource() == btnAngleOK) {
			try {
				List<Integer> as = new LinkedList<Integer>();
				for(int i = 0; i < polygon.getPoints().size(); i++) {
					double a = Double.parseDouble(jsAEditors[i].getTextField().getText());
					as.add((int)Math.round(a));
				}
				if(polygon.setAngles(as)) {
					setVisible(false); 
					repaintComponent();
				} else {
					// 提示出错
					JOptionPane.showMessageDialog(this, "角度输入有误，请重新输入！");
				}
			} catch (Exception e) {
				e.printStackTrace();
				// 提示出错
				JOptionPane.showMessageDialog(this, "角度输入有误，请重新输入！");
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
