package net.yura.blackberry.rim;

import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Ui;

public final class Font {

	public static final int STYLE_PLAIN = javax.microedition.lcdui.Font.STYLE_PLAIN;
	public static final int STYLE_BOLD = javax.microedition.lcdui.Font.STYLE_BOLD;
	public static final int STYLE_ITALIC = javax.microedition.lcdui.Font.STYLE_ITALIC;
	public static final int STYLE_UNDERLINED = javax.microedition.lcdui.Font.STYLE_UNDERLINED;

	public static final int SIZE_SMALL = javax.microedition.lcdui.Font.SIZE_SMALL;
	public static final int SIZE_MEDIUM = javax.microedition.lcdui.Font.SIZE_MEDIUM;
	public static final int SIZE_LARGE = javax.microedition.lcdui.Font.SIZE_LARGE;

	public static final int FACE_SYSTEM = javax.microedition.lcdui.Font.FACE_SYSTEM;
	public static final int FACE_MONOSPACE = javax.microedition.lcdui.Font.FACE_MONOSPACE;
	public static final int FACE_PROPORTIONAL = javax.microedition.lcdui.Font.FACE_PROPORTIONAL;

	public static final int FONT_STATIC_TEXT = javax.microedition.lcdui.Font.FONT_STATIC_TEXT;
	public static final int FONT_INPUT_TEXT = javax.microedition.lcdui.Font.FONT_INPUT_TEXT;
	
	//following variables are implicitely defined by getter- or setter-methods:
	private static Font defaultFont;
	private final int style;
	private final int size;
	private final int face;
	private final int height;
	private final int baselinePosition;
	
	protected net.rim.device.api.ui.Font font;

	private boolean isItalic;

	private Font( int face, int style, int size ) throws ClassNotFoundException {
		this.face = face;
		this.style = style;
		this.size = size;
		this.isItalic = (style & STYLE_ITALIC) == STYLE_ITALIC;
		//if !building.theme
			if ( face == FACE_SYSTEM && style == STYLE_PLAIN && size == SIZE_MEDIUM) {
				this.font = net.rim.device.api.ui.Font.getDefault();
			} else {
				//if polish.blackberry.font.family:defined
				//= FontFamily family = FontFamily.forName( "${polish.blackberry.font.family}" );
				//else
				FontFamily family = FontFamily.forName( FontFamily.FAMILY_SYSTEM );
				//endif
				
				
				int bbStyle = 0;
				if ( (style & STYLE_BOLD) == STYLE_BOLD  ) {
					bbStyle |= net.rim.device.api.ui.Font.BOLD;
				}
				if ( (style & STYLE_ITALIC) == STYLE_ITALIC  ) {
					bbStyle |= net.rim.device.api.ui.Font.ITALIC;
				}
				if ( (style & STYLE_UNDERLINED) == STYLE_UNDERLINED  ) {
					bbStyle |= net.rim.device.api.ui.Font.UNDERLINED;
				}
				int bbSize;
				int defaultSize = net.rim.device.api.ui.Font.getDefault().getHeight();
				if (size == SIZE_SMALL ) {
					bbSize = defaultSize - ((defaultSize * 30) / 100);
				} else if (size == SIZE_MEDIUM) {
					bbSize = defaultSize; 
				} else {
					bbSize = defaultSize + ((defaultSize * 30) / 100);
				}
				this.font = family.getFont( bbStyle, bbSize, Ui.UNITS_px  );
				if (this.font == null) {
					//#debug
					System.out.println("Unable to retrieve font...");
					this.font = net.rim.device.api.ui.Font.getDefault();
				}
			}
			if (this.font != null) {
				this.height = this.font.getHeight();
				this.baselinePosition = this.font.getBaseline();
			} else {
				this.height = 12;
				this.baselinePosition = 10;
			}
		//else
			// this.height = 0;
			// this.baselinePosition = 0;
		//endif
	}
	
