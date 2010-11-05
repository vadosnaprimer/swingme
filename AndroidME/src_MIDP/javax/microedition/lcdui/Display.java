package javax.microedition.lcdui;

import java.util.Hashtable;

import javax.microedition.midlet.MIDlet;

import android.app.Activity;
import android.view.View;

public class Display
{
    public static final int COLOR_BACKGROUND = 0;
    public static final int COLOR_FOREGROUND = 1;
    public static final int COLOR_BORDER     = 2;
    public static final int COLOR_HIGHLIGHTED_BACKGROUND     = 3;
    public static final int COLOR_HIGHLIGHTED_FOREGROUND     = 4;
    public static final int COLOR_HIGHLIGHTED_BORDER         = 5;


    public static final int LIST_ELEMENT             = 0;
    public static final int ALERT                    = 1;
    public static final int CHOICE_GROUP_ELEMENT     = 2;

    private static final Hashtable<MIDlet, Display> DISPLAYS = new Hashtable<MIDlet, Display>( 1 );

    public static Display getDisplay( MIDlet midlet )
    {
        Display display = DISPLAYS.get( midlet );
        if( display == null )
        {
            display = new Display( midlet );
            DISPLAYS.put( midlet, display );
        }
        return display;
    }

    private Displayable current;
    private MIDlet midlet;

    private Display( MIDlet midlet )
    {
        this.midlet = midlet;
    }

    public Displayable getCurrent()
    {
        return this.current;
    }

    public int getColor( int colorSpecifier ) {
        // TODO :is there any way to look this up
        int color;
        switch( colorSpecifier ) {
        case COLOR_BACKGROUND:
            color = 0x000000;
            break;
        case COLOR_FOREGROUND:
            color = 0xFFFFFF;
            break;
        case COLOR_BORDER:
            color = 0x888888;
            break;
        case COLOR_HIGHLIGHTED_BACKGROUND:
            color = 0xff8600;
            break;
        case COLOR_HIGHLIGHTED_FOREGROUND:
            color = 0x000000;
            break;
        case COLOR_HIGHLIGHTED_BORDER:
            color = 0xAAAAAA;
            break;
        default:
            color = 0xFF0000;
            break;
        }
        return color;
    }

    public int getBestImageWidth( int imageType ) {
        return 48;
    }

    public int getBestImageHeight( int imageType ) {
        return 48;
    }

    public MIDlet getMIDlet()
    {
        return this.midlet;
    }

    public void setCurrent(final Displayable current) {
        if (current == null) {
            // Set Application to background
            midlet.getActivity().moveTaskToBack(true);
            return;
        }

        if (current != this.current) {
            final Displayable old = this.current;
            this.current = current;

            this.midlet.invokeAndWait(new Runnable() {
                public void run() {
                    if (old != null) {
                        old.setCurrentDisplay(null);
                    }

                    current.setCurrentDisplay(Display.this);
                    current.initDisplayable(Display.this.midlet);

                    Activity activity = Display.this.midlet.getActivity();
                    View view = current.getView();
                    if (view != null) {
                        activity.setContentView(view);
                        view.requestFocus();
                    }
                }
            });

            // Wait for the view to be set...
            this.midlet.invokeAndWait(new Thread());
        }
    }

    public void callSerially(Runnable runner) {
        // delegate to Canvas
        midlet.getHandler().post(runner);
    }

    public void vibrate(int duration) {
        // TODO Auto-generated method stub
    }

    public void flashBacklight(int duration) {
        // TODO Auto-generated method stub
    }

    public int numAlphaLevels() {
        // TODO Auto-generated method stub
        return 255;
    }

    public int numColors() {
        // TODO Auto-generated method stub
        return 0x00FFFFFF;
    }
}
