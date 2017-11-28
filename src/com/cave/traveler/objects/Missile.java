package com.cave.traveler.objects;

import java.io.IOException;

import android.opengl.Matrix;

import com.cave.traveler.engine.Body;
import com.cave.traveler.engine.DynamicLightSource;
import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.Particle;
import com.cave.traveler.engine.ParticleEmitter;
import com.cave.traveler.engine.PhysicBody;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.Light;
import com.cave.traveler.engine.Graphics.PerPixelShader;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Shaders;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.engine.control.ControlEvent;
import com.cave.traveler.engine.control.TouchControlEvent;
import com.cave.traveler.engine.sound.Sound;
import com.cave.traveler.engine.triggers.TriggerDispatcher;
import com.cave.traveler.model.Model;
import com.cave.traveler.model.ModelFactory;

public class Missile extends GameObject {

	private float scaleAdjust = 2f;
	private Model missileModel;
	private ParticleEmitter missileEmitter;
	private float elapsedTime = 5;
	private float duration = 5;
	private Light missileLight = new Light(new float[] { 0, 0, 0, 1 }, new float[] { 1, 0, 1, 1 }, 1);
	private boolean collided = false;
	private boolean firstFire = true;
	private float impulseAcceleration = 200;
	private boolean buttonDown = false;

	@Override
	public void update(float elapsedTime, World world) {

		if (collided == false) {
			missileEmitter.setxSpeed(getxSpeed() * 0.5f);
			missileEmitter.setySpeed(getySpeed() * 0.5f);
			missileEmitter.emit(elapsedTime, this);

			if(buttonDown)
				setyAcceleration(impulseAcceleration);
			else 
				setyAcceleration(0);
			
			DynamicLightSource.getInstance().updateLight(1, world, this);
			super.update(elapsedTime, world);
		}

		this.elapsedTime += elapsedTime;
		missileLight.update(elapsedTime);
		missileModel.update(elapsedTime);
	}

	public boolean hasExpired() {
		return this.elapsedTime >= duration;
	}

	public void fire(GameObject obj, World world) {
		this.setxPosition(obj.getBody().getRealX(obj, 1, -0.4f, obj.getRotation()));
		this.setyPosition(obj.getBody().getRealY(obj, 1, -0.4f, obj.getRotation()));

		this.setDynamic(true);
		this.setxSpeed(obj.getxSpeed());
		this.setySpeed(obj.getySpeed());
		this.setxAcceleration(obj.getxAcceleration() + 100f);
		this.setyAcceleration(0);

		elapsedTime = 0;
		missileLight = new Light(new float[] { 0, 0, 0, 1 }, new float[] { 1, 0, 1, 1 }, 1);
		collided = false;
		missileModel.resetExplosion();
		missileEmitter.setEnableDraw(true);

		Sound s = world.getSoundManager().getSound("missile_sound");
		s.playNormal();
		
		if(firstFire) {
			firstFire = false;
			world.getScore().setBottomMessage("Remember, you can control the missile");
		}
	}

	private float[] color = new float[] { 0.5f, 0.5f, 0.5f, 1 };

	@Override
	public void setupLights(World world, Shaders shaders) {

		if (DynamicLightSource.getInstance().isLightSource(1, this)) {
			PerPixelShader shader = (PerPixelShader) shaders.getShaderByName("default");
			shader.load();
			shader.setLight(1, new float[] { getxPosition(), getyPosition(), 0 }, missileLight, 5f);
		}

		super.setupLights(world, shaders);
	}

	@Override
	public void trigger(TriggerDispatcher dispatcher, World world) {
		if (dispatcher == TriggerDispatcher.CaveCollision && collided == false) {
			if (world.getCameraController().getCurrentState().getCamera() != null)
				world.getCameraController().getCurrentState().getCamera().explosionEffect(5, 20);

			missileModel.explode3D(15, 100);
			setDynamic(false);
			missileLight = new Light(new float[] { 3, 0, 0, 1 }, new float[] { 0, 0, 0, 1 }, 0.5f);
			collided = true;
			missileEmitter.setEnableDraw(false);

			Sound explosionSound = world.getSoundManager().getSound("ship_explosion");
			explosionSound.playNormal(0.3f, 0.3f);
		}
	}

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {

		if (hasExpired())
			return;

		PerPixelShader shader = (PerPixelShader) shaders.getShaderByName("default");
		shader.load();

		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, getxPosition(), getyPosition(), 0);
		Matrix.rotateM(shader.getModelMatrix(), 0, getRotation(), 0, 0, 1);
		Matrix.rotateM(shader.getModelMatrix(), 0, -90, 0, 0, 1);
		Matrix.scaleM(shader.getModelMatrix(), 0, getBody().getScaleX() * scaleAdjust, getBody().getScaleY() * scaleAdjust, getBody().getScaleZ() * scaleAdjust);

		missileModel.draw(shader);
		missileModel.setColor(color);

		super.draw(textureMap, shaders);
	}

	@Override
	public void load(World world, ResourceMap resourceMap) {
		super.load(world, resourceMap);

		try {
			missileModel = ModelFactory.getInstance().createModelFromResource(resourceMap, "missilemodel");
			missileModel.load(world.getTextureMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		missileEmitter.load(world, resourceMap);
	}

	@Override
	public void processControlEvent(ControlEvent event) {
		if (event.getClass().equals(TouchControlEvent.class)) {
			TouchControlEvent evt = (TouchControlEvent) event;
			if(evt.isLeftSide() == false) {
				buttonDown = evt.isButtonDown();
			}
		}
	}

	public Missile() {

		setName("missile");
		setDynamic(false);
		setCollidable(true);
		setInitialState("normal");
		setPlayable(true);
		setBody(new Body(1, 1, 1));

		PhysicBody physicBody = new PhysicBody("points", 3);
		physicBody.setPoints(new float[][] { new float[] { -0.5f, -0.2f }, new float[] { 0.5f, 0f }, new float[] { -0.5f, 0.2f } });
		setPhysicBody(physicBody);

		setInitialXPosition(-1000);
		setAllowWorldForces(true);

		State normalState = new State();
		normalState.setState("normal");
		addState(normalState);

		missileEmitter = new ParticleEmitter();
		missileEmitter.setParticleType(Particle.ParticleType.SquareGlow);
		missileEmitter.setFromAlpha(0);
		missileEmitter.setToAlpha(1);
		missileEmitter.setParticles(10);
		missileEmitter.setDuration(0.55f, 0.55f);
		missileEmitter.setFromSize(1);
		missileEmitter.setToSize(5);
		missileEmitter.setTextureName("smoke");
		missileEmitter.setxPosition(-0.1f);
		missileEmitter.setInterval(0.000005f);
		missileEmitter.setColor(new float[] { 1, 0, 1, 1 });
	}
}
