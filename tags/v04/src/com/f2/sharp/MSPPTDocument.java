package com.f2.sharp;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;

import com.f2.tool.PaintBrush;

public class MSPPTDocument extends PagablePicture {

	private InputStream contentInputStream;
	SlideShow slideShow;
	Slide[] slides;
	int slideIndex;

	public MSPPTDocument(PaintBrush brush) {
		super(brush);
	}
	
	@Override
	protected boolean nextPage() {
		if (slides != null) {
			gotoSlide((slideIndex + 1) % slides.length);
		}
		return true;
	}
	
	@Override
	protected boolean prevPage() {
		if (slides != null) {
			if (slideIndex <= 0) {
				gotoSlide(slides.length - 1);
			} else {
				gotoSlide(slideIndex - 1);
			}
		}
		return true;
	}

	public void gotoSlide(int index) {
		if (slides != null && index >= 0 && index < slides.length) {
			slideIndex = index;
			renderPicture();
		}
	}
	
	private void renderPicture() {
		if (slideShow != null) {
			Dimension die = slideShow.getPageSize();
			if (die != null && die.width > 0 && die.height > 0) {
				if (image == null || image.getWidth(null) != die.width || image.getHeight(null) != die.height) {
					image = new BufferedImage(die.width, die.height, BufferedImage.TYPE_INT_ARGB);
				}
				if (slides != null && slideIndex >=0 && slideIndex < slides.length) {
					try {
						slides[slideIndex].draw((Graphics2D)image.getGraphics());
					} catch (Exception e) {
						System.out.println("cannot draw ppt slide " + slideIndex + "/" + slides.length + ", because " + e.getMessage());
					}
				}
			}
		}
	}
	
	public void release() {
		try {
			slideShow = null;
			slides = null;
			slideIndex = 0;
			contentInputStream.close();
		} catch (Exception e) {}
	}

	@Override
	public void setFilepath(String filepath) {
		super.setFilepath(filepath);
		try {
			if (contentInputStream != null) {
				contentInputStream.close();
				contentInputStream = null;
			}
			File file = new File(filepath);
			if (file.isFile() && file.exists()) {
				contentInputStream = new FileInputStream(file);
			}
			contentInputStream = new FileInputStream(filepath);
			slideShow = new SlideShow(contentInputStream);
			slides = slideShow.getSlides();
		} catch (Exception e) {
		}
		gotoSlide(0);
	}
}
