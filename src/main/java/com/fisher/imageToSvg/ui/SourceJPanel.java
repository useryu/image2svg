package com.fisher.imageToSvg.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class SourceJPanel extends JPanel {

	private File sourceFile;
	
	private static final long serialVersionUID = 1L;

	public SourceJPanel() {
		super();
		this.setBackground(Color.white);
	}

	@Override
	public void paintComponent(Graphics g) {
		if(this.sourceFile==null) {
			super.paintComponent(g);
		}else {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;  
			try {
				BufferedImage image = getImage();
				float scale = this.getScale(image);
				g2.drawImage(image, 0, 0, this.round(image.getWidth()*scale) , this.round(image.getHeight()*scale), null);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
    }

	private int round(float f) {
		int rounded = Math.round(f);
		return rounded;
	}

	private float getScale(BufferedImage image) {
		int maxWidth = this.getWidth();
		if(image.getWidth()<=maxWidth) {
			return 1;
		}
		return maxWidth*1f/image.getWidth();
	}

	private BufferedImage getImage() throws IOException {
		return ImageIO.read(sourceFile);
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	} 
}
