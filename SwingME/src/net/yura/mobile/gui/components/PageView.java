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
    private boolean prevLayoutDone, nextLayoutDone;

    public PageView(Vector panels) {
        model = panels;

        setMode(ScrollPane.MODE_NONE);

        currentViewIdx = 0;

        Component currView = getCurrentView();
        if (currView != null) {
            // Add the current view, only if available
            add(currView);
        }

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
        if (currView != getView()) {
            setCurrentView(currView);
        }

        // TODO: How to detect that the component is in "pinch mode"?
        if (!currView.consumesMotionEvents() &&           // Not Pinching
             currView.getWidth() >= getViewPortWidth() && // Not Animate to fit
             currView.getHeight() >= getViewPortHeight()) {

            int currViewPosX = currView.posX; //TODO: Avoid Thread access...

            // here we want to draw the prev or next panel in the model

            if (currViewPosX - spacing > 0 && hasPreviousView()) {
                Component prevView = getPreviousView();

                if (prevView != null) {
                    if (!prevLayoutDone) {
                        prevLayoutDone = true;
                        resetViewSize(prevView);
                    }

                    int prevViewPosX = currViewPosX - prevView.getWidth() - spacing;
                    g.translate(prevViewPosX, 0);
                    prevView.paint(g);
                    g.translate(-prevViewPosX, 0);
                }
            }

            int nextViewPosX = currViewPosX + currView.getWidth() + spacing;
            if (nextViewPosX < getWidth() && hasNextView()) {
                Component nextView = getNextView();

                if (nextView != null) {
                    if (!nextLayoutDone) {
                        nextLayoutDone = true;
                        resetViewSize(nextView);
                    }

                    g.translate(nextViewPosX, 0);
                    nextView.paint(g);
                    g.translate(-nextViewPosX, 0);
                }
            }
        }

        super.paintChildren(g);

        g.setColor(0xFF000000);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
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
        resetDragSpeed();

        animating = false;
        prevLayoutDone = false;
        nextLayoutDone = false;

        Component oldView = null;
        try {
            oldView = getView();
        } catch (Exception e) {
        }

        resetViewSize(view);

        if (view != oldView) {
            if (oldView != null) {
                view.setLocation(oldView.getX(), oldView.getY());
            }

            add(view); // Note: add() resets the view Location...

            if (oldView != null) {
                view.setLocation(oldView.getX(), oldView.getY());
            }
        }

        resetDragMode();
    }

    protected boolean hasNextView() {
        return (getNextView() != null);
    }

    protected boolean hasPreviousView() {
        return (getPreviousView() != null);
    }

    protected void changeView(int delta) {
        if (delta < 0 && hasPreviousView()) {
            currentViewIdx--;
        }
        else if (delta > 0 && hasNextView()) {
            currentViewIdx++;
        }

        setCurrentView(getCurrentView());
    }


    public void resetViewSize(Component view) {
        if (view != null) {
            int vpW = getViewPortWidth();
            int vpH = getViewPortHeight();

            view.validate();
            view.setSize(vpW, vpH);
        }
    }

    private void checkViewChange() {
        int viewPortW = getWidth();
        int viewX = getView().getX();

        if (hasPreviousView()) {
            if (viewX > viewPortW / 2) {
                goPrev();
            }
        }

        if (hasNextView()) {
            if (viewX + getView().getWidth() < viewPortW / 2) {
                goNext();
            }
        }
    }

    private void goNext() {
        resetDragSpeed(); // Stop any animation

        Component currView = getView();
        int newViewX = Math.min(currView.getX() + currView.getWidth(), getWidth());
        int newViewY = getViewPortY();

        currView.setLocation(newViewX, newViewY);
        currView = null; // Help GC

        changeView(1);

        // Update the changed view to its new location
        getView().setLocation(newViewX, newViewY);
    }

    private void goPrev() {
        resetDragSpeed(); // Stop any animation

        Component currView = getView();
        int newViewX = Math.max(currView.getX(), 0) - getWidth();
        int newViewY = getViewPortY();

        currView.setLocation(newViewX, newViewY);
        currView = null; // Help GC

        changeView(-1);

        // Update the changed view to its new location
        getView().setLocation(newViewX, newViewY);
    }

    private void resetDragMode() {
        int bounceMode = 0;
        if (hasPreviousView()) {
            bounceMode |= BOUNCE_LEFT;
        }
        if (hasNextView()) {
            bounceMode |= BOUNCE_RIGHT;
        }
        setBounceMode(bounceMode);
    }
}
