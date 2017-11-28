package com.cave.traveler.engine;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cave.traveler.engine.Graphics.Drawable;
import com.cave.traveler.engine.Graphics.GLESHelper;
import com.cave.traveler.engine.Graphics.ParticleShader;
import com.cave.traveler.engine.Graphics.Point2D;
import com.cave.traveler.engine.Graphics.Point3D;
import com.cave.traveler.engine.Graphics.Shaders;
import com.cave.traveler.engine.Graphics.Texture;
import com.cave.traveler.engine.Graphics.TextureMap;

public class Particle implements Drawable {

	public enum ParticleType {
		SquareTexture, SquareTextureDepthTest, SquareColor, SquareGlow, TriangleTexture, TriangleColor
	}

	// Creator
	private ParticleEmitter particleEmitter;

	// Configuration
	private ParticleType particleType = ParticleType.SquareTexture;

	// State
	private boolean alive = true;
	private boolean stopped = false;
	private float duration;
	private float current;
	private float life = 1;
	private float xPosition;
	private float yPosition;
	private float zPosition;
	private float xSpeed;
	private float ySpeed;
	private float xAcceleration;
	private float yAcceleration;
	private float rotation;
	private float rotationSpeed = 100;
	private Texture texture;
	private float fromSize;
	private float toSize;
	private float fromAlpha;
	private float toAlpha;
	private float[] color = new float[] { 1, 1, 1, 1 };
	private boolean collision;

	// Draw
	private FloatBuffer vertexBuffer;
	private FloatBuffer colorBuffer;
	private FloatBuffer textureBuffer;
	private String selectedShader;

	public Particle(ParticleEmitter particleEmitter, ParticleType particleType, float[] color) {
		this.particleEmitter = particleEmitter;
		this.particleType = particleType;
		this.color = color;

		switch (particleType) {
		case SquareColor:
			vertexBuffer = GLESHelper.allocateFloatBuffer(12, Point3D.expandToSquare(1));
			colorBuffer = GLESHelper.allocateFloatBuffer(16, color);
			selectedShader = "color_particle";
			break;
		case SquareTexture:
		case SquareTextureDepthTest:
		case SquareGlow:
			vertexBuffer = GLESHelper.allocateFloatBuffer(12, Point3D.expandToSquare(1));
			textureBuffer = GLESHelper.allocateFloatBuffer(8, Point2D.expandToSquare(0, 0, 1, 1));
			colorBuffer = GLESHelper.allocateFloatBuffer(16, color);
			selectedShader = "particle";
			break;
		case TriangleColor:
			vertexBuffer = GLESHelper.allocateFloatBuffer(9, Point3D.expandToTriangle(1));
			colorBuffer = GLESHelper.allocateFloatBuffer(12, color);
			selectedShader = "color_particle";
			break;
		case TriangleTexture:
			vertexBuffer = GLESHelper.allocateFloatBuffer(9, Point3D.expandToTriangle(1));
			colorBuffer = GLESHelper.allocateFloatBuffer(12, color);
			textureBuffer = GLESHelper.allocateFloatBuffer(6, Point2D.expandToTriangle(0, 0, 1, 1));
			selectedShader = "particle";
			break;
		}
	}

	public void prepare(World world, TextureMap textureMap) {
	}

	public void setupLights(World world, Shaders shaders) {

	}

	public void draw(TextureMap textureMap, Shaders shaders) {

		if (this.particleEmitter.isEnableDraw() == false)
			return;

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		ParticleShader shader = (ParticleShader) shaders.getShaderByName(selectedShader);
		shader.load();

		float currentAlpha = getCurrentAlpha();

		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, xPosition, yPosition, zPosition - (1f - currentAlpha));
		Matrix.rotateM(shader.getModelMatrix(), 0, rotation, 0, 0, 1);
		Matrix.scaleM(shader.getModelMatrix(), 0, getCurrentSize(), getCurrentSize(), 1);

		for (int i = 3; i < colorBuffer.capacity(); i += 4)
			colorBuffer.put(i, currentAlpha);

		switch (particleType) {
		case SquareTextureDepthTest:
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			GLES20.glDepthFunc(GLES20.GL_LEQUAL);
			break;
		case SquareGlow:
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			GLES20.glBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			break;
		default:
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			break;
		}

		switch (particleType) {
		case SquareColor:
		case TriangleColor:
			GLES20.glDisableVertexAttribArray(shader.getTextureHandle());
			break;
		case SquareTexture:
		case TriangleTexture:
		case SquareTextureDepthTest:
		case SquareGlow:
			GLES20.glBindTexture(GL10.GL_TEXTURE_2D, texture.getTextureId());
			GLES20.glVertexAttribPointer(shader.getTextureHandle(), 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
			GLES20.glEnableVertexAttribArray(shader.getTextureHandle());
			break;

		}

		GLES20.glEnableVertexAttribArray(shader.getColorHandle());
		GLES20.glEnableVertexAttribArray(shader.getVertexHandle());

		GLES20.glVertexAttribPointer(shader.getColorHandle(), 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
		GLES20.glVertexAttribPointer(shader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

		shader.computeViewMatrix();
		GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertexBuffer.capacity() / 3);

		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void update(float elapsedTime, GameObject obj, World world) {
		if (alive) {
			current += elapsedTime;
			alive = current < duration;
			life = 1 - Math.min(current / duration, 1);

			if (stopped == false) {
				xPosition += xSpeed * elapsedTime;
				yPosition += ySpeed * elapsedTime;
				xSpeed += xAcceleration * elapsedTime;
				ySpeed += yAcceleration * elapsedTime;
				rotation += rotationSpeed * elapsedTime;
			}
		} else {
			life = 0;
		}
	}

	public float getLife() {
		return life;
	}

	public boolean isAlive() {
		return alive;
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

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
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

	public float getCurrentSize() {
		return fromSize + (toSize - fromSize) * (1f - life);
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

	public float getCurrentAlpha() {
		return fromAlpha + (toAlpha - fromAlpha) * (1f - life);
	}

	public float[] getColor() {
		return color;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public ParticleType getParticleType() {
		return particleType;
	}

	public void setParticleType(ParticleType particleType) {
		this.particleType = particleType;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}

	public boolean isGameObject() {
		return false;
	}

	public float getzPosition() {
		return zPosition;
	}

	public void setzPosition(float zPosition) {
		this.zPosition = zPosition;
	}

	public ParticleEmitter getParticleEmitter() {
		return particleEmitter;
	}

	public void setParticleEmitter(ParticleEmitter particleEmitter) {
		this.particleEmitter = particleEmitter;
	}
}
