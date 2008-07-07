package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Canvas;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.util.ButtonGroup;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.Border;

public class Button extends Label implements ActionListener {

	private static CommandButton selectButton = new CommandButton("Select","select");

	public static void setSelectButtonText(String a) {
		
		selectButton = new CommandButton(a,selectButton.getActionCommand());
		
	}
	
	protected Border normalBorder;
	protected Border activeBorder;
	
	private ActionListener al;
	private String actionCommand;
	
	protected boolean selected;
	protected ButtonGroup buttonGroup;
	
	private boolean useSelectButton;

	public Button(){
            this(null);
        }
	public Button(String label) {
		
		this(label,
				DesktopPane.getDefaultTheme().font,
				DesktopPane.getDefaultTheme().background,
				DesktopPane.getDefaultTheme().itemBorderColor,
				DesktopPane.getDefaultTheme().itemActiveBorderColor);

	}
	
	public Button(String label, Font font) {
		
		this(label,font,0x00FFFFFF,0x00000000,0);

	}
	
	// full constructor
	public Button(String label, Font font,int a,int b,int c) {
		super(label, font);
		
		background = a;
		transparent = false;
		
		setBorder(new LineBorder(b));
                setActiveBorder(new LineBorder(c));
		selectable = true;
		
		setForegroundByFontColorIndex(0);
		
	}

        public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
        
	public void addActionListener(ActionListener l) {
		
		al = l;
		
	}
	public void removeActionListener(ActionListener l) {
		
		if (al == l) { al = null; }
	}
	
	public void setActionCommand(String ac) {
		
		actionCommand=ac;
	}

	public boolean keyEvent(KeyEvent keyEvent) {
            
            	if (keyEvent.justPressedAction(Canvas.FIRE)) {

                        selected = true;
                    
			fireActionPerformed();
			
			return true;
		}
		return false;
    
        }

        public void pointerEvent(int type, int x, int y) {
            super.pointerEvent(type, x, y);
            
            if (type == DesktopPane.PRESSED) {
                selected=true;
            }
            else if (selected && type == DesktopPane.RELEASED) {
                fireActionPerformed();
            }
        }
        
	 public void fireActionPerformed() {
		
            	if (buttonGroup!=null) {
                        // this unselects all other buttons in the same button group as this 1
			buttonGroup.setSelected(this);
		}
            
		if (al!=null) {
			al.actionPerformed((actionCommand!=null)?actionCommand:getText());
		}

	}
        
	public void setBorder(Border b) {
		normalBorder = b;
                super.setBorder(b);
	}
 
	public void setActiveBorder(Border b) {
		activeBorder = b;
	}

	public void focusLost() {

		super.setBorder(normalBorder);
		setForegroundByFontColorIndex(0);
		
		if (useSelectButton) {
			DesktopPane.getDesktopPane().setComponentCommand(0, null);
		}
		
		repaint();
	}

	public void focusGained() {

		super.setBorder(activeBorder);
		setForegroundByFontColorIndex(1);
		
		if (useSelectButton) {
			DesktopPane.getDesktopPane().setComponentCommand(0, selectButton);
		}
		
		repaint();
	}


	public void setButtonGroup(ButtonGroup buttonGroup) {
		
		this.buttonGroup = buttonGroup;
		
	}
	
	public void actionPerformed(String actionCommand) {
		
		if(selectButton.getActionCommand().equals(actionCommand)) {
			
			fireActionPerformed();
		}
		
	}

	public boolean isUseSelectButton() {
		return useSelectButton;
	}

	public void setUseSelectButton(boolean useSelectButton) {
		this.useSelectButton = useSelectButton;
	}

        protected int getBorderColor() {
            if (border instanceof LineBorder) {
                return ((LineBorder)border).getLineColor();
            }
            return 0;
        }
	
}