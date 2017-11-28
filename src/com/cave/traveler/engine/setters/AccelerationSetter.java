package com.cave.traveler.engine.setters;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.World;

public class AccelerationSetter extends Setter {

	public AccelerationSetter(int x, float y) {
		this.setX(x);
		this.setY(y);
	}

	public AccelerationSetter() {
	}

	@Override
	public void apply(float elapsedTime, World world, GameObject gameObject) {
		gameObject.setxAcceleration(getX());
		gameObject.setyAcceleration(getY());
		super.apply(elapsedTime, world, gameObject);
	}
}
