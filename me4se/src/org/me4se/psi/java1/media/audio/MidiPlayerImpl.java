package org.me4se.psi.java1.media.audio;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.ToneControl;
import javax.microedition.midlet.ApplicationManager;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import org.me4se.impl.BasicMMDataSource;
import org.me4se.impl.BasicMMPlayer;
import org.w3c.dom.stylesheets.MediaList;


public class MidiPlayerImpl extends BasicMMPlayer implements ToneControl, MetaEventListener {

	static Object lock = new Object();
    static MidiPlayerImpl activeMidiPlayer;
    static Sequencer sequencer;
  //  static MidiPlayerImpl watchDog;
    
	
	
    int level = 100;
    
	// Sound Stuff
	private Sequence sequence;
	public MidiPlayerImpl() {
		super(true);
        
	/*	synchronized(lock){
		
			if(watchDog == null){
				watchDog = this;
				new Thread(this).start();
			}
		}
		*/
	}
    
  
 
	// add fancy stuff later -- if at all
//	 private MidiPlayerImpl interruptedPlayer;  
//	  private long interruptedMediaTime;
//	private long interruptedSystemTime; 
    
	

	public long getDuration() {
		synchronized(lock){
			return sequence == null ? TIME_UNKNOWN : sequence.getMicrosecondLength();
		}
	}

	public long getMediaTime() {
		synchronized(lock){
			return state == STARTED ?  sequencer.getMicrosecondPosition() : Math.max(0, desiredTime);
		}
	}



	public void prefetchImpl() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException{

		synchronized(lock){

			if(sequencer == null) {
				sequencer = MidiSystem.getSequencer();
				sequencer.addMetaEventListener(this);
				
				if(!sequencer.isOpen()){
					sequencer.open();
				}
			}
        	
			 sequence = MidiSystem.getSequence(
                "audio/x-tone-seq".equalsIgnoreCase(dataSource.getContentType())
                ? convertToneSequence(dataSource.getData())
                 : dataSource.getInputStream());
			 
			 duration = sequence.getMicrosecondLength();
		}
    }


	public void startImpl() throws InvalidMidiDataException, MediaException {
        
		synchronized(lock){
			if(activeMidiPlayer == this) return;
		
			if(activeMidiPlayer != null){
				throw new MediaException("Too many midi/tone players");
			}
        
			activeMidiPlayer = this;
        
//		System.out.println("MidiAudioImpl.startImpl() called! " + sequencer.getMicrosecondPosition());

			sequencer.setSequence(sequence);
     //   sequencer.setLoopCount(loopCount);
        
			if(desiredTime != -1){
				sequencer.setMicrosecondPosition(desiredTime);
				desiredTime = -1;
			}
			else if(sequencer.getMicrosecondPosition() >= sequencer.getMicrosecondLength()){
				sequencer.setMicrosecondPosition(0);
			}
			sequencer.start();
		}
	}

	public void stopImpl() throws MediaException {
		synchronized(lock){
        if(activeMidiPlayer == this){
            sequencer.stop();
            activeMidiPlayer = null;
        }
		}
	}

	public Control getControl(String s) {
		// System.out.println("MidiAudioImpl.getControl(" + s + ") called!");
		if (state == UNREALIZED){
			throw new IllegalStateException();
		}
		// important: no exception for unsupported controls!
		return s.endsWith("ToneControl")
			? this 	: super.getControl(s);
	}

      static byte[] MIDI_HEADER = {
            'M', 'T', 'h', 'd',  //  4D 54 68 64     MThd
            0, 0, 0, 6,  //  00 00 00 06     chunk length
            0, 0, //  00 00   format 0
            0, 1, //  00 01   one track
            0 //, 8,  //  00 60   96 per quarter-note

         //   'M', 'T', 'r', 'k', //        4D 54 72 6B     MTrk
      };
   
    /** Helper for ToneSequence */
      
    private void writeTime(OutputStream os, int time, int overflow) throws IOException{
        if(time > 127){
            writeTime(os, time >> 7, 128);
        }
        os.write((time & 127) | overflow);
    }
      
    /** Helper for ToneSequence */

