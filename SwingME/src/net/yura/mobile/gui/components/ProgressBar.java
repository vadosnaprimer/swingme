package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.RootPane;

public class ProgressBar extends Component {

	protected boolean go;
	protected int wait;
	
	protected int loaded;
	protected int loading;
	
	
	// goes from 0 to loaded (inclusive)
	public ProgressBar() {
		
		
		wait = 50;
	}
	
	public void paintComponent(Graphics g) {

		g.drawRect(0, 0, (width*loading)/loaded, height);

	}

	public void animate() {
		
		loading = 0;
		
		while (go) {
			
			repaint();
			wait(wait);
			
			if (loading == loaded) { loading=0; }
			else { loading ++; }
		}
		
	}

	public void start() {
		
		go = true;
		RootPane.getRootPane().animateComponent(this);
	}

	public void stop() {
		
		go = false;
	}
	
}
