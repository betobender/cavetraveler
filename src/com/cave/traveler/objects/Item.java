package com.cave.traveler.objects;

import java.io.IOException;

import android.opengl.Matrix;

import com.cave.traveler.engine.Body;
import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.Particle;
import com.cave.traveler.engine.ParticleEmitter;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.NoiseShader;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Shaders;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.engine.sound.Sound;
import com.cave.traveler.model.Model;
import com.cave.traveler.model.ModelFactory;

public class Item extends GameObject {

	private float scaleAdjust = 6f;
	private Model itemModel;
	private boolean collided = false;
	private float rotation = 0;
	private float rotationSpeed = 50;
	private float relativeYPosition;
	private float collisionRegion = 20f;
	private ParticleEmitter emitter;

	@Override
	public void update(float elapsedTime, World world) {

		if (collided == false) {
			rotation += rotationSpeed * elapsedTime;
			emitter.emit(elapsedTime, this);

			GameObject obj = world.getCameraController();
			if (obj != null) {
				if (obj.getxPosition() > getxPosition() - collisionRegion && obj.getxPosition() < getxPosition() + collisionRegion) {
					if (obj.getyPosition() > getyPosition() - collisionRegion && obj.getyPosition() < getyPosition() + collisionRegion) {
						collided = true;
						pickup(world);
					}
				}
			}
		}

		float caveBottom = world.getDynamicCave().getBottom(getxPosition());
		float caveHeight = world.getDynamicCave().getHeight(getxPosition());
		setyPosition(caveBottom += caveHeight * relativeYPosition);
		itemModel.update(elapsedTime);
		super.update(elapsedTime, world);
	}

	private void pickup(World world) {
		itemModel.explode3D(50, 50);
		Sound explosionSound = world.getSoundManager().getSound("bonus_sound");
		explosionSound.playNormal(0.3f, 0.3f);
		world.getScore().addPoints(1000f);
	}

	public boolean hasExpired() {
		return collided;
	}

	private float[] color = new float[] { 0f, 1f, 0f, 1f };

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {

		NoiseShader shader = (NoiseShader) shaders.getShaderByName("noise");
		shader.load();

		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, getxPosition(), getyPosition(), 0);
		Matrix.rotateM(shader.getModelMatrix(), 0, rotation, 1, 1, 1);
		Matrix.scaleM(shader.getModelMatrix(), 0, getBody().getScaleX() * scaleAdjust, getBody().getScaleY() * scaleAdjust, getBody().getScaleZ() * scaleAdjust);

		itemModel.setColor(color);
		itemModel.draw(shader);

		super.draw(textureMap, shaders);
	}

	@Override
	public void load(World world, ResourceMap resourceMap) {
		super.load(world, resourceMap);

		try {
			itemModel = ModelFactory.getInstance().createModelFromResource(resourceMap, "cubemodel");
			itemModel.load(world.getTextureMap());
			emitter.load(world, resourceMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Item() {

		setName("item");
		setDynamic(false);
		setCollidable(false);
		setInitialState("normal");
		setBody(new Body(1, 1, 1));

		setInitialXPosition(-1000);

		State normalState = new State();
		normalState.setState("normal");
		addState(normalState);
		
		emitter = new ParticleEmitter();
		emitter.setParticleType(Particle.ParticleType.SquareTextureDepthTest);
		emitter.setFromAlpha(1);
		emitter.setToAlpha(1);
		emitter.setFromDuration(1f);
		emitter.setToDuration(1f);
		emitter.setFromSize(1);
		emitter.setToSize(80);
		emitter.setTextureName("bluebloom");
		emitter.setInterval(1f);
		emitter.setColor(new float[] { 0f, 1f, 0f, 1f });		
	}

	public void setyRelativePosition(float relativeYPosition) {
		this.relativeYPosition = relativeYPosition;
	}

	public void resetCollision() {
		itemModel.resetExplosion();
		collided = false;
	}
}
