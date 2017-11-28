package com.cave.traveler.engine.triggers;

import com.cave.traveler.engine.GameObject;

public class CaveCollisionTrigger extends Trigger {

	@Override
	public void apply(TriggerDispatcher dispatcher, GameObject obj) {
		if (testState(obj)) {
			if (dispatcher.equals(TriggerDispatcher.CaveCollision) || dispatcher.equals(TriggerDispatcher.CaveBottomCollision)
					|| dispatcher.equals(TriggerDispatcher.CaveTopCollision)) {
				obj.setCurrentState(getNextState());
			}
		}
		super.apply(dispatcher, obj);
	}

}
