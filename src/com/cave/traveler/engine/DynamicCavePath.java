package com.cave.traveler.engine;

public class DynamicCavePath {

	protected DynamicCave owner;
	protected DynamicCavePathGenerator generator;
	
	protected int steps;
	protected int currentStep;
	protected float topValue = 300;
	protected float bottomValue = 0;

	public DynamicCavePath(DynamicCave owner, DynamicCavePathGenerator generator) {
		this.owner = owner;
		this.generator = generator;
	}

	public void prepare(DynamicCavePath previous, float caveXPosition) {
		if(previous != null) {
			this.topValue = previous.topValue;
			this.bottomValue = previous.bottomValue;
		}
	}

	public boolean end() {
		return currentStep >= steps;
	}

	public void reset() {
		currentStep = 0;
	}

	public void generate(float caveXPosition) {
		++currentStep;
	}

	public float getTopValue() {
		return topValue;
	}

	public float getBottomValue() {
		return bottomValue;
	}
}
