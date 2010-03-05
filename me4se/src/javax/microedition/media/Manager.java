package javax.microedition.media;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.midlet.ApplicationManager;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

import org.me4se.impl.BasicMMDataSource;
import org.me4se.impl.BasicMMPlayer;

/**
 * @API MIDP-2.0
 * @API MMAPI-1.0
 */
public final class Manager {

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */
	public static final String MIDI_DEVICE_LOCATOR = "device://midi";

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */
	public static final String TONE_DEVICE_LOCATOR = "device://tone";

	private Manager() {
	}

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 * @ME4SE UNIMPLEMENTED
	 */
	public static String[] getSupportedContentTypes(String s) {
		System.out.println("ME4SE: Manager.getSupportedContentTypes() called with no effect!");
		return new String[0];
	}

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 * @ME4SE UNIMPLEMENTED
	 */
	public static String[] getSupportedProtocols(String s) {
		System.out.println("ME4SE: Manager.getSupportedProtocols() called with no effect!");
		return new String[0];
	}

	/**
	 * @API MMAPI-1.0
	 * @ME4SE IMPLEMENTED 02/07/2006
	 */
	public static Player createPlayer(DataSource source) throws IOException, MediaException {
        // first, handle locator based special cases
	    String loc = source.getLocator();
        String className = null;
        
        if(loc != null){
           if(loc.startsWith("capture://video")){
               className = "media.video.VideoCaptureImpl";
           }
           else if(loc.startsWith(TONE_DEVICE_LOCATOR)){
               className = "media.audio.MidiPlayerImpl";
           }
           else if(loc.startsWith(MIDI_DEVICE_LOCATOR)){
               className = "media.audio.MidiPlayerImpl";
           }
        }
	    
        if(className == null){
            source.connect();
            String ct = source.getContentType();
            
            if(ct != null){
            	ct = ct.toLowerCase();
            
            	if(ct.equals("audio/midi") || ct.equals("audio/x-mid") || ct.equals("audio/x-tone-seq")){
            		className = "media.audio.MidiPlayerImpl";
            	}
            	else if(ct.startsWith("audio")){
            		className = "media.audio.WavePlayerImpl";
            	}
            	else if(ct.startsWith("video")){
            		className = "media.video.VideoPlayerImpl";
            	}
            }
            	
            if(className == null) {
            	throw new MediaException("Unrecognized content type: "+ct);
            }
        }
        
//        if(!checkPlatform15() &&  className.startsWith("media.audio")){
//        	return new BasicMMPlayer(true);
//        }
        

		try {
			BasicMMPlayer player = (BasicMMPlayer) ApplicationManager.getInstance().instantiate(className);
			player.setSource((BasicMMDataSource) source);
			return player;
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			throw new MediaException("ME4SE: Cannot create a DataSource for: ContentType " + source.getContentType() + " not supported!. " + ex);
		}
	}

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 * @ME4SE IMPLEMENTED 01/06/2006
	 */
	public static Player createPlayer(String locator) throws IOException, MediaException {
		System.out.println("ME4SE: Manager.createPlayer(" + locator + ")");
        return createPlayer(new BasicMMDataSource(locator));
 	}

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 * @ME4SE IMPLEMENTED 02/08/2006
	 */
	public static Player createPlayer(InputStream inputstream, String type) throws IOException, MediaException {
		System.out.println("ME4SE: Manager.createPlayer(" + inputstream + ", " + type + ")");
		BasicMMDataSource ds = new BasicMMDataSource(null);
		if (inputstream == null)
			throw new IllegalArgumentException("ME4SE: Cannot create Player for: InputStream must not be null !");
		if (type == null)
			throw new MediaException("ME4SE: Cannot create a Player for: cannot determine the media type");
		type = type.toLowerCase();
		ds.setInputStream(inputstream);
		ds.setContentType(type);
		return createPlayer(ds);
	}

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 * @ME4SE IMPLEMENTED 01/06/2006
	 */
	public synchronized static void playTone(int note, int ms, int volume) throws MediaException {
		System.out.println("ME4SE: Manager.playTone(" + note + ", " + ms + ", " + volume +")");
		try {
			Player player = createPlayer(Manager.TONE_DEVICE_LOCATOR);
            player.realize();
            
			ToneControl tControl = (ToneControl) player.getControl("ToneControl");
			
			// ms = duration * 60 * 1000 * 4 / (resolution * tempo)
            // ms = duration * 60 * 1000 * 4 / (120 * 64)
			// ms * (120 * 64) / (60 * 1000 * 4) = duration
		
            int duration = ms * (120*64) / (60 * 1000 * 4);
            
			tControl.setSequence(new byte[] { 
					ToneControl.VERSION, 
					1, 
					(byte) note, 
					(byte) Math.min(duration, 127)});
					
			player.start();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			throw new MediaException("ME4SE: No implementation found for Manager.playTone().");
		}
	}
	
	/** checks if the underlying java platform is 1.4 or 1.5  */
	
	private static boolean checkPlatform15() {
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			if (sequencer instanceof Synthesizer) {
				return false;
			}
			else {
				return true;
			}
		}
		catch(Exception ex) {
			return false;
		}
	}
}