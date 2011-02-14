package net.yura.tools.translation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.microedition.midlet.ApplicationManager;
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

        // todo find this from a better place
        desktop.setLookAndFeel( new NimbusLookAndFeel() );

    }

    //public void destroy() {
    //    can not use this as causes System.exit in all events
    //    manager.destroy(true, false); // true will cause System.exit
    //}

    public java.awt.Component getComponent() {
        return getComponents()[0];
    }

    public void add(Panel panel) {

        frame1 = new Frame();
        frame1.setUndecorated(true);
        frame1.add(panel);
        frame1.setMaximum(true);

        desktop.add(frame1);
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
