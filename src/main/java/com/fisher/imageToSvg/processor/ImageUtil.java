package com.fisher.imageToSvg.processor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class ImageUtil {


    private static final long FILE_COPY_BUFFER_SIZE = 1024* 1024 * 30;

	public static Color getColor(String col) {
		Color convertedColor = Color.ORANGE;
        try {
            convertedColor = new Color(Integer.parseInt(col.substring(1), 16));
        } catch(NumberFormatException e) {
            // codes to deal with this exception
        }
        return convertedColor;
	}
	
	
	public static String getColorStr(Color col) {
		int rgb = col.getRGB();
		String str = Integer.toHexString(rgb);
		return "#"+str.substring(2);
	}
	
	/**
	 * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
	 * penalty of BufferedImage.getRGB unmanaging the image.
	 */
	public static int[] getRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
		int type = image.getType();
		if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
			return (int [])image.getRaster().getDataElements( x, y, width, height, pixels );
		return image.getRGB( x, y, width, height, pixels, 0, width );
    }

	/**
	 * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
	 * penalty of BufferedImage.setRGB unmanaging the image.
	 */
	public static void setRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
		int type = image.getType();
		if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
			image.getRaster().setDataElements( x, y, width, height, pixels );
		else
			image.setRGB( x, y, width, height, pixels, 0, width );
    }
	
    public static BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
        if ( dstCM == null )
            dstCM = src.getColorModel();
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }
    
    public static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input  = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            closeQuietly(output);
            closeQuietly(fos);
            closeQuietly(input);
            closeQuietly(fis);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
    

	
	public static void getGrayImg(BufferedImage source, BufferedImage dist) {
		int width = source.getWidth();
		int height = source.getHeight();
        int[] inPixels = new int[width * height];  
        int[] outPixels = new int[width * height];  
        source.getRGB(0, 0, width, height, inPixels, 0, width);
        int index = 0;  
        for (int row = 0; row < height; row++) {  
            int ta = 0, tr = 0, tg = 0, tb = 0;  
            for (int col = 0; col < width; col++) {  
                index = row * width + col;  
                ta = (inPixels[index] >> 24) & 0xff;  
                tr = (inPixels[index] >> 16) & 0xff;  
                tg = (inPixels[index] >> 8) & 0xff;  
                tb = inPixels[index] & 0xff;  
                int gray = (int) (0.299 * tr + 0.587 * tg + 0.114 * tb);  
                outPixels[index] = (ta << 24) | (gray << 16) | (gray << 8)  
                        | gray;  
            }  
        }
        dist.setRGB(0, 0, width, height, outPixels, 0, width); 
	}
	
}
