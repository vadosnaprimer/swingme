/*
 * Created on 01.11.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package javax.microedition.lcdui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.midlet.ApplicationManager;

import org.kobjects.util.Csv;
import org.me4se.scm.ScmComponent;

/**
 * @author haustein
 *
* @ME4SE INTERNAL */
class ScmIcon extends ScmComponent {

	Hashtable states = new Hashtable();
	String name;
	BufferedImage currentImage;
	String currentState;

	private ScmIcon(String name, String propStr) {
		this.name = name;
		ApplicationManager manager = ApplicationManager.getInstance();
		String[] props = Csv.decode(propStr);

		setX(Integer.parseInt(props[0]));
		setY(Integer.parseInt(props[1]));

		String seek = name + ".";
		for (Enumeration e = manager.properties.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			if (key.toLowerCase().startsWith(seek)) {
				String value = manager.getProperty(key).trim();
				String state = key.substring(seek.length());
			//	System.out.println("item state '" + state + "' for " + name + " is " + value);

				if (!value.equals("")) {
					BufferedImage image;
					try {
						image = manager.getImage(ApplicationManager.concatPath(manager.getProperty("skin"), value));
						int w = image.getWidth();
						int h = image.getHeight();
						if(w > getWidth()) setWidth(w);
						if(h > getHeight()) setHeight(h);
						states.put(state, image);
					}
					catch (Exception e1) {
						e1.printStackTrace();
					}

					// adjust size here!!

				}
			}
		}
	//	System.out.println("states: "+states);
		setState(props[2]);
	}

	public void setState(String state){
		if(state.equals(currentState)) return;
		currentState = state;
		//System.out.println("setting icon "+name+" to '"+state+"'");
		currentImage = (BufferedImage) states.get(state);
		//if(currentImage == null) System.out.println("no image!");
		repaint();
	}


	public void paint(java.awt.Graphics g){
		if(currentImage == null) return;
		
		g.drawImage(currentImage, 0, 0, null);
	}

	public static ScmIcon create(String name) {
		String propStr = ApplicationManager.getInstance().getProperty(name);
	//	System.out.println("creating icon '" + name + "'; entry found: " + propStr);
		return (propStr == null) ? null : new ScmIcon(name, propStr);
	}
}
