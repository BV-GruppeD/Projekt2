package com.bv_gruppe_d.imagej;

import java.awt.Scrollbar;

import ij.gui.GenericDialog;

public class ContrastAdaptionUserDialog {
	private final int BLACK = 0;
	private final int WHITE = 255;
	
	private GenericDialog dialog;
	
	public ContrastAdaptionUserDialog() {
		dialog = new GenericDialog("Kontrastanpassung");
		dialog.addSlider("Minimaler Pixelwert", BLACK, WHITE, BLACK);
		dialog.addSlider("Maximaler Pixelwert", BLACK, WHITE, WHITE);
		dialog.addSlider("Sättigung in %", 0, 49, 1);
	}
	
	public void showDialog() {
		dialog.showDialog();
	}
	
	public int getMinimumPixelValue() {
		return ((Scrollbar)dialog.getSliders().elementAt(0)).getValue();
	}
	
	public int getMaximumPixelValue() {
		return ((Scrollbar)dialog.getSliders().elementAt(1)).getValue();
	}
	
	public double getSaturationValue() {
		return ((double)((Scrollbar)dialog.getSliders().elementAt(2)).getValue())/100;
	}
	
	public boolean wasCanceled() {
		return dialog.wasCanceled();
	}
}