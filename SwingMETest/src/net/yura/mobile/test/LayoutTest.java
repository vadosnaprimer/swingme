/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.test;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.test.MainPane.Section;

/**
 *
 * @author Administrator
 */
public class LayoutTest  extends Section {

    private Frame xuldialog;

    public void createTests() {

                                // layout
                                addTest("XUL mobile demo","xulTest");
                                addTest("XUL tabbedpane","xulTest1");
                                addTest("XUL generate","xulTest2");
                                addTest("XUL demodialog","xulTest3");
    }

    public void openTest(String actionCommand) {

                if ("xulTest".equals(actionCommand)) {

                    Panel panel = null;

                    try {
                        XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/demo.xml"), this);
                        //XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/tabbedpane.xml"), this);
                        //XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/generate.xml"), this);
                        panel = (Panel)loader.getRoot();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    addToScrollPane(panel, null );
                }
                else if ("xulTest1".equals(actionCommand)) {

                    Panel panel = null;

                    try {
                        //XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/demo.xml"), this);
                        XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/tabbedpane.xml"), this);
                        //XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/generate.xml"), this);
                        panel = (Panel)loader.getRoot();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    addToScrollPane(panel, null );
                }
                else if ("xulTest2".equals(actionCommand)) {

                    final Frame window = new Frame("GEN DEMO");

                    try {
                        XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/generate.xml"), new ActionListener() {
                            public void actionPerformed(String arg0) {
                                if ("ok()".equals(arg0)) {
                                    OptionPane.showMessageDialog(this, "you clicked ok", "info", OptionPane.INFORMATION_MESSAGE);

                                }
                                else if ("close()".equals(arg0) || "ok".equals(arg0)) {
                                    window.setVisible(false);
                                }
                                else if ("buttonOutputDirClicked()".equals(arg0)) {
System.out.println("open file browser");
                                }
                            }
                        });
                        window.setContentPane( (Panel)loader.getRoot() );
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    window.setMaximum(true);
                    window.setVisible(true);

                }
                else if ("xulTest3".equals(actionCommand)) {

                    try {
                        //XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/demo.xml"), this);
                        //XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/tabbedpane.xml"), this);
                        XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/demodialog.xml"), new ActionListener() {
                            public void actionPerformed(String arg0) {
                                if ("closeDialog".equals(arg0)) {
                                    xuldialog.setVisible(false);
                                }
                            }
                        });
                        xuldialog = (Frame)loader.getRoot();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
//System.out.println(xuldialog);
                    xuldialog.pack();
                    xuldialog.setVisible(true);
                }
    }
}
