package com.bv_gruppe_d.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Kontrastanpassung_PlugIn implements PlugInFilter {
	
	private int width;
	private int height;
	private int[] histogram;
	private int lowestPixelValue;
	private int highestPixelValue;
	
	private int maximumPixelValue;
	private int minimumPixelValue;
	
	@Override
	public void run(ImageProcessor ip) {
		initializeImageInformation(ip);
		
		maximumPixelValue = 255;
		minimumPixelValue = 0;
		
		automaticContrastAdaption(ip);
		
	}

	private void automaticContrastAdaption(ImageProcessor ip) {
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int oldPixelValue = ip.getPixel(w, h);
				int newPixelValue = minimumPixelValue + 
						(oldPixelValue - lowestPixelValue) * (maximumPixelValue - minimumPixelValue) / (highestPixelValue - lowestPixelValue);
				ip.putPixel(w, h, newPixelValue);
			}
		}
	}

	private void initializeImageInformation(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();
		histogram = ip.getHistogram();
		
		extractLowestPixelValueFromHistogram();
		extractHighestPixelValueFromHistogram();
	}

	private void extractHighestPixelValueFromHistogram() {
		highestPixelValue = 255;
		while(histogram[highestPixelValue] == 0)
			highestPixelValue--;
	}

	private void extractLowestPixelValueFromHistogram() {
		lowestPixelValue = 0;
		while(histogram[lowestPixelValue] == 0)
			lowestPixelValue++;
	}

	@Override
	public int setup(String arg0, ImagePlus img) {
		return DOES_8G;
	}

}
