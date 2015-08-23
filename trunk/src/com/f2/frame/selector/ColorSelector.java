package com.f2.frame.selector;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import com.f2.tool.ImageTool;

@SuppressWarnings("serial")
public class ColorSelector extends JFrame implements ActionListener{

	private JPanel contentPane;

	private Color color = null;
	private ColorSelectionListener colorSelectionListener;
	private ComponentListener componentListener;
    
	private JButton btnMore;
	
	private Color[] colors = new Color[]{
			Color.RED, 
			Color.GREEN, 
			Color.BLUE, 
			Color.YELLOW, 
			Color.PINK, 
			Color.MAGENTA, 
			};
	private String[] colorLables = new String[]{
			"ºì", 
			"ÂÌ", 
			"À¶", 
			"»Æ", 
			"·Û", 
			"×Ï", 
			};
	private List<JButton> colorBtns = new ArrayList<JButton>();

	/**
	 * Create the frame.
	 */
	public ColorSelector(ComponentListener componentListener) {
		super();
		setTitle("Ñ¡Ôñ±ÊÉ«");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 240);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(2, 1, 0, 0));
				
		JPanel btnMorePane = new JPanel();
		btnMorePane.setBorder(null);
		btnMorePane.setLayout(new GridLayout(1, 1, 0, 0));
		
		btnMore = new JButton("Ñ¡Ôñ¸ü¶àÑÕÉ«...");
		btnMore.setFont(btnMore.getFont().deriveFont(20.0f));
		btnMore.addActionListener(this);
		btnMorePane.add(btnMore);
		
        contentPane.add(btnMorePane);
        
        JPanel btnPane = new JPanel();
        btnPane.setBorder(null);
        btnPane.setLayout(new GridLayout(2, 3, 0, 0));
		for(int i = 0; i < colors.length; i++) {
			JButton btn = new JButton(colorLables[i], ImageTool.getResImageIcon("/res/images/color/c" + i + ".png"));
			btn.setFont(btn.getFont().deriveFont(20.0f));
			btn.addActionListener(this);
			colorBtns.add(btn);
			btnPane.add(btn);
		}
        contentPane.add(btnPane);        
        
		if(componentListener != null) {
			this.addComponentListener(componentListener);
			this.componentListener = componentListener;
		}		
	}

	public void setColorSelectionListener(ColorSelectionListener listener, Color color) {
		this.colorSelectionListener = listener;
		this.color = color;
    	setTitle("±ÊÉ« (RGB=" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")");
	}
	
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == btnMore) {
			Color c = JColorChooser.showDialog(ColorSelector.this, "Ñ¡Ôñ»­±ÊÑÕÉ«", color);
			if(c != null) {
				setColor(c);
			}
		} else {
			for(int i = 0; i < colors.length; i++) {
				JButton btn = colorBtns.get(i);
				if(event.getSource() == btn) {
					setColor(colors[i]);
				}
			}
		}		
	}
	
	private void setColor(Color color) {
		if(color != null) {
			colorSelectionListener.onColorSelection(color);
			setVisible(false); 
			repaintComponent();
		}
	}

	private void repaintComponent() {
		if(componentListener != null) {
			componentListener.componentShown(null);
		}
	}	

	public interface ColorSelectionListener {
		public void onColorSelection(Color color);
	}

}
