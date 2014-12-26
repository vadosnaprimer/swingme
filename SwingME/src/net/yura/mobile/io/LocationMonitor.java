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
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.Timer;



/**
 *
 * @author AP
 */
public abstract class LocationMonitor implements ServiceLink.TaskHandler,Runnable {

    public static final String COUNTRY_CODE_TYPE = "-1";
    public static final String NETWORK_CODE_TYPE = "-2";
    public static final String LOCATION_AREA_CODE_TYPE = "-3";
    public static final String SUBSCRIBER_IMSI_TYPE = "-4";
    public static final String SUBSCRIBER_HOME_COUNTRY_TYPE = "-5";

    static final int j2meCellPollRateInSeconds = 300;
    boolean bJ2MECellMonitorLoop = true;
    boolean bCellRequestMade = false;
    boolean bCellNotifyMade = false;
    Timer timer;

    public static class J2MECellMonitor {
        protected int cellPropertyIndex = -1;
        protected int homeCountryPropertyIndex = -1;
        protected int mncPropertyIndex = -1;
        protected int signalPropertyIndex = -1;
        protected int locationAreaPropertyIndex = -1;
        protected int currentCountryPropertyIndex = -1;
        protected int imsiPropertyIndex = -1;
        static final String[] sysPropertyNames = {
            "CellID", "Cell-ID", "CELLID", "Cell ID", "ID", "Cellid", "CellID",
            "phone.cid",
            "com.nokia.mid.cellid",
            "com.sonyericsson.net.cellid",
            "phone.cid",
            "com.samsung.cellid",
            "com.siemens.cellid",
            "cid"};
        static final String[] currentCountryPropertyNames = {
            "com.nokia.mid.countrycode",
            "com.sonyericsson.net.cmcc",
            "com.sonyericsson.net.mcc",
            "cmcc",
            "CMCC",
            "mcc",
            "MCC"
        };
        static final String[] homeCountryPropertyNames = {
            "com.nokia.mid.countrycode",
            "com.sonyericsson.net.mcc",
            "mcc",
            "MCC"
        };
        static final String[] mncPropertyNames = {
            "com.sonyericsson.net.cmnc",
            "com.nokia.mid.networkid",
            "phone.mnc",
            "mnc",
            "MNC"
        };
        static final String[] signalPropertyNames = {
            "com.nokia.mid.networksignal",
            "NETWORKSIGNAL"
        };
        static final String[] locationAreaPropertyNames = {
            "com.nokia.mid.lac",
            "com.sonyericsson.net.lac",
            "LAC",
            "LocAreaCode",
            "phone.lac"
        };
        static final String[] imsiPropertyNames = {
            "com.nokia.mid.imsi",
            "com.sonyericsson.sim.subscribernumber",
            "IMSI"
        };
        public J2MECellMonitor() {
            cellPropertyIndex = getPropertyIndex(sysPropertyNames);
            currentCountryPropertyIndex = getPropertyIndex(currentCountryPropertyNames);
            homeCountryPropertyIndex = getPropertyIndex(homeCountryPropertyNames);
            mncPropertyIndex = getPropertyIndex(mncPropertyNames);
            signalPropertyIndex = getPropertyIndex(signalPropertyNames);
            locationAreaPropertyIndex = getPropertyIndex(locationAreaPropertyNames);
            imsiPropertyIndex = getPropertyIndex(imsiPropertyNames);
        }

        private int getPropertyIndex(String[] properties) {
            for (int index=0;index < properties.length;index++) {
                String name = properties[index];
                try {
                    String property = System.getProperty(name);
                    if ((property != null) && (property.length() > 0))
                        return index;
                }
                catch (Exception t) {
                  Logger.warn("cant get " + name, t);
                }
            }
            return -1;
        }
        
        private String getProperty(int index,String[] array) {
            if (index >= 0) {
                return System.getProperty( array[index] );
            }
            return null;
        }
        
        public String getCellIdProperty() {
            return getProperty(cellPropertyIndex, sysPropertyNames);
        }
        public String getSignalProperty() {
            String signal = getProperty(signalPropertyIndex,signalPropertyNames);
            if (signal == null) {
                signal = "0";
            }
            return signal;
        }
        public String getLocationAreaProperty() {
            return getProperty(locationAreaPropertyIndex, locationAreaPropertyNames);
        }
        public String getImsiProperty() {
            return getProperty(imsiPropertyIndex, imsiPropertyNames);
        }
        public String getMncProperty() {
            return getProperty(mncPropertyIndex, mncPropertyNames);
        }
        public String getHomeCountryProperty() {
            return getProperty(homeCountryPropertyIndex, homeCountryPropertyNames);
        }
        public String getCurrentCountryProperty() {
            return getProperty(currentCountryPropertyIndex, currentCountryPropertyNames);
        }

