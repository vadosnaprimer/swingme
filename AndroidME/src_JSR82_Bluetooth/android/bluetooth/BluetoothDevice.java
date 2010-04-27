package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

public class BluetoothDevice implements Parcelable {

    public static final String ACTION_FOUND = "android.bluetooth.device.action.FOUND";
    public static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
    public String getAddress() {
        // TODO Auto-generated method stub
        return null;
    }
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        // TODO Auto-generated method stub

    }
}
