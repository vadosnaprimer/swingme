package org.me4se.psi.java1.media;

import javax.microedition.midlet.ApplicationManager;

import org.me4se.Initializer;

public class MediaInitializer implements Initializer {

	// 	microedition.media.version 			The string returned designates the version of the MMAPI 
	//																specification that is implemented. It is "1.1" for this version 
	//																of the specification.
	//
	//		supports.mixing 							Query for whether audio mixing is supported. 
	//																The string returned is either "true" or "false". 
	//																If mixing is supported, the following conditions are true:
	//    														* At least two tones can be played with Manager.playTone simultaneously.
	//    														* Manager.playTone can be used at the same time when at least one Player is playing back audio.
	//    														* At least two Players can be used to play back audio simultaneously.
	//
	// 	supports.audio.capture 				Query for whether audio capture is supported. 
	//																The string returned is either true or false. 
	//																If supports.audio.capture is true, audio.encoding must not be null or empty.
	//
	// 	supports.video.capture 				Query for whether video capture is supported. The string returned is either 
	//																true or false. If supports.video.capture is true, video.encoding must not be null or empty.
	//
	// 	supports.recording 						Query for whether recording is supported. The string returned is either true 
	//																or false. If supports.recording is true, at least one Player type supports recording.
	//
	// 	audio.encodings 							The string returned specifies the supported capture audio formats. 
	//																Each format is specified in the audio encoding syntax. The formats 
	//																are delimited by at least one space. If audio capture is not supported, 
	//																audio.encoding returns null.
	//
	// 	video.encodings 							The string returned specifies the supported capture video formats. 
	//																Each format is specified in the video encoding syntax. The formats are 
	//																delimited by at least one space. If video capture is not supported, 
	//																video.encoding returns null.
	//
	// 	video.snapshot.encodings 			Supported video snapshot formats for the getSnapshot method in 
	//																VideoControl. The string returned specifies the supported capture 
	//																image formats. Each format is specified in the image encoding syntax. 
	//																The formats are delimited by at least one space. The first format returned 
	//																is the default. If video snapshot is not supported, video.snapshot.encoding returns null.
	//
	// 	streamable.contents 						The string returned specifies the supported streamable content types. 
	//																Media of these content types will be handled by the Player as streamable 
	//																media -- media that is played as the data is received by the Player. Playing back 
	//																media of this type does not require the Player to buffer the entire content. 
	//																The content types are given in the MIME syntax as specified in content types, 
	//																each delimited by at least one space.
	
	public void initialize(ApplicationManager manager) {
		manager.setSystemProperty("microedition.media.version", "1.1");
		manager.setSystemProperty("supports.mixing", "false");
		manager.setSystemProperty("supports.audio.capture", "false");
		manager.setSystemProperty("supports.video.capture", "false");
		manager.setSystemProperty("supports.recording", "false");
		//manager.setSystemProperty("audio.encodings", null);
		//manager.setSystemProperty("video.encodings", null);
		manager.setSystemProperty("video.snapshot.encodings", "encoding=jpeg&width=160&height=120 encoding=jpeg&width=320&height=240");
		//manager.setSystemProperty("streamable.contents", null);
	}
}
