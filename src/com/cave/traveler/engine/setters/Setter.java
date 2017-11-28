package com.cave.traveler.engine.setters;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.World;

public class Setter {
	private String type;
	private float x;
	private float y;
	private String value;
	private boolean applied = false;

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void apply(float elapsedTime, World world, GameObject gameObject) {
	}

	public boolean isApplied() {
		return applied;
	}

	public void markAsApplied() {
		applied = true;
	}
}
