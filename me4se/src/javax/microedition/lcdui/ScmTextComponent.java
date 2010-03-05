//ME4SE - A MicroEdition Emulation for J2SE 
//
//Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
//Contributors:
// thiago.leao.moreira@terra.com.br: mask password field, 
//		maxSize of TextField and TextField.getCaretPosition().
//
//STATUS: API complete
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License as
//published by the Free Software Foundation; either version 2 of the
//License, or (at your option) any later version. This program is
//distributed in the hope that it will be useful, but WITHOUT ANY
//WARRANTY; without even the implied warranty of MERCHANTABILITY or
//FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
//License for more details. You should have received a copy of the
//GNU General Public License along with this program; if not, write
//to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
//Boston, MA 02111-1307, USA.

package javax.microedition.lcdui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

import org.me4se.impl.lcdui.FontInfo;
import org.me4se.impl.lcdui.PhysicalFont;
import org.me4se.impl.lcdui.WordWrap;

/**
 * @author Stefan Haustein
 * 
 * @ME4SE INTERNAL */

public class ScmTextComponent extends ScmDeviceComponent {

	protected int constraints;
	protected int cx;
	protected int cy;
	protected Vector lines = new Vector();
	protected int maxSize;
	protected int y0;

	public ScmTextComponent(Item item, String type, boolean editable) {
		super(item, type, editable);
		lines.addElement("");
	}

	boolean check(char c) {
		switch (constraints & TextField.CONSTRAINT_MASK) {
			case TextField.PHONENUMBER :
				if ("/()#*- ".indexOf(c) != -1)
					return true;
			case TextField.NUMERIC :
				return (c >= '0' && c <= '9') || c == '-';
			case TextField.EMAILADDR :
				return (c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')
					|| (".-_".indexOf(c) != -1);
			case TextField.URL :
				return true;
			default :
				return true;
		}
	}

	public void doLayout() {
		//		System.out.println("relayouting");

		int charPos = getCharPos();
		String text = getText();
		lines = new Vector();
		//		System.out.println ("full text: "+text);

	//	System.out.println("width: "+getWidth());
		
		
		WordWrap ww = new WordWrap(getFontInfo().font, text, getWidth()== 0 ? 100 : getWidth());
		int p0 = 0;
		cx = 0;
		cy = 0;
		while (true) {
			int p1 = ww.next();
			if (p1 == -1)
				break;
			int chars = p1 - p0;
			if (charPos > chars) {
				charPos -= chars;
				cy++;
			} else if (charPos > 0) {
				cx = charPos;
				charPos = 0;
			}
			String l = text.substring(p0, p1);
		//				System.out.println ("p0: "+p0+" p1:"+p1+" appending line: "+l);
			lines.addElement(l);
			p0 = p1;
		}

		if (lines.size() == 0 || charPos > 0)
			lines.addElement("");

	}
	
	public int getCaretPosition() {
		return cx;
	}

	public int getCharPos() {
		int pos = cx;
		for (int i = 0; i < Math.min(cy, lines.size()); i++)
			pos += getLine(i) == null ? 0 : getLine(i).length();
		return pos;
	}

	public int getConstraints() {
		return constraints;
	}

	String getLine(int n) {
		return (String) lines.elementAt(n);
	}

	/**
	 * @return the maximun size of the String in the TextComponent
	 */
	public int getMaxSize() {
		return maxSize;
	}

	public Dimension getMinimumSize() {
		return new Dimension(20, (getFocusable() ? 1 : lines.size())* getFontInfo().height);
	}

	/**
	 * @return the String in the TextComponent
	 */
	public String getText() {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < lines.size(); i++)
			buf.append((String) lines.elementAt(i));

		return buf.toString();
	}

	public boolean keyPressed(String code) {
	//	System.out.println("code0: "+(int) code.charAt(0));
		if (code.equals("LEFT")) {
			if (cx > 0)
				cx--;
			else if (cy > 0) {
				cy--;
				cx = getLine(cy).length();
			}
			repaint();
			return true;
		} else if (code.equals("RIGHT")) {
			if (cx < getLine(cy).length())
				cx++;
			else if (cy < lines.size() - 1) {
				cy++;
				cx = 0;
			}
			repaint();
			return true;
		}
		else if (code.equals("CLEAR")) {
			String line = getLine(cy);
			
			if (cx == 0 && cy > 0) {
				line = getLine(--cy);
				cx = line.length();
			}

			if (cx > 0) {
				line = line.substring(0, cx - 1) + line.substring(cx);
				cx--;
			}

			lines.setElementAt(line, cy);
			doLayout();
			repaint();
			return true;
			
			/* delete #127
			
			if (line.length() != cx) {
				line = line.substring(0, cx) + line.substring(cx + 1);
			}
			lines.setElementAt(line, cy);
			doLayout(); */
		} 
		else if(code.equals("SPACE")){
			return enterChar(' ');			
		}
		else if(code.length()==1){
			return enterChar(code.charAt(0));
		}
		return super.keyPressed(code);
	}

	public boolean enterChar(char c) {
        if((constraints & TextField.UNEDITABLE) != 0) {
            return false;
        }
        
		String line = getLine(cy);

		// key DELETE
		//TODO in Jornada (HP) device don't work 
		
			// key BACKSPACE
			if (c >= 32 && check(c)) {
				//don't allow the user to digit more symbols 
				if (line.length() == maxSize) {
					return false;
				}

				line = line.substring(0, cx) + c + line.substring(cx);
				cx++;

				lines.setElementAt(line, cy);
				if (getFontInfo().font.stringWidth(line) > getWidth()) {
					doLayout();
				}
			}
		
		repaint();
		return true;
	}

	public void paint(Graphics g) {
		super.paint(g);

		FontInfo fi = getFontInfo();
		PhysicalFont pf = fi.font;

		g.setColor(fi.foreground);

		if (cy >= lines.size())
			cy = lines.size() - 1;

		if (cy * pf.height - y0 >= getHeight())
			y0 = cy * pf.height;
		else if (y0 > cy * pf.height)
			y0 = cy * pf.height;

		int y = -y0;

		for (int i = 0; i < lines.size(); i++) {

			String line = getLine(i);

			//Mask the PASSWORD field
			if ((constraints & TextField.PASSWORD) != 0) {
				line = this.toMask(line);
			}
			fi.drawString(g, line, 0, y + pf.ascent);

			if (i == cy && hasFocus() && ((constraints & TextField.UNEDITABLE) == 0)) {
				if (cx > line.length())
					cx = line.length();
				int x = pf.stringWidth(line.substring(0, cx));
				g.fillRect(x, y, 2, pf.height);
			}

			y += pf.height;
		}
	}

	public void setConstraints(int constraints) {
		this.constraints = constraints;
	}

	/**
	 * @param i
	 */
	public int setMaxSize(int i) {
		return maxSize = i > 65536 ? 65536 : i;
	}

	public void setText(String text) {
		lines = new Vector();
		lines.addElement(text == null ? "" : text);
		invalidate();
		doLayout();
	}

	/**
	 * @param password the String to mask
	 * @return mascared String
	 */
	protected String toMask(String password) {
		int size = password.length();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < size; i++) {
			buffer.append('*');
		}
		return buffer.toString();
	}

}
