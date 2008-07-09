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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JLabel
 */
public class Label extends Component {
    
	private static String extension = "...";
    
	private Font font;
	
	private String string;
	private Image icon;
	protected int padding=2;	
	protected int gap=2;
	
        protected int verticalAlignment;
        protected int horizontalAlignment;

        protected int verticalTextPosition;
        protected int horizontalTextPosition;
	
        /**
         * @see javax.swing.JLabel#JLabel() JLabel.JLabel
         */
        public Label() {
            this((String)null);
        }
        
        /**
         * @param text The text to be displayed by the label
         * @see javax.swing.JLabel#JLabel(java.lang.String) JLabel.JLabel
         */
	public Label(String text) {
		
		this(text,null);
		
	}
	
        /**
         * @param icon The image to be displayed by the label
         * @see javax.swing.JLabel#JLabel(javax.swing.Icon) JLabel.JLabel
         */
	public Label(Image icon) {
		
		this(null,null,icon);
		
	}
        
        /**
         * @param text The text to be displayed by the label
         * @param icon The image to be displayed by the label
         * @see javax.swing.JLabel#JLabel(java.lang.String, javax.swing.Icon, int) JLabel.JLabel
         */
        public Label(String text,Image icon) {
		
		this(text,DesktopPane.getDefaultTheme().font,icon);
		
	}
        
	public Label(String text,Font f,Image icon) {
		
		setFont(f);
		this.icon = icon;
		selectable = false;
		if (text!=null) { setText(text); }
		//else { workoutSize(); }
		foreground = DesktopPane.getDefaultTheme().foreground;
                
                verticalAlignment = Graphics.VCENTER;
                horizontalAlignment = (font==null)?Graphics.HCENTER:Graphics.LEFT;

                verticalTextPosition = Graphics.VCENTER;
                horizontalTextPosition = Graphics.RIGHT;
                
	}

    public void setPadding(int padding) {
        this.padding = padding;
    }

    /**
     * @return The padding of the label
     * @see javax.swing.JComponent#getInsets() JComponent.getInsets
     */
    public int getPadding() {
        return padding;
    }

	public void doLayout() {
		
		int w = getCombinedWidth();
		int h = getCombinedHeight();

		if (w==0 && h==0) {
			width=0;
			height=0;
		}
		else {
			
			width = w + (padding*2);
			height = h + (padding*2);
		}

	}
	
	public void paintComponent(Graphics g) {

		int x=padding;
		int y=padding;
		
		int combinedwidth = getCombinedWidth();
		int combinedheight = getCombinedHeight();
		
		if (horizontalAlignment == Graphics.HCENTER) {	
			x = (width - combinedwidth)/2;
		}
		else if (horizontalAlignment == Graphics.RIGHT) {	
			x = (width - combinedwidth) -padding;
		}

		if (verticalAlignment == Graphics.VCENTER) {	
			y = (height - combinedheight)/2;
		}
		else if (verticalAlignment == Graphics.BOTTOM) {	
			y = (height - combinedheight) -padding;
		}
		
		
		if (icon!=null) {

			int ix=x;
			int iy=y;
			
			if (horizontalTextPosition == Graphics.HCENTER) {
				
				ix = x + (combinedwidth - icon.getWidth())/2;
				
			}
			else if (horizontalTextPosition == Graphics.LEFT && font!=null) {
				
				ix = x + font.getWidth(string)+gap;
				
			}

			if (verticalTextPosition == Graphics.VCENTER) {
				
				iy = y + (combinedheight - icon.getHeight())/2;
				
			}
			else if (verticalTextPosition == Graphics.TOP && font!=null) {
				
				iy = y + font.getHeight()+gap;
				
			}
			
			g.drawImage(icon, ix, iy , Graphics.TOP | Graphics.LEFT );
		}
		
		if (font!=null && string!=null) {
			
			int tx = x;
			int ty = y;
			
			if (horizontalTextPosition == Graphics.HCENTER) {
				
				tx = x + (combinedwidth - font.getWidth(string))/2;
				
			}
			else if (horizontalTextPosition == Graphics.RIGHT && icon!=null) {
				
				tx = x + icon.getWidth()+gap;
				
			}

			if (verticalTextPosition == Graphics.VCENTER) {
				
				ty = y + (combinedheight - font.getHeight())/2;
				
			}
			else if (verticalTextPosition == Graphics.BOTTOM && icon!=null) {
				
				ty = y + icon.getHeight()+gap;
				
			}
			
			
			g.setColor(foreground);
			font.drawString(g, string, tx,ty, Graphics.TOP | Graphics.LEFT );
		}

	}

	protected int getCombinedWidth() {
            return getCombinedWidth(string,icon);
	}
        
