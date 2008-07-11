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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.util.ButtonGroup;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.Border;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JButton
 */
public class Button extends Label implements ActionListener {

	private static CommandButton selectButton = new CommandButton("Select","select");

	public static void setSelectButtonText(String a) {
		
		selectButton = new CommandButton(a,selectButton.getActionCommand());
		
	}
	
	protected Border normalBorder;
	protected Border activeBorder;
	
	private ActionListener al;
	private String actionCommand;
	
	protected boolean selected;
	protected ButtonGroup buttonGroup;
	
	private boolean useSelectButton;

        /**
         * @see javax.swing.JButton#JButton() JButton.JButton
         */
	public Button() {
            this(null);
        }
        
        /**
         * @param label the text of the button
         * @see javax.swing.JButton#JButton(java.lang.String) JButton.JButton
         */
	public Button(String label) {
		
		this(label,null);

	}
	
        /**
         * @param label the text of the button
         * @param img the Icon image to display on the button
         * @see javax.swing.JButton#JButton(java.lang.String, javax.swing.Icon) JButton.JButton
         */
	public Button(String label, Image img) {
		
		this(label,img,
                        DesktopPane.getDefaultTheme().background,
                        DesktopPane.getDefaultTheme().normalBorder,
                        DesktopPane.getDefaultTheme().activeBorder
                );

	}
	
	// full constructor
	public Button(String label, Image img,int a,Border b,Border c) {
		super(label, img);
		
		background = a;
		
		setBorder(b);
                setActiveBorder(c);
		selectable = true;
		
		setForegroundByFontColorIndex(0);
		
	}

        public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
        
	public void addActionListener(ActionListener l) {
		
		al = l;
		
	}
	public void removeActionListener(ActionListener l) {
		
		if (al == l) { al = null; }
	}
	
	public void setActionCommand(String ac) {
		
		actionCommand=ac;
	}
        public ActionListener getActionListener() {
            return al;
        }

	public boolean keyEvent(KeyEvent keyEvent) {
            
            	if (keyEvent.justPressedAction(Canvas.FIRE)) {

                        selected = true;
                    
			fireActionPerformed();
			
			return true;
		}
		return false;
    
        }

        public void pointerEvent(int type, int x, int y) {
            super.pointerEvent(type, x, y);
            
            if (type == DesktopPane.PRESSED) {
                selected=true;
            }
            else if (selected && type == DesktopPane.RELEASED) {
                if (x>=0 && x<=width && y>=0 && y<=height) {
                    fireActionPerformed();
                }
            }

        }
        
	 public void fireActionPerformed() {
		
            	if (buttonGroup!=null) {
                        // this unselects all other buttons in the same button group as this 1
			buttonGroup.setSelected(this);
		}
            
		if (al!=null) {
			al.actionPerformed((actionCommand!=null)?actionCommand:getText());
		}

	}
        
	public void setBorder(Border b) {
		normalBorder = b;
                super.setBorder(b);
	}
 
	public void setActiveBorder(Border b) {
		activeBorder = b;
	}

	public void focusLost() {
                super.focusLost();
		super.setBorder(normalBorder);
		setForegroundByFontColorIndex(0);
		
		if (useSelectButton) {
			DesktopPane.getDesktopPane().setComponentCommand(0, null);
		}
		
		repaint();
	}

	public void focusGained() {
                super.focusGained();
		super.setBorder(activeBorder);
		setForegroundByFontColorIndex(1);
		
		if (useSelectButton) {
			DesktopPane.getDesktopPane().setComponentCommand(0, selectButton);
		}
		
		repaint();
	}


	public void setButtonGroup(ButtonGroup buttonGroup) {
		
		this.buttonGroup = buttonGroup;
		
	}
	
	public void actionPerformed(String actionCommand) {
		
		if(selectButton.getActionCommand().equals(actionCommand)) {
			
			fireActionPerformed();
		}
		
	}

	public boolean isUseSelectButton() {
		return useSelectButton;
	}

	public void setUseSelectButton(boolean useSelectButton) {
		this.useSelectButton = useSelectButton;
	}

        protected int getBorderColor() {
            if (border instanceof LineBorder) {
                return ((LineBorder)border).getLineColor();
            }
            return 0;
        }
	public String toString() {
            return super.toString() +"("+ getText()+")";
        }
}