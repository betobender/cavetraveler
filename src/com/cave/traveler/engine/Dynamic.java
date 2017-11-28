package com.cave.traveler.engine;

import android.annotation.SuppressLint;
import java.util.Random;

@SuppressLint("FloatMath")
public class Dynamic {

	private String name;
	private Float from;
	private Float to;
	private String function;
	private Float x;
	private Float y;
	private float multiplier = 1;

	public void setFrom(float from) {
		this.from = from;
	}

	public Float getFrom() {
		return from;
	}

	public void setTo(float to) {
		this.to = to;
	}

	public Float getTo() {
		return to;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getFunction() {
		return function;
	}

	public float getValue(int position, int range) {

		if (function.equals("const")) {
			return from;
		}

		else if (function.equals("linear")) {
			float factor = (to - from) / range;
			return factor * position + from;
		}

		else if (function.equals("random")) {
			Random random = new Random();
			return from + random.nextInt((int) (to - from + 1));
		}
		
		else if(function.equals("sin")) {
			float max = (to - from) / 2;
			return (float) (from + (Math.sin(position * x) + 1f) * max);
		}
 
		return 0;
	}

	public float getValueX(double x, GameObject obj) {
		if (function.equals("const")) {
			return from * this.x * multiplier;
		}

		return 0;
	}

	public float getValueY(double y, GameObject obj) {
		if (function.equals("const")) {
			return from * this.y * multiplier;
		}

		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public Float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}
}
