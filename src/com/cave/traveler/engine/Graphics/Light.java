package com.cave.traveler.engine.Graphics;

import java.util.Random;

public class Light {
	private float duration;
	private float elapsedTime = 0;

	private float[] colorTransition;
	private float[] color;
	private float[] fromColor;
	private float[] toColor;

	private boolean flickering = false;
	private float minFlickering = 0;
	private float maxFlickering = 1;
	private Random random = new Random();

	public Light(float[] fromColor, float[] toColor, float duration) {
		this.duration = duration;
		this.fromColor = fromColor;
		this.toColor = toColor;
		colorTransition = new float[] { (toColor[0] - fromColor[0]) / duration, (toColor[1] - fromColor[1]) / duration, (toColor[2] - fromColor[2]) / duration };
		color = new float[] { 0, 0, 0 };
	}

	public void update(float elapsedTime) {
		this.elapsedTime += elapsedTime;
		if (this.elapsedTime < duration) {
			color[0] = fromColor[0] + colorTransition[0] * this.elapsedTime;
			color[1] = fromColor[1] + colorTransition[1] * this.elapsedTime;
			color[2] = fromColor[2] + colorTransition[2] * this.elapsedTime;
		} else {
			color[0] = toColor[0];
			color[1] = toColor[1];
			color[2] = toColor[2];
		}

		if (flickering) {
			float intesity = minFlickering + random.nextFloat() * (maxFlickering - minFlickering);
			color[0] *= intesity;
			color[1] *= intesity;
			color[2] *= intesity;
		}
	}

	public float[] getColor() {
		return color;
	}

	public boolean isFlickering() {
		return flickering;
	}

	public void setFlickering(boolean flickering, float minFlickering, float maxFlickering) {
		this.flickering = flickering;
		this.minFlickering = minFlickering;
		this.maxFlickering = maxFlickering;
	}

}
