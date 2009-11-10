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

package net.yura.mobile.gui;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.lang.Math;
import java.util.Random;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.util.ImageUtil;

public class Font {

        private Hashtable characters; // maps unicode chars to their index
	private int	height;
	private int	startX[];
	private int	startY[];
        private int     offsetX[];
        private int     offsetY[];
        private int     advance[];
	private int	spaceWidth;
	private byte	characterWidth[];
        private int     characterHeight[];
	private Image	characterImage;
	private int	characterSpacing;
        private Hashtable kerning = new Hashtable();

	private javax.microedition.lcdui.Font systemFont=null;

	private Hashtable imageTable;
	private int[] colors;
        private int color;

	private boolean numbermode;

	public Font() {
            this( javax.microedition.lcdui.Font.getDefaultFont() );
        }

	public Font(javax.microedition.lcdui.Font f) {

		setSystemFont( f );

		// not sure if this is needed
		colors = new int[2];
		colors[0]=0x00000000;
		colors[1]=0x000000FF;
	}

        /**
         * BMFont format
         */
        public Font(String imagePath,String descriptor) {
            //System.out.println("FONT: Reading font: "+imagePath);
            try {

                InputStream is = getClass().getResourceAsStream(descriptor);
                DataInputStream dis = new DataInputStream(is);

                Image image = Image.createImage(getClass().getResourceAsStream(imagePath));

                // Set loaded character set images as default
                characterImage = image;

                // header
                dis.skipBytes(4);

                // block: info
                //System.out.println("FONT: Reading info");
                dis.skipBytes(1);
                int size = getLong(dis);
                dis.skipBytes(size);

                // block: common
                //System.out.println("FONT: Reading common");
                dis.skipBytes(1);
                size = getLong(dis);
                height = getShortUnsigned(dis);
                dis.skipBytes(size-2);

                // block: page
                //System.out.println("FONT: Reading pages");
                dis.skipBytes(1);
                size = getLong(dis);
                dis.skipBytes(size);

                // block: chars
                //System.out.println("FONT: Reading chars");
                characters = new Hashtable();

                dis.skipBytes(1);
                int chars = getLong(dis)/20;

		startX = new int[chars];
		startY = new int[chars];
                characterWidth = new byte[chars];
                characterHeight = new int[chars];
                offsetX = new int[chars];
                offsetY = new int[chars];
                advance = new int[chars];
                Image glyphs[] = new Image[chars];

                for (int i = 0; i<chars; i++) {
                    int id = getLong(dis);
                    Character key = new Character((char) id);
                    characters.put(key,new Integer(i));

                    startX[i] = getShortUnsigned(dis);
                    startY[i] = getShortUnsigned(dis);
                    characterWidth[i] = (byte)getShortUnsigned(dis);
                    characterHeight[i] = getShortUnsigned(dis);
                    offsetX[i] = getShortSigned(dis);
                    offsetY[i] = getShortSigned(dis);
                    advance[i] = getShortSigned(dis);
                    glyphs[i] = null;
                    int page = dis.readByte();
                    int channel = dis.readByte();
                }

                imageTable = new Hashtable();
                imageTable.put(new Integer(0), glyphs);
                color = 0;

                // Kerning
                if (dis.available() > 0) {
                    dis.skipBytes(1);
                    dis.skipBytes(4);
                    //System.out.println("FONT: Starting kerning reading");
                    while (dis.available() > 0) {
                        int first = getLong(dis);
                        int second = getLong(dis);
                        int amount = getShortSigned(dis);
                        kerning.put(first+"-"+second, new Integer(amount));
                        //System.out.println("FONT: Kerning for "+first+"-"+second+" = "+amount);
                    }
                }
                else {
                    //System.out.println("FONT: No kerning info available");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        public int getShortUnsigned( DataInputStream dis )
        {
            try {
                byte b0 = dis.readByte();
                byte b1 = dis.readByte();
                return (((((int) b1) << 8) & 0xff00) | (((int) b0) & 0xff)) & 0xffff;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return 0;
        }

        public int getShortSigned( DataInputStream dis )
        {
            try {
                byte b0 = dis.readByte();
                byte b1 = dis.readByte();
                return (((((int) b1) << 8)) | (((int) b0)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return 0;
        }

        public int getLong( DataInputStream dis )
        {
            int short0 = getShortUnsigned( dis );
            int short1 = getShortUnsigned( dis );
            return (short0 | ( short1 << 16 ));
        }

//	public Font(String name) {
//
//		try {
//
//			Properties newfont = new Properties();
//
//			newfont.load( getClass().getResourceAsStream(name) );
//
//
//			String[] offsetsText = StringUtil.split(newfont.getProperty("offsets"), ',');
//			byte[] offsetsint = new byte[offsetsText.length];
//
//                        characters = new Hashtable();
//
//                        int charCode = 33; // Only supporting Latin char set
//			for (int c=0;c<offsetsText.length;c++) {
//
//				offsetsint[c] = Byte.parseByte(offsetsText[c]);
//                                Character key = new Character((char)charCode);
//                                characters.put(key, new Integer(c));
//                                charCode++;
//
//			}
//
//			String[] colorsText = StringUtil.split(newfont.getProperty("colors"), ',');
//			colors = new int[colorsText.length];
//
//			imageTable = new Hashtable();
//
//			for (int c=0;c<colorsText.length;c++) {
//
//				String imageName = newfont.getProperty("image."+colorsText[c]);
//
//				if (name.charAt(0)=='/' && imageName.charAt(0)!='/') {
//					imageName = "/"+imageName;
//				}
//
//				Image fontimage = Image.createImage(imageName);
//
//				colors[c] = Integer.parseInt(colorsText[c],16);
//
//				imageTable.put( new Integer(colors[c]) , fontimage);
//
//			}
//
//			String numbermodeString = newfont.getProperty("numbermode");
//			numbermode = "T".equals(numbermodeString);
//
//			construct( (Image) imageTable.get( new Integer(colors[0]) ), offsetsint);
//
//			setSpaceWidth( Integer.parseInt( newfont.getProperty("space") ) );
//			setCharacterSpacing( Integer.parseInt( newfont.getProperty("spacing") ) );
//
//		}
//		catch (IOException ex) {
//
//			ex.printStackTrace();
//			throw new RuntimeException("unable to load font: "+name);
//		}
//
//
//	}

//	private void construct(Image image, byte widths[]) {
//
//		int i, x, y, cutoff, numCharacters;
//
//		// Set the charIndex data imagePath.
//		characterImage = image;
//
//		// Set the widths array.
//		characterWidth = widths;
//		numCharacters = widths.length;
//
//		// Calculate the start positions.
//		startX = new int[numCharacters];
//		startY = new int[numCharacters];
//                advance = new int[numCharacters];
//                offsetX = new int[numCharacters];
//                offsetY = new int[numCharacters];
//
//		x = y = 0;
//		cutoff = characterImage.getWidth();
//
//		for(i = 0; i < numCharacters; i++)
//		{
//			if((x + characterWidth[i]) > cutoff)
//			{
//				x = 0;
//				y++;
//			}
//
//			startX[i] = x;	// x position in font imagePath.
//			startY[i] = y;	// y (row) in font imagePath.
//                        advance[i] = characterWidth[i];
//                        offsetX[i] = 0;
//                        offsetY[i] = 0;
//
//			x += characterWidth[i];
//		}
//
//		// Get the rowHeight
//		height = characterImage.getHeight() / (y + 1);
//
//		// Go back through and calculate the true Y positions and set rowHeight.
//		for(i = 0; i < numCharacters; i++)
//		{
//			startY[i] *= height;
//		}
//
//		// Set the default charIndex spacing.
//		characterSpacing = 1;
//
//
//		if (numbermode) {
//			// we don't need this
//			startY = null;
//		}
//		else {
//			// Set the default space width.
//			// we don't really use this anyway as its set in the .font file
//			spaceWidth = characterWidth['o' - 32];
//		}
//	}

/*        private Image colorize(Image original, int newColor) {
            int[] rgba = new int[original.getWidth()*original.getHeight()];
            original.getRGB(rgba, 0, original.getWidth(), 0, 0, original.getWidth(), original.getHeight());

            for (int i=0; i< rgba.length;i++) {
                int alpha = ((rgba[i] >> 24) & 0xFF);
                rgba[i] = (newColor | (alpha << 24));
            }

            Image coloured = Image.createRGBImage(rgba, original.getWidth(), original.getHeight(), true);

            return coloured;
        }
*/
	private void setColor(int a) {

            if (imageTable!=null) {

                Image glyphs[] = (Image[]) imageTable.get(new Integer(a));

                if (glyphs!=null) {
                    color = a;
                }
                else {

                    // initialise new chars
                    glyphs = new Image[characters.size()];
                    for (int i=0;i<characters.size();i++) {
                        glyphs[i] = null;
                    }

                    imageTable.put(new Integer(a), glyphs);
                    color = a;

                }
            }
	}

	/**
	 * Sets the system font.
	 * @param systemFont - javax.microedition.lcdui.Font object
	 */
	public void setSystemFont(javax.microedition.lcdui.Font systemFont)
	{
		this.systemFont = systemFont;
		height = getHeight();
	}


	public void setCharacterSpacing(int spacing)
	{
		characterSpacing = spacing;
	}


	public void setSpaceWidth(int width)
	{
		spaceWidth = width;
	}

	/**
	* Gets the width of a single charIndex using this font.
	* This will return the space width for any zero-width characters as these characters are treated as spaces.
	* @param c The charIndex to get the width of.
	* @return The width of the requested charIndex when rendered using this font.
	*/
	public int getWidth(char c) {

            	if(systemFont != null) {
			return systemFont.charWidth(c);
		}
                else if (numbermode) {

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
            Character key = new Character(ch);
            Object index = characters.get(key);
            if (index != null) {
                return ((Integer)index).intValue();
            }
            return 0;
	}

	public int getHeight() {
		if(systemFont != null) {
			return systemFont.getHeight();
		}

		return height;
	}

	public int getWidth(String s) {

            	if(systemFont != null) {

			return systemFont.stringWidth(s);

		}
                else if (numbermode) {

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
		else {

			int i, width = 0, prevCharacter = 0;
			char character;

			// Go through each charIndex and compoud it's width
			for(i = (s.length() - 1); i >= 0; i--)
			{
				character = s.charAt(i);

				int charIndex = getCharPosition(character);

                                //System.out.println("FONT: get kergning for: "+prevCharacter+"-"+charIndex);

                                Integer kerningModifier = (Integer) kerning.get(prevCharacter+"-"+charIndex);
                                if (kerningModifier != null) {
                                    width += kerningModifier.intValue();
                                }

				if (i == (s.length() - 1)){
				    width += characterWidth[charIndex]  +  offsetX[charIndex];
				}
				else if(i == 0) {
				    width += advance[charIndex] + characterSpacing - offsetX[charIndex];
				}
				else {
                                    width += advance[charIndex] + characterSpacing;
                                } 


                                prevCharacter = charIndex;
			}

			// Return the width, minus the spacing from the final charIndex.
			return width;
		}
	}

        private Image getGlyph(int index) {
            Image glyphs[] = (Image[]) imageTable.get(new Integer(color));
            if (glyphs[index] == null) {
                if (color == 0) {
                    glyphs[index] = Image.createImage( characterImage , startX[index] , startY[index] , characterWidth[index] , characterHeight[index] , Sprite.TRANS_NONE );
                } else {
                    int colorTemp = color;
                    color = 0;
                    Image glyph = getGlyph(index);
                    color = colorTemp;
                    Image coloured = ImageUtil.imageColor(glyph, color);

                    glyphs[index] = coloured;
                }
            }
            return glyphs[index];
        }

	/**
	 * Returns system font object, null if system font object is null and bitmap fonts being used.
	 * @return - javax.microedition.lcdui.Font or null if bitmap fonts used.
	 */
	public javax.microedition.lcdui.Font getFont() {
		return systemFont;
	}

       /**
	* Renders a string onto a graphics object.
	* This will draw the specified string onto the specified graphics object using this font at the requested position and alignment.
	* @param	g The graphics object to draw onto.
	* @param	s The string to draw.
	* @param	x The x position in the graphics object.
	* @param	y The y position in the graphics object.
	* @param	alignment The alignment to use.
        */
	public int drawString(Graphics g, String s, int x, int y, int alignment) {

            if(systemFont != null) {

                    javax.microedition.lcdui.Font f = g.getFont();

                    g.setFont(systemFont);
                    g.drawString(s, x, y, alignment);

                    g.setFont(f);

                    return getWidth(s);
            }
            else {

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
		else {


                        int i, width;
                        int length = s.length();
                        int charIndex;

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

			char character;
                        int  prevCharacter = 0;

			// Render each charIndex of the string.
			for(i = 0; i < length; i++)
			{
                            character = s.charAt(i);

                            charIndex = getCharPosition(character);

                            // Kerning
                            Integer kerningModifier = (Integer) kerning.get(prevCharacter+"-"+charIndex);
                            if (kerningModifier != null) {
                                x += kerningModifier.intValue();
                            }

                            // Draw character
                            Image glyph = getGlyph(charIndex);

                            int thisx;
                            if (i > 0) {
                                thisx = x+offsetX[charIndex];
                            }
                            else {
                                thisx = x;
                                x -= offsetX[charIndex];
                            }

                            //#mdebug
                            if (DesktopPane.debug) {
                                g.setColor(r.nextInt());
                                g.drawRect(thisx, y+offsetY[charIndex], glyph.getWidth(), glyph.getHeight());
                            }
                            //#enddebug

                            g.drawImage(glyph, thisx, y+offsetY[charIndex], Graphics.TOP | Graphics.LEFT);

                            // Advance drawing position.
                            x += advance[charIndex] + characterSpacing;

                            prevCharacter = charIndex;
                        }

			return width;
		}
            }
	}

        //#debug
        Random r = new Random();

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


