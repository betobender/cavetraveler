package com.cave.traveler.engine;

import com.cave.traveler.engine.cavepaths.SimpleRippleRamp;

public class SimpleRandomCavePathGenerator extends DynamicCavePathGenerator {

	private SimpleRippleRamp pathGenerator;
	
	public SimpleRandomCavePathGenerator(DynamicCave owner) {
		pathGenerator = new SimpleRippleRamp(owner, this);
	}

	public DynamicCavePath getNewDynamicCavePath(DynamicCavePath current, float caveXPosition) {
		pathGenerator.reset();
		pathGenerator.prepare(current, caveXPosition);
		return pathGenerator;
	}
}