	/**
	 * Gets the <code>Font</code> used by the high level user interface
	 * for the <code>fontSpecifier</code> passed in. It should be used
	 * by subclasses of
	 * <code>CustomItem</code> and <code>Canvas</code> to match user
	 * interface on the device.
	 * 
	 * @param fontSpecifier - one of FONT_INPUT_TEXT, or FONT_STATIC_TEXT
	 * @return font that corresponds to the passed in font specifier
	 * @throws IllegalArgumentException - if fontSpecifier is not a valid fontSpecifier
	 * @since  MIDP 2.0
	 */
	public static Font getFont(int fontSpecifier)
	{
		//TODO implement getFont( int fontSpecifier )
		return getDefaultFont();
	}

	/**
	 * Gets the default font of the system.
	 * 
	 * @return the default font
	 */
	public static Font getDefaultFont()
	{
		if (defaultFont == null) {
			try {
				defaultFont = new Font( FACE_SYSTEM, STYLE_PLAIN, SIZE_MEDIUM );
			} catch (ClassNotFoundException e) {
				//#debug error
				System.out.println("Unable to create default font" + e);
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to create default font" + e);
			}
		}
		return defaultFont;
	}

	/**
	 * Obtains an object representing a font having the specified face, style,
	 * and size. If a matching font does not exist, the system will
	 * attempt to provide the closest match. This method <em>always</em>
	 * returns
	 * a valid font object, even if it is not a close match to the request.
	 * 
	 * @param face - one of FACE_SYSTEM, FACE_MONOSPACE, or FACE_PROPORTIONAL
	 * @param style - STYLE_PLAIN, or a combination of STYLE_BOLD, STYLE_ITALIC, and STYLE_UNDERLINED
	 * @param size - one of SIZE_SMALL, SIZE_MEDIUM, or SIZE_LARGE
	 * @return instance the nearest font found
	 * @throws IllegalArgumentException - if face, style, or size are not legal values
	 */
	public static Font getFont(int face, int style, int size)
	{
		try {
			return new Font( face, style, size );
		} catch (ClassNotFoundException e) {
			//#debug error
			System.out.println("Unable to create font" + e);
			return getDefaultFont();
		}
	}


	/**
	 * Gets the style of the font. The value is an <code>OR'ed</code>
	 * combination of
	 * <code>STYLE_BOLD</code>, <code>STYLE_ITALIC</code>, and
	 * <code>STYLE_UNDERLINED</code>; or the value is
	 * zero (<code>STYLE_PLAIN</code>).
	 * 
	 * @return style of the current font
	 * @see #isPlain()
	 * @see #isBold()
	 * @see #isItalic()
	 */
	public int getStyle()
	{
		return this.style;
	}

	/**
	 * Gets the size of the font.
	 * 
	 * @return one of SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE
	 */
	public int getSize()
	{
		return this.size;
	}

	/**
	 * Gets the face of the font.
	 * 
	 * @return one of FACE_SYSTEM, FACE_PROPORTIONAL, FACE_MONOSPACE
	 */
	public int getFace()
	{
		return this.face;
	}

	/**
	 * Returns <code>true</code> if the font is plain.
	 * 
	 * @return true if font is plain
	 * @see #getStyle()
	 */
	public boolean isPlain()
	{
		return (this.style == STYLE_PLAIN);
	}

	/**
	 * Returns <code>true</code> if the font is bold.
	 * 
	 * @return true if font is bold
	 * @see #getStyle()
	 */
	public boolean isBold()
	{
		return (this.style & STYLE_BOLD) == STYLE_BOLD ;
	}

	/**
	 * Returns <code>true</code> if the font is italic.
	 * 
	 * @return true if font is italic
	 * @see #getStyle()
	 */
	public boolean isItalic()
	{
		return this.isItalic;
	}

	/**
	 * Returns <code>true</code> if the font is underlined.
	 * 
	 * @return true if font is underlined
	 * @see #getStyle()
	 */
	public boolean isUnderlined()
	{
		return (this.style & STYLE_UNDERLINED) == STYLE_UNDERLINED;
	}

