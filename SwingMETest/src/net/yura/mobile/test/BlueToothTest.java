/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.test;

import javax.bluetooth.UUID;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.io.BTDiscovery;
import net.yura.mobile.io.BTService;
import net.yura.mobile.test.MainPane.Section;

/**
 *
 * @author Administrator
 */
public class BlueToothTest extends Section {
        MyBluetoothClient btClient;
        private MyBTDiscovery btDiscovery;
        MyBluetoothServer btServer;

        Panel info;
        TextArea infoLabel;


        class MyBTDiscovery extends BTDiscovery {
            public void handleMyId(String name, String address) {
                    infoLabel.setText("\nMy Name: " + name);
                    infoLabel.append("\nMy Address: " + address);
            }
            public void handleRemoteId(String name, String address) {
                    infoLabel.append("\nRemote Name: " + name);
                    infoLabel.append("\nRemote Address: " + address);
            }
            public void handleInquiryCompleted() {
                    infoLabel.append("\nDone");
            }
            public void handleException(Exception ex) {
                    infoLabel.append("\n" + ex.getMessage());
            }
        }
        class MyBluetoothServer extends BTService {
            public boolean registerServer(UUID aUuid, String name) {
                if (super.registerServer(aUuid, name)) {
                    registerForTask("BTClientToServer", this);
                    return true;
                }
                return false;
            }
            public void send() {
                  addToOutbox(new Task("BTServerToClient", null));
                  infoLabel.append("Message Sent");
            }
            public void handleTask(Task task) {
                infoLabel.append(task.getMethod());
            }
        }
        class MyBluetoothClient extends BTService {
            public boolean registerClient(UUID aUuid) {
                if (super.registerClient(aUuid)) {
                    registerForTask("BTServerToClient", this);
                    return true;
                }
                return false;
            }
            public void send() {
                  addToOutbox(new Task("BTClientToServer", null));
                  infoLabel.append("Message Sent");
            }
            public void handleTask(Task task) {
                infoLabel.append(task.getMethod());
            }
        }


    public void createTests() {
                                // bluetooth
                                addTest("Open Bluetooth Server","BTServer");
                                addTest("Open Bluetooth Client","BTClient");
                                addTest("Bluetooth Server Send","BTServerSend");
                                addTest("Bluetooth Client Send","BTClientSend");
                                addTest("Start Bluetooth Discovery","BTDiscovery");
    }

    public void openTest(String actionCommand) {


                if ("BTServer".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        infoLabel = new TextArea("Bluetooth Server Starting\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        info.add(infoLabel);
     			addToScrollPane(info,null);
                        javax.bluetooth.UUID uuid = new javax.bluetooth.UUID("D6F7CD62B97111DE8F1720ED55D89593", false);
                        if (btServer == null) {
                            btServer = new MyBluetoothServer();
                            btServer.registerServer(uuid, "Badoo");
                        }

                }
                else if ("BTServerSend".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        infoLabel = new TextArea("Bluetooth Server Send\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        info.add(infoLabel);
     			addToScrollPane(info,null);
                        if (btServer != null) {
                            btServer.send();
                        } else {
                            infoLabel.append("\nNo Server");
                        }

                }
                else if ("BTClient".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        infoLabel = new TextArea("Bluetooth Client Starting\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        info.add(infoLabel);
     			addToScrollPane(info,null);
                        javax.bluetooth.UUID uuid = new javax.bluetooth.UUID("D6F7CD62B97111DE8F1720ED55D89593", false);
                        if (btClient == null) {
                            btClient = new MyBluetoothClient();
                            btClient.registerClient(uuid);
                        }

                }
                else if ("BTClientSend".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        infoLabel = new TextArea("Bluetooth Client Send\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        info.add(infoLabel);
     			addToScrollPane(info,null);
                        if (btClient != null) {
                            btClient.send();
                        } else {
                            infoLabel.append("\nNo Client");
                        }

                }
                else if ("BTDiscovery".equals(actionCommand)) {
			if (info==null) {
                            info = new Panel( new BorderLayout() );
                        }
                        info.removeAll();
                        infoLabel = new TextArea("Bluetooth Discovery Starting\n",Graphics.LEFT);
                        infoLabel.setFocusable(false);
                        info.add(infoLabel);
     			addToScrollPane(info,null);
                        if (btDiscovery == null)
                            btDiscovery = new MyBTDiscovery();
                        infoLabel.append("\nAbout to start");
                        if (!btDiscovery.start(javax.bluetooth.DiscoveryAgent.GIAC))
                            infoLabel.append("\nNot Supported");
                        else
                            infoLabel.append("\nStarting");

                }
    }
}
