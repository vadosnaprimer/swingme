package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Canvas;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.util.ButtonGroup;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.KeyEvent;

public class Button extends Label implements ActionListener {

	private static CommandButton selectButton = new CommandButton("Select","select");

	public static void setSelectButtonText(String a) {
		
		selectButton = new CommandButton(a,selectButton.getActionCommand());
		
	}
	
	protected int borderColor;
	protected int activeBorderColor;
	
	private ActionListener al;
	private String actionCommand;
	
	protected boolean selected;
	protected ButtonGroup buttonGroup;
	
	private boolean useSelectButton;

	/**
	 * Creates a new MButtonItem with the given label with requested button alignment.
	 * @param label - the button label
	 * @param font - preffered font object
	 * @param align - One of the 3 available alignments(LEFT, HCENTER, RIGHT).
	 */
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Creates a new MButtonItem with the given label, desired button alignment and style.
	 * @param label - the button label
	 * @param align - One of the 3 available alignments(LEFT, HCENTER, RIGHT).
	 * @param style - FormStyle object
	 */
	public Button(String label) {
		
		this(label,
				RootPane.getDefaultStyle().font,
				RootPane.getDefaultStyle().background,
				RootPane.getDefaultStyle().itemBorderColor,
				RootPane.getDefaultStyle().itemActiveBorderColor);

	}
	
	public Button(String label, Font font) {
		
		this(label,font,0x00FFFFFF,0x00000000,0);

	}
	
	// full constructor
	public Button(String label, Font font,int a,int b,int c) {
		
		super(label, font);

		//this.font = font;
		//this.align = align;
		
		
		//this.label = label;
		
		// the sizing is done in label
		// as it needs to take icons into account too
		
		//Default button width
		//width = font.getWidth(label) + 4;
		
		//if(width < (GameCanvas.m_canvasWidth/4))
		//{
		//	width = GameCanvas.m_canvasWidth/4;
		//}
		
		//height = font.getHeight() + 2;
		
		background = a;
		this.borderColor = b;
		this.activeBorderColor = c;
		transparent = false;
		
		setBorder(new LineBorder(borderColor));
		selectable = true;
		
		setForegroundByFontColorIndex(0);
		
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
	
	/**
	 * Does nothing????
	 * @param int - time since last frame, should be 16..16 number format
	 * @param Keypad - Keypad object
	 */
	public boolean keyEvent(KeyEvent keypad)
	{
		if (keypad.justPressedAction(Canvas.FIRE)) {

			fireActionPerformed();
			
			return true;
		}
		return false;
	}
	public void fireActionPerformed() {
		
		if (al!=null) {
			al.actionPerformed((actionCommand!=null)?actionCommand:getText());
		}
		if (buttonGroup!=null) {
			selected = true;
			buttonGroup.setSelected(this);
		}
		if (isSelected()==false){
			selected = true;
		}
		
		
	}
	/**
	 * Draws the button at given y position with set alignment
	 * @param Graphics - the graphics object
	 * @param int - Y position
	 * @return int - height of the item
	 */
	
	// LOL, NONE OF THIS IS ACTUALLY NEEDED!
	//public void paintComponent(Graphics g) {

		//int x = 0;// 1;
		//int y = 0;
		//int ht = getHeight();
		
		// Calculate X position
		//if(align == ALIGN_RIGHT)
		//{
		//	x = GameCanvas.m_canvasWidth - width - 1;
		//}
		//else if(align == ALIGN_HCENTER)
		//{
		//	x = (GameCanvas.m_canvasWidth - width) >> 1;
		//}
		
		//Draw Button
		//g.setColor(background);
		//g.fillRect(0, 0, width, height);
		
		//Draw label
		//font.drawString(g, label, (width >> 1), (height >> 1), Font.MIDDLE_CENTER);
		
		//Draw border
		//if(isFocused())
		//{	
		//	g.setColor(activeBorderColor);
		//}
		//else
		//{
		//	g.setColor(borderColor);
		//}
		
		//g.drawRect(0, 0, width, height);
		
		//return ht;
		
	//}	

	/**
	 * Sets the new border color for boxes.
	 * @param int - color
	 */
	public void setBorderColor(int color)
	{
		borderColor = color;
	}
	
	/**
	 * Set's the active box border color.
	 * @param color
	 */
	public void setActiveBorderColor(int color)
	{
		activeBorderColor = color;
	}

	public void focusLost() {

		setMyBorderColor(borderColor);
		setForegroundByFontColorIndex(0);
		
		if (useSelectButton) {
			RootPane.getRootPane().setComponentCommand(0, null);
		}
		
		repaint();
	}

	public void focusGained() {

		setMyBorderColor(activeBorderColor);
		setForegroundByFontColorIndex(1);
		
		if (useSelectButton) {
			RootPane.getRootPane().setComponentCommand(0, selectButton);
		}
		
		repaint();
	}
	
	private void setMyBorderColor(int c) {
		
		if (border!= null && border instanceof LineBorder) {
			((LineBorder)border).setColor(c);
		}
		
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

	
}