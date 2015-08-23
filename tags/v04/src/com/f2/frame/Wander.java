package com.f2.frame;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.f2.listener.WanderListener;
import com.f2.listener.ZoomListener;

import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Dictionary;
import java.util.Hashtable;

@SuppressWarnings("serial")
public class Wander extends JFrame  implements ChangeListener, MouseListener, Runnable {

	private JPanel contentPane;
	
	private JButton btnUp;
	private JButton btnDown;
	private JButton btnLeft;
	private JButton btnRight;
	private JButton btnCenter;

	private int wanderAction = ACT_NONE;
	
	private static final int ACT_NONE = 0;
	private static final int ACT_UP = 1;
	private static final int ACT_DOWN = 2;
	private static final int ACT_LEFT = 3;
	private static final int ACT_RIGHT = 4;
	private static final int ACT_CENTER = 5;
	
	public static final int SLIDER_ZOOM_MAX = 50;
	public static final int SLIDER_ZOOM_MID = 25;
	public static final int SLIDER_ZOOM_MIN = 0;
	
	private JSlider zoomSlider = new JSlider(SwingConstants.VERTICAL, SLIDER_ZOOM_MIN, SLIDER_ZOOM_MAX, SLIDER_ZOOM_MID); //横向,1~51,当前12 
    
	private WanderListener wanderListener;
	private ZoomListener zoomListener;
	private ComponentListener componentListener;
	
	/**
	 * Create the frame.
	 */
	public Wander(ComponentListener componentListener) {
		super();

		setTitle("页面漫游");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 166, 380);
		
		
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(2, 1, 0, 0));
		setContentPane(contentPane);		
		
		JPanel wanderPanel = new JPanel();
		wanderPanel.setLayout(new GridLayout(3, 3, 0, 0));
		
		wanderPanel.add(new JLabel(""));

		btnUp = new JButton(new ImageIcon("images/btn/wander_up.png"));
		btnUp.setFont(btnUp.getFont().deriveFont(20.0f));
		btnUp.addMouseListener(this);
		wanderPanel.add(btnUp);

		wanderPanel.add(new JLabel(""));

		btnLeft = new JButton(new ImageIcon("images/btn/wander_left.png"));
		btnLeft.setFont(btnLeft.getFont().deriveFont(20.0f));
		btnLeft.addMouseListener(this);
		wanderPanel.add(btnLeft);

		btnCenter = new JButton(new ImageIcon("images/btn/wander_center.png"));
		btnCenter.setFont(btnCenter.getFont().deriveFont(20.0f));
		btnCenter.addMouseListener(this);
		wanderPanel.add(btnCenter);
				
		btnRight = new JButton(new ImageIcon("images/btn/wander_right.png"));
		btnRight.setFont(btnRight.getFont().deriveFont(20.0f));
		btnRight.addMouseListener(this);
		wanderPanel.add(btnRight);

		wanderPanel.add(new JLabel(""));

		btnDown = new JButton(new ImageIcon("images/btn/wander_down.png"));
		btnDown.setFont(btnDown.getFont().deriveFont(20.0f));
		btnDown.addMouseListener(this);
		wanderPanel.add(btnDown);

		wanderPanel.add(new JLabel(""));
		contentPane.add(wanderPanel);
		
		JPanel zoomPanel = new JPanel();
		zoomPanel.setLayout(new GridLayout(1, 3, 0, 0));
		zoomPanel.add(new JLabel(""));
		
		//设置绘制刻度  
		zoomSlider.setPaintTicks(true);  
        //设置主、次刻度的间距  
        zoomSlider.setMajorTickSpacing(20);  
        zoomSlider.setMinorTickSpacing(5);  
        //设置绘制刻度标签  
        zoomSlider.setPaintLabels(true);  
        Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();  
        labelTable.put(SLIDER_ZOOM_MIN, new JLabel("最小"));  
        labelTable.put(SLIDER_ZOOM_MID, new JLabel("正常"));  
        labelTable.put(SLIDER_ZOOM_MAX, new JLabel("最大"));  
        //指定刻度标签，标签是JLabel  
        zoomSlider.setLabelTable(labelTable);  
        zoomSlider.addChangeListener(this);          
        zoomPanel.add(zoomSlider);
        
		zoomPanel.add(new JLabel(""));
		
		contentPane.add(zoomPanel);
		
		if(componentListener != null) {
			this.addComponentListener(componentListener);
			this.componentListener = componentListener;
		}		
				
		new Thread(this).start();
	}
	
	public void setWanderListener(WanderListener wanderListener) {
		this.wanderListener = wanderListener;
	}

	public void setZoomListener(ZoomListener zoomListener) {
		this.zoomListener = zoomListener;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		//System.out.println("mouseClicked, event=" + event);
		if(wanderListener == null) {
			return;
		}
		if(event.getSource() == btnUp) {
			wanderListener.wanderUp();
		} else if(event.getSource() == btnDown) {
			wanderListener.wanderDown();
		} else if(event.getSource() == btnLeft) {
			wanderListener.wanderLeft();
		} else if(event.getSource() == btnRight) {
			wanderListener.wanderRight();
		} else if(event.getSource() == btnCenter) {
			wanderListener.switchWanderMode();
		}
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		//System.out.println("mouseEntered, event=" + event);
	}

	@Override
	public void mouseExited(MouseEvent event) {
		//System.out.println("mouseExited, event=" + event);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		//System.out.println("mousePressed, event=" + event);
		if(wanderListener == null) {
			return;
		}
		if(event.getSource() == btnUp) {
			wanderAction = ACT_UP;
		} else if(event.getSource() == btnDown) {
			wanderAction = ACT_DOWN;
		} else if(event.getSource() == btnLeft) {
			wanderAction = ACT_LEFT;
		} else if(event.getSource() == btnRight) {
			wanderAction = ACT_RIGHT;
		} else if(event.getSource() == btnCenter) {
			wanderAction = ACT_CENTER;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		//System.out.println("mouseReleased, event=" + event);
		wanderAction = ACT_NONE;
	}

	@Override
	public void run() {
		while(true) {
			if(wanderListener != null) {
				switch(wanderAction) {
				case ACT_UP:
					wanderListener.wanderUp();
					break;
				case ACT_DOWN: 
					wanderListener.wanderDown();
					break;
				case ACT_LEFT: 
					wanderListener.wanderLeft();
					break;
				case ACT_RIGHT: 
					wanderListener.wanderRight();
					break;
				case ACT_CENTER: 
					break;
				}
			}
			try {
				Thread.sleep(40L); // 40毫秒相当于约24帧
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent event) {
		if(zoomListener == null) {
			return;
		}
        //取出滑动条的值，并在文本中显示出来  
        JSlider source = (JSlider) event.getSource(); 
        int value = source.getValue();
		zoomListener.zoom(value);
		repaintComponent();
	}

	private void repaintComponent() {
		if(componentListener != null) {
			componentListener.componentShown(null);
		}
	}

	public void resetZoomValue(int value) {
		zoomSlider.setValue(value);
		this.validate();
	}	
}
