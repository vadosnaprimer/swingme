// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
// STATUS: API Complete
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

package javax.microedition.lcdui;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;

import javax.microedition.midlet.ApplicationManager;

import org.me4se.impl.Log;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public abstract class Canvas extends Displayable {

    
    /** Helper to supress center command assignment */
    
	Command centerCommand;

    /**
	 * @API MIDP-1.0
	 */
    public static final int DOWN = 6;

	/**
	 * @API MIDP-1.0
	 */
    public static final int LEFT = 2;

	/**
	 * @API MIDP-1.0
	 */
    public static final int RIGHT = 5;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int UP = 1;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int FIRE = 8;
    
	/**
     * @API MIDP-1.0
     */
	public static final int GAME_A = 9;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int GAME_B = 10;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int GAME_C = 11;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int GAME_D = 12;

	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM0 = 48;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM1 = 49;
	
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM2 = 50;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM3 = 51;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM4 = 52;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM5 = 53;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM6 = 54;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM7 = 55;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM8 = 56;
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_NUM9 = 57;

	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_POUND = '#';
    
	/**
	 * @API MIDP-1.0
	 */
    public static final int KEY_STAR = '*';

    ScmCanvas component = new ScmCanvas(this);
    boolean hasPointerEvents = !"false".equalsIgnoreCase(ApplicationManager.getInstance().getProperty("touch_screen"));;
    
    /**
     * @ME4SE INTERNAL
     */
    protected boolean _fullScreenMode =  ApplicationManager.getInstance().getFlag("canvasHideSoftButtons");

    	Image videoFrameImage;
	
	int videoFrameX;
	int videoFrameY;
    
    int oldH = getHeight();
    int oldW = getWidth();
    
    
	/**
	 * @API MIDP-1.0
	 */
    protected Canvas() {
        Display.check();
        
        container = new ScmDisplayable(this);
        container.setMain(component, false);
        

        if(ApplicationManager.getInstance().getFlag("SiemensCK")){
            centerCommand = new Command("CenterBlocker", Command.SCREEN, -15); // label must not be empty to avoid filtering
            centerCommand.type = Command.CENTERBLOCKER;
            addCommand(centerCommand);
        }    
    }

	/**
	 * @ME4SE INTERNAL
	 */
    void _showNotify() {
        component.requestFocus();
        showNotify();
        repaint(); // sets repaintPending to true
    }

	  /** Internal method that allows the VideoPlayer to exchange the image and to request a screen update */
    
	public void _setVideoControlData(int x, int y, Image img) {
		videoFrameX = x;
		videoFrameY = y;
		videoFrameImage = img;
		repaint();
	}
	

	/**
	 * @API MIDP-1.0
	 */
    protected abstract void paint(Graphics g);

	/**
	 * @API MIDP-1.0
	 */
    public int getGameAction(int keyCode) {
    		return ApplicationManager.getInstance().getGameAction(keyCode);
    }

	/**
	 * @API MIDP-1.0
	 */
    public int getKeyCode(int game) {
    	String name;
    	switch(game){
    	case UP: name = "UP"; break;
    	case DOWN: name = "DOWN"; break;
    	case LEFT: name = "LEFT"; break;
    	case RIGHT: name = "RIGHT"; break;
    	case FIRE: name = "SELECT"; break;
    	default:
    		return game;
    	}
    	return ApplicationManager.getInstance().getDeviceKeyCode(name);
    }

	/**
	 * @API MIDP-1.0
	 */
    public String getKeyName(int keyCode) {
        return (keyCode > 0)
            ? ("" + (char) keyCode)
            : KeyEvent.getKeyText(-keyCode);
    }

	/**
	 * @API MIDP-1.0
	 */
    public int getHeight() {
        return component.getHeight();
    }

	/**
	 * @API MIDP-1.0
	 */
    public int getWidth() {
        return component.getWidth();
    }

	/**
	 * @API MIDP-1.0
	 */
    public boolean hasPointerEvents() {
        return hasPointerEvents;
    }

    /**
     * @API MIDP-1.0
     */
    public boolean hasPointerMotionEvents() {
        return hasPointerEvents();
    }

	/**
	 * @API MIDP-1.0
	 */
    public boolean hasRepeatEvents() {
        return true;
    }

	/**
	 * @API MIDP-1.0
	 */
    public boolean isDoubleBuffered() {
        return true;
    }

	/**
	 * @API MIDP-1.0
	 */
    protected void hideNotify() {
    }

	/*
	 * @ME4SE INTERNAL
    void handleCommand (Command cmd, Item item) {
        if (cmd == ScmDisplayable.GAME_COMMAND1) 
            keyPressed(-1);
        else if (cmd == ScmDisplayable.GAME_COMMAND2) {
            keyPressed(-4);
        }
        else super.handleCommand (cmd, item);
    }
     */



	/**
	 * @API MIDP-1.0
	 */
    protected void keyPressed(int code) {
    }

	/**
	 * @API MIDP-1.0
	 */
    protected void keyReleased(int code) {
    }

	/**
	 * @API MIDP-1.0
	 */
    protected void keyRepeated(int code) {
    }

	/**
	 * @API MIDP-1.0
	 */
    protected void pointerDragged(int x, int y) {
    }

	/**
	 * @API MIDP-1.0
	 */
    protected void pointerPressed(int x, int y) {
    }

	/**
	 * @API MIDP-1.0
	 */
    protected void pointerReleased(int x, int y) {
    }
	/**
	 * @API MIDP-1.0
	 */
    public void repaint() {
        component.repaintPending = true;
        component.repaint();
    }

	/**
	 * @API MIDP-1.0
	 */
    public void serviceRepaints() {
        
        Log.log(Log.DRAW_EVENTS, "serviceRepaint() entered");
        
        // dont 

        if (component.repaintPending){
            if(EventQueue.isDispatchThread()){
                synchronized (component.repaintLock) {
                    java.awt.Graphics g = component.getGraphics();
                    if (g != null)
                        component.paint(g);
                }
            }
            else {
            		for(int i = 0; i < 20; i++){
            			if(!component.repaintPending){
            				break;
            			}
                    try{
                        Thread.sleep(50);
                    }   
                    catch(InterruptedException e){
                    }
                }
            }
        }
        Log.log(Log.DRAW_EVENTS, "serviceRepaint() left");
    }

	/**
	 * @API MIDP-1.0
	 */
    public void repaint(int x, int y, int w, int h) {
        component.repaintPending = true;
        component.repaint(x, y, w, h);
    }

	/**
	 * @API MIDP-1.0
	 */
    protected void showNotify() {
    }
    
	/**
     * Called by ME4SE in order to 
     * notify applications about size changes. 
     * Default implementation is empty(!)
     * 
	 * @API MIDP-2.0
	 */

    protected void sizeChanged(int w, int h) {
    }
    
    /**
     * Controls whether the Canvas is in full-screen mode or in normal mode.
     * @param mode true if the Canvas is to be in full screen mode, false otherwise
     * 
     * @API MIDP-2.0
     */
	public void setFullScreenMode(boolean mode) {
        if(mode == _fullScreenMode) return;
        oldW = getWidth();
        oldH = getHeight();
		_fullScreenMode = mode;
        container.init();
        container.setMain(component, false);
        container.doLayout();
		container.updateButtons();        
	}
}