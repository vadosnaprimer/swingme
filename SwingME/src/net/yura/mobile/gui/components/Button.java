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
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JButton
 */
public class Button extends Label implements ActionListener {

	private Button selectButton;
//
//        static {
//            selectButton = new Button("Select");
//            selectButton.setActionCommand("select");
//        }
//
//	public static void setSelectButtonText(String a) {
//		selectButton.setText(a);
//	}
//	
//	protected Border activeBorder;
//        protected Border disabledBorder;
//        protected Border selectedBorder;
//        protected Border selectedFocusedBorder;
//
//        protected int selectedForeground;
//        protected int selectedFocusedForeground;
//        protected int activeForeground;
//        protected int disabledForeground;
        
	private ActionListener al;
	private String actionCommand;
	
	private boolean selected;
	protected ButtonGroup buttonGroup;
	
	private boolean useSelectButton;
        private int mneonic;

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
                setHorizontalAlignment(Graphics.HCENTER);
	}
	
        /**
         * @param label the text of the button
         * @param img the Icon image to display on the button
         * @see javax.swing.JButton#JButton(java.lang.String, javax.swing.Icon) JButton.JButton
         */
	public Button(String label, Icon img) {
		this(label);
                setIcon(img);
	}

        public boolean isVisible() {
            if (!DesktopPane.me4se && (mneonic == KeyEvent.KEY_SOFTKEY1 || mneonic == KeyEvent.KEY_SOFTKEY2 || mneonic == KeyEvent.KEY_SOFTKEY3)) return false;
            return super.isVisible();
        }

//        protected void paintBorder(Graphics2D g) {
//
//            if (!focusable && disabledBorder!=null) {
//                disabledBorder.paintBorder(this, g, width, height);
//            }
//            else if (isSelected() && isFocusOwner() && selectedFocusedBorder!=null) {
//                selectedFocusedBorder.paintBorder(this, g, width, height);
//            }
//            else if (isSelected() && selectedBorder!=null) {
//                selectedBorder.paintBorder(this, g, width, height);
//            }
//            else if (isFocusOwner() && activeBorder!=null) {
//                activeBorder.paintBorder(this, g, width, height);
//            }
//            else {
//                super.paintBorder(g);
//            }
//
//        }

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
                //#mdebug
                if (al!=null) {
                    System.out.println("trying to add a ActionListener when there is already one registered");
                }
                //#enddebug
		al = l;
	}
        
        /**
         * @see javax.swing.AbstractButton#removeActionListener(java.awt.event.ActionListener) AbstractButton.removeActionListener
         */
	public void removeActionListener(ActionListener l) {
		if (al == l) { al = null; }
                //#mdebug
                else {
                    System.out.println("trying to remove a ActionListener that is not registered");
                }
                //#enddebug
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

	public boolean processKeyEvent(KeyEvent keyEvent) {
            
            	if (keyEvent.justPressedAction(Canvas.FIRE) || keyEvent.justPressedKey('\n')) {
                    
			fireActionPerformed();
			
			return true;
		}
		return false;
    
        }

        private boolean oldState;
        public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
            super.processMouseEvent(type, x, y, keys);

            if (!focusable) return;

            int cw = getWidthWithBorder();
            int ch = getHeightWithBorder();
            Border insets = getInsets();
            int cx = -insets.getLeft(); // cant use getXWithBorder();
            int cy = -insets.getTop(); // cant use getYWithBorder();

            if (type == DesktopPane.PRESSED) {
                oldState = selected;
                selected = true;
                repaint();
            }
            else if (x>=cx && x<=(cx+cw) && y>=cy && y<=(cy+ch)) {

                if (type == DesktopPane.DRAGGED && !selected) {
                    selected = true;
                    repaint();
                }
                else if (type == DesktopPane.RELEASED) {
                    selected = oldState;
                    fireActionPerformed();
                    repaint();
                }

            }
            else if (type == DesktopPane.DRAGGED && selected) {
                selected = oldState;
                repaint();
            }

        }
        
        /**
         * @see javax.swing.AbstractButton#fireActionPerformed(java.awt.event.ActionEvent) AbstractButton.fireActionPerformed
         */
	public void fireActionPerformed() {
            if (buttonGroup!=null) {
                if (!selected) {
                    setSelected(true);
                }
            }
            else {
                toggleSelection();
            }

            if (al!=null) {
                    al.actionPerformed((actionCommand!=null)?actionCommand:getText());
            }

	}

        /**
         * This method is ONLY used when we do NOT have a button group
         */
        protected void toggleSelection() {
            setSelected(false);
        }
        
//        public void setNormalBorder(Border b) {
//            normalBorder = b;
//        }
//
//	public void setActiveBorder(Border b) {
//		activeBorder = b;
//	}

	public void focusLost() {
                super.focusLost();
		
		if (useSelectButton) {
                    getWindow().removeCommand(selectButton);
                    selectButton = null;
		}
		
		repaint();
	}

	public void focusGained() {
                super.focusGained();
		
		if (useSelectButton) {
                    selectButton = new Button( (String)DesktopPane.get("selectText") );
                    selectButton.addActionListener(this);
                    selectButton.setActionCommand("select");
                    selectButton.setMnemonic(KeyEvent.KEY_SOFTKEY1);
                    getWindow().addCommand(selectButton);
		}
		
		repaint();
	}

//        protected int getCurrentForeground() {
//
//            if (!focusable) {
//                return disabledForeground;
//            }
//            else if (isSelected() && isFocusOwner()) {
//                return selectedFocusedForeground;
//            }
//            else if (isSelected()) {
//                return selectedForeground;
//            }
//            else if (isFocusOwner()) {
//                return activeForeground;
//            }
//            else {
//                return super.getCurrentForeground();
//            }
//        }

	public void setButtonGroup(ButtonGroup buttonGroup) {
		this.buttonGroup = buttonGroup;
	}
	
	public void actionPerformed(String actionCommand) {
		if(selectButton.getActionCommand().equals(actionCommand)) {
			fireActionPerformed();
		}
                //#mdebug
                else {
                    System.out.println("whats going on here??? "+actionCommand);
                }
                //#enddebug
	}

	public boolean isUseSelectButton() {
		return useSelectButton;
	}

	public void setUseSelectButton(boolean useSelectButton) {
		this.useSelectButton = useSelectButton;
	}

        public String getDefaultName() {
            return "Button";
        }

        public int getState() {
            int result = super.getState();
            if (selected) {
                result |= Style.SELECTED;
            }
            return result;
        }

        /**
         * @see javax.swing.AbstractButton#getMnemonic() AbstractButton.getMnemonic
         */
        public int getMnemonic() {
            return mneonic;
        }

        /**
         * @see javax.swing.AbstractButton#setMnemonic(int) AbstractButton.setMnemonic
         */
        public void setMnemonic(int m) {
            mneonic = m;
        }


	public int getXOnScreen() {
            if (!isVisible()) return posX;
            return super.getXOnScreen();
	}
	public int getYOnScreen() {
            if (!isVisible()) return posY;
            return super.getYOnScreen();
        }

        public void setValue(Object obj) {
            if (obj instanceof Boolean) {
                setSelected( ((Boolean)obj).booleanValue() );
            }
            else {
                super.setValue(obj);
            }
        }
        public Object getValue() {
            return new Boolean(isSelected());
        }

        public void updateUI() {
            super.updateUI();
            if (selectButton!=null) {
                selectButton.updateUI();
            }
        }

}
