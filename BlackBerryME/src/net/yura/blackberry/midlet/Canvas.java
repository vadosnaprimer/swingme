package net.yura.blackberry.midlet;

import net.rim.device.api.lcdui.BlackBerryCanvas;
import net.rim.device.api.ui.TouchEvent;

public abstract class Canvas extends BlackBerryCanvas {

    // copy and paste from SwingME DesktopPane
    private static final int DRAGGED = 0;
    private static final int PRESSED = 1;
    private static final int RELEASED = 2;
    //private static final int CANCEL = 3;

    int x1=-1,x2=-1,y1=-1,y2=-1;
    
    public void touchEvent(TouchEvent message) {

        int x1 = message.getX(1);
        int y1 = message.getY(1);
        
        int x2 = message.getX(2);
        int y2 = message.getY(2);
        
        if (x1>=0 && y1>=0 && x2>=0 && y2>=0) {
            //int event = message.getEvent();
            int swingEvent;
            //switch (event) {
            //    case TouchEvent.DOWN: swingEvent = PRESSED; break;
            //    case TouchEvent.UP: swingEvent = RELEASED; break;
            //    case TouchEvent.MOVE: swingEvent = DRAGGED; break;
            //}
            if (this.x1<0 || this.y1<0 || this.x2<0 || this.y2<0) {
                swingEvent = PRESSED;
            }
            else {
                swingEvent = DRAGGED;
            }
            multitouchEvent(new int[]{swingEvent,swingEvent}, new int[]{x1,x2}, new int[]{y1,y2});
        }
        else if (this.x1>=0 && this.y1>=0 && this.x2>=0 && this.y2>=0) {
            multitouchEvent(new int[]{RELEASED,RELEASED}, new int[]{this.x1,this.x2}, new int[]{this.y1,this.y2});
        }
        else {
            System.out.println("BB multitouchEvent skip "+message.getEvent()+" point1="+x1+" "+y1+" point2="+x2+" "+y2);
        }
        
        this.x1=x1;
        this.y1=y1;
        this.x2=x2;
        this.y2=y2;
    }

    public void multitouchEvent(int[] type, int[] x, int[] y) { }

}
