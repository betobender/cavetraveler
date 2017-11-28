package com.cave.traveler.engine;

public class MoveablePoint {

	private float x;
	private float y;
	private float z;

	private float speedX;
	private float speedY;
	private float speedZ;

	private float accelerationX;
	private float accelerationY;
	private float accelerationZ;

	private float rotationX;
	private float rotationY;
	private float rotationZ;

	private float rotationSpeedX;
	private float rotationSpeedY;
	private float rotationSpeedZ;

	private MoveablePoint animated;

	public MoveablePoint() {

	}

	public MoveablePoint(float x, float y, float z, float speedX, float speedY, float speedZ, float accelerationX, float accelerationY, float accelerationZ,
			float rotationX, float rotationY, float rotationZ, float rotationSpeedX, float rotationSpeedY, float rotationSpeedZ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.speedX = speedX;
		this.speedY = speedY;
		this.speedZ = speedZ;
		this.accelerationX = accelerationX;
		this.accelerationY = accelerationY;
		this.accelerationZ = accelerationZ;
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
		this.rotationSpeedX = rotationSpeedX;
		this.rotationSpeedY = rotationSpeedY;
		this.rotationSpeedZ = rotationSpeedZ;
	}

	public void reset() {
		animated = new MoveablePoint(x, y, z, speedX, speedY, speedZ, accelerationX, accelerationY, accelerationZ, rotationX, rotationY, rotationZ,
				rotationSpeedX, rotationSpeedY, rotationSpeedZ);
	}

	public void update(float elapsedTime) {
		animated.x += animated.speedX * elapsedTime;
		animated.y += animated.speedY * elapsedTime;
		animated.z += animated.speedZ * elapsedTime;
		animated.speedX += animated.accelerationX * elapsedTime;
		animated.speedY += animated.accelerationY * elapsedTime;
		animated.speedZ += animated.accelerationZ * elapsedTime;
		animated.rotationX += animated.rotationSpeedX * elapsedTime;
		animated.rotationY += animated.rotationSpeedY * elapsedTime;
		animated.rotationZ += animated.rotationSpeedZ * elapsedTime;
	}

	public MoveablePoint getAnimatedPoint() {
		return animated;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getSpeedX() {
		return speedX;
	}

	public void setSpeedX(float speedX) {
		this.speedX = speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}

	public float getSpeedZ() {
		return speedZ;
	}

	public void setSpeedZ(float speedZ) {
		this.speedZ = speedZ;
	}

	public float getAccelerationX() {
		return accelerationX;
	}

	public void setAccelerationX(float accelerationX) {
		this.accelerationX = accelerationX;
	}

	public float getAccelerationY() {
		return accelerationY;
	}

	public void setAccelerationY(float accelerationY) {
		this.accelerationY = accelerationY;
	}

	public float getAccelerationZ() {
		return accelerationZ;
	}

	public void setAccelerationZ(float accelerationZ) {
		this.accelerationZ = accelerationZ;
	}

	public float getRotationX() {
		return rotationX;
	}

	public void setRotationX(float rotationX) {
		this.rotationX = rotationX;
	}

	public float getRotationY() {
		return rotationY;
	}

	public void setRotationY(float rotationY) {
		this.rotationY = rotationY;
	}

	public float getRotationZ() {
		return rotationZ;
	}

	public void setRotationZ(float rotationZ) {
		this.rotationZ = rotationZ;
	}

	public float getRotationSpeedX() {
		return rotationSpeedX;
	}

	public void setRotationSpeedX(float rotationSpeedX) {
		this.rotationSpeedX = rotationSpeedX;
	}

	public float getRotationSpeedY() {
		return rotationSpeedY;
	}

	public void setRotationSpeedY(float rotationSpeedY) {
		this.rotationSpeedY = rotationSpeedY;
	}

	public float getRotationSpeedZ() {
		return rotationSpeedZ;
	}

	public void setRotationSpeedZ(float rotationSpeedZ) {
		this.rotationSpeedZ = rotationSpeedZ;
	}

}
