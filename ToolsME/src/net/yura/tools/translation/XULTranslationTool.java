package net.yura.tools.translation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.ApplicationManager;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;
import net.yura.translation.MessageTool;
import org.me4se.JadFile;

/**
 * @author Yura Mamyrin
 */
public class XULTranslationTool extends MessageTool {

    public static void main(String[] args) {

        XULTranslationTool tt = new XULTranslationTool();

        final JFrame frame = new JFrame("XUL Translation Tool");


        frame.getContentPane().add( tt.getToolBar() , BorderLayout.NORTH);
        //frame.getContentPane().add( tt );

        frame.setSize(700,500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );


        JPanel panel = new JPanel( new BorderLayout() );
        panel.setMinimumSize( new Dimension(50, 50) );

        JSplitPane split = new JSplitPane();
        split.setDividerLocation(500);
        split.setLeftComponent(tt);
        split.setRightComponent(panel);
        split.setResizeWeight(1);
        split.setContinuousLayout(true);

        frame.getContentPane().add(split);

        JPanel a = new JPanel( new BorderLayout() );
        JPanel b = new JPanel( new BorderLayout() );
        JSplitPane sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, a, b);
        sp1.setContinuousLayout(true);
        panel.add(sp1);

        final ApplicationManager manager = ApplicationManager.createInstance(a, null );
        JadFile jad = new JadFile();
        jad.setValue("MIDlet-1", ",," + EmptyMidlet.class.getName());
        manager.launch(jad, 0);



        final ApplicationManager manager2 = ApplicationManager.createInstance(b, null );
        JadFile jad2 = new JadFile();
        jad2.setValue("MIDlet-1", ",," + EmptyMidlet.class.getName());
        manager2.launch(jad2, 0);


        //manager.destroy(true, false);

        final PLAFLoader loader = new PLAFLoader();

        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String a = e.getActionCommand();
                if ("synth".equals(a)) {

                    SynthLookAndFeel synthPlaf = loader.loadNewSynth(frame);
                    if (synthPlaf!=null) {
                        EmptyMidlet midlet = (EmptyMidlet)manager.active;
                        DesktopPane desktop = (DesktopPane) Display.getDisplay(midlet).getCurrent();
                        desktop.setLookAndFeel(synthPlaf);
                        //DesktopPane.updateComponentTreeUI(midlet.mainWindow);
                    }
                }
            }
        };

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Look&Feel");
        menuBar.add(menu);
        JMenuItem synth = new JMenuItem("Synth");
        synth.setActionCommand("synth");
        synth.addActionListener(al);
        menu.add(synth);

        panel.add(menuBar,BorderLayout.NORTH);


        frame.setVisible(true);
    }

}
