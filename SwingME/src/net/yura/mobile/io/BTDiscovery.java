/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.mobile.io;
import java.io.IOException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.DeviceClass;
/**
 *
 * @author MarkH
 */
public abstract class BTDiscovery implements DiscoveryListener {
    private DiscoveryAgent discoveryAgent;
    public BTDiscovery() {
    }
    public boolean start(int accessCode) {
            try {
              String name = LocalDevice.getLocalDevice().getFriendlyName();
              String address = LocalDevice.getLocalDevice().getBluetoothAddress();
              handleMyId(name, address);
              if (isSupported()) {
                discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
                discoveryAgent.startInquiry(accessCode, this);
                return true;
              }
            } catch (IOException ex) {
                handleException(ex);            
            }
            return false;
    }
    public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass)
    {
        boolean isPhone = (deviceClass.getMajorDeviceClass() == 0x200);
        try {
            if (isPhone)
            {
                String name = device.getFriendlyName(false);
                String address = device.getBluetoothAddress();
                handleRemoteId(name, address);
            }
            } catch (Exception ex) {
                handleException(ex);
            }
    }
   public void servicesDiscovered(int transId, ServiceRecord[] serviceRecords) {
        try {
            // there should only be one record
            if (serviceRecords.length == 1)
            {
                //Do Nothing
            }
        } catch (Exception ex) {
            handleException(ex);
        }
    }
    public void inquiryCompleted(int discoveryType) {
            handleInquiryCompleted();
    }
    public void serviceSearchCompleted(int transId, int responseCode) {
    }

    abstract public void handleMyId(String name, String address);
    abstract public void handleRemoteId(String name, String address);
    abstract public void handleInquiryCompleted();
    abstract public void handleException(Exception ex);

    protected static boolean isSupported() {
        if (System.getProperty( "bluetooth.api.version" ) == null) {
            if (javax.bluetooth.LocalDevice.getProperty("bluetooth.api.version") == null)
                return false;
        }
        return true;
    }
}
