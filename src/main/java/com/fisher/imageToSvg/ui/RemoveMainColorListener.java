package com.fisher.imageToSvg.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class RemoveMainColorListener implements MouseListener {

	private DefaultListModel<String> model;
	private JList<String> list;
	private Set<String> colors;

	public RemoveMainColorListener(Set<String> colors,
			DefaultListModel<String> model,JList<String> list) {
		this.colors = colors;
		this.model = model;
		this.list = list;
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()==2){   //When double click JList  
			for (int i=0; i<model.size(); i++) {
				String item = model.get(i);
				if(list.isSelectedIndex(i)) {
					model.remove(i);
					colors.remove(item);
				}
			}
        } 
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
