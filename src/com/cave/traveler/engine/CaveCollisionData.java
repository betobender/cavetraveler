package com.cave.traveler.engine;

public class CaveCollisionData {
	
	public enum CaveCollision {
		DoesntCollide, CollideWithTop, CollideWithBottom
	}

	private CaveCollision caveCollision;
	private float xPosition;
	private float yPosition;
	private float distance;

	public float getxPosition() {
		return xPosition;
	}

	public void setxPosition(float xPosition) {
		this.xPosition = xPosition;
	}

	public float getyPosition() {
		return yPosition;
	}

	public void setyPosition(float yPosition) {
		this.yPosition = yPosition;
	}

	public CaveCollision getCaveCollision() {
		return caveCollision;
	}

	public void setCaveCollision(CaveCollision caveCollision) {
		this.caveCollision = caveCollision;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}
}
