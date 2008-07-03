package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.RootPane;
import net.yura.mobile.util.Option;


public class Label extends Component {
    
	private static String extension = "...";
    
	private Font font;
	
	private String string;
	private Image icon;
	protected int padding=2;	
	protected int gap=2;
	
        protected int verticalAlignment = Graphics.VCENTER;
        protected int horizontalAlignment = Graphics.LEFT;

        protected int verticalTextPosition = Graphics.VCENTER;
        protected int horizontalTextPosition = Graphics.RIGHT;
	
        public Label() {
            this((String)null);
        }
        
	public Label(String text) {
		
		this(text,RootPane.getDefaultStyle().font);
		
	}
	
        public Label(String text,Image icon) {
		
		this(text,RootPane.getDefaultStyle().font,icon);
		
	}
        
	public Label(String text,Font f) {
		
		this(text,f,null);
		
	}
	
	public Label(Image icon) {
		
		this(null,null,icon);
		
	}
	
	public Label(String text,Font f,Image icon) {
		
		setFont(f);
		this.icon = icon;
		selectable = false;
		if (text!=null) { setText(text); }
		//else { workoutSize(); }
		foreground = RootPane.getDefaultStyle().foreground;
	}

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getPadding() {
        return padding;
    }
	
	public void setText(String a) {
		
		string = a;
		//workoutSize();
		
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
	
	public String getText() {
		
		return string;
		
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
	
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
		if (font!=null) {
			setForegroundByFontColorIndex(0);
		}
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
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

            return RootPane.getDefaultStyle().defaultWidth;

        }
        
	public void setForegroundByFontColorIndex(int a) {
		
		if (a < font.getColors().length ) {
			
			foreground = font.getColors()[a];
		}
		
	}
	
	public void setHorizontalTextPosition(int a) {
		horizontalTextPosition = a;
	}
	
	public void setVerticalTextPosition(int a) {
		verticalTextPosition = a;
	}
	

    public void setHorizontalAlignment(int alignment) {
        horizontalAlignment = alignment;
    }
    
    public void setVerticalAlignment(int alignment) {
        verticalAlignment = alignment;
    }

    
}
