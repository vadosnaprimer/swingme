package javax.microedition.lcdui;

import java.awt.Color;

import org.me4se.impl.lcdui.FontInfo;

public class ScmGaugeS40Interactive extends ScmGauge {

    public ScmGaugeS40Interactive() {
        super(true);
    }
    
    public void init(Gauge gauge){
    	this.gauge = gauge;
    }

    public void paint(java.awt.Graphics g) {
     
        if (getFocusable() && hasFocus()) {
			g.setColor(new Color(0x707070));
			g.drawRect(2, 2, getWidth()-5, getHeight()-5);
        }

		g.setColor(new Color(0x000000));
		g.drawRoundRect(5, 5, 115, 13, 2, 2);
				
		g.setColor(new Color(0x648ABF));

		int mx = (112* gauge.value) / gauge.maximum;

		g.fillRect(7, 7, mx, 10);        
		
		FontInfo fi = FontInfo.getFontInfo("font.monospace.plain.small");
		
		g.setColor(new Color(0x000000));		
		int w = getFontInfo().font.stringWidth("["+gauge.value + "/" + gauge.maximum+"]");
		fi.drawString(g, "["+gauge.value + "/" + gauge.maximum+"]", this.getWidth()/2 - w/2, 31);
		//getFontInfo().drawString(g, "0", 15, getHeight()/2 + 23);
		//getFontInfo().drawString(g, gauge.maximum+"", 135, getHeight()/2 + 23);
    }
}
