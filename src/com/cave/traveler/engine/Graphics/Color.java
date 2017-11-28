package com.cave.traveler.engine.Graphics;

public class Color {
	private float[] rgba = new float[4];

	public Color() {
		rgba[3] = 1f;
	}

	public Color(float r, float g, float b) {
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
	}

	public Color(float r, float g, float b, float a) {
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
		rgba[3] = a;
	}

	public float[] getRGBA() {
		return rgba;
	}

	public float getRed() {
		return rgba[0];
	}

	public float getGreen() {
		return rgba[1];
	}

	public float getBlue() {
		return rgba[2];
	}

	public float getAlpha() {
		return rgba[3];
	}
}
