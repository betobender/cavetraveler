package com.cave.traveler.engine.setters;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.World;

public class DynamicSetter extends Setter {

	public DynamicSetter() {

	}

	public DynamicSetter(Boolean value) {
		setValue(value.toString());
	}

	public void apply(float elapsedTime, World world, GameObject gameObject) {
		gameObject.setDynamic(Boolean.parseBoolean(getValue()));
		super.apply(elapsedTime, world, gameObject);
	}

}
