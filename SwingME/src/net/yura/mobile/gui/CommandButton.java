package net.yura.mobile.gui;

import javax.microedition.lcdui.Command;

public class CommandButton extends Command {
	
	private String actionCommand;
	
	public CommandButton(String label,String com) {
		super(label, Command.OK, 1);

		actionCommand = com;
		
	}

	public void setActionCommand(String ac) {
		
		actionCommand=ac;
	}
	
	public String getActionCommand() {
		
		return actionCommand;
		
	}
	
	public boolean equals(Object a){
		return (a instanceof CommandButton && ((CommandButton) a).getActionCommand().equals(actionCommand));
	}
}
