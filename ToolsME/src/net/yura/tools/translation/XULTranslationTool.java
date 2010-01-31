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
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;
import net.yura.translation.MessageTool;
import org.me4se.JadFile;

/**
 * @author Yura Mamyrin
 */
public class XULTranslationTool extends MessageTool {

    public static void main(String[] args) {

        // this is needed as ME4SE uses AWT and not Swing
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

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

        final ME4SEPanel me4sePanel = new ME4SEPanel();

        final Frame meFrame = new Frame("Test");
        Label l = new Label("hello world");
        meFrame.add(l);
        meFrame.setMaximum(true);
        me4sePanel.getDesktopPane().add(meFrame);

        //manager.destroy(true, false);

        final PLAFLoader loader = new PLAFLoader();

        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String a = e.getActionCommand();
                if ("synth".equals(a)) {

                    SynthLookAndFeel synthPlaf = loader.loadNewSynth(frame);
                    if (synthPlaf!=null) {
                        DesktopPane desktop = me4sePanel.getDesktopPane();
                        desktop.setLookAndFeel(synthPlaf);
                        DesktopPane.updateComponentTreeUI(meFrame);
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


        JSplitPane sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, me4sePanel, new JPanel());
        sp1.setContinuousLayout(true);
        panel.add(sp1);


        panel.add(menuBar,BorderLayout.NORTH);


        frame.setVisible(true);
    }

}
