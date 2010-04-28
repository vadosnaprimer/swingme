package net.yura.android.messaging;

import java.util.Date;

import javax.wireless.messaging.BinaryMessage;

/**
 * Provides BinaryMessage functionalities.
 */
public class BinaryMessageImpl extends MessageImpl implements BinaryMessage {

    private byte[] payloadData;

    public BinaryMessageImpl(String address) {
        this(address, null);
    }

    public BinaryMessageImpl(String address, Date timestamp) {
        super(address, timestamp);
    }

    public byte[] getPayloadData() {
        return this.payloadData;
    }

    public void setPayloadData(byte[] payloadData) {
        this.payloadData = payloadData;
    }
}
