/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.test;

import net.yura.mobile.io.ClipboardManager;
import net.yura.mobile.io.LocationMonitor;
import net.yura.mobile.test.MainPane.Section;

/**
 *
 * @author Administrator
 */
public class ServiceLinkTest  extends Section {

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
    }

    public void openTest(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
        private MyClipboardManager clipboardManager;
        private MyLocationMonitor locationMonitor;
        class MyClipboardManager extends ClipboardManager {
            public void handleTask(Task task) {
                String strMethod = task.getMethod();
                if ("GetClipboardTextError".equals(strMethod)) {
                    infoLabel.setText("GetClipboardTextError");
                }
                if ("PutClipboardText".equals(strMethod)) {
                    infoLabel.setText("");
                    infoLabel.append((String) task.getObject());
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
                else if ("serviceConnect".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();

                        infoLabel = new TextArea("Service Connect\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        info.add(infoLabel);
                        infoLabel.append("\nDone\n");
     			addToScrollPane(info, null,  makeButton("Back","mainmenu") );
                }
                else if ("cellIdTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();


                        infoLabel = new TextArea("Get Cell Id\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        locationMonitor.getCellId();
                        info.add(infoLabel);
     			addToScrollPane(info, null,  makeButton("Back","mainmenu") );
                }
                else if ("pollIdTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();


                        infoLabel = new TextArea("Poll Cell Id\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        locationMonitor.setNotifyForCellId(true);
                        info.add(infoLabel);
     			addToScrollPane(info, null,  makeButton("Back","mainmenu") );
                }
                else if ("getWifiTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();

                        infoLabel = new TextArea("Get Wifi List\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        locationMonitor.getWifiList();
                        info.add(infoLabel);
     			addToScrollPane(info, null,  makeButton("Back","mainmenu") );
                }
                else if ("pollWifiTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (locationMonitor == null)
                            locationMonitor = new MyLocationMonitor();

                        infoLabel = new TextArea("Poll Wifi List\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        locationMonitor.setNotifyForWifiList(true);
                        info.add(infoLabel);
     			addToScrollPane(info, null,  makeButton("Back","mainmenu") );
                }
                else if ("GetClipboardTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (clipboardManager == null)
                            clipboardManager = new MyClipboardManager();

                        infoLabel = new TextArea("Get Clipboard\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        clipboardManager.getClipboard();
                        info.add(infoLabel);
     			addToScrollPane(info, null,  makeButton("Back","mainmenu") );
                }
                else if ("PutClipboardTest".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        if (clipboardManager == null)
                            clipboardManager = new MyClipboardManager();

                        infoLabel = new TextArea("Put Clipboard\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        clipboardManager.putClipboard("XYZ");
                        info.add(infoLabel);
     			addToScrollPane(info, null,  makeButton("Back","mainmenu") );
                }
                else if ("serviceDisconnect".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        locationMonitor = null;

                        infoLabel = new TextArea("Service Disconnect\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        info.add(infoLabel);
                        infoLabel.append("\nDone\n");
     			addToScrollPane(info, null,  makeButton("Back","mainmenu") );
                }
*/
}
