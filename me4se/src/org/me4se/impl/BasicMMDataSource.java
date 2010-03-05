package org.me4se.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

public class BasicMMDataSource extends DataSource {

	private String contentType;
//	private long contentLength;
	private InputStream is;
	private StreamConnection con;
	private byte[] data;
    
	public BasicMMDataSource(String locator) {
		super(locator);
//		contentLength = -1L;
        if(Manager.TONE_DEVICE_LOCATOR.equals(locator)){
            contentType = "audio/x-tone-seq";
        }
        else if(Manager.MIDI_DEVICE_LOCATOR.equals(locator)){
            contentType = "audio/midi";
        }
	}


    public void connect() throws IOException {
        if(data == null){
            if(is == null){
                String loc = getLocator();
                
                if(loc.startsWith("device://")){
                    return;
                }
                
                con = (StreamConnection) Connector.open(loc);
                if(con instanceof ContentConnection){
					ContentConnection cc = (ContentConnection) con;
					contentType =  cc.getType();
  //              contentLength = cc.getLength();
				}
            
				if(contentType == null && loc.startsWith("file:")){
					loc = loc.toLowerCase();
					if(loc.endsWith(".mid")){
						contentType = "audio/midi";
					}
					else if(loc.endsWith(".mp3")){
						contentType = "audio/mp3";
					}
					else if(loc.endsWith(".png")){
						contentType = "image/png";
					}
				}
				is = con.openInputStream();
			}
	    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	    byte[] buf = new byte[1024];
	    	    while(true){
	    	        int count = is.read(buf);
	    	        if(count <= 0){
	    	            break;
	    	        }
	    	        baos.write(buf, 0, count);
	    	    }
	    	    data = baos.toByteArray();
	    	    is.close();
	    	    is = null;
        }
	}
    

	public void disconnect() {
		
	    if(con != null){
            try{
                is.close();
                con.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	public String getContentType() {
		return contentType;
	}

	public Control getControl(String s) {
		return null;
	}

	public Control[] getControls() {
		return new Control[0];
	}

	public SourceStream[] getStreams() {
		 return null;
	}

	public void start() throws IOException {
		// TODO Auto-generated method stub

	}

	public void stop() throws IOException {
		// TODO Auto-generated method stub
	}

	public void setInputStream(InputStream is) {
	    data = null;
		this.is = is;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(getData());
	}

    public void setData(byte[] data){
        this.data = data;
    }
    
	public void setContentType(String type) {
		this.contentType = type;
	}

	public byte[] getData() {
		if(data == null) {
			try{
				connect();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return data;
	}

  
}
