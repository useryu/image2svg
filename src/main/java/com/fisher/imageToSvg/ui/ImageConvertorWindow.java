package com.fisher.imageToSvg.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Color;

import javax.swing.BoxLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JList;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.JSVGComponent;
import org.w3c.dom.svg.SVGDocument;

import com.fisher.imageToSvg.processor.HSBColorComparer;
import com.fisher.imageToSvg.processor.ImageUtil;
import com.fisher.imageToSvg.processor.ImageToSvgGenerator;

public class ImageConvertorWindow {

	private JFrame frame;	
	private JPanel rightPanel;
	private SourceJPanel sourceImgPanel;
	private JList<String> colorList;
	private Set<String> colors = new HashSet<String>();
	private DefaultListModel<String> colorListModel;
	private JSVGCanvas previewPanel = new JSVGCanvas(null, false, false);
	private File svgFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageConvertorWindow window = new ImageConvertorWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ImageConvertorWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBackground(Color.WHITE);
		frame.setBounds(100, 100, 1115, 644);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar);
		
		JButton btnNewButton = new JButton("Load Image");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadFile(frame);
			}
		});
		toolBar.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Save SVG");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileChooser.showSaveDialog(frame);
				if (result == JFileChooser.CANCEL_OPTION) {
					return;
				}
				File f = fileChooser.getSelectedFile();
				try {
					ImageUtil.doCopyFile(svgFile, f, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		toolBar.add(btnNewButton_1);
		
		JButton btnPreview = new JButton("Preview");
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doPreview(frame);
			}
		});
		toolBar.add(btnPreview);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		sourceImgPanel = new SourceJPanel();
		panel.add(sourceImgPanel);
		
		rightPanel = new JPanel();
		panel.add(rightPanel);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel = new JLabel("Preview");
		rightPanel.add(lblNewLabel);
		
		previewPanel.setBackground(Color.white);
		previewPanel.setSize(300, 300);
		rightPanel.add(previewPanel);
		
		JLabel lblNewLabel_1 = new JLabel("Main Colors");
		rightPanel.add(lblNewLabel_1);
		
		JPanel mainColorPanel = new JPanel();
		rightPanel.add(mainColorPanel);
		
		colorList = new JList<String>();
		colorListModel = new DefaultListModel<String>();
		colorList.setModel(colorListModel);
		mainColorPanel.add(colorList);
		sourceImgPanel.addMouseListener(new PickColorListener(colors , colorListModel));
		colorList.addMouseListener(new RemoveMainColorListener(colors, colorListModel, colorList));
	}
	

	public void doPreview(JFrame frame) {
		svgFile = this.genSVGFile();
		if(svgFile==null) {
			//TODO alert
		}else {
			try {
				Dimension dim = new Dimension();
				dim.setSize(300d, 300d);
				AffineTransform at = new AffineTransform();
				at.rotate(10);
				at.scale(0.2, 0.2);
				previewPanel.setRenderingTransform(at, true);
				previewPanel.setMySize(dim );
				this.previewPanel.loadSVGDocument(svgFile.toURL().toString());
				AffineTransform rt = previewPanel.getRenderingTransform();
				rt.scale(0.2d, 0.2d);
			} catch (MalformedURLException e) {
			}
			this.frame.repaint();
		}
	}
	
	private File genSVGFile() {
		File outFile = null;
		try {
			ImageToSvgGenerator svgGenerator = new ImageToSvgGenerator();
			outFile = File.createTempFile("temp", ".svg");
			String[] strArray = new String[colors.size()];
			String[] array = this.colors.toArray(strArray);
			svgGenerator.setColorComparer(new HSBColorComparer(0.08f));
			svgGenerator.genSvg(this.sourceImgPanel.getSourceFile(), outFile, array);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outFile;
	}

	public void loadFile(JFrame frame) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showOpenDialog(frame);
		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}
		File sourceFile = fileChooser.getSelectedFile();
		sourceFile.canRead();
		if (sourceFile == null || sourceFile.getName().equals("")) {
			JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
					"Invalid File Name", JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				sourceImgPanel.setSourceFile(sourceFile);
				this.colors.clear();
				this.colorListModel.clear();
				this.frame.repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