    private void writeNote(OutputStream os, int pause, int note, int duration, int volume) throws IOException{
       writeTime(os, pause, 0);
        os.write(0x90);
        os.write(note);
        os.write(volume);
        
        writeTime(os, duration, 0);
        os.write(0x80);
        os.write(note);
        os.write(0);
    }

    
    public void setSequence(byte[] toneSequence){
        if(toneSequence[0] != VERSION || toneSequence[1] != 1){
            throw new IllegalArgumentException();
        }
        if(state == PREFETCHED || state == STARTED){
            throw new IllegalStateException();
        }

        dataSource = new BasicMMDataSource(Manager.TONE_DEVICE_LOCATOR);
        dataSource.setData(toneSequence);
        dataSource.setContentType("audio/x-tone-seq");
    }
    
    
    public InputStream convertToneSequence(byte[] toneSequence) {
        
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int pos = 2;
        
            baos.write(0);
            baos.write(0xc0);
            baos.write(81);
        
            int[] blocks = new int[127];
        
            int resolution = 64;
            int volume = 127;
            int bpm = 120;
        
            if(toneSequence[pos] == TEMPO){
                bpm = toneSequence[++pos] * 4;
                pos++;
            }
        
            if(toneSequence[pos] == RESOLUTION){
                resolution = toneSequence[++pos];
                pos++;
            }

            baos.write(0x00);
            baos.write(0xff);
            baos.write(0x51);
            baos.write(0x03);
       
            int tempo = 60000000/bpm;
            baos.write((tempo >>> 16) & 255);
            baos.write((tempo >>> 8) & 255);
            baos.write(tempo & 255);
        
            while(pos < toneSequence.length && toneSequence[pos] == BLOCK_START){
                int blockId = toneSequence[pos+1];
            //    System.out.println("Defining block "+blockId);
                blocks[blockId] = pos + 2;
                pos += 2;
                while(true){
                    int cmd = toneSequence[pos++];
                    if(cmd == BLOCK_END){
                        break;
                    }
                    else if(cmd >= 0 || cmd == SILENCE || cmd == REPEAT || cmd == SET_VOLUME){
                        pos++;
                    }
                    else {
                        System.out.println("Unsupported tone sequence command: "+cmd);
                    }
                }
                if(toneSequence[pos] != blockId){
                    throw new RuntimeException("Block termination issue!");
                }
                pos++; // block end id
            }

            StringBuffer stack = new StringBuffer();
        
            int repeat = 1;
            int pause = 0;
        
            while(pos < toneSequence.length){
                int cmd = toneSequence[pos++];
                if(cmd > 0){
                    int duration = toneSequence[pos++] * repeat;
                    writeNote(baos, pause * 4, cmd, duration * 4, volume);
                    pause = 0;
                    repeat = 1;
                }
                else{
                    switch(cmd){
                    case SET_VOLUME:
                    	volume = toneSequence[pos++] * 100 / 127;
                    	break;
                    	
                    case REPEAT:
                        repeat = toneSequence[pos++];
                        break;
                
                    case SILENCE:
                        pause += toneSequence[pos++] * repeat;
                        repeat = 1;
                        break;
                    
                    case PLAY_BLOCK:
            //            System.out.println("Play Block: "+toneSequence[pos]);
                        stack.append((char) (pos+1));
                        pos = blocks[toneSequence[pos]];
                        break;
                    
                    case BLOCK_END:
           //             System.out.println("Return from Block");
                        pos = stack.charAt(stack.length()-1);
                        stack.deleteCharAt(stack.length()-1);
                        break;
                        
                    default:
                        System.out.println("Unrecognized Tone Sequence command: "+cmd);
                    }
                }
            }

            baos.write(0x00);
            baos.write(0xff);
            baos.write(0x2f);
            baos.write(0x00);
            baos.close();
        
            byte[] track = baos.toByteArray();
            baos = new ByteArrayOutputStream();
            int len = track.length;
        
            baos.write(MIDI_HEADER);
            baos.write(resolution); 
        
            baos.write('M');
            baos.write('T');
            baos.write('r');
            baos.write('k');
            baos.write((len >>> 24) & 255);
            baos.write((len >>> 16) & 255);
            baos.write((len >>> 8) & 255);
            baos.write(len & 255);
            baos.write(track);
            baos.close();
        
            return new ByteArrayInputStream(baos.toByteArray());
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /*
    
    public int setLevel(int level) {
        
        int oldLevel = this.level;
        this.level = level;
        
        if (sequencer instanceof Synthesizer) {
            System.out.println("MidiDeviceVolumeControlImpl.setLevel(" + level + ")  called!");
            System.out.println("javax.sound implementation on  jdk1.4");
            MidiChannel[] channels = ((Synthesizer) sequencer).getChannels();
            // gain is a value between 0 and 1 (loudest)
            //ouble gain = 0.9D;
            double gain = new Double(level).doubleValue() / 100.0D;
            for (int i = 0; i < channels.length; i++) {
                channels[i].controlChange(7, (int) (gain * 127.0));
                channels[i].controlChange(39, (int) (gain * 127.0));
            }   
        }
        else {
            System.out.println("MidiDeviceVolumeControlImpl.setLevel(" + level + ")  NYI on jdk1.5!");
            System.out.println("javax.sound implementation on  jdk1.5");
        }
        
        if (this.level != level)
            notifyListeners(PlayerListener.VOLUME_CHANGED, null);       
        return oldLevel;
    }
    */
    


	public void meta(MetaMessage meta) {
	//	System.out.println("MetaMessage: "+meta.getType());
		
		if(meta.getType() == 47) {
			synchronized (lock) {

				if(activeMidiPlayer != null && activeMidiPlayer.state == STARTED
						&& sequencer.getMicrosecondPosition() >= sequencer.getMicrosecondLength()-10000){
          		  // sequencer.stop();
					
					
					if(activeMidiPlayer.loopCount != 1){
          		   		activeMidiPlayer.notifyListeners(PlayerListener.END_OF_MEDIA, null);
          		   	
          		   		sequencer.setMicrosecondPosition(0);
          		   		sequencer.start();
          		   		if(activeMidiPlayer.loopCount != -1){
          		   			activeMidiPlayer.loopCount--;
          		   		}
					}
					else {
						ApplicationManager.getInstance().activePlayers.removeElement(activeMidiPlayer);
						activeMidiPlayer.state = REALIZED;
          		   		activeMidiPlayer.notifyListeners(PlayerListener.STOPPED, null);
          		   
          		   		activeMidiPlayer = null;
					}
          	   	}
			}
		}
	}
    
    
//	private void addMetaEvent(Track track, int type, byte[] data, long tick) {
//		MetaMessage message = new MetaMessage();
//		try {
//			message.setMessage(type, data, data.length);
//			MidiEvent event = new MidiEvent(message, tick);
//			track.add(event);
//		}
//		catch (InvalidMidiDataException e) {
//			e.printStackTrace();
//		}
//	}
}
