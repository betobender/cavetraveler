package com.cave.traveler.engine.Graphics;

public class ScreenConfig {

	private static ScreenConfig instance = new ScreenConfig();
	private int resolution = 2;

	private ScreenConfig() {

	}

	public static ScreenConfig getInstance() {
		return instance;
	}

	public void setScreenResolution(int width, int height) {

		if (width >= 800)
			resolution = 2;
		else
			resolution = 4;
	}

	public int getResolution() {
		return resolution;
	}
}
