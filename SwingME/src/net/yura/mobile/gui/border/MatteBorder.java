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

package net.yura.mobile.gui.border;

import java.util.Random;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.Properties;

/**
 * @author Yura Mamyrin
 * @see javax.swing.border.MatteBorder
 */
public class MatteBorder extends EmptyBorder {

    private Icon activeimage;
    private int imageTop;
    private int imageBottom;
    private int imageRight;
    private int imageLeft;
    private boolean back;
    private int color;

    /**
     * @param top the top inset of the border
     * @param left the left inset of the border
     * @param bottom the bottom inset of the border
     * @param right the right inset of the border
     * @param color the color rendered for the border
     * @see javax.swing.border.MatteBorder#MatteBorder(int, int, int, int, java.awt.Color) MatteBorder.MatteBorder
     */
    	public MatteBorder(int top, int left, int bottom, int right,int color) {
            super(top,left,bottom,right);
            this.color = color;
        }

        /**
         * @param top the top inset of the border
         * @param left the left inset of the border
         * @param bottom the bottom inset of the border
         * @param right the right inset of the border
         * @param tileIcon the icon to be used for tiling the border
         * @see javax.swing.border.MatteBorder#MatteBorder(int, int, int, int, javax.swing.Icon) MatteBorder.MatteBorder
         */
        public MatteBorder(int top, int left, int bottom, int right, Icon tileIcon) {
              super(top,left,bottom,right);
              activeimage = tileIcon;
              back=true;
        }

        /**
         * This wil create a border with a Image skin
         * The skin file is a text file with the following values
         * <ul>
         * <li>active=skin1.png</li>
         *
         * <li>top=23</li>
         * <li>bottom=14</li>
         * <li>right=12</li>
         * <li>left=12</li>
         *
         * <li>itop=23</li>
         * <li>ibottom=14</li>
         * <li>iright=12</li>
         * <li>ileft=12</li>
         *
         * <li>back=Y (Optional)</li>
         * <li>color=FF0000 (Optional)</li>
         * </ul>
         * @param name name of file to load skin from
         */
        public static MatteBorder load(String name) throws Exception {

            int top,bottom,left,right;
            Icon activeimage;
            int imageTop;
            int imageBottom;
            int imageRight;
            int imageLeft;
            boolean back;
            int color;

            Properties newborder = new Properties();

            newborder.load( Midlet.getResourceAsStream(name) );

            String imageName = newborder.getProperty("active");

            if (name.charAt(0)=='/' && imageName.charAt(0)!='/') {
                    imageName = "/"+imageName;
            }

            activeimage = new Icon(imageName);
            imageTop=Integer.parseInt(newborder.getProperty("itop"));
            imageBottom=Integer.parseInt(newborder.getProperty("ibottom"));
            imageRight=Integer.parseInt(newborder.getProperty("iright"));
            imageLeft=Integer.parseInt(newborder.getProperty("ileft"));

            top=Integer.parseInt(newborder.getProperty("top"));
            bottom=Integer.parseInt(newborder.getProperty("bottom"));
            right=Integer.parseInt(newborder.getProperty("right"));
            left=Integer.parseInt(newborder.getProperty("left"));

            String b =newborder.getProperty("back");

            back = (b!=null && b.equals("Y"));

            String c =newborder.getProperty("color");

            color = Style.NO_COLOR;
            if (c!=null) {
                color = Integer.parseInt(c, 16);
                if (c.length()==6) { // add alpha
                    color = color | 0xFF000000;
                }
            }

            return new MatteBorder(activeimage,top,left,bottom,right,
                    imageTop,imageLeft,imageBottom,imageRight,back,color);

	}

        public MatteBorder(Icon i, int top, int left, int bottom, int right, int t, int l, int b, int r, boolean back, int color) {
            super(top,left,bottom,right);
            activeimage = i;
            imageTop=t;
            imageLeft=l;
            imageBottom=b;
            imageRight=r;
            this.back=back;
            this.color=color;
        }

        /**
         * @return the icon used for tiling the border or null if a solid color is being used
         * @see javax.swing.border.MatteBorder#getTileIcon() MatteBorder.getTileIcon
         */
        public Icon getTileIcon() {
            return activeimage;
        }



