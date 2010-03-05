package org.me4se.impl.lcdui;

import java.awt.*;
import java.util.*;
import javax.microedition.midlet.*;

public class FontInfo {

	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int CENTER = 3;

	public static final int TOP = 4;
	public static final int BOTTOM = 8;

	public static final int BORDER = 1 + 2 + 4 + 8;
	public static final int ROUND = 16;
	public static final int SHADOW = 32;
	public static final int COMPACT = 64;

	//    public static final int LIGHT = 128;
	public static final int UNDERLINE_CHARS = 256;

	String type;
	public PhysicalFont font;

	public Color foreground;
	public Color border;
	public Color shadow;

	/** null means transparent! */
	public Color background;

	public int align = 2;
	public int decoration;

	/** 
	 * height of the frame/ui element. The font height is stored with the 
	 * physical font */

	public int height;

	public static Hashtable infoCache = new Hashtable();
	public static Hashtable physicalCache = new Hashtable();

	/** type string as used in the property file, e.g. "SoftButton", 
	 * 	"system.bold.medium" */

	public static FontInfo getFontInfo(String type) {
		type = type.toLowerCase();
		FontInfo result = (FontInfo) infoCache.get(type);
		if (result == null) {
			result = new FontInfo(type);
			infoCache.put(type, result);
		}
		return result;
	}

	int getIntProperty(String t, String p, int dflt) {
		String v = getProperty(t, p);
		return v == null ? dflt : Integer.decode(v).intValue();
	}

	String getProperty(String type, String property) {
		String fullName =
			property == null
				? type
				: (property.equals("font") ? "font." + type : type + "." + property);

		String v = ApplicationManager.getInstance().getProperty(fullName);

		//System.out.println("property: '"+fullName+ (v == null ? "' not found " : "' found: '"+v+"'"));

		if (v != null)
			return v;

		boolean focus = type.endsWith(".focus");
		if (focus) {
			type = type.substring(0, type.length() - 6);
		}

		int cut = type.lastIndexOf('.');

		if (cut == -1) {
			if (type.equals("default")) {
				if (!focus)
					return null;
			}
			type = focus ? "focus" : "default";
			focus = false;
		}
		else
			type = type.substring(0, cut);

		if (focus)
			type = type + ".focus";
		return getProperty(type, property);
	}

	protected FontInfo(String type) {

		this.type = type.toLowerCase();
		ApplicationManager manager = ApplicationManager.getInstance();

		height = getIntProperty(type, "height", -1);
		int bg = getIntProperty(type, "background", -1);
		int fg = getIntProperty(type, "foreground", (bg ^ 0x0ffffff) & 0x0ffffff);
		int bc = getIntProperty(type, "border", 0);
		int sc = getIntProperty(type, "shadow", 0x0aaaaaa);

		/*
		        if (bg == -1 && type.equals("softbutton") ? 0x0000000 : 0x0ffffffff;*/

		foreground = new Color(manager.getDeviceColor(fg & 0x0ffffff));

		if (bg != -1)
			background = new Color(manager.getDeviceColor(bg & 0x0ffffff));

		shadow = new Color(manager.getDeviceColor(sc & 0x0ffffff));
		border = new Color(manager.getDeviceColor(bc & 0x0ffffff));

		// handle alignment

		String alignS = getProperty(type, "align");

		if (alignS == null)
			align = type.startsWith("softbutton") ? CENTER : LEFT;
		else if ("right".equals(alignS))
			align = RIGHT;
		else if ("center".equals(alignS))
			align = CENTER;
		else if ("left".equals(alignS))
			align = LEFT;
		else if ("border".equals(alignS))
			align = BORDER;

		// handle decoration

		String decorationStr = getProperty(type, "decoration");

		if (decorationStr == null) {
			if (type.equals("item.focus") && bg == -1)
				decoration = BORDER;
			else if (type.equals("title"))
				decoration = BOTTOM;
			else if(type.equals("hyperlink"))
				decoration = UNDERLINE_CHARS;
			else if(type.equals("button"))
				decoration = BORDER|SHADOW;
		}
		else {
			decorationStr = decorationStr.toLowerCase();
			if (decorationStr.indexOf("shadow") != -1)
				decoration |= SHADOW;
			if (decorationStr.indexOf("underline") != -1)
				decoration |= BOTTOM;
			//           if (decorationStr.indexOf("light") != -1)
			//             decoration |= LIGHT;
			if (decorationStr.indexOf("overline") != -1)
				decoration |= TOP;
			if (decorationStr.indexOf("round") != -1)
				decoration |= ROUND;
			if (decorationStr.indexOf("border") != -1)
				decoration |= BORDER;
			if (decorationStr.indexOf("underlinechars") != -1)
				decoration |= UNDERLINE_CHARS;
			if (decorationStr.indexOf("compact") != -1)
				decoration |= COMPACT;
		}

		// ok, look for the physical font

		String description = getProperty(type, "font");

		if (description != null) {
			font = (PhysicalFont) physicalCache.get(description);
			if (font == null) {
				font = description.endsWith(".properties") ? getAwtFont() : new AwtFont(description);
				physicalCache.put(description, font);
			}
		}
		else {
			font = getAwtFont();
		}

		if (height == -1)
			height = font.height;

		//stem.out.println ("requested: "+type+" got size: "+size);
	}

	public AwtFont getAwtFont() {

		boolean bold =
			((type.startsWith("softbutton")
				|| "title".equals(type)
				|| type.indexOf(".bold") != -1
				|| type.indexOf(".focus") != -1));

		boolean italic = type.indexOf(".italic") != -1;

		int height;

		if ("ME4SE-iPhoneEmulator".equals(ApplicationManager.getInstance().getProperty("microedition.platform"))) {
	    if (font != null) {
	      height = font.height;
	    }
	    else {
	      if (type.indexOf(".small") != -1) {
	        height = 20;
	      }
	      else if (type.indexOf(".large") != -1) {
	        height = 28;
	      }
	      else {
	        height = 24;
	      }
	    }
		}
		else {
	    if (font != null) {
	      height = font.height;
	    }
	    else {
	      if (type.indexOf(".small") != -1) {
	        height = 14;
	      }
	      else if (type.indexOf(".large") != -1) {
	        height = 18;
	      }
	      else {
	        height = 16;
	      }
	    }
		}
		return new AwtFont(height, bold, italic);
	}

	public void drawString(Graphics g, String s, int x, int y) {
		font.drawString(g, s, x, y);

		if ((decoration & UNDERLINE_CHARS) != 0)
			g.drawLine(x, y + 1, x + font.stringWidth(s), y + 1);
	}
}