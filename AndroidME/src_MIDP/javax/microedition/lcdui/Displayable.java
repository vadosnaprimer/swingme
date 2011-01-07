package javax.microedition.lcdui;

import java.util.Vector;

import javax.microedition.midlet.MIDlet;

import net.yura.android.AndroidMeActivity;

import android.view.View;

public abstract class Displayable {
	protected CommandListener commandListener;
	private Vector<Command> commands = new Vector<Command>();
	private Display currentDisplay;

	public void addCommand(Command command) {
		boolean added = false;
		for (int i = 0; i < this.commands.size(); i++) {
			Command found = this.commands.elementAt(i);
			if (found.getPriority() > command.getPriority()) {
				this.commands.insertElementAt(command, i);
				added = true;
				break;
			}
		}
		if (!added) {
			this.commands.addElement(command);
		}
	}

	public void removeCommand(Command command) {
		this.commands.removeElement(command);
	}

	public Vector<Command> getCommands() {
		return this.commands;
	}

	public CommandListener getCommandListener() {
		return this.commandListener;
	}

	public void setCommandListener(CommandListener commandListener) {
		this.commandListener = commandListener;
	}

	public int getWidth() {
		View view = this.getView();
		int w = (view == null) ? 0 : view.getWidth();

		return (w <= 0) ? AndroidMeActivity.DEFAULT_ACTIVITY.getScreenWidth() : w;
	}

	public int getHeight() {
		View view = this.getView();
		int h = (view == null) ? 0 : view.getHeight();

        return (h <= 0) ? AndroidMeActivity.DEFAULT_ACTIVITY.getScreenHeight() : h;
	}

	public Display getCurrentDisplay() {
		return currentDisplay;
	}

	public void setCurrentDisplay(Display currentDisplay) {
		this.currentDisplay = currentDisplay;
	}

	public abstract void initDisplayable(MIDlet midlet);

	public abstract View getView();
}
