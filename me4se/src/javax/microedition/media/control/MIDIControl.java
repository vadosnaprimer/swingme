/*
 * Created on 02.07.2005
 */
package javax.microedition.media.control;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */
public interface MIDIControl extends Control {

    /**
     * @API MMAPI-1.0
     */ 
    public static final int NOTE_ON = 144;
    
    /**
     * @API MMAPI-1.0
     */ 
    public static final int CONTROL_CHANGE = 176;    
    
    /**
     * @API MMAPI-1.0
     */ 
    public abstract boolean isBankQuerySupported();

    /**
     * @API MMAPI-1.0
     */ 
    public abstract int[] getProgram(int channel) throws MediaException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract int getChannelVolume(int channel);

    /**
     * @API MMAPI-1.0
     */ 
    public abstract void setProgram(int channel, int bank, int program);

    /**
     * @API MMAPI-1.0
     */ 
    public abstract void setChannelVolume(int channel, int volume);

    /**
     * @API MMAPI-1.0
     */ 
    public abstract int[] getBankList(boolean custom) throws MediaException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract int[] getProgramList(int bank) throws MediaException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract String getProgramName(int bank, int prog) throws MediaException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract String getKeyName(int bank, int prog, int key) throws MediaException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract void shortMidiEvent(int type, int data1, int data2);

    /**
     * @API MMAPI-1.0
     */ 
    public abstract int longMidiEvent(byte[] data, int offset, int length);
}