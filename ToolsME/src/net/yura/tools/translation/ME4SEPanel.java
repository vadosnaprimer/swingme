package net.yura.tools.translation;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.microedition.midlet.ApplicationManager;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.plaf.MetalLookAndFeel;
import org.me4se.JadFile;

/**
 * @author Yura Mamyrin
 */
public class ME4SEPanel extends Container {

    private DesktopPane desktop;

    public ME4SEPanel() {
        setLayout( new BorderLayout() );

        // this can only be happening in 1 thread at a time

        final ApplicationManager manager = ApplicationManager.createInstance(this, null );

        JadFile jad = new JadFile();
        jad.setValue("MIDlet-1", ",," + EmptyMidlet.class.getName());
        manager.launch(jad, 0);

        desktop = DesktopPane.getDesktopPane();

        // todo find this from a better place
        desktop.setLookAndFeel( new MetalLookAndFeel() );
    }

    public void add(Panel panel) {

        Frame frame1 = new Frame();
        frame1.setUndecorated(true);
        frame1.add(panel);
        frame1.setMaximum(true);

        desktop.add(frame1);
    }

}
