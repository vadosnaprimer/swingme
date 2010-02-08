package net.yura.tools.translation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.microedition.lcdui.Graphics;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.TextField;
import net.yura.translation.MessageTool;
import net.yura.translation.MyNode;

/**
 * @author Yura Mamyrin
 */
public class XULTranslationTool extends MessageTool {

        ControlPanel control;

	public void valueChanged(TreeSelectionEvent e) {

            MyNode node = (MyNode)e.getPath().getLastPathComponent();

            control.scanForName( node.getName() );

        }


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
        meFrame.add( new Label("hello world") );
        meFrame.add( new TextField(), Graphics.BOTTOM );
        meFrame.setMaximum(true);
        me4sePanel.getDesktopPane().add(meFrame);
        Dimension d = new Dimension(100, 100);
        me4sePanel.setPreferredSize(d);
        me4sePanel.setMinimumSize(d);

        //manager.destroy(true, false);

        
        /*
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String a = e.getActionCommand();
                if ("synth".equals(a)) {


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
        */

        //JPanel area = new JPanel();
        JDesktopPane area = new JDesktopPane();
        JInternalFrame box = new JInternalFrame("Preview");
        box.add(me4sePanel);
        box.setResizable(true);
        box.pack();
        box.setVisible(true);
        area.add(box);

        tt.control = new ControlPanel(me4sePanel,box,tt);

        panel.add(tt.control,BorderLayout.NORTH);
        panel.add(area);


        frame.setVisible(true);
    }

}
