package com.cave.traveler.engine.setters;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.World;

public class PlayableSetter extends Setter {

	public void apply(float elapsedTime, World world, GameObject gameObject) {
		gameObject.setPlayable(Boolean.parseBoolean(getValue()));
		super.apply(elapsedTime, world, gameObject);
	}	
	
}
