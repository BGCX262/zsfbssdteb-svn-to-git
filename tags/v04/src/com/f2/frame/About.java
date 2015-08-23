package com.f2.frame;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;

@SuppressWarnings("serial")
public class About extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public About(ComponentListener componentListener) {
		setTitle("关于");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 315, 210);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				
		JLabel label = new JLabel("新学友.五好学生.电子版本软件");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label);
		
		JLabel label2 = new JLabel("Five Best Student Board V0.4");
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label2);
		
		JLabel label4 = new JLabel("");
		label4.setIcon(new ImageIcon("images/logo.png"));
		label4.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label4);

		JLabel label3 = new JLabel("http://www.515best.com");
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label3);

		JPanel panel2 = new JPanel();
		panel2.setBorder(new EmptyBorder(0, 100, 0, 100));
		panel.add(panel2);
		panel2.setLayout(new BorderLayout(0, 0));
		
		JButton btnOK = new JButton("确定");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				About.this.dispose();
			}
		});
		panel2.add(btnOK, BorderLayout.CENTER);
		
		this.addComponentListener(componentListener);
	}

}
