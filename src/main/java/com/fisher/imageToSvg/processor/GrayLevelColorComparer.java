package com.fisher.imageToSvg.processor;

import java.awt.Color;

public class GrayLevelColorComparer implements IColorComparer{

	private int tolence;

	public GrayLevelColorComparer(int tolence) {
		super();
		this.tolence = tolence;
	}

	public boolean isSimileColor(Color color, int i) {
		int leftGrayLevel = getGrayLevel(color);
		int rightGrayLevel = getGrayLevel(i);
		return Math.abs(rightGrayLevel-leftGrayLevel)<=this.tolence ;
	}

	private int getGrayLevel(int i) {
        //int ta = (i >> 24) & 0xff;  
        int tr = (i >> 16) & 0xff;  
        int tg = (i >> 8) & 0xff;  
        int tb = i & 0xff;  
		return (int) (tr * 0.299 + tg * 0.587 + tb * 0.114);
	}

	private int getGrayLevel(Color color) {
		return (int) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
	}
}
