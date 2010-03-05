/*
 * Created on Feb 22, 2006 by Stefan Haustein
 */
package org.me4se.launcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.ApplicationManager;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.me4se.JadFile;

public class MIDletLauncher extends MIDlet implements CommandListener {

	File currentDir = new File(".");
	List fileList;
	Display display;
	Command cmdBack = new Command("Back", Command.BACK, 0);
	Command cmdExit = new Command("Exit", Command.EXIT, 0);
	int level = 0;
	
    
    private static final Class[] parameters = new Class[] {URL.class};

    public static void addFile(String s) throws IOException {
        addFile(new File(s));
    }

    public static void addFile(File f) throws IOException {
        addURL(f.toURL());
    }

    public static void addURL(URL u) throws IOException {
        System.out.println("Adding URL to CP: "+u);
        URLClassLoader sysloader=(URLClassLoader)ClassLoader.getSystemClassLoader();
        Class sysclass=URLClassLoader.class;
        try {
            Method method=sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] {u});
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }
    }
    
    
    
    
	boolean checkDir(File dir){
		if(!dir.isDirectory()) return dir.getName().endsWith(".jad");
		File[] files = dir.listFiles();
		for(int i = 0; i < files.length; i++){
			if(checkDir(files[i])) return true;
		}
		return false;
	}
	
	public void open(String target){
		System.out.println("opening: "+this);
		
		File toOpen = new File(currentDir, target);
		if(toOpen.isDirectory()){
			fileList = new List(toOpen.getAbsoluteFile().getName(), List.IMPLICIT);
			File[] files = toOpen.listFiles();
			
			for(int i = 0; i < files.length; i++){
				if(checkDir(files[i])) {
					fileList.append(files[i].getName(), null);
				}
			}
			fileList.setCommandListener(this);
			
			display.setCurrent(fileList);
			currentDir = toOpen;

			if(fileList.size() == 1 && fileList.getString(0).endsWith(".jad")){
				open(fileList.getString(0));
			}
			else{
				fileList.addCommand(cmdExit);
				
				if(level > 0){
					fileList.addCommand(cmdBack);
				}
			}
		}
		else {
			openJad(toOpen);
		}
	}
	

	void openJad(File file){
		
		JadFile jadFile = new JadFile();
		File dir = file.getParentFile();
		
		try{
			jadFile.load("file:///"+file.getAbsolutePath());
		
			java.util.Properties props = System.getProperties();
			props.put("rms.home", dir);
			System.setProperties(props);

			String fileName = jadFile.getValue("MIDlet-Jar-URL");
			int lastSlash = fileName.lastIndexOf('/');
			if(lastSlash != -1){
				fileName = fileName.substring(lastSlash+1);
			}
			
            addFile(new File(dir, fileName));
          
            //ApplicationManager.manager.jadFile = jadFile;
            
            ApplicationManager.getInstance().launch(jadFile, 0);

            /*
            try {
                if (jadFile.getMIDletCount() != 1) {
                    ApplicationManager.manager.startMIDlet("org.me4se.impl.MIDletChooser");
                }
                else {
                    ApplicationManager.manager.startMIDlet(jadFile.getMIDlet(1).getClassName());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
			*/
			
		}
		catch(Exception e){
			e.printStackTrace();
		}

	

	}
	
	
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}
	
	
	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		open(".");
	}

	
	public void commandAction(Command cmd, Displayable d) {
		if(cmd == cmdBack){
			open("..");
			level--;
		}
		else if(cmd == cmdExit){
			notifyDestroyed();
		}
		else{
			level++;
			open(fileList.getString(fileList.getSelectedIndex()));
		}
	}
	
	
	
	
	
}
