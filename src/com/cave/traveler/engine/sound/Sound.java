package com.cave.traveler.engine.sound;

import android.media.MediaPlayer;

public class Sound {

	public enum Type {
		SoundEffect, Music
	}

	private String name;
	private Type type;
	private String resource;
	private MediaPlayer mediaPlayer = null;
	private boolean paused = false;
	private SoundMap owner = null;
	private boolean autoload = true;

	public Sound(String name, Type type, String resource) {
		this.name = name;
		this.type = type;
		this.resource = resource;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	private void loadSound() {
		if (mediaPlayer == null && autoload)
			owner.loadSound(this);
	}

	private void unloadSound() {
		if (mediaPlayer != null && autoload) {
			owner.unloadSound(this);
		}
	}

	public void playLoop() {
		loadSound();

		if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
			mediaPlayer.setLooping(true);
			mediaPlayer.start();
		}
	}

	public void playLoop(float leftVolume, float rightVolume) {
		loadSound();

		if (mediaPlayer != null) {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.setLooping(true);
				mediaPlayer.setVolume(leftVolume, rightVolume);
				mediaPlayer.start();
			} else {
				mediaPlayer.setVolume(leftVolume, rightVolume);
			}
		}
	}

	public void playNormal() {
		loadSound();
		if (mediaPlayer != null) {
			mediaPlayer.setLooping(false);
			mediaPlayer.start();
		}
	}

	public void playNormal(float leftVolume, float rightVolume) {
		loadSound();

		if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
			mediaPlayer.setLooping(false);
			mediaPlayer.setVolume(leftVolume, rightVolume);
			mediaPlayer.start();
		}
	}

	public void stop() {
		if (mediaPlayer != null && mediaPlayer.isPlaying())
			try {
				mediaPlayer.stop();
			} catch (IllegalStateException e) {
			}

		unloadSound();
	}

	public void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			paused = true;
		}
	}

	public void resume() {
		if (mediaPlayer != null && paused) {
			mediaPlayer.start();
			paused = false;
		}
	}

	public SoundMap getOwner() {
		return owner;
	}

	public void setOwner(SoundMap owner) {
		this.owner = owner;
	}

	public boolean isAutoload() {
		return autoload;
	}

	public void setAutoload(boolean autoload) {
		this.autoload = autoload;
	}
}
