package net.yura.tools.translation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.util.Properties;
import net.yura.translation.Mtcomm;
import net.yura.translation.plugins.PropertiesComm;

/**
 * @author Yura Mamyrin
 */
public class ControlPanel extends ME4SEPanel implements ActionListener {

    XULTranslationTool tt;
    ME4SEPanel me4sePanel;
    JInternalFrame box;

    XULLoader loader;
    
    File baseXULdir;

    public ControlPanel(ME4SEPanel me4sePanel, JInternalFrame box,XULTranslationTool tt) {
        this.me4sePanel = me4sePanel;
        this.box = box;
        this.tt = tt;
        
        Panel panel = null;
        try {
            loader = XULLoader.load( getClass().getResourceAsStream("/control.xml"), this);
            panel = (Panel)loader.getRoot();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        add(panel);

    }

    public void actionPerformed(String arg0) {
        if ("new_synth".equals(arg0)) {

            final PLAFLoader ploader = new PLAFLoader();

            LookAndFeel synthPlaf = ploader.loadNewSynth( SwingUtilities.getWindowAncestor(this) );
            if (synthPlaf!=null) {
                setLookAndFeel(synthPlaf);
            }

        }
        else if ("set_xul_dir".equals(arg0)) {

            JFileChooser chooser = new JFileChooser(baseXULdir==null?new File("."):baseXULdir);

            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = chooser.showOpenDialog(this);

            if (result==JFileChooser.APPROVE_OPTION) {
                setBaseXULDir( chooser.getSelectedFile() );
                
            }

        }
        else if ("load_xul".equals(arg0)) {

            List list = (List)loader.find("xul_list");

            XULFile file = (XULFile)list.getSelectedValue();

            Properties properties = null;

            Mtcomm conn = tt.getPlugin();

            if (conn instanceof PropertiesComm) {
                PropertiesComm p = (PropertiesComm)conn;
                final java.util.Properties defaultProp = p.getLocaleProperties( null );
                final java.util.Properties prop = p.getLocaleProperties( tt.getCurrentLocale() );

                properties = new Properties() {
                    @Override
                    public String getProperty(String key) {
                        String first = prop.getProperty(key);
                        return first!=null?first:defaultProp.getProperty(key);
                    }
                };
            }
            else {
                // TODO make it work for all plugins
            }

            // hack tso themeing works again
            me4sePanel.getDesktopPane().showNotify();

            Component panel = null;
            try {

                XULLoader xloader = new XULLoader() {
                    @Override
                    public Icon loadIcon(String value) {
                        try {
                            Image img = Image.createImage( new FileInputStream(new File(baseXULdir, value)) );
                            return new Icon(img);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            return null;
                        }
                    }
                };
                xloader.load( new FileReader(file.file), null, properties);
                panel = xloader.getRoot();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            DesktopPane dp = me4sePanel.getDesktopPane();

            // remove all windows
            Vector<Window> v = dp.getAllFrames();
            while (!v.isEmpty()) {
                dp.remove( v.firstElement() );
            }

            if (panel instanceof Window) {
                if (panel instanceof Frame) {
                    if ( !((Frame)panel).isMaximum()) {
                        // YURA:TODO
                        // THIS IS CORRECT but in badoo, it thinks all
                        // thse windows need to be maximum
                        // ((Window)panel).pack();
                        System.err.println("XUL NOT Maximum: "+file.toString());
                        ((Frame)panel).setMaximum(true);
                    }
                }
                dp.add((Window)panel);
            }
            else {
                me4sePanel.add( (Panel)panel );
            }

        }
        else {
            System.out.println("unknown action "+arg0);
        }
    }

    public void setBaseXULDir(File selectedFile) {
        baseXULdir = selectedFile;
    }

    public void setLookAndFeel(LookAndFeel plaf) {
            DesktopPane desktop = me4sePanel.getDesktopPane();
            desktop.setLookAndFeel(plaf);
            Vector frames = desktop.getAllFrames();
            for (int c = 0;c<frames.size();c++) {
                Component comp = (Component)frames.elementAt(c);
                DesktopPane.updateComponentTreeUI( comp );
            }
            desktop.repaint();
    }

    class XULFile {
        File file;
        public XULFile(File f) {
            file = f;
        }
        public String toString() {
            return file.getName();
        }
    }

    void scanForName(String name) {
        if (baseXULdir!=null) {

            XULScanner scanner = new XULScanner();

            Vector<File> result = scanner.scan(baseXULdir, name);

            System.out.println("result = " + result);

            List list = (List)loader.find("xul_list");

            Vector v = new Vector(result.size());
            for (File f: result) {
                v.add( new XULFile(f) );
            }

            list.setListData(v);

            list.getParent().getParent().revalidate();
            list.getParent().getParent().repaint();

        }
    }

}
