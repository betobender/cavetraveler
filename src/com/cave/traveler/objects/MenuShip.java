package com.cave.traveler.objects;

import java.io.IOException;

import android.opengl.Matrix;

import com.cave.traveler.engine.Body;
import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.Particle;
import com.cave.traveler.engine.ParticleEmitter;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.Light;
import com.cave.traveler.engine.Graphics.PerPixelShader;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Shaders;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.engine.setters.AcceleratedSetter;
import com.cave.traveler.engine.sound.Sound;
import com.cave.traveler.model.Model;
import com.cave.traveler.model.ModelFactory;

public class MenuShip extends GameObject {

	private Light thrusterLight = new Light(new float[] { 0, 0, 0 }, new float[] { 1, 0, 0 }, 1);
	private ParticleEmitter thrusterEmitter;
	private Sound engineSound = null;

	@Override
	public void update(float elapsedTime, World world) {
		
		thrusterLight.update(elapsedTime);
		shipModel.update(elapsedTime);
		super.update(elapsedTime, world);
		
		setySpeed((world.getDynamicCave().getMiddle(getxPosition()) - getyPosition())*0.5f);
		
		if(getxPosition() >= 4000)
			setxPosition(0);

		float volume = 0.8f;
		if(getxPosition() < 1000) {
			volume = (getxPosition() / 1000) * 0.8f;
		} else {
			volume = ((3000 - (getxPosition() - 1000)) / 3000) * 0.8f;
		}
		
		engineSound.playLoop(volume, volume);
		
		super.update(elapsedTime, world);
	}

	@Override
	public void setupLights(World world, Shaders shaders) {

		PerPixelShader shader = (PerPixelShader) shaders.getShaderByName("default");
		shader.load();

		// set a light in front of the object
		shader.setLight(0, new float[] { getBody().getRealX(this, -1, 0, getRotation()), getBody().getRealY(this, -1.1f, 0, getRotation()), 0 }, thrusterLight,
				2f);

		super.setupLights(world, shaders);
	}

	Model shipModel;
	private float scaleAdjust = 1f / 6f;

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {

		PerPixelShader shader = (PerPixelShader) shaders.getShaderByName("default");
		shader.load();

		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, getxPosition(), getyPosition(), 0);
		Matrix.rotateM(shader.getModelMatrix(), 0, getRotation(), 0, 0, 1);
		Matrix.scaleM(shader.getModelMatrix(), 0, getBody().getScaleX() * scaleAdjust, getBody().getScaleY() * scaleAdjust, getBody().getScaleZ() * scaleAdjust);

		shipModel.draw(shader);

		super.draw(textureMap, shaders);
	}

	@Override
	public void load(World world, ResourceMap resourceMap) {
		super.load(world, resourceMap);
		
		try {
			shipModel = ModelFactory.getInstance().createModelFromResource(resourceMap, "spaceshipmodel");
			shipModel.load(world.getTextureMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		engineSound = world.getSoundManager().getSound("ship_engine_sound");
	}

	public MenuShip() {

		setName("shipMenu");
		setAttachedToCamera(false);
		setCollidable(false);
		setDynamic(true);
		setInitialState("normal");
		setInitialYPosition(50);
		setInitialXSpeed(80);
		setPlayable(false);

		setBody(new Body(50, 50, 50));

		State normalState = new State();
		normalState.setCopyEmitters(false);
		normalState.setState("normal");
		normalState.addSetter(new AcceleratedSetter(true));

		thrusterEmitter = new ParticleEmitter();
		thrusterEmitter.setParticleType(Particle.ParticleType.SquareTexture);
		thrusterEmitter.setFromAlpha(1);
		thrusterEmitter.setToAlpha(0);
		thrusterEmitter.setFromDuration(2.6f);
		thrusterEmitter.setToDuration(2.6f);
		thrusterEmitter.setFromSize(10);
		thrusterEmitter.setToSize(50);
		thrusterEmitter.setTextureName("bluebloom");
		thrusterEmitter.setxPosition(-0.5f);
		thrusterEmitter.setInterval(0.04f);
		thrusterEmitter.setColor(new float[] { 1, 0, 0, 1 });

		normalState.addParticleEmitter(thrusterEmitter);

		thrusterLight.setFlickering(true, 0.9f, 1f);
		
		addState(normalState);
	}
}
