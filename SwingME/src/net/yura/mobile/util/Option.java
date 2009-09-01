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

package net.yura.mobile.util;

import net.yura.mobile.gui.Icon;

/**
 * @author Yura Mamyrin
 */
public class Option {

	private String key;
	private String value;
        private String tip;
        private Icon icon;

        public Option() {
        }

	public Option(final String key, final String val) {
                this(key,val,null);
	}
        public Option(final String key, final String val,final Icon img) {
		this.key = key;
		value = val;
                icon = img;
        }
        public Option(String id, String title, Icon icon, String tip) {
            	this.key = id;
		value = title;
                this.icon = icon;
                this.tip = tip;
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }
        public String getTip() {
            return tip;
        }
        public void setTip(String tip) {
            this.tip = tip;
        }
        public String getToolTip() {
            return tip;
        }
        public String getValue() {
		return value;
	}
        public Icon getIcon() {
            return icon;
        }


	public String toString() {
		return value;
	}
}