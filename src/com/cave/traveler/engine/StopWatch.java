package com.cave.traveler.engine;

import android.os.SystemClock;

public class StopWatch {
	private long startTime = 0;
	
	public StopWatch() {
		reset();
	}
	
	public float elapsedTime() {
		long currentTime = SystemClock.uptimeMillis();
		return (currentTime - startTime) / 1000.0f;
	}
	
	public void reset() {
		startTime = SystemClock.uptimeMillis();
	}
}
