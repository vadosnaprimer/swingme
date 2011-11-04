package net.yura.android.messaging;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Date;

import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

import net.yura.android.AndroidMeApp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * Provides MessageConnection functionalities. Note: <uses-permission
 * id="android.permission.RECEIVE_SMS" /> is required for receiving SMS Note 2:
 * when using the PushRegistry, we could register the service the Manifest.xml
 * as well.
 */
public class MessageConnectionImpl extends BroadcastReceiver implements MessageConnection {
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private final String url;

    private MessageListener messageListener;
    private short port;
    private final Object receiveLock;
    private final ArrayList<Message> receivedMessages;
    private boolean isListeningForMessages;

    public MessageConnectionImpl(String url) {
        url = url.substring("sms://".length());
        this.url = url;

        this.receiveLock = new Object();
        this.receivedMessages = new ArrayList<Message>();
        int colonIndex = url.indexOf(':');
        if (colonIndex != -1 && colonIndex < url.length() - 1) {
            String portStr = url.substring(colonIndex + 1);
            for (int i = 0; i < portStr.length(); i++) {
                if (!Character.isDigit(portStr.charAt(i))) {
                    portStr = portStr.substring(0, i);
                    break;
                }
            }
            this.port = (short) Integer.parseInt(portStr);
        }
    }

    public Message newMessage(String type) {
        return newMessage(type, this.url);
    }

    public Message newMessage(String type, String address) {
        if (MessageConnection.TEXT_MESSAGE.equals(type)) {
            return new TextMessageImpl(address);
        }

        if (MessageConnection.BINARY_MESSAGE.equals(type)) {
            return new BinaryMessageImpl(address);
        }

        throw new IllegalArgumentException();
    }

    public int numberOfSegments(Message msg) {
        // maximum length is 140 byts or 160 characters (with 7bit characters):
        if (msg instanceof TextMessage) {

            String text = ((TextMessage) msg).getPayloadText();
            ArrayList<String> segments = SmsManager.getDefault().divideMessage(text);
            return segments.size();
        }

        if (msg instanceof BinaryMessage) {
            byte[] data = ((BinaryMessage) msg).getPayloadData();
            return (data == null) ? 1 : (data.length / 140) + 1;
        }

        return 0;
    }

    public Message receive() throws IOException, InterruptedIOException {
        if (!this.isListeningForMessages) {
            setupMessageReceiver();
        }
        synchronized (this.receiveLock) {
            if (this.receivedMessages.size() > 0) {
                Message msg = this.receivedMessages.remove(0);
                return msg;
            }
            try {
                this.receiveLock.wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }
        if (this.receivedMessages.size() > 0) {
            Message msg = this.receivedMessages.remove(0);
            return msg;
        } else {
            throw new InterruptedIOException();
        }
    }

    private void setupMessageReceiver() {
        this.isListeningForMessages = true;
        IntentFilter filter = new IntentFilter(ACTION);
        AndroidMeApp.getIntance().registerReceiver(this, filter);
    }

    public void send(Message msg) throws IOException, InterruptedIOException {
        String address = msg.getAddress();
        if (msg instanceof TextMessage) {
            // TODO when a port is specified use data message?
            String text = ((TextMessage) msg).getPayloadText();
            SmsManager.getDefault().sendTextMessage(address, null, text, null, null);
        } else if (msg instanceof BinaryMessage) {
            byte[] data = ((BinaryMessage) msg).getPayloadData();
            SmsManager.getDefault().sendDataMessage(address, null, this.port, data, null, null);
        } else {
            throw new IOException("invalid type: " + msg);
        }
    }

    public void setMessageListener(MessageListener l) throws IOException {
        if (!this.isListeningForMessages) {
            setupMessageReceiver();
        }
        this.messageListener = l;
    }

    public void close() throws IOException {
        // TODO check if SmsManager needs to be released
        synchronized (this.receiveLock) {
            this.receiveLock.notify();
        }
        if (this.isListeningForMessages) {
            AndroidMeApp.getIntance().unregisterReceiver(this);
            this.isListeningForMessages = false;
        }
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        // #debug debug
        System.out.println("SMS:onReceive.");
        Bundle bundle = intent.getExtras();
        Object messages[] = (Object[]) bundle.get("pdus");
        for (int n = 0; n < messages.length; n++) {
            SmsMessage msg = SmsMessage.createFromPdu((byte[]) messages[n]);
            // TODO: how to create binary messages?
            TextMessage textMsg = new TextMessageImpl(msg.getOriginatingAddress(), new Date(msg.getTimestampMillis()));
            this.receivedMessages.add(textMsg);
            synchronized (this.receiveLock) {
                this.receiveLock.notify();
            }
            if (this.messageListener != null) {
                this.messageListener.notifyIncomingMessage(this);
            }
        }
    }
}
