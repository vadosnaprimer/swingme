/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui.components;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.DesktopPane;

/**
 * a component thats like a read-only TextArea
 * @author Yura Mamyrin
 * @see javax.swing.JTextArea
 */
public class TextArea extends TextComponent {
	
	private int[] lines;
        private int widthUsed;
        
       	private int align;
        private int lineSpacing;

        public TextArea() {
            this("");
        }
        
	public TextArea(String text) {
            this(text,Graphics.LEFT);
	}

	/**
         * @param text the text content
	 * @param alignment Alignment of the text, should be one of the alignment from Font class
	 */
	public TextArea(String text, int alignment) {
            
                setConstraints(TextComponent.ANY);
                
		align = alignment;
                width = DesktopPane.getDesktopPane().getWidth() - DesktopPane.getDesktopPane().defaultWidthOffset;
                setText(text);
	}
	
        
        public int getAlignment() {
		return align;
	}

	public void setAlignment(int alignment) {
		align = alignment;
	}
        
        
	/**
	 * @param g The Graphics object
	 */
	public void paintComponent(Graphics g) {

                String text = getDisplayString();
            
		int y = 0;
		int x = 0;
		
		// Adjust the x position for horizontal alignment.
		if((align & Graphics.HCENTER) != 0)
		{
			x = width/2;
		}
		else if((align & Graphics.RIGHT) != 0)
		{
			x = width;
		}

		// Set up starting position depending on alignment.
		if((align & Graphics.VCENTER) != 0)
		{
			y -= height >> 1;
		}
		else if((align & Graphics.BOTTOM) != 0)
		{
			y -= height;
		}

                g.setColor( isFocused()?activeTextColor:foreground );
                
                int i, startLine, endLine, lineHeight;
                
		// Set alignment to top, with correct left/center/right alignment.
		int alignment = Graphics.TOP | (align & (Graphics.LEFT | Graphics.HCENTER | Graphics.RIGHT));

		// Calculate which lines are vertically within the current clipping rectangle.
		lineHeight = font.getHeight() + lineSpacing;
		startLine = Math.max(0, (g.getClipY() - y) / lineHeight);
		endLine = Math.min(lines.length, startLine + (g.getClipHeight() / lineHeight) + 1);

		// Offset the starting position to skip the lines before startLine
		y += lineHeight * startLine;

		// Go through each line and render according to alignment and lineSpacing
               
                int beginIndex = (startLine==0)?0:lines[startLine-1];
		for(i = startLine; i < endLine; i++) {


                    int lastIndex = lines[i];
                    // as long as we r not right at the start of the string
                    if (lastIndex > 0) {
                        // dont paint the last char if its a space or (not the last line && hard return)
                        // the last line cant physically have a hard return on it!
                        char lastChar = text.charAt(lines[i]-1);
                        if (lastChar==' ' || ((i!=(lines.length-1)) && lastChar=='\n')) {
                            lastIndex = (lines[i]-1);
                        }
                    }

                    font.drawString(g, text.substring(beginIndex, lastIndex) , x, y, alignment);
                    y += lineHeight;
                    beginIndex = lines[i];
		}
                
		
	}

	/**
	 * Set's the line spacing
	 * @param lineSpacing spacing between lines
	 */	
	public void setLineSpacing(int lineSpacing) {
		this.lineSpacing = lineSpacing;
	}
	
	/**
	 * Set's the text
	 * @param txt The text
	 */
	public void setText(String txt) {
            super.setText(txt);
            setupHeight(getLines(txt,font,0,width),width);
	}
	
        /**
         * @param a The text to append
         * @see javax.swing.JTextArea#append(java.lang.String) JTextArea.append
         */
	public void append(String a) {
	
            String newtext = getText() + a;
            
            if (lines==null || lines.length<=1) {
                // we dont have enough text in the box to know where to append yet
                setText(newtext);
            }
            else {
	
                int[] l2 = getLines(newtext,font,lines[lines.length-2],width);

                // just copy the 1st array and the 2nd array into the new array
                // 1 value will be lost from the end of the old array
                int[] l3 = new int[ lines.length -1 + l2.length];
                for (int c=0;c<l3.length;c++) {
                    if (c<lines.length-1) {
                        l3[c] = lines[c];
                    }
                    else {
                        l3[c] = l2[ c - lines.length+1 ];
                    }
                }
                
                // set the text and adjust the height
                super.setText(newtext);
                setupHeight(l3,width);

            }
            

	}


	

