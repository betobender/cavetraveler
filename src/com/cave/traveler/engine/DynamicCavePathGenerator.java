package com.cave.traveler.engine;

import java.util.Random;

public class DynamicCavePathGenerator {

	private Random[] random = new Random[] { new Random(), new Random(), new Random() };

	public Random getRandomProvider(int index) {
		return random[index];
	}

	public DynamicCavePath getNewDynamicCavePath(DynamicCavePath current, float caveXPosition) {
		return null;
	}

	public void reset() {
	}
}
