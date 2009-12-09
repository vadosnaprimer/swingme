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
import java.util.Hashtable;
import net.yura.mobile.util.Timer;



/**
 *
 * @author AP
 */
public abstract class LocationMonitor implements ServiceLink.TaskHandler {

    public static final String COUNTRY_CODE_TYPE = "-1";
    public static final String NETWORK_CODE_TYPE = "-2";
    public static final String LOCATION_AREA_CODE_TYPE = "-3";
    public static final String SUBSCRIBER_IMSI_TYPE = "-4";
    public static final String SUBSCRIBER_HOME_COUNTRY_TYPE = "-5";

    static final int j2meCellPollRateInSeconds = 300;
    boolean bJ2MECellMonitorLoop = true;
    boolean bCellRequestMade = false;
    boolean bCellNotifyMade = false;
    String j2mePreviousCell = "UNSET";
    Timer timer;

    public class J2MECellMonitor implements Runnable {
        protected int cellPropertyIndex = -1;
        protected int mccPropertyIndex = -1;
        protected int mncPropertyIndex = -1;
        protected int signalPropertyIndex = -1;
        protected int locationAreaPropertyIndex = -1;
        protected int cmccPropertyIndex = -1;
        protected int imsiPropertyIndex = -1;
        String[] sysPropertyNames = {
            "CellID", "Cell-ID", "CELLID", "Cell ID", "ID", "Cellid", "CellID",
            "phone.cid",
            "com.nokia.mid.cellid",
            "com.sonyericsson.net.cellid",
            "phone.cid",
            "com.samsung.cellid",
            "com.siemens.cellid",
            "cid"};
        String[] cmccPropertyNames = {
            "com.sonyericsson.net.cmcc"
        };
        String[] mccPropertyNames = {
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
        String[] imsiPropertyNames = {
            "com.sonyericsson.sim.subscribernumber"
        };
        public J2MECellMonitor() {
            cellPropertyIndex = getPropertyIndex(sysPropertyNames);
            cmccPropertyIndex = getPropertyIndex(cmccPropertyNames);
            mccPropertyIndex = getPropertyIndex(mccPropertyNames);
            mncPropertyIndex = getPropertyIndex(mncPropertyNames);
            signalPropertyIndex = getPropertyIndex(signalPropertyNames);
            locationAreaPropertyIndex = getPropertyIndex(locationAreaPropertyNames);
            imsiPropertyIndex = getPropertyIndex(imsiPropertyNames);
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

        private void addPropertyToHash(Hashtable values, String property, Object key) {
            Object value = System.getProperty(property);
            if (value != null) {
                values.put(value, key);
            }
        }

        protected void getCellId() {
            try {
                if (cellPropertyIndex >= 0) {
                    String cell = System.getProperty(sysPropertyNames[cellPropertyIndex]);
                    if (!cell.equals(j2mePreviousCell)) {
                        String signal = null;
                        if (signalPropertyIndex >= 0) {
                            signal = System.getProperty(signalPropertyNames[signalPropertyIndex]);
                        }
                        if (signal == null) {
                            signal = "0";
                        }
                        Hashtable hash = new Hashtable(6);

                        hash.put(cell, signal);
                        if (cmccPropertyIndex >= 0) {
                            addPropertyToHash(hash,mccPropertyNames[cmccPropertyIndex],COUNTRY_CODE_TYPE);
                        }
                        if (mncPropertyIndex >= 0) {
                            addPropertyToHash(hash,mncPropertyNames[mncPropertyIndex],NETWORK_CODE_TYPE);
                        }
                        if (locationAreaPropertyIndex >= 0) {
                            addPropertyToHash(hash,locationAreaPropertyNames[locationAreaPropertyIndex],LOCATION_AREA_CODE_TYPE);
                        }
                        if (imsiPropertyIndex >= 0) {
                            addPropertyToHash(hash,imsiPropertyNames[imsiPropertyIndex],SUBSCRIBER_IMSI_TYPE);
                        }
                        if (mccPropertyIndex >= 0) {
                            addPropertyToHash(hash,mccPropertyNames[mccPropertyIndex],SUBSCRIBER_HOME_COUNTRY_TYPE);
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

        public synchronized void run() {
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

                    if (timer != null){
                        timer.cancel();
                    }

                    timer = new Timer();
                    timer.schedule("J2MECellMonitor1", this, j2meCellPollRateInSeconds*1000);
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
        if (!bCellRequestMade) {
            link.addToOutbox(new ServiceLink.Task("GetCellId", null));
            bCellRequestMade = true;
        }
        if (!link.isConnected()) {
            new Thread(new J2MECellMonitor()).start();
        }
    }

    public void getWifiList() {
        ServiceLink link = ServiceLink.getInstance();
        link.addToOutbox(new ServiceLink.Task("GetWiFiSsList", null));
    }

    public void setNotifyForCellId(boolean b) {
        ServiceLink link = ServiceLink.getInstance();
        if (!bCellNotifyMade) {
            link.addToOutbox(new ServiceLink.Task("PutOptionCellIdPush", new Boolean(b)));
            bCellNotifyMade = true;
        }
        if (!link.isConnected()) {
            bJ2MECellMonitorLoop = b;
            if (b) {
                if (timer != null){
                    timer.cancel();
                }
                timer = new Timer();
                timer.schedule("J2MECellMonitor3", new J2MECellMonitor(), j2meCellPollRateInSeconds*1000);
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
