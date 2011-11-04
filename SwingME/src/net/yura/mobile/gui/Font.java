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
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.ImageUtil;
import net.yura.mobile.util.Properties;
import net.yura.mobile.util.StringUtil;

/**
 * @see java.awt.Font
 */
public class Font {

        private static Vector integers;

	private Hashtable characters; // maps unicode chars to their index
	private int height;
        private int characterSpacing;


	private int startX[];
	private int startY[];
        private byte characterWidth[];
	private byte characterHeight[];


	private int offsetX[];
	private int offsetY[];
	private int advance[];
	private int spaceWidth;



	private int[] colors = new int[1]; // the colors of the source images
        private Image characterImage[]; // source images
	private Hashtable imageTable; // stores arrays of glyph images for each color


	private boolean numbermode;
	private Hashtable kerning = new Hashtable();
	private javax.microedition.lcdui.Font systemFont = null;

        /**
         * make new default font
         */
	private Font() {

	}

        /**
         * @see java.awt.Font#Font(java.lang.String, int, int)
         */
        public Font(int fname, int fstyle, int fsize) {
            setSystemFont( javax.microedition.lcdui.Font.getFont(fname, fstyle, fsize) );
        }

	/**
	 * Sets the system font.
	 * @param systemFont - javax.microedition.lcdui.Font object
	 */
	private void setSystemFont(javax.microedition.lcdui.Font systemFont) {
		this.systemFont = systemFont;
		height = getHeight();
	}

        //  -----------------------------------------------------------------
        //   o   \ o /  _ o         __|    \ /     |__        o _  \ o /   o
        //  /|\    |     /\   __\o    \o    |    o/    o/__   /\     |    /|\
        //  / \   / \   | \  /)  |    ( \  /o\  / )    |  (\  / |   / \   / \
        //  -----------------------------------------------------------------

        public static Font getDefaultSystemFont() {
            Font f = new Font();
            f.setSystemFont(javax.microedition.lcdui.Font.getDefaultFont());
            return f;
        }

	/**
	 * BMFont format
	 */
	public static Font getFont(String descriptor, String[] imagePaths, int[] colors) {

		try {

                    InputStream is = Midlet.getResourceAsStream(descriptor);

                    Image[] characterImage = new Image[imagePaths.length];
                    for (int c=0;c<imagePaths.length;c++) {

                        Image image = Image.createImage( Midlet.getResourceAsStream(imagePaths[c]));

                        // Set loaded character set images as default
                        characterImage[c] = image;
                    }

                    return getFont(is, characterImage, colors);
		}
                catch (IOException ex) {
                    Logger.warn(ex);
                    throw new RuntimeException();
		}
	}

        public static Font getFont(InputStream is, Image[] imagesArray, int[] colorsArray) throws IOException {

            Font f = new Font();
	    f.characterImage = imagesArray;

            f.colors = colorsArray;

            DataInputStream dis = new DataInputStream(is);

            // header
            dis.skipBytes(4);

            // block: info
            //Logger.debug("FONT: Reading info");
            dis.skipBytes(1);
            int size = getLong(dis);
            dis.skipBytes(size);

            // block: common
            //Logger.debug("FONT: Reading common");
            dis.skipBytes(1);
            size = getLong(dis);
            f.height = getShortUnsigned(dis);
            dis.skipBytes(size - 2);

            // block: page
            //Logger.debug("FONT: Reading pages");
            dis.skipBytes(1);
            size = getLong(dis);
            dis.skipBytes(size);

            // block: chars
            //Logger.debug("FONT: Reading chars");
            f.characters = new Hashtable();

            dis.skipBytes(1);
            int chars = getLong(dis) / 20;

            f.startX = new int[chars];
            f.startY = new int[chars];
            f.characterWidth = new byte[chars];
            f.characterHeight = new byte[chars];
            f.offsetX = new int[chars];
            f.offsetY = new int[chars];
            f.advance = new int[chars];
            Image glyphs[] = new Image[chars];

            for (int i = 0; i < chars; i++) {
                    int id = getLong(dis);
                    Character key = new Character((char) id);
                    f.characters.put(key, new Integer(i));

                    f.startX[i] = getShortUnsigned(dis);
                    f.startY[i] = getShortUnsigned(dis);
                    f.characterWidth[i] = (byte)getShortUnsigned(dis);
                    f.characterHeight[i] = (byte)getShortUnsigned(dis);
                    f.offsetX[i] = getShortSigned(dis);
                    f.offsetY[i] = getShortSigned(dis);
                    f.advance[i] = getShortSigned(dis);
                    glyphs[i] = null;
                    int page = dis.readByte();
                    int channel = dis.readByte();
            }

            f.imageTable = new Hashtable();
            f.imageTable.put(new Integer(0), glyphs);
            // if base color is 0x00000000 then we must use the colorize method to recolor
            // for any ofther color we need to use the imageUtil.changeColor method to recolor


            //color = 0;

            // Kerning
            if (dis.available() > 0) {
                    dis.skipBytes(1);
                    dis.skipBytes(4);
                    //Logger.debug("FONT: Starting kerning reading");
                    while (dis.available() > 0) {
                        try {
                            char first = (char) getLong(dis);
                            char second = (char) getLong(dis);
                            Integer charPairIdentifier = getCharPairId(first,second);

                            int amount = getShortSigned(dis);

                            Integer kerningValue = getInteger(amount);
                            f.kerning.put(charPairIdentifier, kerningValue);

                            //Logger.debug("FONT: Kerning for "+first+"-"+second+" = "+amount);
                        } catch (EOFException e) {
                            //#debug debug
                            Logger.debug(e);
                        }
                    }
            }
            else {
                    //Logger.debug("FONT: No kerning info available");
            }

            integers = null;
            return f;

        }

