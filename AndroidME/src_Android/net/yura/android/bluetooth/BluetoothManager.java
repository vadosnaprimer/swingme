package net.yura.android.bluetooth;

import java.util.ArrayList;

import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;

import net.yura.android.AndroidMeActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothManager {

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 2;

    private static BluetoothManager instance;

    private DiscoveryListener listener;
    private Activity activity;
    private EventReceiver_2_0 eventReceiver_2_0;
    private EventReceiver_1_6 eventReceiver_1_6;
    private LocalBluetoothDevice localBT;


    public LocalBluetoothDevice getBluetoothDevice() {
        return localBT;
    }

    public static BluetoothManager getBluetoothManager() {
        if (instance == null) {
            instance = new BluetoothManager(AndroidMeActivity.DEFAULT_ACTIVITY);

            // Attempt to use older Bluetooth Android 1.6 API's
            // If it fails, attempt Android 2.0 API's
            try {
                instance.start_1_6();
            } catch (Throwable e) {

                instance.start_2_0();
            }
        }

        return instance;
    }

    private BluetoothManager(Activity activity) {

        this.activity = activity;
    }

    private void start_1_6() throws Exception {
        System.out.println(">>> Bluetooth trying 1.6 API...");

        // If we are not running on Android 1.6, this will fail
        localBT = LocalBluetoothDevice.initLocalDevice(activity);
        System.out.println("localBT = " + localBT);

        eventReceiver_1_6 = new EventReceiver_1_6();
        localBT.setListener(eventReceiver_1_6);

        // TODO: When should enable be done?
        localBT.setEnabled(true);
        System.out.println(">>> isEnabled = " + localBT.isEnabled());
    }

    private void start_2_0() {
        System.out.println(">>> Bluetooth trying 2.0 API...");

        eventReceiver_2_0 = new EventReceiver_2_0();

        // Get local Bluetooth adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(eventReceiver_2_0, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(eventReceiver_2_0, filter);

        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            // Set result CANCELED in case the user backs out
            activity.setResult(Activity.RESULT_CANCELED);

            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }


    // TODO: This should be called when the activity is paused...
    public void stop() {
        if (eventReceiver_2_0 != null) {
            activity.unregisterReceiver(eventReceiver_2_0);
        }
        if (eventReceiver_1_6 != null) {
            // TODO: Is cancel enough?
        }
    }

    public void setDiscoveryListener(DiscoveryListener listener) {
        this.listener = listener;
    }


    // Listener for Bluetooth Android 1.6 API's
    class EventReceiver_1_6 implements LocalBluetoothDeviceListener {
        public void bluetoothDisabled() {
            System.out.println(">>>> bluetoothDisabled");
        }

        public void bluetoothEnabled() {
            System.out.println(">>>> bluetoothEnabled");
        }

        public void deviceFound(String deviceAddress) {
            System.out.println(">>>> deviceFound: " + deviceAddress);

            if (listener != null) {
                try {
                    RemoteBluetoothDevice r = localBT.getRemoteBluetoothDevice(deviceAddress);
                    RemoteDevice btDevice = new RemoteDevice(deviceAddress, r.getName());

                    // TODO: Get device class... For now everything is a Mobile Phone
                    DeviceClass deviceClass = new DeviceClass(0x200);

                    listener.deviceDiscovered(btDevice, deviceClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void scanCompleted(ArrayList<String> devices) {
            System.out.println(">>>> scanCompleted");

            if (listener != null) {
                // TODO: add the right discType
                listener.inquiryCompleted(DiscoveryListener.SERVICE_SEARCH_COMPLETED);
            }
        }

        public void scanStarted() {
            System.out.println(">>>> scanStarted");
        }
    }


    // Listener for Bluetooth Android 2.0 API's
    class EventReceiver_2_0 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println(">>> onReceive: " + intent.getAction());

            if (listener == null) {
                return;
            }

            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_REQUEST_ENABLE.equals(action)) {
                System.out.println(">>>> ACTION_REQUEST_ENABLE " + getResultCode());

                // Android 1.6 code
//              switch (requestCode) {
                //
//                        case REQUEST_ENABLE_BT:
//                            // When the request to enable Bluetooth returns
//                            if (mBluetoothAdapter.isEnabled()) {
//                                System.out.println("BT enabled!");
                //
                //
//                            } else {
//                                // User did not enable Bluetooth or an error occured
//                                System.out.println("BT not enabled");
//                                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_LONG).show();
//                                finish();
//                            }
//                        }
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                RemoteDevice btDevice = new RemoteDevice(device.getAddress(), device.getName());

                // TODO: Get device class... For now everything is a Mobile Phone
                DeviceClass deviceClass = new DeviceClass(0x200);

                if (listener != null) {
                    listener.deviceDiscovered(btDevice, deviceClass);
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                if (listener != null) {
                    // TODO: add the right discType
                    listener.inquiryCompleted(DiscoveryListener.SERVICE_SEARCH_COMPLETED);
                }
            }
        }
    }
}
