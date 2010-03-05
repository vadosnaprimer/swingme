/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package javax.microedition.lcdui;


class TickerThread extends Thread {

	Display display;
	int step; // pos if ticker was active

	TickerThread(Display display){
		this.display = display;
		setName("ME4SE ticker thread");
	}

	public void run() {
		
		while(true){
			try{
				Thread.sleep(200);		
			}
			catch(InterruptedException e){
			}
			
			Displayable curr = display.current;

			if(curr instanceof Screen) {
				Screen scr = (Screen) curr;
				
				if(scr.ticker != null){
					String t = scr.ticker.getString();
					t = t.substring(step % t.length()) + " --- " + t;
					step ++;

					if(scr.tickerComponent.location == null){
						scr.titleComponent.setText(scr.title + "  "+t);
						scr.titleComponent.repaint();
					}
					else {
						scr.tickerComponent.setText(t);
						scr.tickerComponent.setInvisible(false);
						scr.tickerComponent.repaint();
					}
				}
				else if(step != 0){
					step = 0;
					if(scr.tickerComponent.location == null){
						scr.titleComponent.setText(scr.title);
						scr.titleComponent.repaint();
					}
					else{
						scr.tickerComponent.setInvisible(true);
						scr.container.repaint();
					}
						
				}
					
			}
		}
	}
}
