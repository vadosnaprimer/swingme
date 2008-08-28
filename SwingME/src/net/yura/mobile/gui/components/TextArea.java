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
 * what happens with sizes:
 * <ul>
 * <li>set some text</li>
 * <li>size is calculated right away (if the text is wraped then a defualt width is used)</li>
 * <li>if wrap is turned on later, the size is recalculated</li>
 * <li>when revalidate is called somewhere</li>
 * <li>(if its inside a Panel, then the default size is used, just like swing, this can sometimes look strange)</li>
 * <li>if its inside a ScrollPane, then workoutSize() is called from ScrollPane.doLayout() (the ScrollPane knows its correct size at this time)</li>
 * <li>if wrap is TRUE the width is set to the ViewPort width of the ScrollPane (taking into account that it already has the rightish size, so can find out if scrollbars are needed on the ScrollPane)</li>
 * <li>if the width or height is less then the ViewPort, the ScrollPane will streach the size</li>
 * </ul>
 * @author Yura Mamyrin
 * @see javax.swing.JTextArea
 */
public class TextArea extends TextComponent {
	
	private int[] lines;
        private int widthUsed;
        
       	private int align;
        private int lineSpacing;
	private boolean wrap;

        public TextArea() {
            this("");
        }
        
	public TextArea(String text) {
            this(text,Graphics.LEFT);
	}

	/**
	 * If text is set in the Constructor and the width is unknown, its ok as wrap is off be default!
         * @param text the text content
	 * @param alignment Alignment of the text, should be one of the alignment from Font class
	 */
	public TextArea(String text, int alignment) {
            super("TextArea",text, 1000, TextComponent.ANY);

            align = alignment;
            width = DesktopPane.getDesktopPane().getWidth() - DesktopPane.getDesktopPane().defaultWidthOffset;
	}
	
        
        public int getAlignment() {
		return align;
	}

	public void setAlignment(int alignment) {
		align = alignment;
	}

