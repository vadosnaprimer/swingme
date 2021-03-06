/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.test;

import java.util.Hashtable;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.io.ClipboardManager;
import net.yura.mobile.io.LocationMonitor;
import net.yura.mobile.io.ServiceLink.Task;
import net.yura.mobile.test.MainPane.Section;
import net.yura.mobile.io.ContactManager;

/**
 *
 * @author Administrator
 */
public class ServiceLinkTest  extends Section {

    private TextArea infoLabel;
    private Panel info;

        private ClipboardManager clipboardManager = ClipboardManager.getInstance();
        private MyLocationMonitor locationMonitor;
        private MyContactManager contactManager;

        class MyContactManager extends ContactManager {
            public void handleTask(Task task) {
                String strMethod = task.getMethod();
                if ("PutContactCount".equals(strMethod)) {
                    infoLabel.setText("");
                    Integer value = (Integer) task.getObject();
                    infoLabel.append(Integer.toString(value.intValue()));
                    infoLabel.append("\n");
                }
            }
        }
        class MyLocationMonitor extends LocationMonitor {
            public void handleTask(Task task) {
                String strMethod = task.getMethod();
                if ("GetCellIdError".equals(strMethod)) {
                    infoLabel.setText("GetCellIdError");
                }
                if ("PutCellId".equals(strMethod)) {
                    Hashtable hashtable = (Hashtable) task.getObject();
                    java.util.Enumeration e = hashtable.keys();
                    infoLabel.setText("");
                    infoLabel.append(strMethod);
                    while (e.hasMoreElements())
                    {
                        String key = (String) e.nextElement();
                        String value = (String) hashtable.get(key);
                        infoLabel.append("\n");
                        infoLabel.append(key);
                        infoLabel.append(" = ");
                        infoLabel.append(value);
                    }
                }
                if ("GetWiFiSsListError".equals(strMethod)) {
                    infoLabel.setText("GetWiFiSsListError");
                }
                if ("PutWiFiSsList".equals(strMethod)) {
                    Hashtable hashtable = (Hashtable) task.getObject();
                    java.util.Enumeration e = hashtable.keys();
                    infoLabel.setText("");
                    while (e.hasMoreElements())
                    {
                        String key = (String) e.nextElement();
                        Integer value = (Integer) hashtable.get(key);
                        infoLabel.append(strMethod);
                        infoLabel.append("\n");
                        infoLabel.append(key);
                        infoLabel.append("\n");
                        infoLabel.append(Integer.toString(value.intValue()));
                        infoLabel.append("\n");
                    }
                }

            }
        }


    public void createTests() {
                                // service link
                                addTest("Connect To Service","serviceConnect");
                                addTest("Get Cell Id","cellIdTest");
                                addTest("Poll Cell Id","pollIdTest");
                                addTest("Get Wifi","getWifiTest");
                                addTest("Poll Wifi","pollWifiTest");
                                addTest("Get Clipboard","GetClipboardTest");
                                addTest("Put XYZ to Clipboard","PutClipboardTest");
                                addTest("Close Connection","serviceDisconnect");
                                addTest("Count Contacts","countContacts");
    }

    public void openTest(String actionCommand) {

                if ("serviceConnect".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();

                        infoLabel = new TextArea("Service Connect\n");
                        infoLabel.setFocusable(false);
                        info.add(infoLabel);
                        infoLabel.append("\nDone\n");
     			addToScrollPane(info, null );
                }
                else if ("cellIdTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();


                        infoLabel = new TextArea("Get Cell Id\n");
                        infoLabel.setFocusable(false);
                        locationMonitor.getCellId();
                        info.add(infoLabel);
     			addToScrollPane(info, null );
                }
                else if ("pollIdTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();


                        infoLabel = new TextArea("Poll Cell Id\n");
                        infoLabel.setFocusable(false);
                        locationMonitor.setNotifyForCellId(true);
                        info.add(infoLabel);
     			addToScrollPane(info, null );
                }
                else if ("getWifiTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();

                        infoLabel = new TextArea("Get Wifi List\n");
                        infoLabel.setFocusable(false);
                        locationMonitor.getWifiList();
                        info.add(infoLabel);
     			addToScrollPane(info, null );
                }
                else if ("pollWifiTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();

                        infoLabel = new TextArea("Poll Wifi List\n");
                        infoLabel.setFocusable(false);
                        locationMonitor.setNotifyForWifiList(true);
                        info.add(infoLabel);
     			addToScrollPane(info, null );
                }
                else if ("GetClipboardTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();

                        infoLabel = new TextArea("Get Clipboard\n");
                        infoLabel.setFocusable(false);
                        String txt = clipboardManager.getText();
                        if (txt!=null) {
                            infoLabel.append(txt);
                        }
                        info.add(infoLabel);
     			addToScrollPane(info, null );
                }
                else if ("PutClipboardTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();

                        infoLabel = new TextArea("Put Clipboard\n");
                        infoLabel.setFocusable(false);
                        clipboardManager.setText("XYZ");
                        info.add(infoLabel);
     			addToScrollPane(info, null );
                }
                else if ("serviceDisconnect".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        locationMonitor = null;

                        infoLabel = new TextArea("Service Disconnect\n");
                        infoLabel.setFocusable(false);
                        info.add(infoLabel);
                        infoLabel.append("\nDone\n");
     			addToScrollPane(info, null );
                }
                else if ("countContacts".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (contactManager == null) {
                            contactManager = new MyContactManager();
                        }
                        infoLabel = new TextArea("Count Contacts\n");
                        infoLabel.setFocusable(false);
                        contactManager.getContactCount();
                        info.add(infoLabel);
     			addToScrollPane(info, null );
                }
    }

}
