package com.f2.sharp;

import com.f2.tool.PaintBrush;
import com.f2.tool.PointF;
import com.f2.tool.SimulateTool;
import com.f2.tool.constant.SharpType;

public class Simulator extends Handwriting {

	public Simulator(PaintBrush brush) {
		super(brush);
	}

	public void endPoint(PointF p) {
		addPoint(p);
	}

	@Override
	public int getSharpType() {
		return SharpType.SIMULATE;
	}
	
	public AbsMenuSharp getSimulateSharp() {
		AbsMenuSharp sharp = SimulateTool.simulate(mPaintBrush, ps);
		return sharp;
	}
}
