package net.yura.mobile.gui.components;

import java.util.Vector;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;

public class PageView extends ScrollPane {

    private Vector model;
    private int currentViewIdx;
    private boolean animating;
    private int spacing = 10;

    public PageView(Vector panels) {
        model = panels;

        setMode(ScrollPane.MODE_NONE);

        currentViewIdx = 0;
        add(getCurrentView());

        resetDragMode();
    }

    public PageView() {
        this(null);
    }


    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }


    public int getSpacing() {
        return spacing;
    }


    // Override
    public void paintChildren(Graphics2D g) {

        Component currView = getCurrentView();

        if (!currView.consumesMotionEvents()) { // TODO: How to detect that the component is in "pinch mode"?

            int currViewPosX = currView.posX; //TODO: Avoid Thread access...
            Component prevView = getPreviousView();

            // here we want to draw the prev or next panel in the model
            if (prevView != null) {
                int prevViewPosX = currViewPosX - prevView.getWidth() - spacing;
                g.translate(prevViewPosX, 0);
                prevView.paint(g);
                g.translate(-prevViewPosX, 0);
            }

            prevView = null; // Help GC
            Component nextView = getNextView();

            if (nextView != null) {
                int nextViewPosX = currViewPosX + currView.getWidth() + spacing;
                g.translate(nextViewPosX, 0);
                nextView.paint(g);
                g.translate(-nextViewPosX, 0);
            }
        }

        super.paintChildren(g);

        g.setColor(0xFF000000);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
    }


    // Override
    public void setSize(int w, int h) {
        super.setSize(w, h);

        int vpW = getViewPortWidth();
        int vpH = getViewPortHeight();

        // TODO: How to calculate the size of the components that are not being displayed?
        setViewSize(getPreviousView(), vpW, vpH);
        setViewSize(getNextView(), vpW, vpH);
    }

    // Override
    public void processMouseEvent(int type, int pointX, int pointY, KeyEvent keys) {

        animating = (type == DesktopPane.RELEASED);

        if (animating) {
            checkViewChange();
        }

        super.processMouseEvent(type, pointX, pointY, keys);
    }

    // Override
    protected void setViewLocation(int viewX, int viewY) {
        super.setViewLocation(viewX, viewY);

        if (animating) {
            checkViewChange();
        }
    }

    /**
     *  To be overridden by sub-classes. The default implementation uses an Vector.
     * @return The next display View, or null if there is none (end of the list)
     */
    protected Component getNextView() {
        return (currentViewIdx + 1 < model.size()) ? (Component) model.elementAt(currentViewIdx + 1) : null;
    }

    /**
     *  To be overridden by sub-classes. The default implementation uses an Vector.
     * @return The Current View. Cannot be null.
     */
    protected Component getCurrentView() {
        return (Component) model.elementAt(currentViewIdx);
    }

    /**
     *  To be overridden by sub-classes. The default implementation uses an Vector.
     * @return The previous display View, or null if there is none (end of the list)
     */
    protected Component getPreviousView() {
        return (currentViewIdx > 0) ? (Component) model.elementAt(currentViewIdx - 1) : null;
    }

    /**
     *  To be overridden by sub-classes. Called when the central view changes.
     */
    protected void setCurrentView(Component view) {

        if (model != null) {
            currentViewIdx = model.indexOf(view);
        }

        animating = false;

        Component currView = getCurrentView();
        add(currView);

        int vpW = getViewPortWidth();
        int vpH = getViewPortHeight();

        //TODO: How to reset the size of the components?
        setViewSize(getPreviousView(), vpW, vpH);
        setViewSize(currView, vpW, vpH);
        setViewSize(getNextView(), vpW, vpH);

        resetDragMode();
        resetDragSpeed();
    }


    private void setViewSize(Component view, int w, int h) {
        if (view != null) {
            view.validate();
            view.setSize(w, h);
        }
    }

    private void checkViewChange() {
        int viewPortW = getWidth();
        int viewX = getView().getX();

        if (getPreviousView() != null) {
            if (viewX > viewPortW / 2) {
                goPrev();
            }
        }

        if (getNextView() != null) {
            if (viewX + getView().getWidth() < viewPortW / 2) {
                goNext();
            }
        }
    }

    private void goNext() {

        Component currView = getCurrentView();
        int newViewX = Math.min(currView.getX() + currView.getWidth(), getWidth());
        currView = null; // Help GC

        Component nextView = getNextView();
        setCurrentView(nextView);

        // NOTE: setCurrentView calls add(), and that resets the view location,
        // so this call needs to be after it
        nextView.setLocation(newViewX, getViewPortY());
    }

    private void goPrev() {

        int newViewX = Math.max(getCurrentView().getX(), 0) - getWidth();

        Component prevView = getPreviousView();
        setCurrentView(prevView);

        // NOTE: setCurrentView calls add(), and that resets the view location,
        // so this call needs to be after it
        prevView.setLocation(newViewX, getViewPortY());
    }

    private void resetDragMode() {
        int bounceMode = 0;
        if (getPreviousView() != null) {
            bounceMode |= BOUNCE_LEFT;
        }
        if (getNextView() != null) {
            bounceMode |= BOUNCE_RIGHT;
        }
        setBounceMode(bounceMode);
    }
}
