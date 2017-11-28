package com.cave.traveler.engine.control;

public class TouchControlEvent implements ControlEvent {
	private boolean buttonDown;
	private int buttonsPressed;
	private boolean leftSide;

	public TouchControlEvent(boolean buttonDown, int buttonsPressed, boolean leftSide) {
		this.buttonDown = buttonDown;
		this.setButtonsPressed(buttonsPressed);
		this.leftSide = leftSide;
	}

	public boolean isButtonDown() {
		return buttonDown;
	}

	public void setButtonDown(boolean buttonDown) {
		this.buttonDown = buttonDown;
	}

	public int getButtonsPressed() {
		return buttonsPressed;
	}

	public void setButtonsPressed(int buttonsPressed) {
		this.buttonsPressed = buttonsPressed;
	}

	public boolean isLeftSide() {
		return leftSide;
	}

	public void setLeftSide(boolean leftSide) {
		this.leftSide = leftSide;
	}
}
