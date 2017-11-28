package com.cave.traveler.engine;

import android.os.SystemClock;

import com.cave.traveler.engine.control.ControlEvent;

public class Engine {
	private boolean running = false;
	private long lastFrameTime;
	private World world;
	private float oneSecond = 1000f;
	private boolean gameOver = false;

	public Engine() {
		lastFrameTime = SystemClock.uptimeMillis();
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	public void reset() {
		lastFrameTime = SystemClock.uptimeMillis();
		world.reset();
	}

	public void pause() {
		running = false;
		world.pause();
	}

	public void resume() {
		lastFrameTime = SystemClock.uptimeMillis();
		running = true;
		world.resume();
	}

	public void stop() {
		world.reset();
		gameOver = false;
		running = false;
	}

	public void update() {
		if (running) {
			long currentFrameTime = SystemClock.uptimeMillis();
			float elapsedTime = (float) ((currentFrameTime - lastFrameTime) / oneSecond);
			if (elapsedTime != 0) {
				lastFrameTime = currentFrameTime;
				world.update(elapsedTime, this);
			}
		}
	}

	public void processControlEvent(ControlEvent event) {
		world.processControlEvent(event);
	}

	public long getLastUpdateTime() {
		return world.getUpdateTime();
	}

	public float getOneSecond() {
		return oneSecond;
	}

	public void setOneSecond(float oneSecond, float transitionTime) {
		this.oneSecond = oneSecond;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
}
