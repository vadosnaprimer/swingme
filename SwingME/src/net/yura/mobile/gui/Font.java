package net.yura.mobile.gui;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.util.Properties;
import net.yura.mobile.util.StringUtil;

public class Font {
	
	private int			height;
	private int			startX[];
	private int			startY[];
	private int			spaceWidth;
	private byte		characterWidth[];
	private Image		characterImage;
	private int			characterSpacing;
	
	private javax.microedition.lcdui.Font systemFont=null;
	private int	fontColor=0;

	private Hashtable imageTable;
	private int[] colors;
	
	private boolean numbermode;
	
	
	public Font() {
		
		setSystemFont( javax.microedition.lcdui.Font.getDefaultFont() );
		
		// not sure if this is needed
		colors = new int[2];
		colors[0]=0x00000000;
		colors[1]=0x000000FF;
	}
	
	public Font(String name) {

		try {
			
			Properties newfont = new Properties();
			
			newfont.load( getClass().getResourceAsStream(name) );
	
			
			String[] offsetsText = StringUtil.split(newfont.getProperty("offsets"), ',');
			byte[] offsetsint = new byte[offsetsText.length];
			
			for (int c=0;c<offsetsText.length;c++) {
				
				offsetsint[c] = Byte.parseByte(offsetsText[c]);
			}
			
			String[] colorsText = StringUtil.split(newfont.getProperty("colors"), ',');
			colors = new int[colorsText.length];
			
			imageTable = new Hashtable();
			
			for (int c=0;c<colorsText.length;c++) {
				
				String imageName = newfont.getProperty("image."+colorsText[c]);
				
				if (name.charAt(0)=='/' && imageName.charAt(0)!='/') {
					imageName = "/"+imageName;
				}
				
				Image fontimage = Image.createImage(imageName);
				
				colors[c] = Integer.parseInt(colorsText[c],16);
				
				imageTable.put( new Integer(colors[c]) , fontimage);
				
			}
			
			String numbermodeString = newfont.getProperty("numbermode");
			numbermode = "T".equals(numbermodeString);
			
			construct( (Image) imageTable.get( new Integer(colors[0]) ), offsetsint);
			
			setSpaceWidth( Integer.parseInt( newfont.getProperty("space") ) );
			setCharacterSpacing( Integer.parseInt( newfont.getProperty("spacing") ) );
			
		}
		catch (IOException ex) {
			
			ex.printStackTrace();
			throw new RuntimeException("unable to load font: "+name);
		}
		
		
	}

	private void construct(Image image, byte widths[]) {
            
		int i, x, y, cutoff, numCharacters;

		// Set the character data image.
		characterImage = image;

		// Set the widths array.
		characterWidth = widths;
		numCharacters = widths.length;

		// Calculate the start positions.
		startX = new int[numCharacters];
		startY = new int[numCharacters];

		x = y = 0;
		cutoff = characterImage.getWidth();

		for(i = 0; i < numCharacters; i++)
		{
			if((x + characterWidth[i]) > cutoff)
			{
				x = 0;
				y++;
			}

			startX[i] = x;	// x position in font image.
			startY[i] = y;	// y (row) in font image.

			x += characterWidth[i];
		}

		// Get the height
		height = characterImage.getHeight() / (y + 1);

		// Go back through and calculate the true Y positions.
		for(i = 0; i < numCharacters; i++)
		{
			startY[i] *= height;
		}

		// Set the default character spacing.
		characterSpacing = 1;

		
		if (numbermode) {
			// we don't need this
			startY = null;
		}
		else {
			// Set the default space width.
			// we don't really use this anyway as its set in the .font file
			spaceWidth = characterWidth['o' - 32];
		}
	}
	
	

	private void setColor(int a) {
		
		fontColor = a;
		
		if (imageTable!=null) {
		
			Image b = (Image) imageTable.get(new Integer(a));
			
			if (b!=null) {
				
				characterImage = b;
			}
			else {
				
				if (DesktopPane.debugMode) {
					System.out.println("trying to set a font color to a unknown color: "+a);
				}
				setColor( colors[0] );

			}
		}
	}
	
	public int[] getColors() {
		
		return colors;
		
	}
	
	
	/**
	 * Set's the system font.
	 * @param systemFont - javax.microedition.lcdui.Font object
	 */
	public void setSystemFont(javax.microedition.lcdui.Font systemFont)
	{
		this.systemFont = systemFont;
		height = getHeight();
	}
	
	/**
	* Sets the distance in pixels between each character.
	* This is used when a string is rendered, between each adjacent pair of non-space characters a spacing gap is inserted.
	* This gap may be negative in order to overlap the character images. Be careful when changing this value as it affects
	* the width of strings and subsequently any alignment applied to them when rendered.
	* @param	spacing The size in pixels of the gap between non-space characters (can be negative if desired).
	*/
	public void	setCharacterSpacing(int spacing)
	{
		characterSpacing = spacing;
	}