        protected int getCombinedWidth(String string,Image icon) {
            	int fw = (font!=null&&string!=null)?font.getWidth(string):0;
		if (horizontalTextPosition == Graphics.HCENTER) {
			if (icon == null && font == null) return 0;
			if (icon != null) {
				return (icon.getWidth() > fw)?icon.getWidth():fw;
			}
			return fw;
		}
		else {
			if (icon != null) { fw=    ((fw==0)?0:gap)     +fw+icon.getWidth(); }
			return fw;
		}
        }
	
        protected int getCombinedHeight() {
            return getCombinedHeight(icon);
        }
        
	protected int getCombinedHeight(Image icon) {
		int fw = (font!=null)?font.getHeight():0;
		if (verticalTextPosition == Graphics.VCENTER) {
			if (icon == null && font == null) return 0;
			if (icon != null) {
				return (icon.getHeight() > fw)?icon.getHeight():fw;
			}
			return fw;
		}
		else {
			if (icon != null) { fw=    ((fw==0)?0:gap)     +fw+icon.getHeight(); }
			return fw;
		}
	}
	
        /**
         * @param a The text of the label
         * @see javax.swing.JLabel#setText(java.lang.String) JLabel.setText
         */
	public void setText(String a) {
		
		string = a;
		
	}
        
        /**
         * @return The text of the label
         * @see javax.swing.JLabel#getText() JLabel.getText
         */
        public String getText() {
		
		return string;
		
	}
        
        /**
         * @return The font of the label
         * @see java.awt.Component#getFont() Component.getFont
         */
	public Font getFont() {
		return font;
	}

        /**
         * @param font The font of the label
         * @see javax.swing.JComponent#setFont(java.awt.Font) JComponent.setFont
         */
	public void setFont(Font font) {
		this.font = font;
		if (font!=null) {
			setForegroundByFontColorIndex(0);
		}
	}

        /**
         * @return The icon of the label
         * @see javax.swing.JLabel#getIcon() JLabel.getIcon
         */
	public Image getIcon() {
		return icon;
	}

        /**
         * @param icon The icon of the label
         * @see javax.swing.JLabel#setIcon(javax.swing.Icon) JLabel.setIcon
         */
	public void setIcon(Image icon) {
		this.icon = icon;
	}

	/**
         * @param a One of the following constants defined in Graphics: LEFT, HCENTER, RIGHT (the default)
         * @see javax.swing.JLabel#setHorizontalTextPosition(int) JLabel.setHorizontalTextPosition
         */
	public void setHorizontalTextPosition(int a) {
		horizontalTextPosition = a;
	}
	
        /**
         * @param a One of the following constants defined in Graphics: TOP, VCENTER (the default), or BOTTOM
         * @see javax.swing.JLabel#setVerticalTextPosition(int) JLabel.setVerticalTextPosition
         */
	public void setVerticalTextPosition(int a) {
		verticalTextPosition = a;
	}
	
        /**
         * @param alignment One of the following constants defined in Graphics: LEFT (the default for text-only labels), HCENTER (the default for image-only labels), RIGHT
         * @see javax.swing.JLabel#setHorizontalAlignment(int) JLabel.setHorizontalAlignment
         */
        public void setHorizontalAlignment(int alignment) {
            horizontalAlignment = alignment;
        }

        /**
         * @param alignment One of the following constants defined in Graphics: TOP, VCENTER (the default), or BOTTOM
         * @see javax.swing.JLabel#setVerticalAlignment(int) JLabel.setVerticalAlignment
         */
        public void setVerticalAlignment(int alignment) {
            verticalAlignment = alignment;
        }

        /**
         * @param iconTextGap The gap, default is 2
         * @see javax.swing.JLabel#setIconTextGap(int) JLabel.setIconTextGap
         */
        public void setIconTextGap(int iconTextGap) {
            gap = iconTextGap;
        }
        

        public void setValue(Object obj) {
            
            String drawString = String.valueOf(obj);
            Image image = (obj instanceof Option)?((Option)obj).getIcon():null;
            
            if (drawString!=null) {
                int a = getCombinedWidth(drawString, image);
                int w = getMaxTextWidth();

                if (a > w) {
                    int maxCharacters = Math.min((w/getFont().getWidth('0')),drawString.length());
                    drawString = drawString.substring(0, maxCharacters) + extension;
                }
            }

            string = drawString; // dont want to recalc everything twice
            setIcon(image);
        }
        public int getMaxTextWidth() {
            
            // if the width has not beed set yet
            // we will assume as can take the default amount

            return DesktopPane.getDefaultTheme().defaultWidth;

        }
        /**
         * @deprecated 
         */
	public void setForegroundByFontColorIndex(int a) {
		
		if (a < font.getColors().length ) {
			
			foreground = font.getColors()[a];
		}
		
	}

}