        /**
         * @param c
         * @param g
         * @param width
         * @param height
         * @see javax.swing.border.MatteBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int) MatteBorder.paintBorder
         */
        public void paintBorder(Component c, Graphics2D g, int width,int height) {

            if (activeimage==null) {
                // color can NOT be Style.NO_COLOR here
                g.setColor(color);
                if (back) {
                    g.fillRect(-left, -top, width+left+right, height+top+bottom);
                }
                else {
                    g.fillRect(-left, -top, width+left+right, top); // top
                    g.fillRect(-left, height, width+left+right, bottom); // bottom
                    g.fillRect(-left, 0, left, height ); // left
                    g.fillRect(width, 0, right, height ); // right
                }
            }
            // we will have a image, but its not loaded yet or we can not use it for some reason
            else if (activeimage.getImage() == null) {
                if (color!=Style.NO_COLOR) {
                    g.setColor(color);
                    g.fillRect(-left, -top, width+left+right, height+top+bottom);
                }
            }
            else {

                int imageWidth=activeimage.getIconWidth();
                int imageHeight=activeimage.getIconHeight();

                int topDiff = imageTop-top;
                int rightDiff= imageRight-right;
                int leftDiff= imageLeft-left;
                int bottomDiff= imageBottom-bottom;

                Image image = activeimage.getImage();

                // on blackberry we can not draw a image of 0 width or 0 height, so we have to check
                if (imageTop>0) {
                    if (imageLeft>0) {
                        g.drawRegion(image, 0,  0, imageLeft, imageTop, Sprite.TRANS_NONE, -left, -top);
                    }
                    if (imageRight>0) {
                        g.drawRegion(image, imageWidth-imageRight,  0, imageRight, imageTop, Sprite.TRANS_NONE, width-rightDiff, -top);
                    }
                }
                if (imageBottom>0) {
                    if (imageLeft>0) {
                        g.drawRegion(image, 0,  imageHeight-imageBottom, imageLeft, imageBottom, Sprite.TRANS_NONE, -left, height-bottomDiff);
                    }
                    if (imageRight>0) {
                        g.drawRegion(image, imageWidth-imageRight,  imageHeight-imageBottom, imageRight, imageBottom, Sprite.TRANS_NONE, width-rightDiff, height-bottomDiff);
                    }
                }

                g.drawImage(image,imageLeft,0,imageWidth-imageRight-imageLeft,imageTop,
                        leftDiff,-top,width-leftDiff-rightDiff,imageTop,
                        Sprite.TRANS_NONE); // top line

                g.drawImage(image,imageLeft,imageHeight-imageBottom,imageWidth-imageRight-imageLeft,imageBottom,
                        leftDiff,height-bottomDiff,width-leftDiff-rightDiff,imageBottom,
                        Sprite.TRANS_NONE); // bottom line

                g.drawImage(image,0,imageTop,imageLeft,imageHeight-imageTop-imageBottom,
                        -left,topDiff,imageLeft,height-topDiff-bottomDiff,
                        Sprite.TRANS_NONE); // left

                g.drawImage(image,imageWidth-imageRight,imageTop,imageRight,imageHeight-imageTop-imageBottom,
                        width-rightDiff,topDiff,imageRight,height-topDiff-bottomDiff,
                        Sprite.TRANS_NONE); // right

                if (!back && color==Style.NO_COLOR) {

                    // TODO matte tileing the border with the whole image
                    // check that if there is NO bckground that the Diff values are not negative
                    // as if they r negative we need to fill in those aras
                }

                else if (back) {

                    int src_w = imageWidth-imageRight-imageLeft;
                    int src_h = imageHeight-imageTop-imageBottom;
                    int dist_w = width-leftDiff-rightDiff;
                    int dist_h = height-topDiff-bottomDiff;

                    // to make themeing more flexable we will center the image if we do not need to tile it
                    if (src_w>=dist_w&&src_h>=dist_h) {
                        g.drawRegion(image, imageLeft+(src_w-dist_w)/2, imageTop+(src_h-dist_h)/2, dist_w, dist_h, Sprite.TRANS_NONE, leftDiff,topDiff);
                    }
                    else {
                        //#debug debug
                        Logger.debug("filling background with tiled image!");
                        g.drawImage(image,imageLeft,imageTop,src_w,src_h,leftDiff,topDiff,dist_w,dist_h,Sprite.TRANS_NONE); // centre
                    }
                }

                else if (color!=Style.NO_COLOR) {
                    boolean fillsides = (imageHeight-imageTop-imageBottom) == 0;
                    boolean filltop = (imageWidth-imageRight-imageLeft) == 0;
                    g.setColor(color);
                    g.fillRect(fillsides?-left:leftDiff,filltop?-top:topDiff,width-(fillsides?-(left+right):leftDiff+rightDiff),height-(filltop?-(top+bottom):topDiff+bottomDiff));
                }

                //#mdebug info
                else {
                    Logger.info("imagePainter has image but does not have anything to fill the area, this is prob bad");
                }
                if (DesktopPane.debug) {
                    Random r = new Random();
                    g.setColor( r.nextInt() );
                    g.drawLine(-left, topDiff, width+right,topDiff);
                    g.setColor( r.nextInt() );
                    g.drawLine(-left, height-bottomDiff, width+right,height-bottomDiff);
                    g.setColor( r.nextInt() );
                    g.drawLine(leftDiff, -top, leftDiff,height+bottom);
                    g.setColor( r.nextInt() );
                    g.drawLine(width-rightDiff, -top, width-rightDiff,height+bottom);
                }
                //#enddebug
            }

        }


    public boolean isBorderOpaque() {
        return back || (activeimage!=null && color!=Style.NO_COLOR);
    }

    public void setColor(int c) {
        color =c;
    }
}


