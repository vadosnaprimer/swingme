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

package net.yura.mobile.gui.plaf;

import java.util.Hashtable;
import net.yura.mobile.gui.DesktopPane;

/**
 * @author Yura Mamyrin
 * @see javax.swing.LookAndFeel
 * @see javax.swing.UIManager
 */
public abstract class LookAndFeel {

        public static final int ICON_RADIO = 0;
        public static final int ICON_CHECKBOX = 1;
        public static final int ICON_COMBO = 2;
        public static final int ICON_SPINNER_LEFT = 3;
        public static final int ICON_SPINNER_RIGHT = 4;
        public static final int ICON_TRACK_FILL = 5;

        private Hashtable styles;
        
        public LookAndFeel() {
            styles = new Hashtable();
        }
        
        /**
         * @see javax.swing.plaf.synth.SynthLookAndFeel#getStyle(javax.swing.JComponent, javax.swing.plaf.synth.Region) SynthLookAndFeel.getStyle
         * @see DesktopPane#getDefaultTheme(net.yura.mobile.gui.components.Component)
         */
        public Style getStyle(String name) {
            return (Style)styles.get(name);
        }
        
        public void setStyleFor(String key, Style theStyle) {
            styles.put(key, theStyle);
        }

}

