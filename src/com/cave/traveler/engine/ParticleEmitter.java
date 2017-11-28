package com.cave.traveler.engine;

import java.util.Random;

import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Texture;
import com.cave.traveler.engine.Particle.ParticleType;

public class ParticleEmitter {

	public enum Mode {
		Relative, Absolute
	}

	private boolean enableDraw = true;
	private Mode mode = Mode.Relative;
	private Particle.ParticleType particleType = ParticleType.SquareTexture;
	private float xPosition;
	private float yPosition;
	private float xSpeed;
	private float ySpeed;
	private float xAcceleration;
	private float yAcceleration;
	private float positionVariation;
	private float speedVariation;
	private float accelerationVariation;
	private String textureName;
	private Texture texture;
	private float[] color = new float[] { 1, 1, 1, 1 };
	private float fromDuration;
	private float toDuration;
	private float interval;
	private int particles = 1;
	private int limit;
	private float fromSize = 1;
	private float toSize = 1;
	private float fromAlpha = 1;
	private float toAlpha = 1;
	private float alphaVariation = 0;
	private float rotation;
	private float rotationSpeed;
	private float rotationSpeedVariation;
	private boolean collision = false;
	private float zVariation = 0;

	private float elapsedTime = 9999;
	private int emitted;
	private Random random = new Random();

	public float getTimeToNext() {
		float r = interval - this.elapsedTime;
		return (r > 0) ? r : 0;
	}

	public void emit(float elapsedTime, GameObject obj) {
		this.elapsedTime += elapsedTime;
		if (this.elapsedTime > interval && (limit == 0 || emitted < limit)) {
			this.elapsedTime = 0;

			for (int i = 0; i < particles; ++i) {
				Particle p = new Particle(this, particleType, color);
				p.setTexture(texture);
				p.setxPosition(getRandomXPosition(obj));
				p.setyPosition(getRandomYPosition(obj));
				p.setzPosition(getRandomZPosition(obj));

				p.setxSpeed(getRandomXSpeed());
				p.setySpeed(getRandomYSpeed());
				p.setxAcceleration(getRandomXAcceleration());
				p.setyAcceleration(getRandomYAcceleration());
				p.setDuration(getRandomDuration());
				p.setRotation(rotation);
				p.setRotationSpeed(getRandomRotationSpeed());
				p.setFromSize(fromSize);
				p.setToSize(toSize);
				p.setFromAlpha(fromAlpha - random.nextFloat() * alphaVariation);
				p.setToAlpha(toAlpha);
				p.setCollision(collision);

				obj.addParticle(p);
				++emitted;
			}
		}
	}

	public float getRandomXPosition(GameObject obj) {
		switch (mode) {
		case Absolute:
			return xPosition + (-positionVariation + random.nextFloat() * positionVariation * 2);
		case Relative:
			return obj.getBody().getRealX(obj, xPosition, yPosition, obj.getRotation()) + (-positionVariation + random.nextFloat() * positionVariation * 2);
		}
		return 0;
	}

	public float getRandomYPosition(GameObject obj) {
		switch (mode) {
		case Absolute:
			return yPosition + (-positionVariation + random.nextFloat() * positionVariation * 2);
		case Relative:
			return obj.getBody().getRealY(obj, xPosition, yPosition, obj.getRotation()) + (-positionVariation + random.nextFloat() * positionVariation * 2);
		}
		return 0;
	}

	public float getRandomZPosition(GameObject obj) {
		switch (mode) {
		case Absolute:
		case Relative:
			return (-zVariation + random.nextFloat() * zVariation * 2);
		}
		return 0;
	}

	public float getRandomXSpeed() {
		return xSpeed + (-speedVariation + random.nextFloat() * speedVariation * 2);
	}

	public float getRandomYSpeed() {
		return ySpeed + (-speedVariation + random.nextFloat() * speedVariation * 2);
	}

	public float getRandomXAcceleration() {
		return xAcceleration + (-accelerationVariation + random.nextFloat() * accelerationVariation * 2);
	}

	public float getRandomYAcceleration() {
		return yAcceleration + (-accelerationVariation + random.nextFloat() * accelerationVariation * 2);
	}

	public float getRandomRotationSpeed() {
		return rotationSpeed + (-rotationSpeedVariation + random.nextFloat() * rotationSpeedVariation * 2);
	}

