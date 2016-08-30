package com.fisher.imageToSvg.ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import com.fisher.imageToSvg.processor.ImageUtil;

public class PickColorListener implements MouseListener {
	
	private DefaultListModel<String> model;
	private Set<String> colors;

	public PickColorListener(Set<String> colors, DefaultListModel<String> model) {
		super();
		this.colors = colors;
		this.model = model;
	}

	public void mouseClicked(MouseEvent arg0) {
		try {
			Color pickedColor = this.pickColor();
			String pickedColorStr = ImageUtil.getColorStr(pickedColor);
			colors.add(pickedColorStr);
			this.sync();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	private void sync() {
		model.clear();
		for(String color:colors) {
			model.addElement(color);
		}
	}

	public Color pickColor() throws AWTException {
        Point mousepoint = MouseInfo.getPointerInfo().getLocation();
        Robot robot = new Robot();
        Color pixel = robot.getPixelColor(mousepoint.x, mousepoint.y);
        return pixel;
    }

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


}
