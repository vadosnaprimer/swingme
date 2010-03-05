/*
 * Created on 02.07.2005
 */
package javax.microedition.media.control;

import javax.microedition.media.Control;

/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */ 
public interface MetaDataControl extends Control {
    
    /**
     * @API MMAPI-1.0
     */ 
    public static final String AUTHOR_KEY = "author";

    /**
     * @API MMAPI-1.0
     */ 
    public static final String COPYRIGHT_KEY = "copyright";
    
    /**
     * @API MMAPI-1.0
     */ 
    public static final String DATE_KEY = "date";
    
    /**
     * @API MMAPI-1.0
     */ 
    public static final String TITLE_KEY = "title";
    
    /**
     * @API MMAPI-1.0
     */ 
    public String[] getKeys();

    /**
     * @API MMAPI-1.0
     */ 
    public String getKeyValue(String key); 
}
