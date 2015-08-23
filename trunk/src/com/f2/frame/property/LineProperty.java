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

import com.f2.sharp.geometry.Line;
import com.f2.tool.PointF;

@SuppressWarnings("serial")
public class LineProperty extends JFrame implements ActionListener {

	private JPanel contentPane;

	private ComponentListener componentListener;

	private Line line = null;
	
	private JButton btnOK = null;
	private JButton btnCancel = null;
				
	private SpinnerNumberModel jsX1Model = null;
	private SpinnerNumberModel jsY1Model = null;
	private SpinnerNumberModel jsX2Model = null;
	private SpinnerNumberModel jsY2Model = null;
	private JSpinner.NumberEditor jsX1Editor = null;
	private JSpinner.NumberEditor jsY1Editor = null;
	private JSpinner.NumberEditor jsX2Editor = null;
	private JSpinner.NumberEditor jsY2Editor = null;
	
	public LineProperty(ComponentListener componentListener) {
		super();
		setTitle("直线属性");
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

		JPanel pane0 = new JPanel();
		pane0.setBorder(new EmptyBorder(10, 18, 10, 18));
		JLabel label = new JLabel("直线两端点P1和P2的坐标(整数):");
		label.setFont(label.getFont().deriveFont(18.0f));
		pane0.add(label);
		contentPane.add(pane0);
		
		JPanel paneP1 = new JPanel();
		
		JLabel labelPX1 = new JLabel("P1.X=");
		labelPX1.setFont(labelPX1.getFont().deriveFont(20.0f));
		paneP1.add(labelPX1);
		jsX1Model = new SpinnerNumberModel();
		JSpinner jsX1 = new JSpinner(jsX1Model);
		jsX1Editor = new JSpinner.NumberEditor(jsX1, "#");
		jsX1.setEditor(jsX1Editor);
		paneP1.add(jsX1);	
		
		JLabel labelPY1 = new JLabel("，P1.Y=");
		labelPY1.setFont(labelPY1.getFont().deriveFont(20.0f));
		paneP1.add(labelPY1);
		jsY1Model = new SpinnerNumberModel();
		JSpinner jsY1 = new JSpinner(jsY1Model);
		jsY1Editor = new JSpinner.NumberEditor(jsY1, "#");
		jsY1.setEditor(jsY1Editor);
		paneP1.add(jsY1);
		
		contentPane.add(paneP1);

		JPanel paneP2 = new JPanel();

		JLabel labelPX2 = new JLabel("P2.X=");
		labelPX2.setFont(labelPX2.getFont().deriveFont(20.0f));
		paneP2.add(labelPX2);
		jsX2Model = new SpinnerNumberModel();
		JSpinner jsX2 = new JSpinner(jsX2Model);
		jsX2Editor = new JSpinner.NumberEditor(jsX2, "#");
		jsX2.setEditor(jsX2Editor);
		paneP2.add(jsX2);

		JLabel labelPY2 = new JLabel("，P2.Y=");
		labelPY2.setFont(labelPY2.getFont().deriveFont(20.0f));
		paneP2.add(labelPY2);
		jsY2Model = new SpinnerNumberModel();
		JSpinner jsY2 = new JSpinner(jsY2Model);
		jsY2Editor = new JSpinner.NumberEditor(jsY2, "#");
		jsY2.setEditor(jsY2Editor);
		paneP2.add(jsY2);

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
	}
	
	public void setSharp(Line line) {
		if(line == null) {
			return;
		}
		this.line = line;			
		PointF s = line.getStartPoint();
		PointF e = line.getEndPoint();
		jsX1Model.setValue(Math.round(s.x));
		jsY1Model.setValue(Math.round(s.y));
		jsX2Model.setValue(Math.round(e.x));
		jsY2Model.setValue(Math.round(e.y));
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(line == null) {
			return;
		}
		if(event.getSource() == btnOK) {
			try {
				double x1 = Double.parseDouble(jsX1Editor.getTextField().getText());
				double y1 = Double.parseDouble(jsY1Editor.getTextField().getText());
				double x2 = Double.parseDouble(jsX2Editor.getTextField().getText());
				double y2 = Double.parseDouble(jsY2Editor.getTextField().getText());
				line.setStartPoint(new PointF(x1, y1));
				line.setEndPoint(new PointF(x2, y2));
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