	public static int getShortUnsigned(DataInputStream dis) throws IOException {
            byte b0 = dis.readByte();
            byte b1 = dis.readByte();
            return (((b1 << 8) & 0xff00) | (b0 & 0xff)) & 0xffff;
	}

	public static int getShortSigned(DataInputStream dis) throws IOException {
            byte b0 = dis.readByte();
            byte b1 = dis.readByte();
            return (((b1 << 8)) | (b0));
	}

	public static int getLong(DataInputStream dis) throws IOException {
            int short0 = getShortUnsigned(dis);
            int short1 = getShortUnsigned(dis);
            return (short0 | (short1 << 16));
	}

        private static Integer getInteger(int integer) {
            if (integers == null) {
                integers = new Vector(1,1);
            }
            Integer lookup = new Integer(integer);
            int index = integers.indexOf(lookup);
            if (index > -1) {
                return (Integer) integers.elementAt(index);
            }
            else {
                integers.addElement(lookup);
                return lookup;
            }
        }

        private static Integer getCharPairId(char first, char second) {
            return new Integer((first | (second << 16)));
        }

	public static Font getFont(String name) {

		try {

                        Font f = new Font();

			Properties newfont = new Properties();

			newfont.load( Midlet.getResourceAsStream(name) );
			String baseDir = name.substring(0, name.lastIndexOf('/') + 1);

			String[] offsetsText = StringUtil.split(newfont.getProperty("offsets"), ',');
			byte[] offsetsint = new byte[offsetsText.length];

                        f.characters = new Hashtable();

                        int numCharacters=0;

			{ // Fill the characters table. skip missing glyphs where offsets are 0
				//int charIndex = 0;
				int charCode = 32; // Only supporting Latin char set

				for (int c =0;c<offsetsText.length;c++) {

					byte size = Byte.parseByte(offsetsText[c]);
					Character key = new Character((char)charCode);

					if(size > 0) {
						f.characters.put(key, new Integer(numCharacters));
                                                offsetsint[numCharacters] = size;
						numCharacters++;
					}

					charCode++;

				}
			}


			String[] colorsText = StringUtil.split(newfont.getProperty("colors"), ',');
			f.colors = new int[colorsText.length];

			f.imageTable = new Hashtable();

                        f.characterImage = new Image[f.colors.length];
			for (int c=0;c<colorsText.length;c++) {

				String imageName = baseDir + newfont.getProperty("image."+colorsText[c]);

				if (name.charAt(0)=='/' && imageName.charAt(0)!='/') {
					imageName = "/"+imageName;
				}

				Image fontimage = Image.createImage(imageName);

				f.colors[c] = Integer.parseInt(colorsText[c],16) | 0xFF000000;

                                f.characterImage[c] = fontimage;
				//imageTable.put( new Integer(colors[c])	, fontimage);

			}

			String numbermodeString = newfont.getProperty("numbermode");
			f.numbermode = "T".equals(numbermodeString);

			Image image = f.characterImage[0];

                        int i, x, y, cutoff;

                        // Set the charIndex data imagePath.
                        //characterImage = image;

                        // Set the widths array.
                        f.characterWidth = new byte[numCharacters];//;
                        System.arraycopy(offsetsint, 0, f.characterWidth, 0, numCharacters);
                        //numCharacters = offsetsint.length;

                        // Calculate the start positions.
                        f.startX = new int[numCharacters];
                        f.startY = new int[numCharacters];
                        f.advance = new int[numCharacters];
                        f.offsetX = new int[numCharacters];
                        f.offsetY = new int[numCharacters];

                        x = y = 0;
                        cutoff = image.getWidth();

                        for(i = 0; i < numCharacters; i++) {

                                if((x + f.characterWidth[i]) > cutoff) {
                                        x = 0;
                                        y++;
                                }

                                f.startX[i] = x;	// x position in font imagePath.
                                f.startY[i] = y;	// y (row) in font imagePath.
                                f.advance[i] = f.characterWidth[i];
                                f.offsetX[i] = 0;
                                f.offsetY[i] = 0;

                                x += f.characterWidth[i];
                        }

                        // Get the rowHeight
                        f.height = image.getHeight() / (y + 1);

                        f.characterHeight = new byte[numCharacters];
                        for(i = 0; i < numCharacters; i++) {
                            f.characterHeight[i] = (byte)f.height;
                        }

                        // Go back through and calculate the true Y positions and set rowHeight.
                        for(i = 0; i < numCharacters; i++)
                        {
                                f.startY[i] *= f.height;
                        }

                        // Set the default charIndex spacing.
                        f.characterSpacing = 1;


                        if (f.numbermode) {
                                // we don't need this
                                f.startY = null;
                        }
                        else {
                                // Set the default space width.
                                // we don't really use this anyway as its set in the .font file
                                Integer id = (Integer)f.characters.get(new Character(' '));
                                if (id!=null) {
                                    f.spaceWidth = f.characterWidth[id.intValue()];
                                }
                        }

			f.setSpaceWidth( Integer.parseInt( newfont.getProperty("space") ) );
			f.setCharacterSpacing( Integer.parseInt( newfont.getProperty("spacing") ) );

                        return f;
		}
		catch (IOException ex) {
                    Logger.warn(ex);
                    throw new RuntimeException("unable to load font: "+name);
		}

	}

