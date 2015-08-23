package com.f2.frame;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.f2.frame.Wander.WanderListener;
import com.f2.frame.Wander.ZoomListener;
import com.f2.frame.data.CanvasItem;
import com.f2.panel.PreviewPanel;
import com.f2.sharp.AbsMenuSharp;
import com.f2.sharp.AbsSharpFactory;
import com.f2.socket.MySocketStateListener;
import com.f2.socket.TCPSocketClient;
import com.f2.socket.TCPSocketPacket;
import com.f2.tool.CursorTool;
import com.f2.tool.PaintBrush;
import com.f2.tool.file.ByteReader;

@SuppressWarnings("serial")
public class CanvasFrame extends JFrame implements WanderListener, ZoomListener, MySocketStateListener {

	DrawCanvas mDrawCanvas;
	final HashMap<String, AbsMenuSharp> mSessionActionSharpMap = new HashMap<String, AbsMenuSharp>();
	
	/**
	 * Create the frame.
	 */
	public CanvasFrame(PaintBrush brush) {
		super();
		
		setUndecorated(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(null);
		contentPane.setLayout(null);
		setContentPane(contentPane);
		Dimension dime = getToolkit().getScreenSize();
		
		setVisible(true);
		
		mDrawCanvas = new DrawCanvas(brush);
		mDrawCanvas.setSize(dime.width, dime.height);
		contentPane.add(mDrawCanvas);
		
		CursorTool.setCursorFrame(this);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		mDrawCanvas.release();
	}
	
	public void initBufferImage() {
		mDrawCanvas.initBufferImage();
	}

	public void setCanvasItem(CanvasItem canvasItem) {
		mDrawCanvas.setCanvasItem(canvasItem);
		mDrawCanvas.cancelSelected();
	}

	public void setCanvasRepaintListener(PreviewPanel pp) {
		mDrawCanvas.setCanvasRepaintListener(pp);
	}

	public void initCanvasItem(CanvasItem canvasItem) {
		mDrawCanvas.initCanvasItem(canvasItem);
	}

	public void undoRedo(CanvasItem clone) {
		mDrawCanvas.undoRedo(clone);
	}

	public void sharpPaste(AbsMenuSharp sharp) {
		mDrawCanvas.sharpPaste(sharp);
	}

	public void addScreenShot(BufferedImage image) {
		mDrawCanvas.addScreenShot(image);
	}

	public CanvasItem getCanvasItem() {
		return mDrawCanvas.getCanvasItem();
	}

	public void setFilePath(String filepath) {
		mDrawCanvas.setFilePath(filepath);
	}

	public void cancelSelected() {
		mDrawCanvas.cancelSelected();
	}
	
	public void setCanvasResetListener(MainFrame mainFrame) {
		mDrawCanvas.setCanvasResetListener(mainFrame);
	}

	@Override
	public void onZoomAction(int value) {
		mDrawCanvas.onZoomAction(value);
	}

	@Override
	public void onWander(double dx, double dy) {
		mDrawCanvas.onWander(dx, dy);
	}

	@Override
	public void onWanderReset() {
		mDrawCanvas.onWanderReset();
	}

	public void repaintCanvas() {
		mDrawCanvas.repaintCanvas();
	}

	public void sharpRemoveLast() {
		mDrawCanvas.sharpRemoveLast();
	}

	public void sharpClear() {
		mDrawCanvas.sharpClear();
	}

	private void fireKey(int key) {
		try {
			Robot robot = new Robot();
			robot.keyPress(key);
			robot.keyRelease(key);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnected(TCPSocketClient client) {
		
	}

	@Override
	public void onDisconnected(TCPSocketClient client) {
		
	}

	@Override
	public void onConnectFailed(TCPSocketClient client) {
		
	}

	@Override
	public void onMessageFetched(TCPSocketClient client, TCPSocketPacket packet) {
		String addressString = client.getSocket().getInetAddress().toString();
		// System.out.println("message fetched from " + addressString);
		ByteReader byteReader;
		try {
			byte[] data = packet.getData();
			byteReader = data != null ? new ByteReader(data) : null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		switch (packet.getPacketId()) {
		case TCPSocketPacket.PACKET_ID_DATA:
			AbsMenuSharp sharp;
			try {
				switch (byteReader.readInt()) {
				case TCPSocketPacket.DATA_ADD_COMPONENT:
					sharp = AbsSharpFactory.getSharp(byteReader.readInt());
					sharp.initFromBytes(byteReader.readBytes());
					mDrawCanvas.sharpAdd(sharp);
					mSessionActionSharpMap.put(addressString, sharp);
					mDrawCanvas.repaintCanvas();
					break;
				case TCPSocketPacket.DATA_UPDATE_COMPONENT:
					sharp = mSessionActionSharpMap.get(addressString);
					int type = byteReader.readInt();
					if (sharp == null || type != sharp.getSharpType()) {
						sharp = AbsSharpFactory.getSharp(type);
						mSessionActionSharpMap.put(addressString, sharp);
						mDrawCanvas.sharpAdd(sharp);
					}
					sharp.initFromBytes(byteReader.readBytes());
					mDrawCanvas.repaintCanvas();
					break;
				case TCPSocketPacket.DATA_REMOVE_COMPONENT:
					sharp = mSessionActionSharpMap.remove(addressString);
					if (sharp != null) {
						mDrawCanvas.sharpRemove(sharp);
						mDrawCanvas.repaintCanvas();
					}
					break;
				case TCPSocketPacket.DATA_KEY_LEFT:
					fireKey(KeyEvent.VK_LEFT);
					break;
				case TCPSocketPacket.DATA_KEY_RIGHT:
					fireKey(KeyEvent.VK_RIGHT);
					break;
				case TCPSocketPacket.DATA_KEY_UP:
					fireKey(KeyEvent.VK_UP);
					break;
				case TCPSocketPacket.DATA_KEY_DOWN:
					fireKey(KeyEvent.VK_DOWN);
					break;
				case TCPSocketPacket.DATA_KEY_OPEN:
					break;
				case TCPSocketPacket.DATA_KEY_SWITCH:
					OleObjectManagerThread thread = mDrawCanvas.getOleObjectManagerThread();
					if (thread != null) {
						thread.requestFocusNext();
					}
					break;
				case TCPSocketPacket.DATA_KEY_CONFIRM:
					fireKey(KeyEvent.VK_ENTER);
					break;
				case TCPSocketPacket.DATA_KEY_PAGE_DOWN:
					fireKey(KeyEvent.VK_PAGE_DOWN);
					break;
				case TCPSocketPacket.DATA_KEY_PAGE_UP:
					fireKey(KeyEvent.VK_PAGE_UP);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case TCPSocketPacket.PACKET_ID_REQUEST_DATA:
			switch (byteReader.readInt()) {
			case TCPSocketPacket.DATA_ALL_SCREEN:
				CanvasItem item = mDrawCanvas.getCanvasItem();
				client.send(TCPSocketPacket.createDataPacket(TCPSocketPacket.DATA_ALL_SCREEN, item == null ? null : item.toBytes()));
				break;
			}
			break;
		}
	}

	public void hideExtFrames() {
		if(mDrawCanvas != null) {
			mDrawCanvas.hideExtFrames();
		}
	}
}
