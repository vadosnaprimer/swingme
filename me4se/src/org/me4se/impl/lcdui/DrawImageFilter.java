/*
 * Created on 16.11.2003
 *
 * 
 */
package org.me4se.impl.lcdui;

import java.awt.image.RGBImageFilter;

/**
 * Merges an rectangular area of ARGB data into an existing image 
 * source. Needed to support drawing on transparent mutable images 
 * (to some extent).
 */
public class DrawImageFilter extends RGBImageFilter{

	int[] data;	
	int x;
	int y;
	int w;
	int h;
	
	public DrawImageFilter(int[] data, int x, int y, int w, int h){
		this.data = data;
		if(data.length < w*h) throw new IllegalArgumentException("data.length "+data.length+ " < "+w+" * "+h);
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	/* (non-Javadoc)
	 * @see java.awt.image.RGBImageFilter#filterRGB(int, int, int)
	 */
	public int filterRGB(int fx, int fy, int argb) {
		fx -= x;
		fy -= y;
		
		if( fx < 0 || fy < 0 || fx >= w || fy >= h)
			return argb;
			
		int d = data[fx + w*fy];
		return (d < 0) // first bit set -> opaque
			? d 
			: argb;
	}


}
