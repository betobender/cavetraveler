package com.cave.traveler.engine;

import com.cave.traveler.engine.triggers.CaveCollisionTrigger;
import com.cave.traveler.engine.triggers.Trigger;

public class TriggerFactory {
	private static TriggerFactory instance = new TriggerFactory();
	private TriggerFactory() {
		
	}
	
	public static TriggerFactory getInstance() {
		return instance;
	}
	
	public Trigger createFromType(String type) {
		Trigger trigger = null;
		if(type.equals("caveCollisionTrigger"))
			trigger = new CaveCollisionTrigger();
		trigger.setType(type);
		return trigger;
	}
}
