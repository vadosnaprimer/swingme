package org.me4se.psi.java1.location;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.microedition.location.LocationProvider;
import javax.microedition.location.LocationProviderImpl;
import javax.microedition.midlet.ApplicationManager;

import org.me4se.Initializer;


public class LocationInitializer implements Initializer, ActionListener {

  private MenuItem statusAvailableMenuItem;
  private MenuItem statusOutOfServiceMenuItem;
  private MenuItem statusTemporarlyUnavailableMenuItem;
  
  private String statusAvailableText = "Status 'AVAILABLE'";
  private String outOfServiceText = "Status 'OUT_OF_SERVICE'";
  private String temporarlyUnavailableText = "Status 'TEMPORARILY_UNAVAILABLE'";
  
  public void initialize(ApplicationManager manager) {
    MenuBar menuBar = manager.frame.getMenuBar();
    
    Menu locationMenu = new Menu("Location");

    statusAvailableMenuItem = new MenuItem("Set: " + statusAvailableText);
    statusAvailableMenuItem.addActionListener(this);
    locationMenu.add(statusAvailableMenuItem);
    
    statusOutOfServiceMenuItem = new MenuItem(outOfServiceText);
    statusOutOfServiceMenuItem.addActionListener(this);
    statusOutOfServiceMenuItem.setEnabled(false);
    locationMenu.add(statusOutOfServiceMenuItem);
    
    statusTemporarlyUnavailableMenuItem = new MenuItem("Set: " + temporarlyUnavailableText);
    statusTemporarlyUnavailableMenuItem.addActionListener(this);
    locationMenu.add(statusTemporarlyUnavailableMenuItem);

    menuBar.add(locationMenu); 
  }
  
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    if (source == statusAvailableMenuItem) {
      LocationProviderImpl.state = LocationProvider.AVAILABLE;
      statusAvailableMenuItem.setEnabled(false);
      statusAvailableMenuItem.setLabel(statusAvailableText);
      statusOutOfServiceMenuItem.setLabel("Set: " + outOfServiceText);
      statusOutOfServiceMenuItem.setEnabled(true);
      statusTemporarlyUnavailableMenuItem.setLabel("Set: " + temporarlyUnavailableText);
      statusTemporarlyUnavailableMenuItem.setEnabled(true);
    }
    else if (source == statusOutOfServiceMenuItem) {
      LocationProviderImpl.state = LocationProvider.OUT_OF_SERVICE;
      statusAvailableMenuItem.setLabel("Set: " + statusAvailableText);
      statusAvailableMenuItem.setEnabled(true);
      statusOutOfServiceMenuItem.setEnabled(false);
      statusOutOfServiceMenuItem.setLabel(outOfServiceText);
      statusTemporarlyUnavailableMenuItem.setLabel("Set: " + temporarlyUnavailableText);
      statusTemporarlyUnavailableMenuItem.setEnabled(true);
    }
    else if (source == statusTemporarlyUnavailableMenuItem) {
      LocationProviderImpl.state = LocationProvider.TEMPORARILY_UNAVAILABLE;
      statusAvailableMenuItem.setLabel("Set: " + statusAvailableText);
      statusAvailableMenuItem.setEnabled(true);
      statusOutOfServiceMenuItem.setLabel("Set: " + outOfServiceText);
      statusOutOfServiceMenuItem.setEnabled(true);
      statusTemporarlyUnavailableMenuItem.setEnabled(false);
      statusTemporarlyUnavailableMenuItem.setLabel(temporarlyUnavailableText);
    }
  }
}