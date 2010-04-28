package net.yura.android.messaging;

import java.util.Date;

import javax.wireless.messaging.TextMessage;

/**
 * Provides TextMessage functionalities.
 */
public class TextMessageImpl extends MessageImpl implements TextMessage {

    private String payloadText;

    public TextMessageImpl(String address) {
        this(address, null);
    }

    public TextMessageImpl(String address, Date timestamp) {
        super(address, timestamp);
    }

    public String getPayloadText() {
        return this.payloadText;
    }

    public void setPayloadText(String payloadText) {
        this.payloadText = payloadText;
    }
}
