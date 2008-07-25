package net.yura.mobile.gui.cellrenderer;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.plaf.Style;
/**
 * @author Yura Mamyrin
 */
public class DefaultSoftkeyRenderer extends Component implements ListCellRenderer {

    private String text;
    private Font font;
    
    public DefaultSoftkeyRenderer() {
    }
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean top, boolean left) {
        if (value==null) return null;
        text = value.toString();
        //text = (top?"top":"bottom") + " " + (left?"left":"right");
        
        return this;
    }

    public void paintComponent(Graphics g) {
        g.setColor(0x00000000);
        font.drawString(g, text, (width-font.getWidth(text))/2, (height-font.getHeight())/2, Graphics.TOP | Graphics.LEFT);

    }
    public void workoutSize() {
        
        height = font.getHeight();
        width = DesktopPane.getDesktopPane().getWidth()/2 - 10;
        
    }

    public String getName() {
        return "SoftkeyRenderer";
    }
    public void updateUI() {
        super.updateUI();
        font = DesktopPane.getDefaultTheme(this).getFont(Style.ALL);
    }

}
