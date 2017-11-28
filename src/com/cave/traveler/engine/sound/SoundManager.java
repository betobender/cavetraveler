package com.cave.traveler.engine.sound;

import com.cave.traveler.engine.Graphics.ResourceMap;

public class SoundManager extends SoundMap {

	public void initialize(ResourceMap resourceMap) {
		this.initializeSounds(resourceMap);
	}

	public void reset() {
		for (Sound sound : soundMap.values()) {
			sound.stop();
		}
	}

	public void pause() {
		for (Sound sound : soundMap.values()) {
			sound.pause();
		}
	}
	
	public void stop() {
		for (Sound sound : soundMap.values()) {
			sound.stop();
		}
	}	

	public void resume() {
		for (Sound sound : soundMap.values()) {
			sound.resume();
		}
	}
}
