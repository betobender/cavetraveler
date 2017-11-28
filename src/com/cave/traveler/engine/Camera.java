package com.cave.traveler.engine;

import java.util.Random;

import android.opengl.Matrix;

import com.cave.traveler.engine.Graphics.Point3D;
import com.cave.traveler.engine.Graphics.Shader;
import com.cave.traveler.engine.Graphics.Shaders;

public class Camera {
	private Random random = new Random();
	private String relativeTo;
	private Point3D from;
	private Point3D to;
	private Point3D lookFrom;
	private Point3D lookTo;
	private float duration;

	private float elapsedTime;
	private Point3D lookSpeed;
	private Point3D speed;
	private Point3D current = new Point3D();
	private Point3D currentLook = new Point3D();

	private Point3D offset = new Point3D();
	private float variationRegion = 0;
	private float variationRegionDecay = 0;

	public void update(float elapsedTime) {
		this.elapsedTime += elapsedTime;

		if (variationRegion > 0) {
			for (int i = 0; i < 3; ++i) {
				offset.get()[i] = -variationRegion + (random.nextFloat() * variationRegion * 2);
			}
			variationRegion -= variationRegionDecay * elapsedTime;
		}

		float maxElapsedTime = this.elapsedTime;
		if (this.elapsedTime > duration)
			maxElapsedTime = duration;

		for (int i = 0; i < 3; ++i) {
			current.get()[i] = from.get()[i] + speed.get()[i] * maxElapsedTime + offset.get()[i];
			currentLook.get()[i] = lookFrom.get()[i] + lookSpeed.get()[i] * maxElapsedTime + offset.get()[i];
		}

	}

	public void explosionEffect(float variation, float decay) {
		variationRegion = variation;
		variationRegionDecay = decay;
	}

	public void load(Camera lastCamera) {
		if (lastCamera != null) {
			if (from == null)
				from = lastCamera.getCurrent();
			if (to == null)
				to = lastCamera.getCurrent();
			if (lookFrom == null)
				lookFrom = lastCamera.getCurrentLook();
			if (lookTo == null)
				lookTo = lastCamera.getCurrentLook();
		}

		lookSpeed = new Point3D();
		speed = new Point3D();

		for (int i = 0; i < 3; ++i) {
			lookSpeed.get()[i] = (lookTo.get()[i] - lookFrom.get()[i]) / duration;
			speed.get()[i] = (to.get()[i] - from.get()[i]) / duration;
		}

		update(0);
	}

	private Point3D getCurrent() {
		return current;
	}

	private Point3D getCurrentLook() {
		return currentLook;
	}

	public void setCamera(Shaders shaders, GameObject obj) {
		// TODO: relative to implementation
		for (Shader shader : shaders.getShaders())
			Matrix.setLookAtM(shader.getViewMatrix(), 0, obj.getxPosition() + current.getX(), obj.getyPosition() + current.getY(), +current.getZ(),
					obj.getxPosition() + currentLook.getX(), obj.getyPosition() + currentLook.getY(), currentLook.getZ(), 0, 1, 0);

	}

	public String getRelativeTo() {
		return relativeTo;
	}

	public void setRelativeTo(String relativeTo) {
		this.relativeTo = relativeTo;
	}

	public Point3D getFrom() {
		return from;
	}

	public void setFrom(Point3D from) {
		this.from = from;
	}

	public Point3D getTo() {
		return to;
	}

	public void setTo(Point3D to) {
		this.to = to;
	}

	public Point3D getLookFrom() {
		return lookFrom;
	}

	public void setLookFrom(Point3D lookFrom) {
		this.lookFrom = lookFrom;
	}

	public Point3D getLookTo() {
		return lookTo;
	}

	public void setLookTo(Point3D lookTo) {
		this.lookTo = lookTo;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public void reset() {
		this.elapsedTime = 0;

	}
}
