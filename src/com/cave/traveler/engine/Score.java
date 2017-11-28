package com.cave.traveler.engine;

import com.cave.traveler.objects.Ship;

public class Score {

	public enum Modifier {
		booster, reducer,
	}

	private float elapsedTime;
	private String bottomMessage = "Touch the screen to control the ship";
	private float currentSpeed = 0;
	private float currentScore = 0;
	private float currentMultiplier = 1;
	private float currentDistance = 0;
	private float messageExpiration = 10f;
	private boolean nearMiss = false;
	private float nearMissAdd = 0.1f;
	private float scoreAdjust = 0.1f;
	private boolean scoring = true;

	public void reset() {
		currentScore = 0;
		currentSpeed = 0;
		currentDistance = 0;
	}

	public void toggleNearMiss(boolean enable) {
		nearMiss = enable;
	}

	public boolean isNearMissEnabled() {
		return nearMiss;
	}

	public void update(float elapsedTime, World world) {
		this.elapsedTime += elapsedTime;

		if (scoring) {
			GameObject cameraController = world.getCameraController();
			if (cameraController != null && cameraController.getClass() == Ship.class) {
				Ship playerShip = (Ship) cameraController;
				currentSpeed = playerShip.getxSpeed();
				currentScore += (playerShip.getxPosition() - currentDistance) * currentMultiplier * scoreAdjust;
				currentDistance = playerShip.getxPosition();

				if (nearMiss) {
					currentMultiplier += nearMissAdd * elapsedTime;
				}
			}
		}

		if (this.elapsedTime > messageExpiration) {
			bottomMessage = "";
		}
	}

	public String getBottomMessage() {
		return bottomMessage;
	}

	public void setBottomMessage(String bottomMessage) {
		this.bottomMessage = bottomMessage;
		messageExpiration = this.elapsedTime + 10;
	}

	public int getCurrentSpeed() {
		return (int) currentSpeed;
	}

	public float getCurrentMultiplier() {
		return currentMultiplier;
	}

	public int getCurrentScore() {
		return (int) currentScore;
	}

	public int getCurrentDistance() {
		return (int) currentDistance;
	}
	
	public void increaseMultiplier(int step) {
		currentMultiplier += step;
	}

	public void stop() {
		this.scoring = false;
	}

	public void addPoints(float points) {
		currentScore += points * currentMultiplier;
	}
}
