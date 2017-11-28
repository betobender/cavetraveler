package com.cave.traveler.engine.sound;

import java.util.HashMap;
import java.util.Map;

import android.media.MediaPlayer;

import com.cave.traveler.engine.Graphics.ResourceMap;

public class SoundMap {

	protected Map<String, Sound> soundMap = new HashMap<String, Sound>();
	private ResourceMap resourceMap;

	public void addSound(Sound sound) {
		sound.setOwner(this);
		soundMap.put(sound.getName(), sound);
	}

	public Sound getSound(String name) {
		return soundMap.get(name);
	}

	public void loadSound(Sound sound) {
		sound.setMediaPlayer(MediaPlayer.create(resourceMap.getResourceContext(), resourceMap.getResourceByName(sound.getResource())));
		
		if(sound.getMediaPlayer() != null)
			sound.getMediaPlayer().setOnCompletionListener(new SoundEndListener(sound));
	}

	public void unloadSound(Sound sound) {
		sound.getMediaPlayer().release();
		sound.setMediaPlayer(null);
	}
	
	public void initializeSounds(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
		for (Sound sound : soundMap.values()) {
			if (sound.isAutoload() == false) {
				MediaPlayer mediaPlayer = MediaPlayer.create(resourceMap.getResourceContext(), resourceMap.getResourceByName(sound.getResource()));
				sound.setMediaPlayer(mediaPlayer);
			}
		}

	}

}
