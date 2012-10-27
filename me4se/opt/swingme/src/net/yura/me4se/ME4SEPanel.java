package net.yura.me4se;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import javax.microedition.midlet.ApplicationManager;
import javax.swing.SwingUtilities;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.plaf.nimbus.NimbusLookAndFeel;
import org.me4se.JadFile;

/**
 * @author Yura Mamyrin
 */
public class ME4SEPanel extends Container {

    private ApplicationManager manager;
    private DesktopPane desktop;
    private Frame frame1;

    public DesktopPane getDesktopPane() {
        return desktop;
    }

    public ME4SEPanel() {
        setLayout( new BorderLayout() );

        // this can only be happening in 1 thread at a time

        manager = ApplicationManager.createInstance(this, null );

        JadFile jad = new JadFile();
        jad.setValue("MIDlet-1", ",," + EmptyMidlet.class.getName());
        manager.launch(jad, 0);

        desktop = DesktopPane.getDesktopPane();

        //((EmptyMidlet)Midlet.getMidlet()).parent = this;

        // todo find this from a better place
        desktop.setLookAndFeel( new NimbusLookAndFeel() );

    }

    public ApplicationManager getApplicationManager() {
        return manager;
    }

    public void destroy() {
    //    can not use this as causes System.exit in all events
    //    manager.destroy(true, false); // true will cause System.exit
        
        try {

            Field man = ApplicationManager.class.getDeclaredField("manager");
            man.setAccessible(true);
            if (man.get(null) == manager) {
                System.out.println("[ME4SEPanel] setting manager to null");
                man.set(null, null);
            }

            ((EmptyMidlet)manager.active).destroyApp(true); // this will set desktop to null

        }
        catch(Exception ex) {
            throw new RuntimeException(ex);
        }
            
    }

    public java.awt.Component getComponent() {
        return getComponents()[0];
    }

    public void add(Panel panel) {

        frame1 = new Frame() {
            // do not allow anyone to close this window
            // if anyone tries, close the Swing Window instead
            public void setVisible(boolean b) {
                if (b) {
                    super.setVisible(b);
                }
                else {
                    actionPerformed(CMD_CLOSE);
                }
            }
        };
        frame1.setUndecorated(true);
        frame1.getContentPane().add(panel);
        frame1.setMaximum(true);
        //frame1.addWindowListener(this);   // this is not good enough as does not catch someone
                                            // calling "frame1.setVisible(false);" directly
        desktop.add(frame1);
    }

    // in case someone tries to close the window
    public void actionPerformed(String string) {
        Window window = SwingUtilities.getWindowAncestor(this);
        WindowEvent wev = new WindowEvent(window, WindowEvent.WINDOW_CLOSING);
        //Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        window.dispatchEvent(wev);
    }

    public Dimension getMinimumSize() {
        Dimension d = super.getPreferredSize();

        if (frame1!=null) {
            Dimension a = new Dimension( frame1.getWidth(), frame1.getHeight());
            frame1.pack(); // TODO: does this really set the height right away to the min???
            Dimension b = new Dimension( frame1.getWidth(), frame1.getHeight());
            //System.out.println("d="+d);
            frame1.setSize(a.width, a.height);
            return b;
        }

        return d;
    }

    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (d==null || d.width == 0 || d.height == 0) {
            return getMinimumSize();
        }
        return d;
    }
}
