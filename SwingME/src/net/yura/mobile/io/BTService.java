/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.mobile.io;
import java.io.IOException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.UUID;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;
import net.yura.mobile.logging.Logger;
/**
 *
 * @author MarkH
 */
public class BTService extends ServiceLink {

    protected DiscoveryAgent discoveryAgent;
    private UUID uuid;
    protected LocalDevice localDevice;
    protected StreamConnectionNotifier streamNotifier;
    protected boolean bRegistered = false;
    protected boolean bClient = false;

    public BTService() {
        super();
    }

    public boolean registerClient(UUID aUuid) {
         if (bRegistered)
             return false;

         uuid = aUuid;
         bClient = true;

         if (isSupported()) {
              try {
                   discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
                   bRegistered = true;
              }
              catch (Exception ex) {
                Logger.warn("cant reg " + aUuid, ex);
              }
         }
         return bRegistered;
    }

    public boolean registerServer (UUID aUuid, String serviceName) { //Register as Server
         if (bRegistered)
             return false;

         bClient = false;
         if (isSupported()) {
                try {
                    LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);
                    streamNotifier = (StreamConnectionNotifier) Connector.open("btspp://localhost:" + aUuid.toString() + ";name=" + serviceName + ";authorize=false");
                    bRegistered = true;
                }
                catch (Exception ex) {
                    Logger.warn("cant reg " + aUuid + " " + serviceName, ex);
                }
         }
         return bRegistered;
    }

    protected StreamConnection openConnection() throws IOException {
        if (bClient) {
            if (discoveryAgent == null)
                throw new IOException("BlueTooth Not Supported");
            String connectionString = discoveryAgent.selectService(uuid, ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (connectionString == null)
                throw new IOException("No Services Found");
            return (StreamConnection)Connector.open(connectionString);
        } else {
            if (streamNotifier == null)
                throw new IOException("BlueTooth Not Supported");
            return streamNotifier.acceptAndOpen();
        }            
    }
    public boolean isRegistered() {
        return bRegistered;
    }

    protected static boolean isSupported() {
        if (System.getProperty( "bluetooth.api.version" ) == null) {
            if (javax.bluetooth.LocalDevice.getProperty("bluetooth.api.version") == null)
                return false;
        }
        return true;
    }
}