	/**
	 * @see javax.swing.JTextArea#setLineWrap(boolean) JTextArea.setLineWrap
	 */
        public void setLineWrap(boolean w) {

		wrap = w;

		// this component always needs it size to be correct
		// so that if its in a scrollPane it knows if it should have the scrollbars
		// and so then so we know the correct width to wrap text at
		workoutSize();

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
		endLine = Math.min(lines.length+1, startLine + (g.getClipHeight() / lineHeight) + 1);

		// Offset the starting position to skip the lines before startLine
		y += lineHeight * startLine;

		// Go through each line and render according to alignment and lineSpacing
               
                int beginIndex = (startLine==0)?0:lines[startLine-1];
		for(i = startLine; i < endLine; i++) {


                    int lastIndex = (i==lines.length)?text.length():lines[i];
                    // as long as we r not right at the start of the string
                    if (lastIndex > 0) {
                        // dont paint the last char if its a space or (not the last line && hard return)
                        // the last line cant physically have a hard return on it!
                        //char lastChar = text.charAt(lastIndex-1);
                        if ( (i!=lines.length) ) { // && (lastChar=='\n' || lastChar==' ')
                            lastIndex--;
                            // we NEED to show the last line ' '
                        }
                    }

                    String line = text.substring(beginIndex, lastIndex);
                    font.drawString(g, line , x, y, alignment);

                    if (showCaret && caretPosition >= beginIndex && caretPosition <= lastIndex) {

                        int w = font.getWidth( text.substring(beginIndex, caretPosition) );
                        
                        if((align & Graphics.HCENTER) != 0) {
                                w = (width - font.getWidth(line))/2 + w;
                        }
                        else if((align & Graphics.RIGHT) != 0) {
                                w = width - (font.getWidth(line)-w); // not best efficency
                        }
                        
                        g.drawLine(w, y, w, y+lineHeight);
                    }

                    // save this info and go round the loop again
                    if (i!=(endLine-1)) {
                        y += lineHeight;
                        beginIndex = lines[i];
                    }
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

	    int w = wrap?width:Integer.MAX_VALUE;

            setupHeight(getLines(txt,font,0,w),w,true);
	}
	
        /**
         * @param a The text to append
         * @see javax.swing.JTextArea#append(java.lang.String) JTextArea.append
         */
	public void append(String a) {
	
            String newtext = getText() + a;
            
            if (lines==null || lines.length==0) {
                // we dont have enough text in the box to know where to append yet
                setText(newtext);
            }
            else {

		int w = wrap?width:Integer.MAX_VALUE;

                int[] l2 = getLines(newtext,font,lines[lines.length-1],w);

                // just copy the 1st array and the 2nd array into the new array
                // 1 value will be lost from the end of the old array
                int[] l3 = new int[ lines.length + l2.length];

		System.arraycopy(lines, 0, l3, 0, lines.length);
		System.arraycopy(l2, 0, l3, lines.length, l2.length);

                // set the text and adjust the height
                super.setText(newtext);
                setupHeight(l3,w,true);

            }


	}

    private void setupHeight(int[] l,int w,boolean relayout) {
        lines = l;
        widthUsed = w;
        int oldh = height;
        height = ((lines.length+1) * font.getHeight()) + (lines.length * lineSpacing);


	// we have just changed out height
	// if we are in a scrollPane we should tell it, so it can adjust
	if (relayout && oldh!=height && parent!=null) {

            parent.doLayout();
            parent.repaint();

	}

/* overkill
        // this is kind of a hack
        if (relayout && oldh!=height && parent!=null && owner!=null) {
            // so the scroll parent can strech my size
            Panel p = parent;
            while (!(p instanceof ScrollPane)) {
                Panel pp = p.parent;
                if (pp==null) {
                    break;
                }
                else {
                    p=pp;
                }
            }
            p.doLayout();
            p.repaint();
        }
*/

    }


    public void workoutSize() {

	if (wrap) {

		// scrollpane will handel out size
		// we assume that the scrollPane size is already setup and correct
		// this saves lots of un-needed calls to getLines
		if (parent instanceof ScrollPane) {
			width = ((ScrollPane)parent).getViewPortWidth();
		}
		else {
			width = DesktopPane.getDesktopPane().getWidth() - DesktopPane.getDesktopPane().defaultWidthOffset;
		}
	}

	// ALWAYS setup the height in this method!
	int w = wrap?width:Integer.MAX_VALUE;
        setupHeight((w!=widthUsed)?getLines(getText(),font,0,w):lines,w,false);

	if (!wrap) {

		width=0;
		String text = getDisplayString();

		int beginIndex = 0;
		for(int i = 0; i <= lines.length; i++) {

                    int lastIndex = (i==lines.length)?text.length():lines[i];

                    w = font.getWidth( text.substring(beginIndex, (i!=lines.length)?lastIndex-1:lastIndex) );

		    if (width<w) {
			width = w;
		    }

                    beginIndex = lastIndex;
		}


	}

    }


    public String getName() {
        return "TextArea";
    }




	/**
	 * If w == Integer.MAX_VALUE, then it wont wrap on words
	 */
        public static int[] getLines(String str,Font f,int startPos,int w) {
            
//#debug
System.out.println("getLines start="+startPos +" w="+w+" stringLength="+str.length());
// this is here as this is quite a CPU intensive method

		final Vector parts = new Vector();

                int wordStart=startPos;
                int lineStart=startPos;
		int wordEnd;
                int lineEnd;
                // go though word by word
		while (true) {
                    
                        wordEnd = (w==Integer.MAX_VALUE)?-1:str.indexOf(' ',wordStart);
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

                        int currentLineLength = (w==Integer.MAX_VALUE)?-1:f.getWidth(str.substring(lineStart, end));
                        
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
                            //parts.addElement(new Integer(end));
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


}