	/**
	* Sets the size of a space character.
	* A space character is defined as any character with a width of zero. The default value for the space width in a newly
	* created font is the same width as that font's lower case 'o' character, so make sure this value is set for fonts
	* that do not contain a lowe case 'o'.
	* @param	width The width of the space character.
	*/
	public void	setSpaceWidth(int width)
	{
		spaceWidth = width;
	}

	/**
	* Gets the width of a single character using this font.
	* This will return the space width for any zero-width characters as these characters are treated as spaces.
	* @param	c The character to get the width of.
	* @return	The width of the requested character when rendered using this font.
	*/
	public int getWidth(char c) {
		
		if (numbermode) {
			
			if (c == '-') {
				
				return characterWidth[ POSITION_MINUS ];
			}
			else if (c == ':') {
				
				return characterWidth[ POSITION_TIME ];

			}
			else if (c == '.') {
				
				return characterWidth[ POSITION_DOT ];
			}
			else {
				
				// it must be a number then if its none of these!!
				return characterWidth[ Integer.parseInt( String.valueOf(c) ) ];

			}

		}
		else if(systemFont != null) {
			return systemFont.charWidth(c);
		}
		else {
		
			int width = characterWidth[ getCharPosition(c) ];
	
			if(width == 0)
			{
				return spaceWidth;
			}
	
			return width;
		}
	}

	private int getCharPosition(char ch) {
		
		// DONOT KNOW WHETHER ITS RIGHT WAY OF DOING OR THE 33 EMPTY '0' SHOULD BE ADDED TO
		// THE OFFSETS ARRAY IN THE CODE ITSELF
		// Unicode character
		if(ch >= 160)
		{
			ch -= 33;
		}
		
		int character = ch - 32;
		
		if (character>=characterWidth.length) {
			System.out.println("ArrayIndexOutOfBounds for char: "+ch);
			character=0;
		}
		
		// a = 65
		// z = 90
		if(characterWidth[character] == 0 && character >= 65 && character <= 90) {
			character = character - 32;
		}
		
		return character;
		
	}
	
	/**
	* Gets the height of the font.
	* The height of the font is defined by the character data image. It may not always be the height of the tallest
	* character in the font, but should be the difference between the top of the character that stretches the highest
	* above the baseline and the bottom of the one that reaches the lowest below it.
	* @return	The height of the font in pixels.
	*/
	public int getHeight()
	{
		if(systemFont != null)
		{
			return systemFont.getHeight();
		}

		return height;
	}

	/**
	* Gets the width in pixels of a string rendered using this font.
	* Be careful if you change the space width or the character spacing of the font as this will affect all string widths,
	* so if you need to store string widths, make sure you update those stored values after changing the display properties.
	* @return	The width of the string in pixels when rendered using this font.
	*/
	public int getWidth(String s) {

		if (numbermode) {
		
			// gives width with space either side
			
			int width=characterSpacing;
			
			if (s.indexOf(':') == -1 && characterWidth.length > POSITION_CURRENCY) {
				
				width += characterWidth[POSITION_CURRENCY] + characterSpacing;
				
			}
			
			for (int i = 0; i < s.length(); i++) {
	
				char ch = s.charAt(i);
				
				width += getWidth(ch) + characterSpacing;
				
			}
			return width;
		}
		else if(systemFont != null) {
			
				return systemFont.stringWidth(s);
				
		}
		else {
	
			int i, w, width = 0;
			char ch;
	
			// Go through each character and compoud it's width
			for(i = (s.length() - 1); i >= 0; i--)
			{
				ch = s.charAt(i);
				
				int character = getCharPosition(ch);
				
				// Sort of an inline implementation of getWidth(char c);
				if((w = characterWidth[character]) == 0)
				{
					// Increase by the width of a space character.
					width += spaceWidth;
				}
				else
				{
					// Increase by the character width plus the character spacing.
					width += w + characterSpacing;
				}
			}
	
			// Return the width, minus the spacing from the final character.
			return width - characterSpacing;
		}
	}
	
	/**
	 * Returns system font object, null if system font object is null and bitmap fonts being used.
	 * @return - javax.microedition.lcdui.Font or null if bitmap fonts used.
	 */
	public javax.microedition.lcdui.Font getFont()
	{
		return systemFont;
	}
	
