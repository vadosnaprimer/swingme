package javax.microedition.lcdui;

import java.awt.Color;

public class ScmGaugeS40 extends ScmGauge {

    public ScmGaugeS40() {
        super(false);
    }
    
    public void init(Gauge gauge){
    	this.gauge = gauge;
    }

    public void paint(java.awt.Graphics g) {

		g.setColor(getFontInfo().foreground);
		g.drawRoundRect(5, 5, 115, 13, 2, 2);
				
		g.setColor(new Color(0x648ABF));

		int mx = (112* gauge.value) / gauge.maximum;

		g.fillRect(7, 7, mx, 10);        
    }
}
