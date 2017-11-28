package com.cave.traveler.engine.setters;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.World;

public class SpeedSetter extends Setter {

	@Override
	public void apply(float elapsedTime, World world, GameObject gameObject) {
		gameObject.setxSpeed(getX());
		gameObject.setySpeed(getY());
		super.apply(elapsedTime, world, gameObject);
	}
}
