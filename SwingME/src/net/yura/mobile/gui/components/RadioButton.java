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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JRadioButton
 */
public class RadioButton extends Button {

	protected Image selectedImage;
        protected Image disabledImage;
        protected Image disabledSelectedImage;
	
        /**
         * @see javax.swing.JRadioButton#JRadioButton() JRadioButton.JRadioButton
         */
        public RadioButton() {
        }

        /**
         * @param label
         * @see javax.swing.JRadioButton#JRadioButton(java.lang.String) JRadioButton.JRadioButton
         */
	public RadioButton(String label) {
                super(label);
	}

        /**
         * @param string
         * @param b
         * @see javax.swing.JRadioButton#JRadioButton(java.lang.String, boolean) JRadioButton.JRadioButton
         */
        public RadioButton(String string, boolean b) {
            super(string);
            setSelected(b);
        }

        public void fireActionPerformed() {
            super.fireActionPerformed();
            repaint();
        }
        
        /**
         * @see javax.swing.AbstractButton#getSelectedIcon() AbstractButton.getSelectedIcon
         */
	public Image getSelectedIcon() {
		return selectedImage;
	}

        /**
         * @see javax.swing.AbstractButton#setSelectedIcon(javax.swing.Icon) AbstractButton.setSelectedIcon
         */
	public void setSelectedIcon(Image selectedImage) {
		this.selectedImage = selectedImage;
	}

        /**
         * @see javax.swing.AbstractButton#setDisabledIcon(javax.swing.Icon) AbstractButton.setDisabledIcon
         */
        public void setDisabledIcon(Image disabledIcon) {
            disabledImage = disabledIcon;
        }
        
        /**
         * @see javax.swing.AbstractButton#getDisabledIcon() AbstractButton.getDisabledIcon
         */
        public Image getDisabledIcon() {
            return disabledImage;
        }
        
        /**
         * @see javax.swing.AbstractButton#setDisabledSelectedIcon(javax.swing.Icon) AbstractButton.setDisabledSelectedIcon
         */
        public void setDisabledSelectedIcon(Image disabledSelectedIcon) {
            disabledSelectedImage = disabledSelectedIcon;
        }
        
        /**
         * @see javax.swing.AbstractButton#getDisabledSelectedIcon() AbstractButton.getDisabledSelectedIcon
         */
        public Image getDisabledSelectedIcon() {
            return disabledSelectedImage;
        }
        
        public String getName() {
            return "RadioButton";
        }
        
        public void updateUI() {
                super.updateUI();
                
                Style st = DesktopPane.getDefaultTheme(this);

                icon = (Image)st.getProperty("icon", Style.ALL);
                selectedImage = (Image)st.getProperty("icon", Style.SELECTED);
                disabledImage = (Image)st.getProperty("icon", Style.DISABLED);
                disabledSelectedImage = (Image)st.getProperty("icon", Style.DISABLED | Style.SELECTED);

        }

    protected int getIconWidth() {
        
        if (icon==null) {
            return getFont().getHeight()-1;
            // the -1 is ONLY there so it looks better on the emulator
        }
        else {
            return super.getIconWidth();
        }
    }

    protected int getIconHeight() {
        if (icon==null) {
            return getFont().getHeight()-1;
            // the -1 is ONLY there so it looks better on the emulator
        }
        else {
            return super.getIconHeight();
        }
    }

    protected void paintIcon(Graphics g, int x, int y) {

        if (icon==null) {

            g.setColor(foreground);
            
            int w = getIconWidth();
            int h = getIconHeight();

            g.drawArc(x, y, w-1, h-1, 0, 360);

            if (isSelected()){

                int w2 = w/4;
                int h2 = h/4;
                g.fillArc(x+w2, y+h2, w-(w2*2), h-(h2*2), 0, 360);
            }
            
        }
        else {
            if (isSelected() && !selectable && disabledSelectedImage!=null) {
                g.drawImage(disabledSelectedImage, x, y, Graphics.TOP | Graphics.LEFT);
            }
            else if (isSelected() && selectedImage!=null) {
                g.drawImage(selectedImage, x, y, Graphics.TOP | Graphics.LEFT);
            }
            else if (!selectable && disabledImage!=null) {
                g.drawImage(disabledImage, x, y, Graphics.TOP | Graphics.LEFT);
            }
            else {
                super.paintIcon(g, x, y);
            }
        }

    }
        
}
