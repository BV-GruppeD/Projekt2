package com.bv_gruppe_d.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Kontrastanpassung_PlugIn implements PlugInFilter {
	
	private int width;
	private int height;
	private int[] histogram;
	
	private int maximumPixelValue;
	private int minimumPixelValue;
	private double saturation;
	
	@Override
	public void run(ImageProcessor ip) {
		
		ContrastAdaptionUserDialog dialog = new ContrastAdaptionUserDialog();
		dialog.showDialog();
		
		if(!dialog.wasCanceled()) {
			getUserPreferences(dialog);
			
			if (preferencesAreValid()) {
				initializeImageInformation(ip);
				executeContrastAdaption(ip);	
			} else {
				IJ.showMessage("Ungültige Eingabe der Pixelwerte.");
			}
		}
	}

	private void getUserPreferences(ContrastAdaptionUserDialog dialog) {
		minimumPixelValue = dialog.getMinimumPixelValue();
		maximumPixelValue = dialog.getMaximumPixelValue();
		saturation = dialog.getSaturationValue();		
	}

	private boolean preferencesAreValid() {
		return minimumPixelValue < maximumPixelValue;
	}
	
	private void initializeImageInformation(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();
		histogram = ip.getHistogram();
	}

	private void executeContrastAdaption(ImageProcessor ip) {
		if (usesNoSaturation())
			automaticContrastAdaption(ip);
		else
			modifiedContrastAdaption(ip);
	}

	private boolean usesNoSaturation() {
		return saturation == 0;
	}

	private void automaticContrastAdaption(ImageProcessor ip) {
		int highestPixelValue = getHighestPixelValue(histogram);
		int lowestPixelValue = getLowestPixelValue(histogram);
		
		calculateNewPixelValues(ip, highestPixelValue, lowestPixelValue);
	}
	
	private int getLowestPixelValue(int[] histogram) {
		int pixelValue = 0;
		while(histogram[pixelValue] == 0)
			pixelValue++;
		return pixelValue;
	}

	private int getHighestPixelValue(int[] histogram) {
		int pixelValue = 255;
		while(histogram[pixelValue] == 0)
			pixelValue--;
		return pixelValue;
	}
	
	private void calculateNewPixelValues(ImageProcessor ip, int highestPixelValue, int lowestPixelValue) {
		double scalingFactor = (double)(maximumPixelValue - minimumPixelValue) / (highestPixelValue - lowestPixelValue);
		
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int oldPixelValue = ip.getPixel(w, h);
				
				int newPixelValue = (int) (minimumPixelValue + (oldPixelValue - lowestPixelValue) * scalingFactor);
				newPixelValue = clampPixelValue(newPixelValue, 0, 255);
				
				ip.putPixel(w, h, newPixelValue);
			}
		}
	}
	
	private int clampPixelValue(int value, int minimumPixelValue, int maximumPixelValue) {
		return Math.min(Math.max(minimumPixelValue, value), maximumPixelValue);
	}

	private void modifiedContrastAdaption(ImageProcessor ip) {
		int modifiedHighestPixelValue = calculateHighestModifiedPixelValue();
		int modifiedLowestPixelValue = calculateLowestModifiedPixelValue();
		
		calculateNewPixelValues(ip, modifiedHighestPixelValue, modifiedLowestPixelValue);
	}
	
	private int calculateLowestModifiedPixelValue() {
		int modifiedPixelValue = 0;
		int border = (int) Math.ceil(saturation * height * width);
		int sum = 0;
		
		while(sum < border) {
			sum += histogram[modifiedPixelValue];
			modifiedPixelValue++;
		}
		return modifiedPixelValue;
	}

	private int calculateHighestModifiedPixelValue() {
		int modifiedPixelValue = 255;
		int border = (int) (saturation * height * width);
		int sum = 0;

		while(sum < border) {
			sum += histogram[modifiedPixelValue];
			modifiedPixelValue--;
		}
		return modifiedPixelValue;
	}

	@Override
	public int setup(String arg0, ImagePlus img) {
		return DOES_8G;
	}
}