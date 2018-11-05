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
		highestPixelValue = getHighestPixelValue(histogram);
		lowestPixelValue = getLowestPixelValue(histogram);
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
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int oldPixelValue = ip.getPixel(w, h);
				int newPixelValue = minimumPixelValue + 
						(oldPixelValue - lowestPixelValue) * (maximumPixelValue - minimumPixelValue) / (highestPixelValue - lowestPixelValue);
				ip.putPixel(w, h, newPixelValue);
			}
		}
	}
	
	private void modifiedContrastAdaption(ImageProcessor ip) {
		int modifiedHighestPixelValue = calculateHighestModifiedPixelValue();
		int modifiedLowestPixelValue = calculateLowestModifiedPixelValue();
		
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int oldPixelValue = ip.getPixel(w, h);
				int newPixelValue;
				
				if (oldPixelValue > modifiedLowestPixelValue && oldPixelValue < modifiedHighestPixelValue) {
					newPixelValue = minimumPixelValue + 
							(oldPixelValue - modifiedLowestPixelValue) * (maximumPixelValue - minimumPixelValue) / (modifiedHighestPixelValue - modifiedLowestPixelValue);
					ip.putPixel(w, h, newPixelValue);
				} else if(oldPixelValue <= modifiedLowestPixelValue) {
					newPixelValue = minimumPixelValue;
				}else {
					newPixelValue = maximumPixelValue;
				}
				
				ip.putPixel(w, h, newPixelValue);
			}
		}
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
