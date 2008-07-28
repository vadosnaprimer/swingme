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
	protected Image nonSelectedImage;
	
        public RadioButton() {
        }
            
	public RadioButton(String label) {
		this();
                setText(label);
	}

    public RadioButton(String string, boolean b) {
        this(string);
        setSelected(b);
    }
	
        public RadioButton(String label,Image a,Image b) {
            this(label);
            selectedImage=a;
            nonSelectedImage=b;
        }
        
	protected int getCombinedWidth() {
            
            int w = super.getCombinedWidth();
            if (w!=0) { w=w+gap; }
            
            if (nonSelectedImage!=null) {
                w = w + nonSelectedImage.getWidth();
            }
            else {
                w = w + getFont().getHeight();
            }
	    return w;
	}
        
        protected int getCombinedHeight() {
              
             int h = super.getCombinedHeight();
             if (nonSelectedImage!=null && nonSelectedImage.getHeight() > h) {
                 h = nonSelectedImage.getHeight();
             }
             return h;
        }

        public void paintComponent(Graphics g){

		g.setColor( getBorderColor() );

		if (isSelected()){
			if (selectedImage == null ){

                                paintExtra(g);
				
			}
			else{
				g.drawImage(selectedImage, padding, (height-selectedImage.getHeight())/2 , 0 );
			}
		}
		else{
			if (nonSelectedImage == null){

                                paintExtra(g);

			}
			else{
				g.drawImage(nonSelectedImage, padding, (height-nonSelectedImage.getHeight())/2 , 0 );
			}
		}
		
                int offset = gap;
            
                if (nonSelectedImage!=null) {
                    offset = offset + nonSelectedImage.getWidth();
                }
                else {
                    offset = offset + getFont().getHeight();
                }
                
		g.translate(+ offset, 0);
		super.paintComponent(g);
		g.translate(- offset, 0);

        }
        
	public void paintExtra(Graphics g){

                int size = getFont().getHeight()-1;
                // the -1 is ONLY there so it looks better on the emulator
                
                int x = padding;
                int y = (height-size)/2;
                
                g.drawArc(x, y, size-1, size-1, 0, 360);

		if (isSelected()){

                    int size2 = size/4;      
	            g.fillArc(x+size2, y+size2, size-(size2*2), size-(size2*2), 0, 360);
                }
	}
	
        public void fireActionPerformed() {
            super.fireActionPerformed();
            repaint();
        }
        
	public Image getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(Image selectedImage) {
		this.selectedImage = selectedImage;
	}

	public Image getNonSelectedImage() {
		return nonSelectedImage;
	}

	public void setNonSelectedImage(Image nonSelectedImage) {
		this.nonSelectedImage = nonSelectedImage;
	}
	
    public String getName() {
        return "RadioButton";
    }
        
}