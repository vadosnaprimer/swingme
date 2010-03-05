/*
 * Created on 02.07.2005
 */
package javax.microedition.media.protocol;

/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */ 
public class ContentDescriptor {

    /**
     * @API MMAPI-1.0
     */ 
    public String getContentType() {
        return encoding;
    }

    /**
     * @API MMAPI-1.0
     */ 
    public ContentDescriptor(String contentType) {
        encoding = contentType;
    }

    /**
     * @API MMAPI-1.0
     */ 
    private String encoding;
}