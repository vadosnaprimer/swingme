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
import net.yura.mobile.gui.plaf.Style;
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
	
        protected int normalForeground;
        protected int activeForeground;
        protected int disabledForeground;
        
	private ActionListener al;
	private String actionCommand;
	
	private boolean selected;
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
		
		super(label);
                
                focusable = true;

	}
	
        /**
         * @param label the text of the button
         * @param img the Icon image to display on the button
         * @see javax.swing.JButton#JButton(java.lang.String, javax.swing.Icon) JButton.JButton
         */
	public Button(String label, Image img) {
		this(label);
                
                setIcon(img);
	}

        public void setFocusable(boolean s) {
		if (s) {
                    foreground = normalForeground;
                }
                else {
                    foreground = disabledForeground;
                }
                super.setFocusable(s);
	}
        
        /**
         * @see javax.swing.AbstractButton#isSelected() AbstractButton.isSelected
         */
        public boolean isSelected() {
		return selected;
	}

        /**
         * @see javax.swing.AbstractButton#setSelected(boolean) AbstractButton.setSelected
         */
	public void setSelected(boolean selected) {
                boolean old = this.selected;
		this.selected = selected;
                if (buttonGroup!=null && selected) {
                    // this unselects all other buttons in the same button group as this 1
                    buttonGroup.setSelected(this);
                }
                if (old!=selected) {
                    repaint();
                }
                
	}
        
        /**
         * @see javax.swing.AbstractButton#addActionListener(java.awt.event.ActionListener) AbstractButton.addActionListener
         */
	public void addActionListener(ActionListener l) {
		
		al = l;
		
	}
        
        /**
         * @see javax.swing.AbstractButton#removeActionListener(java.awt.event.ActionListener) AbstractButton.removeActionListener
         */
	public void removeActionListener(ActionListener l) {
		
		if (al == l) { al = null; }
	}
	
        /**
         * @param ac The Action Command for this button
         * @see javax.swing.AbstractButton#setActionCommand(java.lang.String) AbstractButton.setActionCommand
         */
	public void setActionCommand(String ac) {
		
		actionCommand=ac;
	}
        
        /**
         * @return The Action Command for this button
         * @see javax.swing.AbstractButton#getActionCommand() AbstractButton.getActionCommand
         */
        public String getActionCommand() {
            return actionCommand;
        }
        
        /**
         * @see javax.swing.AbstractButton#getActionListeners() AbstractButton.getActionListeners
         */
        public ActionListener getActionListener() {
            return al;
        }

	public boolean keyEvent(KeyEvent keyEvent) {
            
            	if (keyEvent.justPressedAction(Canvas.FIRE)) {
                    
			fireActionPerformed();
			
			return true;
		}
		return false;
    
        }

        public void pointerEvent(int type, int x, int y) {
            super.pointerEvent(type, x, y);
            
            if (type == DesktopPane.RELEASED) {
                if (x>=0 && x<=width && y>=0 && y<=height) {
                    fireActionPerformed();
                }
            }

        }
        
        /**
         * @see javax.swing.AbstractButton#fireActionPerformed(java.awt.event.ActionEvent) AbstractButton.fireActionPerformed
         */
	public void fireActionPerformed() {

            toggleSelection();

            if (al!=null) {
                    al.actionPerformed((actionCommand!=null)?actionCommand:getText());
            }

	}
         
        protected void toggleSelection() {
            setSelected(true);
        }
        
        public void setNormalBorder(Border b) {
            normalBorder = b;
        }
        
	public void setActiveBorder(Border b) {
		activeBorder = b;
	}

	public void focusLost() {
                super.focusLost();
		super.setBorder(normalBorder);
		foreground = normalForeground;
		
		if (useSelectButton) {
			DesktopPane.getDesktopPane().setComponentCommand(0, null);
		}
		
		repaint();
	}

	public void focusGained() {
                super.focusGained();
		super.setBorder(activeBorder);
		foreground = activeForeground;
		
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

	public String toString() {
            return super.toString() +"("+ getText()+")";
        }
        public String getName() {
            return "Button";
        }
        public void updateUI() {
                super.updateUI();
                
                Style st = DesktopPane.getDefaultTheme(this);

                normalBorder = st.getBorder( Style.ENABLED );
                activeBorder = st.getBorder( Style.FOCUSED );
                
		activeForeground = st.getForeground(Style.FOCUSED);
                disabledForeground = st.getForeground(Style.DISABLED);
                normalForeground = st.getForeground(Style.ENABLED);

        }
}