        //  -----------------------------------------------------------------
        //   o   \ o /  _ o         __|    \ /     |__        o _  \ o /   o
        //  /|\    |     /\   __\o    \o    |    o/    o/__   /\     |    /|\
        //  / \   / \   | \  /)  |    ( \  /o\  / )    |  (\  / |   / \   / \
        //  -----------------------------------------------------------------

	public void setCharacterSpacing(int spacing) {
		characterSpacing = spacing;
	}

	public void setSpaceWidth(int width) {
		spaceWidth = width;
	}

	/**
	 * Gets the width of a single charIndex using this font.
	 * This will return the space width for any zero-width characters as these characters are treated as spaces.
	 * @param c The charIndex to get the width of.
	 * @return The width of the requested charIndex when rendered using this font.
	 */
	public int getWidth(char c) {

		if (systemFont != null) {
			return systemFont.charWidth(c);
		}
                else if (numbermode) {

			if (c == '-') {
				return characterWidth[POSITION_MINUS];
			}
                        else if (c == ':') {
				return characterWidth[POSITION_TIME];
			}
                        else if (c == '.') {
				return characterWidth[POSITION_DOT];
			}
                        else {
				// it must be a number then if its none of these!!
				return characterWidth[Integer.parseInt(String.valueOf(c))];
			}
		}
                else {
			int p = getCharIndex(c);
			if(p > -1)
				return characterWidth[getCharIndex(c)];
			// else glyph not fudge-packed
				return 0; // TODO return width of system font char
		}
	}

	private int getCharIndex(char ch) {
		/**
		 * Gets the array index of this char for the various glyph arrays:
		 * advance[], characterWidth[], characterHeight[], offsetX[], offsetY[],
		 */

		Character key = new Character(ch);
		Object index = characters.get(key);

		if (index != null) {
			return ((Integer) index).intValue();
		}

		return -1; // Shizer!
	}

	public int getHeight() {
		if (systemFont != null) {
			return systemFont.getHeight();
		}

		return height;
	}

