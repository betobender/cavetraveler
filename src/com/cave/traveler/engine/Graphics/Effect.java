package com.cave.traveler.engine.Graphics;

public class Effect {
	public enum Type {
		unknown,
		spotLight
	}
	
	private Type type;
	private float fromSize;
	private float toSize;
	private int color;

	public void setType(String type) {
		if(type.equals("spotLight")) {
			this.type = Type.spotLight;
		} else {
			this.type = Type.unknown;
		}
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public float getFromSize() {
		return fromSize;
	}

	public void setFromSize(float fromSize) {
		this.fromSize = fromSize;
	}

	public float getToSize() {
		return toSize;
	}

	public void setToSize(float toSize) {
		this.toSize = toSize;
	}
	
	public float getCurrentSize() {
		return fromSize;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
