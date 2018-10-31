package com.bv_gruppe_d.imagej;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Kanten_Erkennung implements PlugInFilter {
	

	private int lowerPixelBorder = 95;
	private int upperPixelBorder = 135;
	
	@Override
	public void run(ImageProcessor ip) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int pixel = ip.getPixel(w, h);
				
				if (pixel < lowerPixelBorder || pixel > upperPixelBorder) {
					ip.putPixel(w, h, 0);
				} else {
					ip.putPixel(w, h, 255);
				}
			}
		}
		
	}

	@Override
	public int setup(String arg0, ImagePlus img) {
		return DOES_8G;
	}

}
