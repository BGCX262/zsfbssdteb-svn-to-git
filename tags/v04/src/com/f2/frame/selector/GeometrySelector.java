package com.f2.frame.selector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import com.f2.tool.PaintBrush;
import com.f2.tool.constant.SharpType;

@SuppressWarnings("serial")
public class GeometrySelector extends JFrame implements ActionListener {

	private JPanel contentPane;

	private PaintBrush mPaintBrush = null;
	
	private ComponentListener componentListener;

	private int[] types = new int[]{
			SharpType.TRIANGLE_RIGHT, 
			SharpType.TRIANGLE_EQUILATERAL, 
			SharpType.RECTANGLE, 
			SharpType.SQUARE, 
			SharpType.PENTAGON,
			SharpType.HEXAGON,
			SharpType.LINE, 
			SharpType.POLYLINE, 
			SharpType.ELLIPSE,
			SharpType.CIRCLE};
	private String[] btnLabels = new String[]{
			"直角三角", 
			"等边三角", 
			"长方形", 
			"正方形", 
			"正五边形", 
			"正六边形", 
			"直线", 
			"折线段", 
			"椭圆", 
			"圆形"};
	private List<JButton> btns = new ArrayList<JButton>();
	
	/**
	 * Create the frame.
	 */
	public GeometrySelector(PaintBrush brush, ComponentListener componentListener) {
		super();
		this.mPaintBrush = brush;
		setTitle("形状");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//setBounds(100, 100, 100, 474); // 1*9
		//setBounds(100, 100, 268, 178); // 3x3
		setBounds(100, 100, 200, 279); // 2x5
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(types.length/2, 2, 0, 0));
		
		for(int i = 0; i < types.length; i++) {
			JButton btn = new JButton(btnLabels[i]);
			btn.addActionListener(this);
			btns.add(btn);
			contentPane.add(btn);
		}
		
		if(componentListener != null) {
			this.addComponentListener(componentListener);
			this.componentListener = componentListener;
		}		
	}

	public void setPaintBrush(PaintBrush brush) {
		this.mPaintBrush = brush;
	}

	public void actionPerformed(ActionEvent event) {
		for(int i = 0; i < types.length; i++) {
			JButton btn = btns.get(i);
			if(event.getSource() == btn) {
				mPaintBrush.setType(types[i]);
				setVisible(false); 
				repaintComponent();
			}
		}
	}	

	private void repaintComponent() {
		if(componentListener != null) {
			componentListener.componentShown(null);
		}
	}	
}
