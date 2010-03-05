package org.me4se.psi.java1.media.audio;

import java.io.BufferedInputStream;

import javax.microedition.media.PlayerListener;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.me4se.impl.BasicMMDataSource;
import org.me4se.impl.BasicMMPlayer;


public class WavePlayerImpl extends BasicMMPlayer implements LineListener {

	public WavePlayerImpl(){
		super(false);
	}
	
	BasicMMDataSource source;
	Clip clip;
	
	AudioInputStream audioInputStream;
	AudioFormat audioFormat;
	DataLine.Info info;

	
	public void setSource(BasicMMDataSource ds){
		this.source = ds;
    }
	
	
	public void realizeImpl() throws Exception {
		audioInputStream = AudioSystem.getAudioInputStream(
			new BufferedInputStream(source.getInputStream(), 1024));
		audioFormat = audioInputStream.getFormat();
		System.out.println("AudioFormat = " + audioFormat.toString());
		info = new DataLine.Info(Clip.class, audioInputStream.getFormat(), ((int) audioInputStream.getFrameLength() * audioFormat.getFrameSize()));		
		if(audioFormat.getFrameRate() != AudioSystem.NOT_SPECIFIED){
			duration = (long) (audioInputStream.getFrameLength() * 1000000 / audioFormat.getFrameRate());
			
		//	System.out.println("Audio clip duration: "+duration/1000+" ms");
		}
	}
	

	
	public void startImpl() throws Exception {
		System.out.println("Start called for clip: "+clip);
		
		if(clip == null){
			audioInputStream = AudioSystem.getAudioInputStream(source.getInputStream());
			
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioInputStream);
			clip.addLineListener(this);

			if(desiredTime != -1){
				clip.setMicrosecondPosition(desiredTime);
				desiredTime = 0;
			}
			
			clip.start();

			state = STARTED;
			startListenerNotification(PlayerListener.STARTED, null);
		}
	}
	
	
	public void stopImpl() throws Exception{
		if(clip != null){
			desiredTime = clip.getMicrosecondPosition();
			if(desiredTime >= clip.getMicrosecondLength()){
				desiredTime = 0;
			}
			Clip save = clip;
			clip = null;
			
			save.stop();
			save.close();
		}
	}


	public void update(LineEvent event) {
		System.out.println("Event: "+event +";  clip: "+clip);

		
		LineEvent.Type type = event.getType();
		
		
		if(type.equals(LineEvent.Type.STOP)){
			if(clip != null && (loopCount > 1 || loopCount == -1)){
				notifyListeners(PlayerListener.END_OF_MEDIA, null);
				clip.setMicrosecondPosition(0);
				clip.start();
				if(loopCount > 1){
					loopCount--;
				}
			}
			else{
				if(state == STARTED){
					state = PREFETCHED;
				}
				notifyListeners(PlayerListener.STOPPED, null);
				if(clip != null){
					clip.close();
					clip = null;
				}
			}
			
		}
/*		else if(type.equals(LineEvent.Type.START)){
			if(state != STARTED){
				state = STARTED;
			}
			notifyListeners(PlayerListener.STARTED, null);
		}*/
	}
	

    public long getMediaTime() {
        return clip == null ? desiredTime: clip.getMicrosecondPosition();
    }


    public String getContentType() {
    	return source.getContentType();
    }


    /* TBD 
    public Control[] getControls() {
        System.out.println("BasicMMPlayer.getControls()");
        return new Control[]{nullControl};
    }

    public Control getControl(String s) {
        System.out.println("BasicMMPlayer.getControl("+s+")");
        return nullControl;
    }
	*/
}
