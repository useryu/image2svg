package com.fisher.imageToSvg.processor;

import java.awt.Color;

public interface IColorComparer {

	public boolean isSimileColor(Color colorToSelect, int pixel);

}
