package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.GridLayout;

public class Window extends Component implements ActionListener {

	private Panel contentPane;
	private Panel glasspanecomponent;
        
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
                transparent = false;
                
                panelCmds = new CommandButton[2];
                setBorder(b);
	}
	
        public void makeDecoration(String title,Image icon,boolean move,boolean hide,boolean close) {
            
                    // TODO
                    // Font and color need to be changable
                    // move button needs to work, also moving with pointer needs to work
            
                    Panel buttonPanel = new Panel( new GridLayout(0,1,2) );
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
    
	public void setupActiveComponent() {

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
    	contentPane.setSize(width, height);
    }



	private Component old;
    public void setGlassPaneComponent(Panel c) {
    	
    	if (glasspanecomponent == c) {
    		
    		return;
    		
    	}
    	else if (c!=null) {
    		glasspanecomponent = c;
    		glasspanecomponent.setOwnerAndParent(this, null);
    		old = RootPane.getRootPane().getCurrentItem();
    		//if (glasspanecomponent instanceof Panel) { ((Panel)glasspanecomponent).doLayout(); }
    		//glasspanecomponent.doLayout(); // TODO wrong place, will not always work
    		//owner.setActiveComponent(glasspanecomponent);
    		setupActiveComponent();
    	}
    	else if (glasspanecomponent!=null) {
    		glasspanecomponent.setOwnerAndParent(null, null);
    		glasspanecomponent = null;
    		
    		if (old!=null && old.getOwner()!=null) {
    			RootPane.getRootPane().setActiveComponent(old);
    		}
    		else {
    			setupActiveComponent();
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
	 * @return true if it was successful and false otherwise
	 */
	public boolean paintWindow(Graphics g,Component repaintComponent) {
		
		// first try the glass if we have 1
		if (repaintComponent!=null && glasspanecomponent!=null) {

				boolean result;

				// it is the glass but its transparent!
				// so we need to repaint everything
				if (repaintComponent == glasspanecomponent && glasspanecomponent.isTransparent()) {
					
					repaintComponent = null;
					result = false;
				}
				else if (repaintComponent == glasspanecomponent) {
				
					drawGlass(g);
					result = true;	
				}
				else {

					int gx=glasspanecomponent.getX();
					int gy=glasspanecomponent.getY();
					
					// same as in drawGlass!!
					g.translate(gx, gy);
					
					result = glasspanecomponent.repaintComponent(g,repaintComponent);

					g.translate(-gx, -gy);
				}

				if (result) {
					return true;
				}
				
		}
		
		// then try the contentpane
		if (repaintComponent!=null) {
			
			boolean done;
			
			if (repaintComponent == contentPane) {
				
				contentPane.paint(g);
				done = true;
			}
			else {
				done = contentPane.repaintComponent(g,repaintComponent);

			}

			if (done && glasspanecomponent!=null && glasspanecomponent.isTransparent() ) {

				// ah crap, we have painted it, but the glass it transparent
				// this is not good at all, we will need to repaint the whole window
				repaintComponent=null;
			}
			else if (done) {
				drawGlass(g);
				return true;
			}
			
		}
		
		// redraw everything
		if (repaintComponent==null)  {
			
			paint(g);

			return true;
		}

		return false;

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

	
	
	public CommandButton[] panelCmds;

    public CommandButton[] getPanelCommands(){
        return panelCmds;
    }

	public void setWindowCommand(int i, CommandButton softkey) {

		panelCmds[i] = softkey;
		repaint(); // TODO is this correct, will ANY repaint do? also another place
		
	}
	
	public String toString() {
		
		return super.toString() +" "+ contentPane +" "+glasspanecomponent;
	}
	
	public void repaint() {
		
		if (this == RootPane.getRootPane().getCurrentWindow() && !transparent) {
			RootPane.getRootPane().windowRepaint();
		}
		else {
			RootPane.getRootPane().fullRepaint();
		}
		
	}

    public void actionPerformed(String actionCommand) {
         if ("close".equals(actionCommand)) {
             
             if (parent==null) {
                RootPane.getRootPane().closeWindow(this);
             }
             else {
                 parent.remove(this);
             }
         }
         if ("hide".equals(actionCommand)) {
         
             if (parent==null) {
                RootPane.getRootPane().hideWindow(this);
             }
         }
    }
}