	public int getWidth(String s) {

		if (systemFont != null) {

			return systemFont.stringWidth(s);

		} else if (numbermode) {

			// gives width with space either side

			int width = characterSpacing;

			if (s.indexOf(':') == -1 && characterWidth.length > POSITION_CURRENCY) {

				width += characterWidth[POSITION_CURRENCY] + characterSpacing;

			}

			for (int i = 0; i < s.length(); i++) {

				char ch = s.charAt(i);

				width += getWidth(ch) + characterSpacing;

			}
			return width;
		} else {

			int i, width = 0, prevCharacter = 0;
			char character;

			// Go through each charIndex and compoud it's width
			for (i = (s.length() - 1); i >= 0; i--) {
				character = s.charAt(i);

				int charIndex = getCharIndex(character);
				int w = 0;

				if(charIndex > - 1) {

					//Logger.debug("FONT: get kergning for: "+prevCharacter+"-"+charIndex);

					Integer kerningModifier = (Integer) kerning.get(getCharPairId((char)prevCharacter, (char)charIndex));
					if (kerningModifier != null) {
						w = kerningModifier.intValue();

					}

					if (character == ' ' && i == (s.length() - 1)) { // Last space

						w = getWidth(character) + advance[charIndex];

					} else if (i == (s.length() - 1)) { // Last char

						w = getWidth(character) + offsetX[charIndex];

					} else if (i == 0) { // First char

						// If the first char has POSITIVE offset:
						// - It will effectively REDUCE total width
						// - Thus it is SUBTRACTED here
						// - NEGATIVE offsets produce the opposite effect

						w = advance[charIndex] + characterSpacing - offsetX[charIndex];

					} else { // All other chars

						// In the middle of the text:
						// - We don't care about offset
						w = advance[charIndex] + characterSpacing;

					}
				} else { // Mirror system font substitution from drawString

					w = javax.microedition.lcdui.Font.getDefaultFont().charWidth(character);
				}

				prevCharacter = charIndex;
				//Logger.debug(i + ": " + character + " | w:" + w + "\tgW:" + getWidth(character) + "\tcS:" + characterSpacing + "\tadv:" + advance[charIndex] + "\txOff:" + offsetX[charIndex] + "\tkern:" + k);

				width += w;

			}

			// Return the width, minus the spacing from the final charIndex.
			//Logger.debug(s + ":" + width);
			return width;
		}
	}

