package com.cave.traveler.engine.triggers;

import com.cave.traveler.engine.GameObject;

public class Trigger {

	private String type;
	private String currentState;
	private String nextState;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCurrentState() {
		return currentState;
	}

	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	public String getNextState() {
		return nextState;
	}

	public void setNextState(String nextState) {
		this.nextState = nextState;
	}
	
	protected boolean testState(GameObject obj) {
		return currentState.equals(obj.getCurrentState().getState());
	}

	public void apply(TriggerDispatcher dispatcher, GameObject obj) {
	}
}
