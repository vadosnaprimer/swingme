package net.yura.mobile.gui.components;

import java.util.Vector;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;

public class PageView extends ScrollPane {

    Vector model;
    int currentPanel;

    public PageView(Vector panels) {
        model = panels;

        setMode( ScrollPane.MODE_NONE );

//        add((Component)model.firstElement());
//        currentPanel = 0;

        // TODO: REVERT THIS
        add((Component)model.elementAt(1));
        currentPanel = 1;



        setBounceMode(BOUNCE_HORIZONTAL);

    }

    // Override
    public void paint(Graphics2D g) {
        super.paint(g);

        Panel currPanel = (Panel) model.elementAt(currentPanel);


        // here we want to draw the prev or next panel in the model
        if (currentPanel > 0) {
            Panel prevPanel = (Panel) model.elementAt(currentPanel - 1);

            int prevPanelPosX = -prevPanel.getWidth() + currPanel.posX;
            g.translate(prevPanelPosX, 0);
            prevPanel.paint(g);
            g.translate(-prevPanelPosX, 0);
        }

        if (currentPanel + 1 < model.size()) {
            Panel nextPanel = (Panel) model.elementAt(currentPanel + 1);
            int nextPanelPosX = getViewPortWidth() + currPanel.posX;
            g.translate(nextPanelPosX, 0);
            nextPanel.paint(g);
            g.translate(-nextPanelPosX, 0);
        }

        g.setColor(0xFF000000);
        g.drawLine(getViewPortWidth() / 2, 0, getViewPortWidth() / 2, getHeight());
    }


    // Override
    public void setSize(int w, int h) {
        super.setSize(w, h);

        // TODO: How to calculate the size of the components (panels??) that are not being displayed?
        for (int i = 0; i < model.size(); i++) {
            Panel panel = (Panel) model.elementAt(i);
            panel.validate();
            panel.setSize(getViewPortWidth(), getViewPortHeight());
        }
    }

    // Override
    public void processMouseEvent(int type, int pointX, int pointY, KeyEvent keys) {

        if (type == DesktopPane.RELEASED) {


            Panel currPanel = (Panel) model.elementAt(currentPanel);

            if (currentPanel > 0) {

                if (currPanel.posX > getViewPortWidth() / 2) {
                    goPrev();
                }
            }

            if (currentPanel + 1 < model.size()) {
                if (currPanel.posX + getViewPortWidth() < getViewPortWidth() / 2) {
                    goNext();
                }
            }
        }

        super.processMouseEvent(type, pointX, pointY, keys);
    }

    void goNext() {

        Component old = (Component)model.elementAt(currentPanel);

        currentPanel++;

        Component comp = (Component)model.elementAt(currentPanel);

        add(comp);

        comp.setLocation(old.getX() + getWidth(), old.getY());
    }

    void goPrev() {

        Component old = (Component)model.elementAt(currentPanel);

        currentPanel--;

        Component comp = (Component)model.elementAt(currentPanel);

        add(comp);

        comp.setLocation(old.getX() - getWidth(), old.getY());

    }


}
