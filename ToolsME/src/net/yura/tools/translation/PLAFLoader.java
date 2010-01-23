package net.yura.tools.translation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;

/**
 * @author Yura Mamyrin
 */
public class PLAFLoader implements ActionListener {

    private File current;



    public SynthLookAndFeel loadNewSynth(Window parent) {

        ME4SEPanel wrapper = new ME4SEPanel();

        Panel panel = null;
        try {
            XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/synth_load.xml"), this);
            panel = (Panel)loader.getRoot();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

//        ME4SEDialog dialog = new ME4SEDialog(parent, "Load Synth", ModalityType.DOCUMENT_MODAL);
//        dialog.setContentPane(panel);
//        dialog.setVisible(true);


        wrapper.add(panel);

        Dialog dialog = new Dialog(parent, "Load Synth", ModalityType.DOCUMENT_MODAL);
        dialog.add(wrapper);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

            if (current==null) {
                current = new File(".");
            }

            JFileChooser chooser = new JFileChooser(current);

            int result = chooser.showOpenDialog(parent);

            if (result==JFileChooser.APPROVE_OPTION) {

                File file = chooser.getSelectedFile();
                current = file.getParentFile();

                ME4SESynth synth = new ME4SESynth(current);
                try {
                    synth.load( new FileInputStream(file) );
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(parent, ex.toString() );
                }

                return synth;
            }

            return null;
    }

    public void actionPerformed(String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public class ME4SESynth extends SynthLookAndFeel {

        private File current;

        public ME4SESynth(File file) {
            current = file;
        }

        @Override
        protected Icon getIcon( String path ,int x,int y,int w,int h) {
            try {
                InputStream imgStream = getResourceAsStream(path);
                Image img = Image.createImage(imgStream);
                if (w>0 && h>0) {
                    img = Image.createImage(img, x, y, w, h, Sprite.TRANS_NONE);
                }
                return new Icon(img);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected InputStream getResourceAsStream(String path) {
            try {
                return new FileInputStream(new File(current, path));
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
