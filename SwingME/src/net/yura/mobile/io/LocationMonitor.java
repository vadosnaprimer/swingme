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
    static final int j2meCellPollRateInSeconds = 3;
    boolean bJ2MECellMonitorLoop = true;
    String j2mePreviousCell = "";

    public class J2MECellMonitor extends TimerTask {
        protected int cellPropertyIndex = -1;
        String[] sysPropertyNames = {
            "CellID", "Cell-ID", "CELLID", "Cell ID", "ID", "Cellid", "CellID",
            "phone.cid",
            "com.nokia.mid.cellid",
            "com.sonyericsson.net.cellid",
            "phone.cid",
            "com.samsung.cellid",
            "com.siemens.cellid",
            "cid"};
        public J2MECellMonitor() {
            cellPropertyIndex = getPropertyIndex();
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
                        Hashtable hash = new Hashtable(1);
                        hash.put(cell, new Integer(0));
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

        protected int getPropertyIndex() {
            for (int index=0;index < sysPropertyNames.length;index++) {
                try {
                    String property = System.getProperty(sysPropertyNames[index]);
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
   
}
