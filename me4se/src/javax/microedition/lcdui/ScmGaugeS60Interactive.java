package javax.microedition.lcdui;

import java.awt.Color;

public class ScmGaugeS60Interactive extends ScmGauge {

    public ScmGaugeS60Interactive() {
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

		g.setColor(Color.black);
		
		int mx = (130 * gauge.value) / gauge.maximum;
		
		g.drawLine(20, getHeight()/2, 150, getHeight()/2);
		g.setColor(Color.blue);
		g.fillOval(17+mx, getHeight()/2 - 3, 6, 6);
		g.setColor(Color.black);	
		g.drawOval(17+mx, getHeight()/2 - 3, 6, 6);
		
		getFontInfo().drawString(g, gauge.value+"", 15, getHeight()/2 - 12);
		getFontInfo().drawString(g, "0", 15, getHeight()/2 + 23);
		getFontInfo().drawString(g, gauge.maximum+"", 135, getHeight()/2 + 23);
    }
}
