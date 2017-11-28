package com.cave.traveler.engine.sound;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class SoundEndListener implements OnCompletionListener{

	private Sound sound;
	
	public SoundEndListener (Sound sound) {
		this.sound = sound;
	}
	
	public void onCompletion(MediaPlayer mp) {
		if(sound.isAutoload() && sound.getMediaPlayer() == mp) {
			sound.getOwner().unloadSound(sound);
		}
		
	}	
	
}