	/**
	* Renders a string onto a graphics object.
	* This will draw the specified string onto the specified graphics object using this font at the requested position and alignment.
	* @param	g The graphics object to draw onto.
	* @param	s The string to draw.
	* @param	x The x position in the graphics object.
	* @param	y The y position in the graphics object.
	* @param	alignment The alignment to use. This can be one of TOP, MIDDLE, BOTTOM (vertical alignments), LEFT, CENTER, RIGHT
	*			(horizontal alignments) or one of the combined flags TOP_LEFT, TOP_CENTER, TOP_RIGHT, etc. The combined values are the
	*			same as bitwise or'ing together a vertical alignment value with a horizontal alignment value and are provided for
	*			convenience and readability of your code. The default vertical alignment is TOP and the default horizontal alignment is LEFT.
	*			The alignment will align the specified 'handle' of the string to the x, y position, so for example if you specify MIDDLE_RIGHT
	*			then the string will be rendered such that the x, y position is at the middle right of the string, i.e. the entire string will
	*			to the left of the point with half of the string above and half below the point.
	*/
	public int drawString(Graphics g, String s, int x, int y, int alignment) {
		
            setColor( g.getColor() );
            
		if (numbermode) {
			
			int x2 = x;
			
			if (s.indexOf(':') == -1 && characterWidth.length > POSITION_CURRENCY) {
				// Draw $
				x2 = drawDigit(g, x2, y, POSITION_CURRENCY);
			}
			
			if (s.indexOf('.') == -1 && s.indexOf(':') == -1) {
				
				int thenum = Integer.parseInt(s);
				
				x2 += renderNumber(g,x2,y,thenum,s.length());
				
			}
			else {
				
				char sep;
				int code;
				
				if (s.indexOf(':') == -1) {
					sep = '.';
					code = POSITION_DOT;
				}
				else {
					sep = ':';
					code = POSITION_TIME;
				}
				
				String firstnum = s.substring(0, s.indexOf(sep));
				String secondnum = s.substring(s.indexOf(sep)+1, s.length());
				
				int thenum1 = Integer.parseInt(firstnum);
				int thenum2 = Integer.parseInt(secondnum);
				
				x2 += renderNumber(g,x2,y,thenum1,firstnum.length());
				
				// Draw . or :
				x2 = drawDigit(g, x2, y, code);
				
				x2 += renderNumber(g,x2,y,thenum2,secondnum.length());
				
			}
			
			return x2 - x;
			
		}
		else if(systemFont != null) {
			
			javax.microedition.lcdui.Font f = g.getFont();
			
			g.setColor(fontColor);
			g.setFont(systemFont);
			g.drawString(s, x, y, alignment);			
			
			g.setFont(f);

			return getWidth(s);
		}
		else {
		
			
			int			i, width;
			int			length = s.length();
			int			character, cW;
	
			// Calculate the overall string width
			width = getWidth(s);
	
			// Adjust the x position for horizontal alignment.
			if((alignment & Graphics.HCENTER) != 0)
			{
				x -= width >> 1;
			}
			else if((alignment & Graphics.RIGHT) != 0)
			{
				x -= width;
			}
	
			// Adjust the y position for vertical alignment.
			if((alignment & Graphics.VCENTER) != 0)
			{
				y -= height >> 1;
			}
			else if((alignment & Graphics.BOTTOM) != 0)
			{
				y -= height - 1;
			}
			
			int	oldClipX = g.getClipX();
			int	oldClipY = g.getClipY();
			int	oldClipW = g.getClipWidth();
			int	oldClipH = g.getClipHeight();
			char ch;
	
			// Render each character of the string.
			for(i = 0; i < length; i++)
			{
				ch = s.charAt(i);
				
				character = getCharPosition(ch);
				
				if((cW = characterWidth[character]) == 0)
				{
					x += spaceWidth;
					
				}
				else
				{
					g.clipRect(x, y, cW, height);
	
					g.drawImage(characterImage, x - startX[character], y - startY[character], Graphics.TOP | Graphics.LEFT);
	
					x += cW + characterSpacing;
					g.setClip(oldClipX, oldClipY, oldClipW, oldClipH);
				}
			}
	
			g.setClip(oldClipX, oldClipY, oldClipW, oldClipH);
			return width;
		}
	}
	
	
	private static int POSITION_DOT = 10;
	private static int POSITION_CURRENCY = 11;
	private static int POSITION_MINUS = 12;
	private static int POSITION_TIME = 13;
	
	// ########################### NUMBER!!!!
	// 0-9 numbers
	// 10 .
	// 11 $
	// 12 -
	// 13 :
	
	private int renderNumber(Graphics g, int x, int y, int number, int digits) {
		
		int column, digit, sX = x;

		for(digit = 0, column = 1; digit < digits; digit++)
		{
			column *= 10;
		}

		if(number < 0)
		{
			number = -number;
			
			// Draw -
			x = drawDigit(g,x,y,POSITION_MINUS);
		}

		number %= column;

		while(column > 1)
		{
			column /= 10;
			digit = number / column;
			number -= digit * column;

			x = drawDigit(g,x,y,digit);
		}

		return x - sX - characterSpacing;
	}
	
	
	private int drawDigit(Graphics g,int x,int y, int a) {
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipW = g.getClipWidth();
		int clipH = g.getClipHeight();
		
		g.clipRect(x,y,characterWidth[a],characterImage.getHeight());
		g.drawImage(characterImage, x - startX[a], y, Graphics.TOP | Graphics.LEFT);
		x += characterWidth[a] + characterSpacing;	
		
		g.setClip(clipX, clipY, clipW, clipH);
		
		return x;
	}
	
}

