package org.me4se.psi.java1.nfc;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.microedition.contactless.DiscoveryManager;
import javax.microedition.contactless.TargetListener;
import javax.microedition.contactless.TargetProperties;
import javax.microedition.midlet.ApplicationManager;

import org.me4se.Initializer;

import com.phidgets.Phidget;
import com.phidgets.RFIDPhidget;
import com.phidgets.event.TagGainEvent;
import com.phidgets.event.TagGainListener;

public class ContactlessInitializer implements Initializer, ActionListener, TagGainListener {

  private MenuItem rfidTagMenuItem;
  private MenuItem ndefTagMenuItem;
  private MenuItem iso14443TagMenuItem;
  private MenuItem enablePhidgetRfid;
  private MenuItem disablePhidgetRfid;
  
  private RFIDPhidget rfid;
  
  public void initialize(ApplicationManager manager) {
    MenuBar menuBar = manager.frame.getMenuBar();
    
    Menu contactlessMenu = new Menu("NFC");

    rfidTagMenuItem = new MenuItem("RFID Tag in range...");
    rfidTagMenuItem.addActionListener(this);
    contactlessMenu.add(rfidTagMenuItem);
    
    ndefTagMenuItem = new MenuItem("NDEF Tag in range...");
    ndefTagMenuItem.addActionListener(this);
    contactlessMenu.add(ndefTagMenuItem);
    
    iso14443TagMenuItem = new MenuItem("ISO14443 Tag in range...");
    iso14443TagMenuItem.addActionListener(this);
    contactlessMenu.add(iso14443TagMenuItem);
    
    contactlessMenu.addSeparator();
    
    enablePhidgetRfid = new MenuItem("Enable Phidget RFID reader");
    enablePhidgetRfid.addActionListener(this);
    contactlessMenu.add(enablePhidgetRfid);
    
    disablePhidgetRfid = new MenuItem("Disable Phidget RFID reader");
    disablePhidgetRfid.addActionListener(this);
    disablePhidgetRfid.setEnabled(false);
    contactlessMenu.add(disablePhidgetRfid);
    
    menuBar.add(contactlessMenu); 
  }
  
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    if (source == enablePhidgetRfid) {
      // enable the reader here
      enablePhidgetRfid.setEnabled(false);
      disablePhidgetRfid.setEnabled(true);
      
      try {
        System.out.println(Phidget.getLibraryVersion());
        rfid = new RFIDPhidget();
        rfid.addTagGainListener(this);   
        rfid.openAny();
        rfid.waitForAttachment(1000);
        rfid.setAntennaOn(true);
        rfid.setLEDOn(true);
      }
      catch (Exception ex) {
        System.out.println("ME4SE Phidget Error: " + ex.getMessage());
      }
    
    }
    else if (source == disablePhidgetRfid) {
      // disable the reader here
      enablePhidgetRfid.setEnabled(true);
      disablePhidgetRfid.setEnabled(false);
      try {
        if (rfid != null)
          rfid.setAntennaOn(false);
          rfid.setLEDOn(false);
          rfid.close();
        rfid = null;
      }
      catch (Exception ex) {
        System.out.println("ME4SE Phidget Error: " + ex.getMessage());
      }
    }
    else if (source == rfidTagMenuItem) {
      Vector listeners = DiscoveryManager.targetListeners;
      for (int i = 0; i < listeners.size(); i++) {
        TargetListenerContainer cont = (TargetListenerContainer)listeners.elementAt(i);
        TargetListener listener = cont.getListener();
        listener.targetDetected(new TargetProperties[]{new SimpleTargetProperties("0011224455")});
      }
    }
    else if (source == ndefTagMenuItem) {
      System.out.println("ME4SE: NDEF Tag in Range !");      
    }
    else if (source == iso14443TagMenuItem) {
      
      System.out.println("ME4SE: ISO14443 Tag in Range !");
    }
  }
  
  public void tagGained(TagGainEvent tagGainEvent) {
    
    System.out.println(tagGainEvent.getValue());
    
    Vector listeners = DiscoveryManager.targetListeners;
    for (int i = 0; i < listeners.size(); i++) {
      TargetListenerContainer cont = (TargetListenerContainer)listeners.elementAt(i);
      TargetListener listener = cont.getListener();
      listener.targetDetected(new TargetProperties[]{new SimpleTargetProperties(tagGainEvent.getValue())});
    }
  }
}
