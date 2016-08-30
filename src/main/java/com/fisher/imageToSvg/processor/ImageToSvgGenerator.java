package com.fisher.imageToSvg.processor;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

public class ImageToSvgGenerator {

	private IColorComparer colorComparer = new GrayLevelColorComparer(10);
	
	public void genSvg(File inFile, File outFile, String[] mainColors) throws Exception {
		// Get a DOMImplementation.
		DOMImplementation domImpl = GenericDOMImplementation
				.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);
		Element root = document.getDocumentElement();
		root.setAttributeNS(null, "width", "400");
		root.setAttributeNS(null, "height", "400");
		
		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

		// Finally, stream out SVG to the standard output using
		// UTF-8 encoding.
		OutputStream outFileStream = null;
		try {
			BufferedImage img = ImageIO.read(inFile);
			for(String mainColor:mainColors) {
				Color c = ImageUtil.getColor(mainColor);
				svgGenerator.setColor(c);
				svgGenerator.fill(getImageShape(img,c));
			}
			boolean useCSS = true; // we want to use CSS style attributes
			outFileStream =  new FileOutputStream(outFile);
			Writer out = new OutputStreamWriter(outFileStream, "UTF-8");
			svgGenerator.stream(out, useCSS);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(outFileStream!=null) {
				outFileStream.close();
			}
		}
	}

	private Area getImageShape(BufferedImage img,Color color) throws IOException {
		ArrayList<Integer> x = new ArrayList<Integer>();
		ArrayList<Integer> y = new ArrayList<Integer>();
		int width = img.getWidth();// 图像宽度
		int height = img.getHeight();// 图像高度

		// 筛选像素
		// 首先获取图像所有的像素信息
		int pixels[] = new int[width*height];
		img.getRGB(0, 0, width, height, pixels, 0, width);
		// 循环像素
		for (int i = 0; i < pixels.length; i++) {
			// 筛选，将指定的像素的坐标加入到坐标ArrayList x和y中
			if (colorComparer.isSimileColor(color, pixels[i])) {
				x.add(i % width > 0 ? i % width - 1 : 0);
				y.add(i % width == 0 ? (i == 0 ? 0 : i / width - 1) : i / width);
			} else {
				continue;
			}
		}

		// 建立图像矩阵并初始化(0为透明,1为不透明)
		int[][] matrix = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				matrix[i][j] = 0;
			}
		}

		// 导入坐标ArrayList中的不透明坐标信息
		for (int c = 0; c < x.size(); c++) {
			matrix[y.get(c)][x.get(c)] = 1;
		}

		/*
		 * 逐一水平"扫描"图像矩阵的每一行，将不透明的像素生成为Rectangle，再将每一行的Rectangle通过Area类的rec对象进行合并，
		 * 最后形成一个完整的Shape图形
		 */
		Area rec = new Area();
		int temp = 0;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (matrix[i][j] == 1) {
					if (temp == 0)
						temp = j;
					else if (j == width) {
						if (temp == 0) {
							Rectangle rectemp = new Rectangle(j, i, 1, 1);
							rec.add(new Area(rectemp));
						} else {
							Rectangle rectemp = new Rectangle(temp, i,
									j - temp, 1);
							rec.add(new Area(rectemp));
							temp = 0;
						}
					}
				} else {
					if (temp != 0) {
						Rectangle rectemp = new Rectangle(temp, i, j - temp, 1);
						rec.add(new Area(rectemp));
						temp = 0;
					}
				}
			}
			temp = 0;
		}
		return rec;
	}

	public static void main(String[] args) throws IOException {
		ImageToSvgGenerator test = new ImageToSvgGenerator();
		File inFile = new File("d:/temp/china.jpg");
		File outFile = new File("d:/temp/test.svg");
		String[] mainColors = new String[] {"#ed1b24","#23b14d","#000000"};
		try {
			test.setColorComparer(new HSBColorComparer(0.01f));
			test.genSvg(inFile, outFile, mainColors );
		} catch (Exception e) {
			e.printStackTrace();
		}
//		ImageToSvgGenerator test = new ImageToSvgGenerator();
//		File inFile = new File("d:/temp/black.jpg");
//		File outFile = new File("d:/temp/test.svg");
//		String[] mainColors = new String[] {"#ed1b24","#23b14d","#000000"};
//		try {
//			test.setColorComparer(new HSBColorComparer(0.01f));
//			test.genSvg(inFile, outFile, mainColors );
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public IColorComparer getColorComparer() {
		return colorComparer;
	}

	public void setColorComparer(IColorComparer colorComparer) {
		this.colorComparer = colorComparer;
	}
}