	/**
	 * Gets the standard height of a line of text in this font. This value
	 * includes sufficient spacing to ensure that lines of text painted this
	 * distance from anchor point to anchor point are spaced as intended by the
	 * font designer and the device. This extra space (leading) occurs below
	 * the text.
	 * 
	 * @return standard height of a line of text in this font (a  non-negative value)
	 */
	public int getHeight()
	{
		return this.height;
	}

	/**
	 * Gets the distance in pixels from the top of the text to the text's
	 * baseline.
	 * 
	 * @return the distance in pixels from the top of the text to the text's baseline
	 */
	public int getBaselinePosition()
	{
		return this.baselinePosition;
	}

	/**
	 * Gets the advance width of the specified character in this Font.
	 * The advance width is the horizontal distance that would be occupied if
	 * <code>ch</code> were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * <code>ch</code> necessary for proper positioning of subsequent text.
	 * 
	 * @param ch - the character to be measured
	 * @return the total advance width (a non-negative value)
	 */
	public int charWidth(char ch)
	{
		return this.font.getAdvance(ch);
	}

	/**
	 * Returns the advance width of the characters in <code>ch</code>,
	 * starting at the specified offset and for the specified number of
	 * characters (length).
	 * The advance width is the horizontal distance that would be occupied if
	 * the characters were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * the characters necessary for proper positioning of subsequent text.
	 * 
	 * <p>The <code>offset</code> and <code>length</code> parameters must
	 * specify a valid range of characters
	 * within the character array <code>ch</code>. The <code>offset</code>
	 * parameter must be within the
	 * range <code>[0..(ch.length)]</code>, inclusive.
	 * The <code>length</code> parameter must be a non-negative
	 * integer such that <code>(offset + length) &lt;= ch.length</code>.</p>
	 * 
	 * @param ch - the array of characters
	 * @param offset - the index of the first character to measure
	 * @param length - the number of characters to measure
	 * @return the width of the character range
	 * @throws ArrayIndexOutOfBoundsException - if offset and length specify an invalid range
	 * @throws NullPointerException - if ch is null
	 */
	public int charsWidth(char[] ch, int offset, int length)
	{
		return this.font.getAdvance( ch, offset, length );
	}

	/**
	 * Gets the total advance width for showing the specified
	 * <code>String</code>
	 * in this <code>Font</code>.
	 * The advance width is the horizontal distance that would be occupied if
	 * <code>str</code> were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * <code>str</code> necessary for proper positioning of subsequent text.
	 * 
	 * @param str - the String to be measured
	 * @return the total advance width
	 * @throws NullPointerException - if str is null
	 */
	public int stringWidth( String str)
	{
		int width = this.font.getAdvance( str );
		if (this.isItalic) {
			width++;
		}
		return width;
	}

	/**
	 * Gets the total advance width for showing the specified substring in this
	 * <code>Font</code>.
	 * The advance width is the horizontal distance that would be occupied if
	 * the substring were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * the substring necessary for proper positioning of subsequent text.
	 * 
	 * <p>
	 * The <code>offset</code> and <code>len</code> parameters must
	 * specify a valid range of characters
	 * within <code>str</code>. The <code>offset</code> parameter must
	 * be within the
	 * range <code>[0..(str.length())]</code>, inclusive.
	 * The <code>len</code> parameter must be a non-negative
	 * integer such that <code>(offset + len) &lt;= str.length()</code>.
	 * </p>
	 * 
	 * @param str - the String to be measured
	 * @param offset - zero-based index of first character in the substring
	 * @param len - length of the substring
	 * @return the total advance width
	 * @throws StringIndexOutOfBoundsException - if offset and length specify an invalid range
	 * @throws NullPointerException - if str is null
	 */
	public int substringWidth( String str, int offset, int len)
	{
		return this.font.getAdvance( str.substring( offset, offset + len ) );
	}

}

