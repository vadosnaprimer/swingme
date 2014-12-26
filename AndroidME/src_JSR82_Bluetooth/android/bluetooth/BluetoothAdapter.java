package android.bluetooth;


import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import net.yura.android.bluetooth.BluetoothManager;
import net.yura.mobile.logging.Logger;

public class BluetoothAdapter {

    public static final String ACTION_DISCOVERY_FINISHED = "android.bluetooth.adapter.action.DISCOVERY_FINISHED";
    public static final String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";

    private static BluetoothAdapter instance;

    private LocalBluetoothDevice localBT;

    private BluetoothAdapter(LocalBluetoothDevice localBT) {
        this.localBT = localBT;
    }

    public static BluetoothAdapter getDefaultAdapter() {
        if (instance == null) {
            LocalBluetoothDevice localBT = BluetoothManager.getBluetoothManager().getBluetoothDevice();
            if (localBT == null) {
                throw new RuntimeException("BluetoothAdapter not initialized");
            }

            instance = new BluetoothAdapter(localBT);
        }

        return instance;
    }

    public boolean isEnabled() {
        try {
            return localBT.isEnabled();
        } catch (Exception e) {
            Logger.warn(null, e);
        }

        return false;
    }

    public boolean isDiscovering() {
        try {
            return localBT.isScanning();
        } catch (Exception e) {
            Logger.warn(null, e);
        }

        return false;
    }

    public boolean cancelDiscovery ()  {
        try {
            localBT.stopScanning();
            return true;
        } catch (Exception e) {
            Logger.warn(null, e);
        }

        return false;
    }

    public String getName() {
        try {
            return localBT.getName();
        } catch (Exception e) {
            Logger.warn(null, e);
        }
        return null;
    }

    public String getAddress() {
        try {
            return localBT.getAddress();
        } catch (Exception e) {
            Logger.warn(null, e);
        }
        return null;
    }

    public boolean startDiscovery () {
        try {
            localBT.scan();
        } catch (Exception e) {
            Logger.warn(null, e);
        }

        return true;
    }
}