        String j2mePreviousCell = "UNSET";
        protected Hashtable getJ2MECellId() {
            try {
                String cell = getCellIdProperty();
                if (cell!=null && !cell.equals(j2mePreviousCell)) {
                    Hashtable hash = new Hashtable(6);

                    String signal = getSignalProperty();
                    hash.put(cell, signal);

                    addPropertyToHash(hash,getCurrentCountryProperty(),COUNTRY_CODE_TYPE);
                    addPropertyToHash(hash,getMncProperty(),NETWORK_CODE_TYPE);
                    addPropertyToHash(hash,getLocationAreaProperty(),LOCATION_AREA_CODE_TYPE);
                    addPropertyToHash(hash,getImsiProperty(),SUBSCRIBER_IMSI_TYPE);
                    addPropertyToHash(hash,getHomeCountryProperty(),SUBSCRIBER_HOME_COUNTRY_TYPE);

                    j2mePreviousCell = cell;
                    return hash;
                }
            }
            catch (Exception t) {
                Logger.warn(null, t);
            }
            return null;
        }

        private void addPropertyToHash(Hashtable values, String value, Object key) {
            if (value != null) {
                values.put(value, key);
            }
        }
    }
    
    static J2MECellMonitor j2MECellMonitor;
    public static J2MECellMonitor getJ2MECellMonitor() {
        if (j2MECellMonitor==null) {
            j2MECellMonitor = new J2MECellMonitor();
        }
        return j2MECellMonitor;
    }
    
    public synchronized void run() {
          try {
            ServiceLink link = ServiceLink.getInstance();
            if (link.isConnected()) {
                if (bJ2MECellMonitorLoop) {
                    setNotifyForCellId(bJ2MECellMonitorLoop);
                }
                bJ2MECellMonitorLoop = false;
            }
            else {
                putCellId( getJ2MECellMonitor().getJ2MECellId() );
                if (bJ2MECellMonitorLoop) {

                    if (timer != null){
                        timer.cancel();
                    }

                    timer = new Timer();
                    timer.schedule("J2MECellMonitor1", this, j2meCellPollRateInSeconds*1000);
                }
            }
          }
          catch(Throwable t) {
            Logger.error(null, t);
          }
    }

    void putCellId(Hashtable hash) {
        if (hash!=null) {
            ServiceLink.Task task = new ServiceLink.Task("PutCellId", hash);
            handleTask(task);
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
            link.sendTask(new ServiceLink.Task("GetCellId", null));
            bCellRequestMade = true;
        }
        if (!link.isConnected()) {
            // If underline service is not running yet, we create a dummy
            // CellMonitor Object, and force it to populate the Cell Id...
            putCellId( getJ2MECellMonitor().getJ2MECellId() );
        }
    }

    public void getWifiList() {
        ServiceLink link = ServiceLink.getInstance();
        link.sendTask(new ServiceLink.Task("GetWiFiSsList", null));
    }

    public void setNotifyForCellId(boolean b) {
        ServiceLink link = ServiceLink.getInstance();
        if (!bCellNotifyMade) {
            link.sendTask(new ServiceLink.Task("PutOptionCellIdPush", b ? Boolean.TRUE : Boolean.FALSE));
            bCellNotifyMade = true;
        }
        if (!link.isConnected()) {
            bJ2MECellMonitorLoop = b;
            if (b) {
                if (timer != null){
                    timer.cancel();
                }
                timer = new Timer();
                // WTF: Jose - This time is is the initial delay for the first update... We should have an update as soon as we can (before was 300secs)
                timer.schedule("J2MECellMonitor2", this, 0);
            }
        }
    }

    public void setNotifyForWifiList(boolean b) {
        ServiceLink link = ServiceLink.getInstance();
        link.sendTask(new ServiceLink.Task("PutOptionWiFiPush", b ? Boolean.TRUE : Boolean.FALSE));
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

              //#debug debug
              Logger.debug("lat:"+lat+" lng:"+lng);
            }

            return c;
    }
}
