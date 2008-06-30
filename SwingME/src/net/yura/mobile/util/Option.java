package net.yura.mobile.util;

import javax.microedition.lcdui.Image;

/**
 * @author Yura Mamyrin
 */

public class Option {

	private final String id;
	private final String value;
        private final Image icon;

	public Option(final String key, final String val) {
                this(key,val,null);
	}
        public Option(final String key, final String val,final Image img) {
		id = key;
		value = val;
                icon = img;
        }
            
	public String getId() {
		return id;
	}
        public String getValue() {
		return value;
	}
        public Image getIcon() {
            return icon;
        }

	public String toString() {
		return value;
	}
}