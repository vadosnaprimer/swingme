// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
// STATUS: API Complete
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

package javax.microedition.lcdui;

import org.me4se.impl.lcdui.*;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public class Font {

  static Font defaultFont;

  FontInfo info;

  /**
   * @API MIDP-1.0
   */
  public static final int STYLE_PLAIN = 0;

  /**
   * @API MIDP-1.0
   */
  public static final int STYLE_BOLD = 1;

  /**
   * @API MIDP-1.0
   */
  public static final int STYLE_ITALIC = 2;

  /**
   * @API MIDP-1.0
   */
  public static final int STYLE_UNDERLINED = 4;

  /**
   * @API MIDP-1.0
   */
  public static final int SIZE_SMALL = 8;

  /**
   * @API MIDP-1.0
   */
  public static final int SIZE_MEDIUM = 0;

  /**
   * @API MIDP-1.0
   */
  public static final int SIZE_LARGE = 16;

  /**
   * @API MIDP-1.0
   */
  public static final int FACE_MONOSPACE = 32;

  /**
   * @API MIDP-1.0
   */
  public static final int FACE_PROPORTIONAL = 64;

  /**
   * @API MIDP-1.0
   */
  public static final int FACE_SYSTEM = 0;

  /**
   * @API MIDP-2.0
   */
  public static final int FONT_STATIC_TEXT = 0;

  /**
   * @API MIDP-2.0
   */
  public static final int FONT_INPUT_TEXT = 1;

  int face;
  int style;
  int size;

  Font(int face, int style, int size) {

    Display.check();

    this.face = face;
    this.style = style;
    this.size = size;

    StringBuffer buf = new StringBuffer();
    String name;

    //int points = javax.microedition.midlet.ApplicationManager.getInstance().awtContainer.getFont().getSize();

    int awtStyle = 0;

    switch (face) {
    case FACE_SYSTEM:
      buf.append("system");
      break;
    case FACE_PROPORTIONAL:
      buf.append("proportional");
      break;
    case FACE_MONOSPACE:
      buf.append("monospace");
      break;
    default:
      throw new IllegalArgumentException();
    }

    if (isBold()) {
      buf.append(".bold");
    }
    if (isItalic()) {
      buf.append(".italic");
    }
    if (isUnderlined()) {
      buf.append(".underlined");
    }
    if (!isBold() && !isItalic() && !isUnderlined()) {
      buf.append(".plain");
    }

    switch (size) {
    case SIZE_MEDIUM:
      buf.append(".medium");
      break;
    case SIZE_SMALL:
      buf.append(".small");
      break;
    case SIZE_LARGE:
      buf.append(".large");
      break;
    default:
      throw new IllegalArgumentException();
    }

    info = FontInfo.getFontInfo(buf.toString());
  }

  /**
   * @API MIDP-1.0
   */
  public int charWidth(char c) {
    return info.font.charWidth(c);
  }

  /**
   * @API MIDP-1.0
   */
  public int charsWidth(char[] c, int ofs, int len) {
    return info.font.charsWidth(c, ofs, len);
  }

  /**
   * @API MIDP-1.0
   */
  public int getBaselinePosition() {
    return info.font.ascent;
  }

  /**
   * @API MIDP-1.0
   */
  public static Font getDefaultFont() {
    if (defaultFont == null)
      defaultFont = new Font(0, 0, 0);

    return defaultFont;
  }

  /**
   * @API MIDP-1.0
   */
  public int getFace() {
    return face;
  }

  /**
   * @API MIDP-1.0
   */
  public static Font getFont(int face, int style, int size) {
    return new Font(face, style, size);
  }

  /**
   * @API MIDP-1.0
   */
  public int getHeight() {
    return info.font.height;
  }

  /**
   * @API MIDP-1.0
   */
  public int getSize() {
    return size;
  }

  /**
   * @API MIDP-1.0
   */
  public int getStyle() {
    return style;
  }

  /**
   * @API MIDP-1.0
   */
  public boolean isBold() {
    return (style & STYLE_BOLD) != 0;
  }

  /**
   * @API MIDP-1.0
   */
  public boolean isUnderlined() {
    return (style & STYLE_UNDERLINED) != 0;
  }

  /**
   * @API MIDP-1.0
   */
  public boolean isItalic() {
    return (style & STYLE_ITALIC) != 0;
  }

  /**
   * @API MIDP-1.0
   */
  public boolean isPlain() {
    return style == 0;
  }

  /**
   * @API MIDP-1.0
   */
  public int stringWidth(String s) {
    return info.font.stringWidth(s);
  }

  /**
   * @API MIDP-1.0
   */
  public int substringWidth(String s, int ofs, int len) {
    return info.font.stringWidth(s.substring(ofs, ofs + len));
  }

  /**
   * @API MIDP-2.0
   */
  public static Font getFont(int fontSpecifier) {
    switch (fontSpecifier) {
    case FONT_INPUT_TEXT:
    case FONT_STATIC_TEXT:
      return Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
    default:
      throw new IllegalArgumentException();
    }
  }

}