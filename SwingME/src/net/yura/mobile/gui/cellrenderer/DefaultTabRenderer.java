package net.yura.mobile.gui.cellrenderer;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.border.TabBorder;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;

/**
 *
 * @author ymamyrin
 */
public class DefaultTabRenderer extends Label implements ListCellRenderer {

    private Border focus;
    private Border none;
    private Border open;
    
    public DefaultTabRenderer(int a) {
	super("");
        
        TabBorder tb = new TabBorder(a);
        
        if (a==Graphics.TOP || a==Graphics.BOTTOM) {
        
            none = new CompoundBorder(
                        new EmptyBorder(1, 0, 1, 0),
                        new CompoundBorder(
                            tb,
                            new EmptyBorder(0, 1, 0, 1))
                    );
        }
        else {
            
             none = new CompoundBorder(
                        new EmptyBorder(0, 1, 0, 1),
                        new CompoundBorder(
                            tb,
                            new EmptyBorder(1, 0, 1, 0))
                    );
            
            
        }
        focus = new CompoundBorder(tb, new LineBorder(0x00000000,-1, 1,false, Graphics.DOTTED));
        open = new CompoundBorder(tb, new EmptyBorder(1, 1, 1, 1));
        
        setBorder(border);
        setPadding(1);
    }
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        setValue(value);

        setBorder( cellHasFocus?focus:(isSelected?open:none) );
        
        doLayout();
        
        return this;
    }

}
