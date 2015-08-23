package com.f2.frame;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.f2.sharp.OleEmbed;

public class OleObjectManagerThread extends Thread {
	
	final ArrayList<OleEmbed> workQueue = new ArrayList<OleEmbed>(5);
	final Canvas canvas;
	boolean isStopped = false;
	boolean requestFocusNext = false;
	OleEmbed lastFocusOleEmbed;
	
	public OleObjectManagerThread(Canvas canvas) {
		this.canvas = canvas;
	}
	
	public void requestFocusNext() {
		requestFocusNext = true;
	}
	
	@Override
	public void run() {
		final Display display = new Display();
		while (!isStopped) {
			synchronized (this) {
				if (workQueue.size() > 0) {
					Iterator<OleEmbed> it = workQueue.iterator();
					boolean focusNext = false;
					while (it.hasNext()) {
						OleEmbed embed = it.next();
						
						Shell shell = embed.getShell();
						if (shell == null) {
							shell = embed.openShell(display, canvas);
						}
						
						embed.doOleAction();
						if (!embed.isReleased()) {
							if (!shell.isDisposed()) {
								if (requestFocusNext) {
									if (lastFocusOleEmbed == null || focusNext) {
										shell.setFocus();
										requestFocusNext = false;
										lastFocusOleEmbed = embed;
									} else if (lastFocusOleEmbed == embed) {
										focusNext = true;
									}
								}
							}
						} else {
							if (lastFocusOleEmbed == embed) {
								lastFocusOleEmbed = null;
							}
							it.remove();
						}
					}
					if (requestFocusNext) {
						lastFocusOleEmbed = null;
					}
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				} else {
					try {
						wait();
					} catch (Exception e) {
						isStopped = true;
					}
				}
			}
		}
		super.run();
	}
	
	@Override
	public void interrupt() {
		isStopped = true;
		super.interrupt();
	}
	
	public boolean isStopped() {
		return isStopped;
	}

	public void addOleEmbed(OleEmbed embed) {
		synchronized (this) {
			if (!workQueue.contains(embed)) {
				workQueue.add(embed);
			}
			notifyAll();
		}
	}
	
//	public void removeOleEmbed(OleEmbed embed) {
//		synchronized (this) {
//			if (workQueue.remove(embed)) {
//				embed.release();
//				notifyAll();
//			}
//		}
//	}
}
