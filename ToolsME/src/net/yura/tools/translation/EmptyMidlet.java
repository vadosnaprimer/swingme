/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.tools.translation;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Midlet;

/**
 * @author Yura Mamyrin
 */
public class EmptyMidlet extends Midlet {

        @Override
        protected DesktopPane makeNewRootPane() {
            return new DesktopPane(this, -1, null);
        }

        @Override
        protected void initialize(DesktopPane arg0) {

        }
}