	public float getRandomDuration() {
		return fromDuration + (toDuration - fromDuration) * random.nextFloat();
	}

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

	public Texture getBitmap() {
		return texture;
	}

	public void setBitmap(Texture texture) {
		this.texture = texture;
	}

	public float getFromDuration() {
		return fromDuration;
	}

	public void setFromDuration(float fromDuration) {
		this.fromDuration = fromDuration;
	}

	public float getToDuration() {
		return toDuration;
	}

	public void setToDuration(float toDuration) {
		this.toDuration = toDuration;
	}

	public float getInterval() {
		return interval;
	}

	public void setInterval(float interval) {
		this.interval = interval;
	}

	public int getParticles() {
		return particles;
	}

	public void setParticles(int particles) {
		this.particles = particles;
	}

	public float getPositionVariation() {
		return positionVariation;
	}

	public void setPositionVariation(float positionVariation) {
		this.positionVariation = positionVariation;
	}

	public void load(World world, ResourceMap resourceMap) {
		if (textureName != null)
			texture = world.getTextureMap().getTextureByName(textureName);
	}

	public float getFromSize() {
		return fromSize;
	}

	public void setFromSize(float fromSize) {
		this.fromSize = fromSize;
	}

	public float getToSize() {
		return toSize;
	}

	public void setToSize(float toSize) {
		this.toSize = toSize;
	}

	public float getFromAlpha() {
		return fromAlpha;
	}

	public void setFromAlpha(float fromAlpha) {
		this.fromAlpha = fromAlpha;
	}

	public float getToAlpha() {
		return toAlpha;
	}

	public void setToAlpha(float toAlpha) {
		this.toAlpha = toAlpha;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public float getxSpeed() {
		return xSpeed;
	}

	public void setxSpeed(float xSpeed) {
		this.xSpeed = xSpeed;
	}

	public float getySpeed() {
		return ySpeed;
	}

	public void setySpeed(float ySpeed) {
		this.ySpeed = ySpeed;
	}

	public float getxAcceleration() {
		return xAcceleration;
	}

	public void setxAcceleration(float xAcceleration) {
		this.xAcceleration = xAcceleration;
	}

	public float getyAcceleration() {
		return yAcceleration;
	}

	public void setyAcceleration(float yAcceleration) {
		this.yAcceleration = yAcceleration;
	}

	public float getSpeedVariation() {
		return speedVariation;
	}

	public void setSpeedVariation(float speedVariation) {
		this.speedVariation = speedVariation;
	}

	public float getAccelerationVariation() {
		return accelerationVariation;
	}

	public void setAccelerationVariation(float accelerationVariation) {
		this.accelerationVariation = accelerationVariation;
	}

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	public String getTextureName() {
		return textureName;
	}

	public void setTextureName(String textureName) {
		this.textureName = textureName;
	}

	public Particle.ParticleType getParticleType() {
		return particleType;
	}

	public void setParticleType(Particle.ParticleType particleType) {
		this.particleType = particleType;
	}

	public void setParticleType(String particleType) {
		if (particleType.equals("squareTexture")) {
			this.particleType = ParticleType.SquareTexture;
		} else if (particleType.equals("squareColor")) {
			this.particleType = ParticleType.SquareColor;
		} else if (particleType.equals("triangleTexture")) {
			this.particleType = ParticleType.TriangleTexture;
		} else if (particleType.equals("triangleColor")) {
			this.particleType = ParticleType.TriangleColor;
		}
	}

	public float getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public float getRotationSpeedVariation() {
		return rotationSpeedVariation;
	}

	public void setRotationSpeedVariation(float rotationSpeedVariation) {
		this.rotationSpeedVariation = rotationSpeedVariation;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}

	public void setAlpha(float from, float to) {
		this.setFromAlpha(from);
		this.setToAlpha(to);
	}

	public void setDuration(float from, float to) {
		this.setFromDuration(from);
		this.setToDuration(to);
	}

	public void setSize(float from, float to) {
		this.setFromSize(from);
		this.setToSize(to);
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setAlphaVariation(float alphaVariation) {
		this.alphaVariation = alphaVariation;

	}

	public void setZVariation(float zVariation) {
		this.zVariation = zVariation;

	}

	public boolean isEnableDraw() {
		return enableDraw;
	}

	public void setEnableDraw(boolean enableDraw) {
		this.enableDraw = enableDraw;
	}

}
