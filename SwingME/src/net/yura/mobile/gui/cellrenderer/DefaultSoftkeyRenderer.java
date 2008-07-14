package net.yura.mobile.gui.cellrenderer;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.Font;
/**
 * @author Yura Mamyrin
 */
public class DefaultSoftkeyRenderer extends Component implements ListCellRenderer {

    private String text;
    
    public DefaultSoftkeyRenderer() {
        setBackground(0x00FFFFFF);
    }
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean top, boolean left) {
        if (value==null) return null;
        text = value.toString();
        //text = (top?"top":"bottom") + " " + (left?"left":"right");
        height = DesktopPane.getDefaultTheme().font.getHeight();
        width = DesktopPane.getDesktopPane().getWidth()/2 - 10;
        return this;
    }

    public void paintComponent(Graphics g) {
        g.setColor(0x00000000);
        Font f = DesktopPane.getDefaultTheme().font;
        f.drawString(g, text, (width-f.getWidth(text))/2, (height-f.getHeight())/2, Graphics.TOP | Graphics.LEFT);

    }

}
