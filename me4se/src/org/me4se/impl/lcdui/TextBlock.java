/* Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany */
package org.me4se.impl.lcdui;

import java.awt.Graphics;

/** A class supporting a formatted block of text. */

public class TextBlock {

    PhysicalFont font;
    int width;
    String txt;
    StringBuffer positions = new StringBuffer();
    
    /**
     * Initializes the WordWrap object with the given Font, the text string to
     * be wrapped, and the target width.
     * 
     * @param font:
     *            The Font to be used to calculate the character widths.
     * @param txt:
     *            The text string to be wrapped.
     * @param width:
     *            The line width.
     */
    public TextBlock(PhysicalFont font, String txt, int width) {
        this.font = font;
        this.txt = txt == null ? "" : txt;
        this.width = width;
  
        int pos = 0;
        int len = this.txt.length();

        while(pos < len){

        	int start = pos;
        	int i = pos;
        	
        	while (true) {
        		while (i < len && txt.charAt(i) > ' '){
        			i++;
        		}
        		
        		int w = font.stringWidth(txt.substring(start, i));
        		if (pos == start) {
        			if (w > width) {
        				while (font.stringWidth(txt.substring(start, --i)) > width) {
        				}
        				pos = i;
        				break;
        			}
        		}
        		if (w <= width){
        			pos = i;
        		}
        		if (w > width || i >= len || txt.charAt(i) == '\n' || txt.charAt(i) == '\r'){
        			break;
        		}
        		i++;
        	}
        	
        	positions.append((char) (pos >= len ? len : ++pos));
        }
    }


	public int getHeight() {
		return positions.length() * font.height;
	}
	
	
	public void paint(Graphics g, int x, int y){
		
/*          switch(align){
          case 0: align = Graphics.LEFT; break;
          case Graphics.HCENTER: x += width / 2; break;
          case Graphics.RIGHT: x += width; break;
          } */
          
          int count = positions.length();
                    
          int pos = 0;            
          for(int i = 0; i < count; i++){
        	  int cut = positions.charAt(i);
              font.drawString(g,
            		  txt.substring(
            		          pos, 
            		          cut > 0 && txt.charAt(cut-1) <= ' ' ? cut-1 : cut), 
            		  x, 
            		  y+font.ascent); 
            		//  align|Graphics.TOP);
              pos = cut;
              y += font.height;
          }	
	}
	
	
}