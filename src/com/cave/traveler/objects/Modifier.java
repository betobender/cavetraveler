package com.cave.traveler.objects;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cave.traveler.engine.DynamicCave;
import com.cave.traveler.engine.DynamicLightSource;
import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.Particle;
import com.cave.traveler.engine.ParticleEmitter;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.GLESHelper;
import com.cave.traveler.engine.Graphics.NoiseShader;
import com.cave.traveler.engine.Graphics.ParticleShader;
import com.cave.traveler.engine.Graphics.PerPixelShader;
import com.cave.traveler.engine.Graphics.Point2D;
import com.cave.traveler.engine.Graphics.Point3D;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Shaders;
import com.cave.traveler.engine.Graphics.Texture;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.engine.sound.Sound;
import com.cave.traveler.engine.sound.SoundManager;
import com.cave.traveler.model.Model;
import com.cave.traveler.model.ModelFactory;

public class Modifier extends GameObject {

	private float elapsedTime;
	private float height;
	private float depth = 100;
	private float alpha = 1;
	private float length = 0;
	private float[] flickerColor = new float[] { 0, 0, 0, 1 };
	private boolean flickering = true;
	private float lightIntensity = 1;
	private float lightIntensityDecay = 0;
	private Random random = new Random();
	private ParticleEmitter sparks;
	private float scale = 1;
	private float scaleDecaySpeed = 3f;

	private String modifierTextureIconName;
	private Texture modifierTexture;
	private Texture modifierTextureIcon;

	public Modifier() {
		sparks = new ParticleEmitter();
		sparks.setParticleType(Particle.ParticleType.SquareTextureDepthTest);
		sparks.setLimit(40);
		sparks.setParticles(40);
		sparks.setFromSize(1);
		sparks.setToSize(2);
		sparks.setFromAlpha(1);
		sparks.setToAlpha(0);
		sparks.setFromDuration(2);
		sparks.setToDuration(2);
		sparks.setyAcceleration(-50);
		sparks.setSpeedVariation(50);
		sparks.setAccelerationVariation(200);
		sparks.setRotationSpeedVariation(200);
		sparks.setTextureName("modifier_fragment");

		setInitialXPosition(-1000);
	}

	ParticleEmitter getSparks() {
		return sparks;
	}

	public void setFlickerColor(float[] flickerColor) {
		this.flickerColor = flickerColor;
		this.iconColorBuffer = GLESHelper.allocateFloatBuffer(16, flickerColor);
		if (cubeModel != null)
			cubeModel.setColor(flickerColor);
	}

	@Override
	public void setupLights(World world, Shaders shaders) {

		if (DynamicLightSource.getInstance().isLightSource(1, this)) {
			PerPixelShader shader = (PerPixelShader) shaders.getShaderByName("default");
			shader.load();

			if (flickering) {
				shader.setLight(1, new float[] { getxPosition(), getyPosition(), 50 }, new float[] { flickerColor[0] * alpha, flickerColor[1] * alpha,
						flickerColor[2] * alpha, 1 }, 5f);
			} else {
				shader.setLight(1, new float[] { getxPosition(), getyPosition(), 50 }, new float[] { flickerColor[0] * lightIntensity,
						flickerColor[1] * lightIntensity, flickerColor[2] * lightIntensity, 1 }, 5f);
			}
		}
	}

	private FloatBuffer iconVertexBuffer;
	private FloatBuffer iconTextureCoordsBuffer;
	private FloatBuffer iconColorBuffer;

	private Model cubeModel;

