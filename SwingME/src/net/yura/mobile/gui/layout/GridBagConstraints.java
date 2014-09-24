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

package net.yura.mobile.gui.layout;

import net.yura.mobile.gui.components.Component;

/**
 * @see java.awt.GridBagConstraints
 */
public class GridBagConstraints {
    /**
     * @see java.awt.GridBagConstraints#weightx GridBagConstraints.weightx
     */
    public int weightx;

    /**
     * @see java.awt.GridBagConstraints#weighty GridBagConstraints.weighty
     */
    public int weighty;

    /**
     * @see java.awt.GridBagConstraints#gridwidth GridBagConstraints.gridwidth
     */
    public int colSpan;

    /**
     * @see java.awt.GridBagConstraints#gridheight GridBagConstraints.gridheight
     */
    public int rowSpan;

    /**
     * can be: "fill", "center", "left", "right"
     * @see java.awt.GridBagConstraints#anchor GridBagConstraints.anchor
     * @see java.awt.GridBagConstraints#fill GridBagConstraints.fill
     * @see java.awt.Component#getAlignmentX() Component.getAlignmentX
     * @see javax.swing.JComponent#setAlignmentX(float) JComponent.setAlignmentX
     */
    public String halign;

    /**
     * can be: "fill", "center", "top", "bottom"
     * @see java.awt.GridBagConstraints#anchor GridBagConstraints.anchor
     * @see java.awt.GridBagConstraints#fill GridBagConstraints.fill
     * @see java.awt.Component#getAlignmentY() Component.getAlignmentY
     * @see javax.swing.JComponent#setAlignmentY(float) JComponent.setAlignmentY
     */
    public String valign;

    Component component;

    public GridBagConstraints() {
        rowSpan = 1;
        colSpan = 1;
        halign = "fill";
        valign = "fill";
    }


    public String getHalign() {
        return halign;
    }

    public String getValign() {
        return valign;
    }

    public int getWeightx() {
        return weightx;
    }

    public int getWeighty() {
        return weighty;
    }

    public int getColSpan() {
        return colSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

}