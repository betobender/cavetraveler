package com.cave.traveler.engine.setters;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.World;

public class AcceleratedSetter extends Setter {

	public AcceleratedSetter() {
		
	}
	
	public AcceleratedSetter(Boolean value) {
		this.setValue(value.toString());
	}

	public void apply(float elapsedTime, World world, GameObject gameObject) {
		gameObject.setAccelerated(Boolean.parseBoolean(getValue()));
		super.apply(elapsedTime, world, gameObject);
	}	
	
}
