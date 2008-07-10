/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui.components;

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.GridLayout;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JFrame
 */
public class Window extends Component implements ActionListener {

	private Panel contentPane;
	private Panel glasspanecomponent;
        
        private CommandButton[] panelCmds;
        private ActionListener actionListener;
        
        public ActionListener getActionListener() {
		return actionListener;
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
        
        public Window() {
            this(null);
        }
        
	public Window(Border b) {

		contentPane = new Panel();
                contentPane.setLayout( new BorderLayout() );
                
                contentPane.setOwnerAndParent(this,null);
                
                setBackground(0x00FFFFFF); // set default white background
                
                panelCmds = new CommandButton[2];
                setBorder(b);
	}
	
        public void makeDecoration(String title,Image icon,boolean move,boolean hide,boolean close) {
            
                    // TODO
                    // Font and color need to be changable
                    // move button needs to work, also moving with pointer needs to work
            
                    Panel buttonPanel = new Panel( new GridLayout(1,0,2) );
                    if (move) {
                        buttonPanel.add(new Button("O"));
                    }
                    if (hide) {
                        Button b = new Button("_");
                        b.addActionListener(this);
                        b.setActionCommand("hide");
                        buttonPanel.add(b);

                    }
                    if (close) {
                        Button b = new Button("X");
                        b.addActionListener(this);
                        b.setActionCommand("close");
                        buttonPanel.add(b);
                        
                    }
                    
                    Panel decoration = new Panel( new BorderLayout() );
                    decoration.add(new Label( title,icon ));
                    decoration.add(buttonPanel,Graphics.RIGHT);
                    decoration.setBackground(0x00AAAAFF);
            
                    contentPane.add(decoration,Graphics.TOP);
        }
    
	public void setupFocusedComponent() {

            DesktopPane.getDesktopPane().setFocusedComponent(null);
            
            if (glasspanecomponent!=null) {
                    glasspanecomponent.breakOutAction(null,Canvas.DOWN,false);
            }
            else {
                    contentPane.breakOutAction(null,Canvas.DOWN,false);
            }
		
	}
	
	public void passScrollUpDown(int right) {
		
            if (glasspanecomponent!=null) {
                    glasspanecomponent.breakOutAction(null,right,true);
            }
            else {
                    contentPane.breakOutAction(null,right,true);
            }
		
	}
	
	
    public void setSize(int width, int height){
    	super.setSize(width, height);
    	contentPane.setBoundsWithBorder(0,0,width, height);
    }



	private Component old;
    public void setGlassPaneComponent(Panel c) {
    	
    	if (glasspanecomponent == c) {
    		
    		return;
    		
    	}
    	else if (c!=null) {
    		glasspanecomponent = c;
    		glasspanecomponent.setOwnerAndParent(this, null);
    		old = DesktopPane.getDesktopPane().getFocusedComponent();
    		//if (glasspanecomponent instanceof Panel) { ((Panel)glasspanecomponent).doLayout(); }
    		//glasspanecomponent.doLayout(); // TODO wrong place, will not always work
    		//owner.setActiveComponent(glasspanecomponent);
    		setupFocusedComponent();
    	}
    	else if (glasspanecomponent!=null) {
    		glasspanecomponent.setOwnerAndParent(null, null);
    		glasspanecomponent = null;
    		
    		if (old!=null && old.getOwner()!=null) {
    			DesktopPane.getDesktopPane().setFocusedComponent(old);
    		}
    		else {
    			setupFocusedComponent();
    		}
    		repaint();
    	}

    }
    
	public void setContentPane(Panel a) {
		
            if (contentPane == a) {

                    return;

            }
            else if (a!=null) {
    		contentPane = a;
    		contentPane.setOwnerAndParent(this, null);
    		contentPane.setBounds(0, 0, width, height);
    		
    		//if (glasspanecomponent!=null) {
    		//	setActiveComponent(contentPane);
    		//}
    		//else {
    		//	
    		//	setActiveComponent(contentPane);
    		//}
   
            }
            else {
                    throw new RuntimeException();
            }
		
	}
    
    public Panel getGlassPaneComponent() {
    	
    	return glasspanecomponent;
    }
	
	
	public Panel getContentPane() {
		
		return contentPane; 
		
	}

	/**
	 * This method needs to paint a component IF repaintComponent!=null
	 * otherwise it needs to repaint the window
         * 
         * This method can NOT get called if repaintComponent.transparent
         * 
	 * @return true if it was successful and false otherwise
	 */
	public boolean paintWindow(Graphics g,Component repaintComponent) {

            	// redraw everything
		if (repaintComponent==null)  {
			paint(g);
			return true;
		}

                Panel root = repaintComponent.getRootPane();

                // this component is NOT in this window
                if (root!=contentPane && root!=glasspanecomponent) {
                    return false;
                }
                // if the glass is transparent then we need to repaint everything
                // as it MAY overlap something in the window
                if (glasspanecomponent!=null && root!=glasspanecomponent && !glasspanecomponent.isOpaque()) {
                    repaintComponent = contentPane;
                }
                
                int a=g.getClipX();
                int b=g.getClipY();
                int c=g.getClipWidth();
                int d=g.getClipHeight();
                if (repaintComponent.parent!=null) {
                    repaintComponent.parent.clip(g);
                }

                int x = repaintComponent.getXInWindow();
                int y = repaintComponent.getYInWindow();
                g.translate(x, y);
                repaintComponent.paint(g);
                g.translate(-x, -y);

                g.setClip(a,b,c,d);
                
                if (glasspanecomponent!=null && root!=glasspanecomponent) {
                    drawGlass(g);
                }
                
                return true;
                
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		contentPane.paint(g);		
		drawGlass(g);
		
	}
	
	// same translate done above
	private void drawGlass(Graphics g) {

		if (glasspanecomponent!=null) {
			
			int gx=glasspanecomponent.getX();
                        int gy=glasspanecomponent.getY();
			
			g.translate(gx, gy);
			glasspanecomponent.paint(g);
			g.translate(-gx, -gy);
		}
			
	}
	
	
    public void pointerEvent(int type, int x, int y){

    	if (glasspanecomponent!=null) {
    	
    		glasspanecomponent.pointerEvent(type, x - glasspanecomponent.getX(), y - glasspanecomponent.getY());
    	}
    	else {
    		
    		contentPane.pointerEvent(type, x, y);
    		
    	}
    }


	public void paintComponent(Graphics g) { }

        public CommandButton[] getWindowCommands() {
            return panelCmds;
        }

        /**
         * @see DesktopPane#setComponentCommand
         */
	public void setWindowCommand(int i, CommandButton softkey) {

            if (panelCmds[i]!=softkey) {
		panelCmds[i] = softkey;
                if (DesktopPane.getDesktopPane().getSelectedFrame()==this) {
                    DesktopPane.getDesktopPane().softkeyRepaint();
                }
            }
	}
	
	public String toString() {
		
		return super.toString() +" "+ contentPane +" "+glasspanecomponent;
	}
	
	public void repaint() {
		
		if (this == DesktopPane.getDesktopPane().getSelectedFrame() && isOpaque()) {
			DesktopPane.getDesktopPane().windowRepaint();
		}
		else {
			DesktopPane.getDesktopPane().fullRepaint();
		}
		
	}

    public void actionPerformed(String actionCommand) {
         if ("close".equals(actionCommand)) {
             
             if (parent==null) {
                DesktopPane.getDesktopPane().remove(this);
             }
             else {
                 parent.remove(this);
             }
         }
         if ("hide".equals(actionCommand)) {
         
             if (parent==null) {
                 Vector windows = DesktopPane.getDesktopPane().getAllFrames();
                 if (windows.size()>1) {
                     DesktopPane.getDesktopPane().setSelectedFrame((Window)windows.elementAt(windows.size()-2));
                 }
             }
         }
    }
}
