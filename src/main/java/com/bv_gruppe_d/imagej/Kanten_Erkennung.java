package com.bv_gruppe_d.imagej;


import java.util.Arrays;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Kanten_Erkennung implements PlugInFilter {
	
	public void run(ImageProcessor ip) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		int[][] pixel_array = new int[width][height];		
		int Hist[] = ip.getHistogram();
		double g = IJ.getNumber("Obere und untere Grenze in %", 1);
		int p = (int)((int)(width*height)*(g/100));
		
		//Abspeichern des Bildes in einem Array
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int pixel = ip.getPixel(w, h);
				pixel_array[w][h] = pixel;
			}
		}
		// Bestimmen des minimalen und Maximalen Farbwertes
		int minmax[] = minandmax(pixel_array);
		// Bestimmen der oberen und unten Grenze
		int uplowlimit[] = upandlowlimit(Hist,p);
		
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				if (pixel_array[w][h] >= uplowlimit[1]) {
					ip.putPixel(w, h, 0);
				}
				else if (pixel_array[w][h] <= uplowlimit[0]) {
					ip.putPixel(w, h, 255);
				}
				else{
					ip.putPixel(w, h, (pixel_array[w][h]-uplowlimit[0])*255/(uplowlimit[1]-uplowlimit[0]));
				}
			}
		}
	}

	@Override
	public int setup(String arg0, ImagePlus img) {
		return DOES_8G;
	}
	
	public static int[] minandmax(int[][] numbers) {
        int maxValue = numbers[0][0];
        int minValue = numbers[0][0];
        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if (numbers[j][i] > maxValue) {
                    maxValue = numbers[j][i];
                }
                else if (numbers[j][i] < minValue) {
                	minValue = numbers[j][i];
                }
            }
        }
        int[] output = {minValue,maxValue};
        return output;
    }
	
	public static int[] upandlowlimit(int[] Histog, int limit) {
		int lowlimit = 0;
		int uplimit = 0;
        int Q = 0;
        for (int j = 0; j < 256; j++) {
        	Q = Q + Histog[j];
        	if (Q>limit) {
        		lowlimit = j;
        		Q = 0;
        		break;
        	}        
        }        
        for (int j = 0; j < 256; j++) {
        	Q = Q+Histog[255-j];
        	if (Q>limit) {
        		uplimit = (255-j);
        		break;
        	}        
        }            
        int[] output = {lowlimit,uplimit};
        return output;
    }
	
}
