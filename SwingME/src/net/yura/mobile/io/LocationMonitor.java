/*
 * LocationMonitor.java
 *
 * Created on 08 October 2009, 08:56
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package net.yura.mobile.io;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Hashtable;

/**
 *
 * @author AP
 */
public abstract class LocationMonitor implements ServiceLink.TaskHandler {

    public static final String COUNTRY_CODE_TYPE = "-1";
    public static final String NETWORK_CODE_TYPE = "-2";
    public static final String LOCATION_AREA_CODE_TYPE = "-3";

    static final int j2meCellPollRateInSeconds = 3;
    boolean bJ2MECellMonitorLoop = true;
    String j2mePreviousCell = "UNSET";

    public class J2MECellMonitor extends TimerTask {
        protected int cellPropertyIndex = -1;
        protected int mccPropertyIndex = -1;
        protected int mncPropertyIndex = -1;
        protected int signalPropertyIndex = -1;
        protected int locationAreaPropertyIndex = -1;
        String[] sysPropertyNames = {
            "CellID", "Cell-ID", "CELLID", "Cell ID", "ID", "Cellid", "CellID",
            "phone.cid",
            "com.nokia.mid.cellid",
            "com.sonyericsson.net.cellid",
            "phone.cid",
            "com.samsung.cellid",
            "com.siemens.cellid",
            "cid"};
        String[] mccPropertyNames = {
            "com.sonyericsson.net.cmcc",
            "com.nokia.mid.countrycode",
            "com.sonyericsson.net.mcc",
            "mcc",
            "MCC"
        };
        String[] mncPropertyNames = {
            "com.sonyericsson.net.cmnc",
            "com.nokia.mid.networkid",
            "phone.mnc",
            "mnc",
            "MNC"
        };
        String[] signalPropertyNames = {
            "com.nokia.mid.networksignal"
        };
        String[] locationAreaPropertyNames = {
            "com.nokia.mid.lac",
            "com.sonyericsson.net.lac",
            "LAC",
            "LocAreaCode",
            "phone.lac"
        };
        public J2MECellMonitor() {
            cellPropertyIndex = getPropertyIndex(sysPropertyNames);
            mccPropertyIndex = getPropertyIndex(mccPropertyNames);
            mncPropertyIndex = getPropertyIndex(mncPropertyNames);
            signalPropertyIndex = getPropertyIndex(signalPropertyNames);
            locationAreaPropertyIndex = getPropertyIndex(locationAreaPropertyNames);
        }
        public boolean isSupported() {
            return (cellPropertyIndex >= 0);
        }
        public String getCellIdPropertyName() {
            if (isSupported()) {
                return sysPropertyNames[cellPropertyIndex];
            }
            return null;
        }

        protected void getCellId() {
            try {
                if (cellPropertyIndex >= 0) {
                    String cell = System.getProperty(sysPropertyNames[cellPropertyIndex]);
                    if (!cell.equals(j2mePreviousCell)) {
                        String signal = "0";
                        if (signalPropertyIndex >= 0) {
                            signal = System.getProperty(signalPropertyNames[signalPropertyIndex]);
                        }
                        Hashtable hash = new Hashtable(4);
                        hash.put(cell, signal);
                        if (mccPropertyIndex >= 0) {
                            hash.put(System.getProperty(mccPropertyNames[mccPropertyIndex]), COUNTRY_CODE_TYPE);
                        }
                        if (mncPropertyIndex >= 0) {
                            hash.put(System.getProperty(mncPropertyNames[mncPropertyIndex]), NETWORK_CODE_TYPE);
                        }
                        if (locationAreaPropertyIndex >= 0) {
                            hash.put(System.getProperty(locationAreaPropertyNames[locationAreaPropertyIndex]), LOCATION_AREA_CODE_TYPE);
                        }
                        ServiceLink.Task task = new ServiceLink.Task("PutCellId", hash);
                        handleTask(task);
                        j2mePreviousCell = cell;
                    }
                }
            }
            catch (Throwable t) {
                //#debug
                t.printStackTrace();
            }
        }

        protected int getPropertyIndex(String[] properties) {
            for (int index=0;index < properties.length;index++) {
                try {
                    String property = System.getProperty(properties[index]);
                    if ((property != null) && (property.length() > 0))
                        return index;
                }
                catch (Throwable t) {}
            }
            return -1;
        }

        public void run() {
            ServiceLink link = ServiceLink.getInstance();
            if (link.isConnected()) {
                if (bJ2MECellMonitorLoop) {
                    setNotifyForCellId(bJ2MECellMonitorLoop);
                }
                bJ2MECellMonitorLoop = false;
            }
            else {
                getCellId();
                if (bJ2MECellMonitorLoop) {
                    new Timer().schedule(this, j2meCellPollRateInSeconds*1000);
                }
            }
        }
    }
    /** Creates a new instance of LocationMonitor */
    public LocationMonitor() {
        ServiceLink link = ServiceLink.getInstance();
        link.registerForTask("PutCellId", this);
        link.registerForTask("GetCellIdError", this);
        link.registerForTask("PutWiFiSsList", this);
        link.registerForTask("GetWiFiSsListError", this);
    }

    public void getCellId() {
        ServiceLink link = ServiceLink.getInstance();
        if (link.isConnected())
            link.addToOutbox(new ServiceLink.Task("GetCellId", null));
        else
            new Timer().schedule(new J2MECellMonitor(), 1);
    }

    public void getWifiList() {
        ServiceLink link = ServiceLink.getInstance();
        link.addToOutbox(new ServiceLink.Task("GetWiFiSsList", null));
    }

    public void setNotifyForCellId(boolean b) {
        ServiceLink link = ServiceLink.getInstance();
        if (link.isConnected()) {
            link.addToOutbox(new ServiceLink.Task("PutOptionCellIdPush", new Boolean(b)));
        }
        else {
            bJ2MECellMonitorLoop = b;
            if (b) {
                new Timer().schedule(new J2MECellMonitor(), j2meCellPollRateInSeconds*1000);
            }
        }
    }

    public void setNotifyForWifiList(boolean b) {
        ServiceLink link = ServiceLink.getInstance();
        link.addToOutbox(new ServiceLink.Task("PutOptionWiFiPush", new Boolean(b)));
    }
   
    public javax.microedition.location.Coordinates getGPS() throws javax.microedition.location.LocationException, InterruptedException {

            // Set criteria for selecting a location provider:
            // accurate to 500 meters horizontally
            javax.microedition.location.Criteria cr= new javax.microedition.location.Criteria();
            cr.setHorizontalAccuracy(500);

            // Get an instance of the provider
            javax.microedition.location.LocationProvider lp = javax.microedition.location.LocationProvider.getInstance(cr);

            // Request the location, setting a 5 minute timeout
            javax.microedition.location.Location l = lp.getLocation(60*5);
            javax.microedition.location.Coordinates c = l.getQualifiedCoordinates();

            if(c != null ) {
              // Use coordinate information
              double lat = c.getLatitude();
              double lng = c.getLongitude();

              System.out.println("lat:"+lat+" lng:"+lng);
            }

            return c;
    }
}
