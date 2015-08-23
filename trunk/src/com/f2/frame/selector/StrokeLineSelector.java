package com.f2.frame.selector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.f2.tool.DrawTool;
import com.f2.tool.PaintBrush;
import com.f2.tool.PointF;
import com.f2.tool.constant.StrokeDash;
import com.f2.tool.constant.StrokeWidth;

@SuppressWarnings("serial")
public class StrokeLineSelector extends JFrame implements ChangeListener, ItemListener {

	private JPanel contentPane;

	private StrokeSelectionListener strokeSelectionListener = null;	
	private ComponentListener componentListener;
	
	private JSlider sliderStrokeWidth;	
	private JComboBox comboStrokeWidth;	
	private JComboBox comboStrokeDash;

	//private int strokeLineWidth;
	//private float[] strokeDash;
	
	/**
	 * Create the frame.
	 */
	public StrokeLineSelector(ComponentListener componentListener) {
		super();
		setTitle("笔宽");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 280, 260);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(2, 1, 0, 0));
				
		JPanel sliderPane = new JPanel();
		sliderPane.setBorder(null);
		sliderPane.setLayout(new GridLayout(1, 1, 0, 0));
		
		sliderStrokeWidth = new JSlider(SwingConstants.HORIZONTAL, 1, 51, StrokeWidth.W12); //横向,1~51,当前12 
	    //设置绘制刻度  
        sliderStrokeWidth.setPaintTicks(true);  
        //设置主、次刻度的间距  
        sliderStrokeWidth.setMajorTickSpacing(20);  
        sliderStrokeWidth.setMinorTickSpacing(5);  
        //设置绘制刻度标签  
        sliderStrokeWidth.setPaintLabels(true);  
        Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();  
        labelTable.put(1, new JLabel("幼细"));  
        labelTable.put(10, new JLabel("较细"));  
        labelTable.put(20, new JLabel("一般"));  
        labelTable.put(30, new JLabel("中等"));  
        labelTable.put(40, new JLabel("较粗"));  
        labelTable.put(50, new JLabel("特粗"));  
        //指定刻度标签，标签是JLabel  
        sliderStrokeWidth.setLabelTable(labelTable);  
        sliderStrokeWidth.addChangeListener(this);  
        
        sliderPane.add(sliderStrokeWidth);
        contentPane.add(sliderPane);
        
        JPanel btnPane = new JPanel();
        btnPane.setBorder(null);
        btnPane.setLayout(new GridLayout(2, 1, 0, 0));
                
        JPanel cbPanel = new JPanel();
        cbPanel.setBorder(null);
        cbPanel.setLayout(new GridLayout(1, 1, 0, 0));        
        CbStrokeWidth[] cbWidthObj = new CbStrokeWidth[6];
    	cbWidthObj[0] = new CbStrokeWidth(StrokeWidth.W2);
    	cbWidthObj[1] = new CbStrokeWidth(StrokeWidth.W6);
    	cbWidthObj[2] = new CbStrokeWidth(StrokeWidth.W12);
    	cbWidthObj[3] = new CbStrokeWidth(StrokeWidth.W20);
    	cbWidthObj[4] = new CbStrokeWidth(StrokeWidth.W28);
    	cbWidthObj[5] = new CbStrokeWidth(StrokeWidth.W36);
		comboStrokeWidth = new JComboBox(cbWidthObj);
		comboStrokeWidth.setSelectedIndex(2);
		comboStrokeWidth.setEditable(false);
		comboStrokeWidth.setRenderer(new CbStrokeWidth(StrokeWidth.W12));
		//widthComboBox.setMaximumRowCount(3);
		comboStrokeWidth.addItemListener(this);
        cbPanel.add(comboStrokeWidth);        
        btnPane.add(cbPanel);
        
        JPanel cbPanel2 = new JPanel();
        cbPanel2.setBorder(null);
        cbPanel2.setLayout(new GridLayout(1, 1, 0, 0));
        CbStrokeDash[] cbDashObj = new CbStrokeDash[7];
        cbDashObj[0] = new CbStrokeDash(StrokeDash.D0);
        cbDashObj[1] = new CbStrokeDash(StrokeDash.D1);
        cbDashObj[2] = new CbStrokeDash(StrokeDash.D2);
        cbDashObj[3] = new CbStrokeDash(StrokeDash.D3);
        cbDashObj[4] = new CbStrokeDash(StrokeDash.D4);
        cbDashObj[5] = new CbStrokeDash(StrokeDash.D5);
        cbDashObj[6] = new CbStrokeDash(StrokeDash.D6);
        comboStrokeDash = new JComboBox(cbDashObj);
        comboStrokeDash.setSelectedIndex(0);
        comboStrokeDash.setEditable(false);
        comboStrokeDash.setRenderer(new CbStrokeDash(StrokeDash.D0));
		//widthComboBox.setMaximumRowCount(3);
        comboStrokeDash.addItemListener(this);
        cbPanel2.add(comboStrokeDash);
        btnPane.add(cbPanel2);
        
        contentPane.add(btnPane);        
        
		if(componentListener != null) {
			this.addComponentListener(componentListener);
			this.componentListener = componentListener;
		}		
	}

	public void setStrokeSelectionListener(StrokeSelectionListener listener, int strokeLineWidth, float[] strokeDash) {
		this.strokeSelectionListener = listener;
    	//this.strokeLineWidth = strokeLineWidth;
    	//this.strokeDash = strokeDash;
    	sliderStrokeWidth.setValue(strokeLineWidth);
    	setTitle("笔宽 (" + strokeLineWidth + "像素)");
	}
	
    public void stateChanged(ChangeEvent event)  
    {
    	if(strokeSelectionListener == null) {
    		return;
    	}
        //取出滑动条的值，并在文本中显示出来  
        JSlider source = (JSlider) event.getSource(); 
        int value = source.getValue();
        strokeSelectionListener.onStrokeLineWidthSelection(value);
		setTitle("笔宽 (" + value + "像素)");
		repaintComponent();
    }  

	@Override
	public void itemStateChanged(ItemEvent event) {
    	if(strokeSelectionListener == null) {
    		return;
    	}
		if(event.getSource() == comboStrokeWidth) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				CbStrokeWidth cbWidth = (CbStrokeWidth) event.getItem();
				strokeSelectionListener.onStrokeLineWidthSelection(cbWidth.getStrokeWidth());
				setVisible(false); //dispose();
				repaintComponent();
			}
		}
		if(event.getSource() == comboStrokeDash) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				CbStrokeDash cbDash = (CbStrokeDash) event.getItem();
				strokeSelectionListener.onStrokeDashSelection(cbDash.getStrokeDash());
				setVisible(false); //dispose();
				repaintComponent();
			}
		}
	}

	private void repaintComponent() {
		if(componentListener != null) {
			componentListener.componentShown(null);
		}
	}	

	private class CbStrokeWidth extends JLabel implements ListCellRenderer {
		
		private Icon icon = null;
		private int strokeWidth;
						
		public CbStrokeWidth(int width) {
			super();
			this.strokeWidth = width;
		}
		
		public int getStrokeWidth() {
			return strokeWidth;
		}

		public Icon getIcon() {
			if(icon != null) {
				return icon;
			}
			BufferedImage image = new BufferedImage(248, 48, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
			
			PointF p1 = new PointF(0 + strokeWidth/2 + 3, 0 + image.getHeight()/2);
			PointF p2 = new PointF(0 + image.getWidth() - strokeWidth/2 - 6, 0 + image.getHeight()/2);
			PaintBrush brush = new PaintBrush();
			brush.setColor(Color.BLUE);
			brush.setStrokeLineWidth(strokeWidth);
			brush.setStrokeDash(StrokeDash.D0);
			DrawTool.drawLine(g, p1, p2, brush);
			
			g.dispose();
			icon = new ImageIcon(image);
			return icon;
		}
		
		public Component getListCellRendererComponent(JList list,
				Object value, int index, boolean isSelected, boolean cellHasFocus) {
			
			DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
			JLabel renderer = (JLabel) defaultRenderer  
	                .getListCellRendererComponent(list, value, index, isSelected,  
	                        cellHasFocus); 

			renderer.setText(""); //
			if(value instanceof CbStrokeWidth) {
				int width = ((CbStrokeWidth) value).getStrokeWidth();
				renderer.setText(""+width); //
				//renderer.setText(" " + width + " 像素");
				renderer.setIcon(((CbStrokeWidth) value).getIcon());
			}

			return renderer;
		}			
	}

	private class CbStrokeDash extends JLabel implements ListCellRenderer {
		
		private Icon icon = null;
		private float[] strokeDash;
						
		public CbStrokeDash(float[] dash) {
			super();
			this.strokeDash = dash;
		}
		
		public float[] getStrokeDash() {
			return strokeDash;
		}

		public Icon getIcon() {
			if(icon != null) {
				return icon;
			}
			BufferedImage image = new BufferedImage(248, 48, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
			
			PointF p1 = new PointF(0 + StrokeWidth.W12/2 + 3, 0 + image.getHeight()/2);
			PointF p2 = new PointF(0 + image.getWidth() - StrokeWidth.W12/2 - 6, 0 + image.getHeight()/2);
			PaintBrush brush = new PaintBrush();
			brush.setColor(Color.RED);
			brush.setStrokeLineWidth(StrokeWidth.W12);
			brush.setStrokeDash(strokeDash);
			DrawTool.drawLine(g, p1, p2, brush);
			
			g.dispose();
			icon = new ImageIcon(image);
			return icon;
		}
		
		public Component getListCellRendererComponent(JList list,
				Object value, int index, boolean isSelected, boolean cellHasFocus) {
			
			DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
			JLabel renderer = (JLabel) defaultRenderer  
	                .getListCellRendererComponent(list, value, index, isSelected,  
	                        cellHasFocus); 

			renderer.setText(""); //
			if(value instanceof CbStrokeDash) {
				renderer.setIcon(((CbStrokeDash) value).getIcon());
			}

			return renderer;
		}			
	}

	public interface StrokeSelectionListener {
		public void onStrokeLineWidthSelection(int strokeLineWidth);
		public void onStrokeDashSelection(float[] strokeDash);
	}
}
