package net.yura.mobile.gui.components;

import java.util.Vector;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;

public class PageView extends ScrollPane {

    Vector model;
    int currentViewIdx;

    public PageView(Vector panels) {
        model = panels;

        setMode( ScrollPane.MODE_NONE );

        add((Component)model.firstElement());
        currentViewIdx = 0;

        resetDragMode();
    }

    // Override
    public void paintChildren(Graphics2D g) {

        Component currView = (Component) model.elementAt(currentViewIdx);

        if (!currView.consumesMotionEvents()) { // TODO: How to detect that the component is in "pinch mode"?

            int currViewPosX = currView.posX; //TODO: Avoid Thread access...

            // here we want to draw the prev or next panel in the model
            if (currentViewIdx > 0) {
                Component prevView = (Component) model.elementAt(currentViewIdx - 1);

                int prevViewPosX = currViewPosX - prevView.getWidth();
                g.translate(prevViewPosX, 0);
                prevView.paint(g);
                g.translate(-prevViewPosX, 0);
            }

            if (currentViewIdx + 1 < model.size()) {
                Component nextView = (Component) model.elementAt(currentViewIdx + 1);
                int nextViewPosX = currViewPosX + currView.getWidth();
                g.translate(nextViewPosX, 0);
                nextView.paint(g);
                g.translate(-nextViewPosX, 0);
            }
        }

        super.paintChildren(g);

        g.setColor(0xFF000000);
        g.drawLine(getViewPortWidth() / 2, 0, getViewPortWidth() / 2, getHeight());
    }


    // Override
    public void setSize(int w, int h) {
        super.setSize(w, h);

        int vpW = getViewPortWidth();
        int vpH = getViewPortHeight();

        // TODO: How to calculate the size of the components that are not being displayed?
        for (int i = 0; i < model.size(); i++) {
            Component view = (Component) model.elementAt(i);
            view.validate();
            view.setSize(vpW, vpH);
        }
    }

    // Override
    public void processMouseEvent(int type, int pointX, int pointY, KeyEvent keys) {

        System.out.println("PageView:processMouseEvent");

        if (type == DesktopPane.RELEASED) {

            int viewPortW = getViewPortWidth();

            Component currView = (Component) model.elementAt(currentViewIdx);

            int velocityX = getDragVelocity(true);
            System.out.println("VEL = " + velocityX + " VW = " + viewPortW);

            if (currentViewIdx > 0) {
                if (velocityX > viewPortW || currView.posX > viewPortW / 2) {
                    goPrev();
                }
            }

            if (currentViewIdx + 1 < model.size()) {
                if (velocityX < -viewPortW || currView.posX + currView.getWidth() < viewPortW / 2) {
                    goNext();
                }
            }
        }

        super.processMouseEvent(type, pointX, pointY, keys);
    }

    void goNext() {

        Component currComp = (Component)model.elementAt(currentViewIdx);
        int newViewX = Math.min(currComp.getX() + currComp.getWidth(), getWidth());

        selectComp(currentViewIdx + 1, currComp, newViewX);
    }

    void goPrev() {

        Component currComp = (Component) model.elementAt(currentViewIdx);
        int newViewX = Math.max(currComp.getX(), 0) - getWidth();

        selectComp(currentViewIdx - 1, currComp, newViewX);
    }

    private void selectComp(int newViewIdx, Component currComp, int newViewX) {

        currentViewIdx = newViewIdx;
        Component newComp = (Component)model.elementAt(newViewIdx);

        add(newComp);

        int vpW = getViewPortWidth();
        int vpH = getViewPortHeight();

        //TODO: How to reset the size of the components?
        currComp.validate();
        currComp.setSize(vpW, vpH);

        newComp.validate();
        newComp.setSize(vpW, vpH);

        newComp.setLocation(newViewX, getViewPortY());

        resetDragMode();
        resetDragSpeed();
    }

    private void resetDragMode() {
        int bounceMode = 0;
        if (currentViewIdx > 0) {
            bounceMode |= BOUNCE_LEFT;
        }
        if (currentViewIdx < model.size() - 1) {
            bounceMode |= BOUNCE_RIGHT;
        }
        setBounceMode(bounceMode);
    }
}