	private Image getGlyph(int index, int color) {

		Image[] glyphs = (Image[]) imageTable.get(new Integer(color));


                if (glyphs == null ) { // color not found?
                        glyphs = new Image[characters.size()];
                        imageTable.put(new Integer(color), glyphs);
                }

		if (glyphs[index] == null) {
                        // find if we have this color already
                        for (int c=0;c<colors.length;c++) {
                            if (color == colors[c]) {
          //#mdebug debug
//				Logger.debug("characterHeight = " + characterHeight[index]);
//				Logger.debug("characterWidth = " + characterWidth[index]);
//				Logger.debug("startY = " + startY[index]);
//				Logger.debug("startX = " + startX[index]);
//				Logger.debug("characterImage = " + characterImage[c]);
          //#enddebug
                                // hack to stop the thing crashing, this should be taken out
                                //if (characterWidth[index]==0) {
                                //    glyphs[index] = Image.createImage(1, 1);
                                //    return glyphs[index];
                                //}

                                glyphs[index] = Image.createImage(
					characterImage[c],
					startX[index],
					startY[index],
					characterWidth[index],
					characterHeight[index],
					Sprite.TRANS_NONE
					);

                                return glyphs[index];
                            }
                        }

                        int defaultColor = colors[0];

                        Image glyph = getGlyph(index,defaultColor);
                        Image coloured;

                        if (defaultColor==0xFF000000) { // black
                            coloured = ImageUtil.colorize(glyph, color);
                        }
                        else {
                            coloured = ImageUtil.imageColor(glyph, color);
                        }

                        glyphs[index] = coloured;

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


		if (systemFont != null) {
			// TODO: refactor with missing-glyph code
			javax.microedition.lcdui.Font f = g.getFont();

			g.setFont(systemFont);
			g.drawString(s, x, y, alignment);

			g.setFont(f);

			return getWidth(s);
		} else {

                        int color = g.getColor() | 0xFF000000;

			if (numbermode) {

				int x2 = x;

				if (s.indexOf(':') == -1 && characterWidth.length > POSITION_CURRENCY) {
					// Draw $
					x2 = drawDigit(g, x2, y, POSITION_CURRENCY);
				}

				if (s.indexOf('.') == -1 && s.indexOf(':') == -1) {

					int thenum = Integer.parseInt(s);

					x2 += renderNumber(g, x2, y, thenum, s.length());

				} else {

					char sep;
					int code;

					if (s.indexOf(':') == -1) {
						sep = '.';
						code = POSITION_DOT;
					} else {
						sep = ':';
						code = POSITION_TIME;
					}

					String firstnum = s.substring(0, s.indexOf(sep));
					String secondnum = s.substring(s.indexOf(sep) + 1, s.length());

					int thenum1 = Integer.parseInt(firstnum);
					int thenum2 = Integer.parseInt(secondnum);

					x2 += renderNumber(g, x2, y, thenum1, firstnum.length());

					// Draw . or :
					x2 = drawDigit(g, x2, y, code);

					x2 += renderNumber(g, x2, y, thenum2, secondnum.length());

				}

				return x2 - x;

			} else {


				int i, width;
				int length = s.length();
				int charIndex;

				// Calculate the overall string width
				width = getWidth(s);

				// Adjust the x position for horizontal alignment.
				if ((alignment & Graphics.HCENTER) != 0) {
					x -= width >> 1;
				} else if ((alignment & Graphics.RIGHT) != 0) {
					x -= width;
				}

				// Adjust the y position for vertical alignment.
				if ((alignment & Graphics.VCENTER) != 0) {
					y -= height >> 1;
				} else if ((alignment & Graphics.BOTTOM) != 0) {
					y -= height - 1;
				}

				char character;
				int prevCharacter = 0;

				// Render each charIndex of the string.
				for (i = 0; i < length; i++) {
					character = s.charAt(i);

					charIndex = getCharIndex(character);

					if(charIndex > -1) {
						// Kerning
						Integer kerningModifier = (Integer) kerning.get(getCharPairId((char)prevCharacter, (char)charIndex));
						if (kerningModifier != null) {
							x += kerningModifier.intValue();
						}

						// Draw character
						Image glyph = getGlyph(charIndex,color);

						int thisx;
						if (i > 0) {
							thisx = x + offsetX[charIndex];
						}
						else {
							thisx = x;
							x -= offsetX[charIndex];
						}

						//#mdebug
						if (DesktopPane.debug) {
							g.setColor(r.nextInt());
							g.drawRect(thisx, y + offsetY[charIndex], glyph.getWidth(), glyph.getHeight());
							g.setColor(color);
						}
						//#enddebug

						g.drawImage(glyph, thisx, y + offsetY[charIndex], Graphics.TOP | Graphics.LEFT);
						//Logger.debug("yOff:" + offsetY[charIndex] + ' ' + character);
						// Advance drawing position.
						x += advance[charIndex] + characterSpacing;

					} else { // Substitute system font for missing glyphs

						// Swap fonts
						systemFont = javax.microedition.lcdui.Font.getDefaultFont();
						javax.microedition.lcdui.Font savedFont = g.getFont();

						g.setFont(systemFont);
						g.drawChar(character, x, y, alignment);
						x += getWidth(character); // Do advance

						// Revert fonts
						systemFont = null;
						g.setFont(savedFont);
					}

					prevCharacter = charIndex;
				}

				return width;
			}
		}
	}
	//#debug debug
	Random r = new Random();
	private static int POSITION_DOT = 10;
	private static int POSITION_CURRENCY = 11;
	private static int POSITION_MINUS = 12;
	private static int POSITION_TIME = 13;

	// NUMBER!!!! NUMBER!!!! NUMBER!!!! NUMBER!!!! NUMBER!!!! NUMBER!!!!
	// 0-9 numbers
	// 10 .
	// 11 $
	// 12 -
	// 13 :
	private int renderNumber(Graphics g, int x, int y, int number, int digits) {

		int column, digit, sX = x;

		for (digit = 0, column = 1; digit < digits; digit++) {
			column *= 10;
		}

		if (number < 0) {
			number = -number;

			// Draw -
			x = drawDigit(g, x, y, POSITION_MINUS);
		}

		number %= column;

		while (column > 1) {
			column /= 10;
			digit = number / column;
			number -= digit * column;

			x = drawDigit(g, x, y, digit);
		}

		return x - sX - characterSpacing;
	}

	private int drawDigit(Graphics g, int x, int y, int a) {

		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipW = g.getClipWidth();
		int clipH = g.getClipHeight();

                // TODO
		//g.clipRect(x, y, characterWidth[a], characterImage.getHeight());
		//g.drawImage(characterImage, x - startX[a], y, Graphics.TOP | Graphics.LEFT);
		x += characterWidth[a] + characterSpacing;

		g.setClip(clipX, clipY, clipW, clipH);

		return x;
	}
}


