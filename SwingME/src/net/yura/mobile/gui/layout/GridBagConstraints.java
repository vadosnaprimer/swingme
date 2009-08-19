package net.yura.mobile.gui.layout;

import net.yura.mobile.gui.components.Component;

public class GridBagConstraints {
    int weightx;
    int weighty;
    int column;
    int colSpan;
    int rowSpan;
    String halign;
    String valign;

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

    public int getColumn() {
        return column;
    }
}