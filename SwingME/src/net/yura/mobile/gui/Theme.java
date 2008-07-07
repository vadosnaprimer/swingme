package net.yura.mobile.gui;

public class Theme {
	
	public int scrollBarCol=0x00FFFFFF;
	public int scrollTrackCol=0x00000000;
	
	// Font object
	public Font font;

	// Items info
	public int background;
	public int foreground;
	
	public int itemBorderColor;
	public int itemActiveBorderColor;
	
	public int defaultWidth;
        public int defaultSpace;
	public int barHeight;
        
        public Theme(){
            this(null,0,0);
            // these will have defaults setup anyway
        }
        
	public Theme(Font font,int barHeight,int a) {
		
		this.font = font;

                this.barHeight = barHeight;
		defaultWidth = a;
		
		background = 0x00FFFFFF;
		foreground = 0;
		
		itemBorderColor = 0x00808080;
		itemActiveBorderColor = 0;

	}

}