        public static int[] getLines(String str,Font f,int startPos,int w) {

		final Vector parts = new Vector();

                int wordStart=startPos;
                int lineStart=startPos;
		int wordEnd;
                int lineEnd;
                // go though word by word
		while (true) {
                    
                        wordEnd = str.indexOf(' ',wordStart);
                        lineEnd = str.indexOf('\n',wordStart);
                        
                        // work out the end position
                        int end;
                        if (wordEnd==-1 && lineEnd==-1) {
                            end = str.length();
                        }
                        else if (wordEnd==-1) {
                            end = lineEnd;
                        }
                        else if (lineEnd==-1) {
                            end = wordEnd;
                        }
                        else if (wordEnd < lineEnd) {
                            end = wordEnd;
                        }
                        else {
                            end = lineEnd;
                        }

                        int currentLineLength = f.getWidth(str.substring(lineStart, end));
                        
                        if (currentLineLength > w && lineStart==wordStart) {
                               // start to remove 1 char at a time,
                               // and checking if we can fit the string into the width
                               boolean ok=false;
                               for (int c=(end-1);c>lineStart;c--) {
                                   currentLineLength = f.getWidth(str.substring(lineStart, c));

                                   if (currentLineLength<=w) {
                                        parts.addElement(new Integer(c));
                                        wordStart = c;
                                        lineStart = c;
                                        ok = true;
                                        break;
                                   }
                               }
                               if (!ok) {
                                   // this is bad, this means the width of 1 letter is still too wide
                                   // so we must add this letter anyway
                                   parts.addElement(new Integer(lineStart+1));
                                   wordStart = lineStart+1;
                                   lineStart = lineStart+1;
                               }
                        }
                        else if (currentLineLength > w) {
                             // here wordStart is the start of the OLD line
                             // e.g. "Bob the builder"
                             // if builder goes over the width, then the wordStart is on the letter 'b'
                             // so next line we beggin will start with the letter 'b'
                             parts.addElement(new Integer(wordStart));
                             lineStart = wordStart;

                        }
                        else if (wordEnd==-1 && lineEnd==-1) { // (end == str.length()) if we are at the end
                            parts.addElement(new Integer(end));
                            break;
                        }
                        else if (end == lineEnd) { // if we find a end of line
                            parts.addElement(new Integer(end+1));
                            wordStart = end+1;
                            lineStart = end+1;
                        }
                        else { // if (end == wordEnd) // we are safe to add this new word to the line
                            wordStart = end+1;
                        }

		}

                int[] array = new int[parts.size()];
                for (int c=0;c<array.length;c++) {
                    array[c] = ((Integer)parts.elementAt(c)).intValue();
                }
                return array;
        }

    public void workoutSize() {
        // TODO, add preferred width option
        width = DesktopPane.getDesktopPane().getWidth() - DesktopPane.getDesktopPane().defaultWidthOffset;
        if (width!=widthUsed) {
            setupHeight(getLines(getText(),font,0,width),width);
        }
    }
    
    public void setSize(int w,int h) {
        super.setSize(w, h);
        if (width!=widthUsed) {
             setupHeight(getLines(getText(),font,0,width),width);
        }
    }
    
    private void setupHeight(int[] l,int w) {
        lines = l;
        widthUsed = w;
        int oldh = height;
        height = (lines.length * font.getHeight()) + ((lines.length - 1) * lineSpacing);
        
        // this is kind of a hack
        // it asks the parent to relayout if my height has changed
        // but what if its not in a state to be ready to relayout?
        // what if its not visible?
        // what if it is streached/shrunk and ITS parent needs to be relayed out too?
        if (oldh!=height && parent!=null && owner!=null) {
            // so the scroll parent can strech my size
            parent.doLayout();
            parent.repaint();
        }
    }

    public String getName() {
        return "TextArea";
    }
	
}