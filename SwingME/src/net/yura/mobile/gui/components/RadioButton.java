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
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JRadioButton
 */
public class RadioButton extends Button {

	protected Icon selectedImage;
        protected Icon disabledImage;
        protected Icon disabledSelectedImage;
	
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


        protected void toggleSelection() {
            setSelected(true);
        }

        /**
         * @see javax.swing.AbstractButton#getSelectedIcon() AbstractButton.getSelectedIcon
         */
	public Icon getSelectedIcon() {
		return selectedImage;
	}

        /**
         * @see javax.swing.AbstractButton#setSelectedIcon(javax.swing.Icon) AbstractButton.setSelectedIcon
         */
	public void setSelectedIcon(Icon selectedImage) {
		this.selectedImage = selectedImage;
	}

        /**
         * @see javax.swing.AbstractButton#setDisabledIcon(javax.swing.Icon) AbstractButton.setDisabledIcon
         */
        public void setDisabledIcon(Icon disabledIcon) {
            disabledImage = disabledIcon;
        }
        
        /**
         * @see javax.swing.AbstractButton#getDisabledIcon() AbstractButton.getDisabledIcon
         */
        public Icon getDisabledIcon() {
            return disabledImage;
        }
        
        /**
         * @see javax.swing.AbstractButton#setDisabledSelectedIcon(javax.swing.Icon) AbstractButton.setDisabledSelectedIcon
         */
        public void setDisabledSelectedIcon(Icon disabledSelectedIcon) {
            disabledSelectedImage = disabledSelectedIcon;
        }
        
        /**
         * @see javax.swing.AbstractButton#getDisabledSelectedIcon() AbstractButton.getDisabledSelectedIcon
         */
        public Icon getDisabledSelectedIcon() {
            return disabledSelectedImage;
        }
        
        public String getName() {
            return "RadioButton";
        }
        
        public void updateUI() {
                super.updateUI();
                
                Style st = DesktopPane.getDefaultTheme(this);

                icon = (Icon)st.getProperty("icon", Style.ALL);
                selectedImage = (Icon)st.getProperty("icon", Style.SELECTED);
                disabledImage = (Icon)st.getProperty("icon", Style.DISABLED);
                disabledSelectedImage = (Icon)st.getProperty("icon", Style.DISABLED | Style.SELECTED);

        }

    protected void paintIcon(Graphics2D g, int x, int y) {

        if (isSelected() && !focusable && disabledSelectedImage!=null) {
            disabledSelectedImage.paintIcon(this, g, x, y);
        }
        else if (isSelected() && selectedImage!=null) {
            selectedImage.paintIcon(this, g, x, y);
        }
        else if (!focusable && disabledImage!=null) {
            disabledImage.paintIcon(this, g, x, y);
        }
        else {
            super.paintIcon(g, x, y);
        }

    }
        
}
