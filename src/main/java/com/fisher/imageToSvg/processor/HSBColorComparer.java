package com.fisher.imageToSvg.processor;

import java.awt.Color;

public class HSBColorComparer implements IColorComparer {
	
	private float tolence;
	
	public HSBColorComparer(float tolence) {
		super();
		this.tolence = tolence;
	}

	public boolean isSimileColor(Color colorToSelect, int pixel) {
		float[] colorToSelectHsb = Color.RGBtoHSB(colorToSelect.getRed(), colorToSelect.getGreen(), colorToSelect.getBlue(), null);
        int tr = (pixel >> 16) & 0xff;  
        int tg = (pixel >> 8) & 0xff;  
        int tb = pixel & 0xff;  
		float[] pixelHsb = Color.RGBtoHSB(tr, tg, tb, null);
		return Math.abs(colorToSelectHsb[0]-pixelHsb[0])<=tolence;
	}

}
