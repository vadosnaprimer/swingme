package net.yura.android.messaging;

import java.util.Date;

import javax.wireless.messaging.Message;

/**
 * Provides basic Message implementation.
 */
public class MessageImpl implements Message {

    protected String address;
    protected Date timestamp;

    public MessageImpl(String address, Date timestamp) {
        this.timestamp = timestamp;
        if (address != null) {
            setAddress(address);
        }
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String addr) {
        if (addr.startsWith("sms://")) {
            addr = addr.substring(6);
        }
        this.address = addr;
    }
}
