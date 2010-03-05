package org.me4se.impl;

import javax.microedition.media.TimeBase;

public class TimeBaseImpl implements TimeBase {

    public static TimeBase defaultInstance = new TimeBaseImpl();

    public long getTime() {
        return System.currentTimeMillis()*1000;
    }

}
