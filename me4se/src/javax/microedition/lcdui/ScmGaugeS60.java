package javax.microedition.lcdui;

import java.awt.Color;

public class ScmGaugeS60 extends ScmGauge {

    public ScmGaugeS60() {
        super(false);
    }
    
    public void init(Gauge gauge){
    	this.gauge = gauge;
    }

    public void paint(java.awt.Graphics g) {

		g.setColor(new Color(0x88BBEE));
		g.fillRect(9, 25, 120, 8);
				
		g.setColor(new Color(0x001199));

		int mx = (120 * gauge.value) / gauge.maximum;
		
		g.fillRect(9, 22, mx, 8);        
        g.setColor(Color.black);
        
        int value = 100 * gauge.value / gauge.maximum;
                
		getFontInfo().drawString(g, value+"%", 133, 34);
    }
}
