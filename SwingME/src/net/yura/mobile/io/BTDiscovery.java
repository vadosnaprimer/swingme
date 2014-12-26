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
import net.yura.mobile.logging.Logger;
/**
 *
 * @author MarkH
 */
public abstract class BTDiscovery implements DiscoveryListener {

    public boolean start(int accessCode) {
        try {
          String name = LocalDevice.getLocalDevice().getFriendlyName();
          String address = LocalDevice.getLocalDevice().getBluetoothAddress();
          handleMyId(name, address);
          if (isSupported()) {
            DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
            discoveryAgent.startInquiry(accessCode, this);
            return true;
          }
        } catch (IOException ex) {
          Logger.warn("cant start " + accessCode, ex);
        }
        return false;
    }
    
    public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass) {
        boolean isPhone = (deviceClass.getMajorDeviceClass() == 0x200);
        try {
            if (isPhone) {
                String name = device.getFriendlyName(false);
                String address = device.getBluetoothAddress();
                handleRemoteId(name, address);
            }
        }
        catch (Throwable t) {
            Logger.error("error " + device + " " + deviceClass, t);
        }
    }
    
   public void servicesDiscovered(int transId, ServiceRecord[] serviceRecords) {
        try {
            // there should only be one record
            if (serviceRecords.length == 1) {
                //Do Nothing
            }
        }
        catch (Throwable t) {
            Logger.error("error" + transId, t);
        }
    }
    public void inquiryCompleted(int discoveryType) {
        try {
            handleInquiryCompleted();
        }
        catch (Throwable t) {
            Logger.error("error" + discoveryType, t);
        }
    }
    public void serviceSearchCompleted(int transId, int responseCode) {
    }

    abstract public void handleMyId(String name, String address);
    abstract public void handleRemoteId(String name, String address);
    abstract public void handleInquiryCompleted();

    protected static boolean isSupported() {
        String btVersion = System.getProperty("bluetooth.api.version");

        return (btVersion != null && !"".equals(btVersion.trim()));
    }
}
