package com.cave.traveler.engine.cavepaths;

import com.cave.traveler.engine.DynamicCave;
import com.cave.traveler.engine.DynamicCavePath;
import com.cave.traveler.engine.DynamicCavePathGenerator;

public class SimpleRippleRamp extends DynamicCavePath {

	@Override
	public void prepare(DynamicCavePath previous, float caveXPosition) {
		super.prepare(previous, caveXPosition);
	}

	public SimpleRippleRamp(DynamicCave owner, DynamicCavePathGenerator generator) {
		super(owner, generator);
		this.steps = 25;
	}

	private float minTop = 400;
	private float maxBot = 0;
	private float maxVar = 20;
	private float minApe = 100;

	@Override
	public void generate(float caveXPosition) {

		float nextVar1 = -(maxVar / 2) + generator.getRandomProvider(0).nextFloat() * maxVar;
		float nextVar2 = -(maxVar / 2) + generator.getRandomProvider(0).nextFloat() * maxVar;

		this.topValue += nextVar1;
		if (this.topValue >= minTop)
			this.topValue = minTop;

		this.bottomValue += nextVar2;
		if (this.bottomValue <= maxBot)
			this.bottomValue = maxBot;

		while ((this.topValue - this.bottomValue) < minApe) {
			this.topValue += 1;
			this.bottomValue -= 1;
		}
		
		super.generate(caveXPosition);
	}
}
