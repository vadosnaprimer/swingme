package net.yura.mobile.gui.plaf.aether;

import java.util.Vector;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.plaf.LookAndFeel;

/**
 * @author Nathan
 */
public class AetherIcon extends Icon {

    int type;
    int color;
    Vector borders;

    public AetherIcon(int size, int type, Vector borders, int color) {
        width = size;
        height = size;
        this.type = type;
        this.borders = borders;
        this.color = color;
    }

    public void paintIcon(Component c, Graphics2D g, int x, int y) {

        //System.out.println(c.getName()+" icon at "+x+" "+y);

        // Draw aether borders
        if ((type == LookAndFeel.ICON_RADIO)) {
            for (int b = 0 ; b<borders.size() ; b++) {
                AetherBorderSetting border = (AetherBorderSetting) borders.elementAt(b);
                g.setColor(border.color1);
                g.fillArc(x+b, y+b, getIconWidth()-(b*2), getIconHeight()-(b*2), 0, 360);
            }
        }
        else {
            for (int b = 0 ; b<borders.size() ; b++) {
                AetherBorderSetting border = (AetherBorderSetting) borders.elementAt(b);
                AetherBorder.drawRoundedGradientRect(
                            border.color1,
                            border.color2,
                            g, x+b, y+b,
                            getIconWidth()-(2*b),
                            getIconHeight()-(2*b),
                            border.corner, border.reflection,
                            AetherBorder.CLIP_NONE,
                            AetherBorder.ORIENTATION_VERT);
            }
        }

        // Draw the checkbox tick
        if (type == LookAndFeel.ICON_CHECKBOX) {

            if (c instanceof Button) {
                Button b = (Button)c;
                if (b.isSelected()) {
                    int w = getIconWidth();
                    int h = getIconHeight();
                    g.setColor(color);
                    for (int pad=3;pad<6;pad++) {
                        g.drawLine(x+pad, y+h/2, x+w/3, y+h-pad);
                        g.drawLine(x+w/3, y+h-pad,x+w-pad,y+pad);
                    }
                }
            }

        }

        // Draw a radio icon
        if (type == LookAndFeel.ICON_RADIO) {
            int w = getIconWidth();
            int h = getIconHeight();

            if (c instanceof Button) {
                Button b = (Button)c;
                if (b.isSelected()){
                    int w2 = borders.size()+1;
                    int h2 = borders.size()+1;
                    g.setColor(color);
                    g.fillArc(x+w2, y+h2, w-(w2*2), h-(h2*2), 0, 360);
                }
            }
        }

        // Draw the combo box arrow
        if (type == LookAndFeel.ICON_COMBO) {

            g.setColor( color );

//                if ((w%2)==0) {
//                    w++;
//                }
            int top = (c.getHeight()/2)-2;
            int middle = x+(width/2);
            g.fillTriangle(middle, top+5,
                           middle-3, top,
                           middle+3, top);
        }
    }
}
