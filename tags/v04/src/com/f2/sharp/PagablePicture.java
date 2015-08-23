package com.f2.sharp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.f2.tool.PaintBrush;
import com.f2.tool.PointF;

public abstract class PagablePicture extends Picture {

	final Rectangle prevPageButtonBounds = new Rectangle();
	final Rectangle nextPageButtonBounds = new Rectangle();
	
	public PagablePicture(PaintBrush brush) {
		super(brush);
	}
	
	private void resizePageButton() {
		Rectangle bounds = getBounds();
		int buttonWidth = (int)bounds.width / 4;
		int buttonHeight = (int)bounds.height / 6;
		int buttonX = bounds.x + bounds.width - buttonWidth;
		int buttonY = bounds.y + bounds.height - buttonHeight;
		nextPageButtonBounds.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
		prevPageButtonBounds.setBounds(buttonX - buttonWidth - 5, buttonY, buttonWidth, buttonHeight);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		resizePageButton();
	}
	
	@Override
	public void updateBounds(PointF p) {
		super.updateBounds(p);
		resizePageButton();
	}
	
	@Override
	public void setBounds(PointF s, PointF e) {
		super.setBounds(s, e);
		resizePageButton();
	}
	
	@Override
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		resizePageButton();
	}
	
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		drawPageButton((Graphics2D)g);
	}
	
	public void drawPageButton(Graphics2D graphics) {
		graphics.setColor(Color.BLUE);
		graphics.fill(nextPageButtonBounds);
		graphics.fill(prevPageButtonBounds);
	}
	
	@Override
	public boolean onClick(PointF p) {
		if (inBounds(p, nextPageButtonBounds)) {
			return nextPage();
		} else if (inBounds(p, prevPageButtonBounds)) {
			return prevPage();
		}
		return false;
	}
	
	private boolean inBounds(PointF p, Rectangle bounds) {
		return (p.x > bounds.x && p.x < bounds.width + bounds.x
				&& p.y > bounds.y && p.y < bounds.height + bounds.y);
	}
	
	protected abstract boolean nextPage();
	
	protected abstract boolean prevPage();
}
