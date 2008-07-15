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

package net.yura.mobile.gui;

import javax.microedition.lcdui.Command;
import net.yura.mobile.gui.components.Menu;

/**
 * @author Yura Mamyrin
 */
public class CommandButton extends Command {
	
	private String actionCommand;
	private Menu menu;
        
	public CommandButton(String label,String com) {
		super(label, Command.OK, 1);

		actionCommand = com;
		
	}

        public CommandButton(Menu m,String com) {
            this(m.getText(),com);
            menu = m;
        }
        
	public void setActionCommand(String ac) {
		
		actionCommand=ac;
	}
	
	public String getActionCommand() {
		
		return actionCommand;
		
	}

        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.actionCommand != null ? this.actionCommand.hashCode() : 0);
            return hash;
        }
	
	public boolean equals(Object a){
		return (a instanceof CommandButton && ((CommandButton) a).getActionCommand().equals(actionCommand));
	}
        
        public Menu getMenu() {
            return menu;
        }
        public String toString() {
            return getLabel();
        }
}