	@Override
	public void load(World world, ResourceMap resourceMap) {
		super.load(world, resourceMap);

		depth = world.getDynamicCave().getRenderCaveDepth() - 4;
		height = world.getDynamicCave().getRenderCaveTop() - world.getDynamicCave().getRenderCaveBottom();

		try {
			cubeModel = ModelFactory.getInstance().createModelFromResource(resourceMap, "cubemodel");
			cubeModel.load(world.getTextureMap());
			cubeModel.setColor(flickerColor);
			cubeModel.setCullFace(false);
			cubeModel.setBlend(true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		modifierTextureIcon = world.getTextureMap().getTextureByName(modifierTextureIconName);
		iconVertexBuffer = GLESHelper.allocateFloatBuffer(12, Point3D.expandToSquare(300));
		iconTextureCoordsBuffer = GLESHelper.allocateFloatBuffer(8, Point2D.expandToSquare(0, 1, 1, 0));
	}

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {

		NoiseShader noiseShader = (NoiseShader) shaders.getShaderByName("noise");
		noiseShader.load();
		noiseShader.setSeed(elapsedTime);
		noiseShader.setNoiseColor(flickerColor);

		Matrix.setIdentityM(noiseShader.getModelMatrix(), 0);
		Matrix.translateM(noiseShader.getModelMatrix(), 0, getxPosition(), getyPosition(), 0);
		Matrix.scaleM(noiseShader.getModelMatrix(), 0, 10 * scale, scale * (height / 2), scale * (depth / 2));

		cubeModel.draw(noiseShader);

		// draw icon
		ParticleShader particleShader = (ParticleShader) shaders.getShaderByName("particle");
		particleShader.load();

		Matrix.setIdentityM(particleShader.getModelMatrix(), 0);
		Matrix.translateM(particleShader.getModelMatrix(), 0, getxPosition(), getyPosition(), depth / 2 + 20);
		Matrix.rotateM(particleShader.getModelMatrix(), 0, 90, 0, 0, 1);

		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);

		GLES20.glEnableVertexAttribArray(particleShader.getVertexHandle());
		GLES20.glEnableVertexAttribArray(particleShader.getTextureHandle());
		GLES20.glEnableVertexAttribArray(particleShader.getColorHandle());

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glVertexAttribPointer(particleShader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, iconVertexBuffer);
		GLES20.glVertexAttribPointer(particleShader.getTextureHandle(), 2, GLES20.GL_FLOAT, false, 0, iconTextureCoordsBuffer);
		GLES20.glVertexAttribPointer(particleShader.getColorHandle(), 4, GLES20.GL_FLOAT, false, 0, iconColorBuffer);
		GLES20.glBindTexture(GL10.GL_TEXTURE_2D, modifierTextureIcon.getTextureId());

		particleShader.computeViewMatrix();
		GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, iconVertexBuffer.capacity() / 3);
	}

	@Override
	public void update(float elapsedTime, World world) {

		setyPosition(world.getDynamicCave().getMiddle(getxPosition()));
		height = (world.getDynamicCave().getHeight(getxPosition()) + 10);

		// check ship collision
		if (world.getCameraController() != null && world.getCameraController().getClass().equals(Ship.class) == true) {
			GameObject obj = world.getCameraController();
			if (alreadyCollided == false) {
				if (obj.getxPosition() > getxPosition()) {
					shipCollision(elapsedTime, world, obj);
					alreadyCollided = true;
				}
			} else {
				if (scale > 0) {
					scale -= scaleDecaySpeed * elapsedTime;
					if (scale < 0)
						scale = 0f;
				}
			}
		}

		super.update(elapsedTime, world);
		alpha = 0.5f + random.nextFloat() * 0.5f;
		this.elapsedTime += elapsedTime;

		DynamicLightSource.getInstance().updateLight(1, world, this);

		if (lightIntensity > 1) {
			lightIntensity -= lightIntensityDecay * elapsedTime;
		}

		float alpha = 0.5f + (float) Math.sin(this.elapsedTime * 5) * 0.5f;
		for (int i = 0; i < 16; i += 4)
			iconColorBuffer.put(i + 3, alpha);
	}

	public void explodeLight() {
		lightIntensity = 20f;
		lightIntensityDecay = 100f;
		flickering = false;
	}

	public Texture getModifierTextureIcon() {
		return modifierTextureIcon;
	}

	public void setModifierTextureIcon(Texture modifierTextureIcon) {
		this.modifierTextureIcon = modifierTextureIcon;
	}

	public Texture getModifierTexture() {
		return modifierTexture;
	}

	public void setModifierTexture(Texture modifierTexture) {
		this.modifierTexture = modifierTexture;
	}

	public String getModifierTextureIconName() {
		return modifierTextureIconName;
	}

	public void setModifierTextureIconName(String modifierTextureIconName) {
		this.modifierTextureIconName = modifierTextureIconName;
	}

	boolean alreadyCollided = true;

	public void shipCollision(float elapsedTime, World world, GameObject obj) {
	}

	public void resetCollision(DynamicCave dynamicCave) {
		alreadyCollided = false;
		scale = 1;
		setyPosition(dynamicCave.getMiddle(getxPosition()));
	}

	public boolean hasExpired(float caveXPosition) {
		return alreadyCollided && caveXPosition > length + getxPosition();
	}

	public void playModifierSound(SoundManager soundManager) {
		Sound sound = soundManager.getSound("modifier_sound");
		sound.playNormal();
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}
}
