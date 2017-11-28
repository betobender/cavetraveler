package com.cave.traveler.engine.Graphics;

import android.opengl.Matrix;

public class GrowingImage extends Drawing {

	private boolean active = false;
	private float fromWidth;
	private float fromHeight;
	private float duration;
	private float widthScaleFactor;
	private float heightScaleFactor;
	private float elapsedTime;

	public GrowingImage(String textureName, float fromWidth, float toWidth, float fromHeight, float toHeight, float duration) {
		super(textureName, 1, 1);
		this.fromWidth = fromWidth;
		this.fromHeight = fromHeight;
		this.duration = duration;

		widthScaleFactor = (toWidth - fromWidth) / duration;
		heightScaleFactor = (toHeight - fromHeight) / duration;
	}

	public void reset() {
		this.elapsedTime = 0;
		this.active = true;
	}

	public void update(float elapsedTime) {
		this.elapsedTime += elapsedTime;
		if (this.elapsedTime > duration) {
			this.active = false;
		} else {
			this.setSize(fromWidth + this.elapsedTime * widthScaleFactor, fromHeight + this.heightScaleFactor * this.elapsedTime);
			this.setAlpha(1 - this.elapsedTime / duration);
		}
	}

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {
		if(this.active) {
			ParticleShader shader = (ParticleShader) shaders.getShaderByName("particle");
			shader.load();
			Matrix.setIdentityM(shader.getModelMatrix(), 0);
			super.draw(textureMap, shaders);
		}
	}

}
