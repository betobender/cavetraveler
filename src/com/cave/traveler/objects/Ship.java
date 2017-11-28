package com.cave.traveler.objects;

import java.io.IOException;
import java.util.Random;

import android.opengl.Matrix;

import com.cave.traveler.engine.Body;
import com.cave.traveler.engine.Camera;
import com.cave.traveler.engine.CaveCollisionData;
import com.cave.traveler.engine.CaveCollisionData.CaveCollision;
import com.cave.traveler.engine.DynamicCave;
import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.Particle;
import com.cave.traveler.engine.ParticleEmitter;
import com.cave.traveler.engine.PhysicBody;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.GameoverBoard;
import com.cave.traveler.engine.Graphics.Light;
import com.cave.traveler.engine.Graphics.PerPixelShader;
import com.cave.traveler.engine.Graphics.Point3D;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Shaders;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.engine.control.ControlEvent;
import com.cave.traveler.engine.control.TouchControlEvent;
import com.cave.traveler.engine.setters.AcceleratedSetter;
import com.cave.traveler.engine.setters.AccelerationSetter;
import com.cave.traveler.engine.setters.DynamicSetter;
import com.cave.traveler.engine.sound.Sound;
import com.cave.traveler.engine.triggers.TriggerDispatcher;
import com.cave.traveler.model.Model;
import com.cave.traveler.model.ModelFactory;

public class Ship extends GameObject {

	private Light thrusterLight = new Light(new float[] { 0, 0, 0 }, new float[] { 0.5f, 0.5f, 1 }, 1);
	private ParticleEmitter thrusterEmitter;
	private boolean collided = false;
	private GameoverBoard gameOverMessageBoard = new GameoverBoard("Game Over", 1, -1, 3);
	private ParticleEmitter nearMissEmitter;
	private ParticleEmitter warningEmitter;
	private Sound engineSound = null;
	private float impulseAcceleration = 200;
	private ControlEvent lastEvent = null;
	private boolean randomImpulseGeneration = false;
	private boolean impulseOn = false;
	private Random random = new Random();
	private Missile missile = new Missile();
	private boolean barrierWarning = false;

	@Override
	public void prepare(World world, TextureMap textureMap) {
		gameOverMessageBoard.prepare(world, textureMap);
		super.prepare(world, textureMap);
	}

	@Override
	public void trigger(TriggerDispatcher dispatcher, World world) {
		if (dispatcher == TriggerDispatcher.CaveCollision && getCurrentState().getState().equals("normal")) {
			if (getCurrentState().getCamera() != null)
				getCurrentState().getCamera().explosionEffect(20, 10);

			setCurrentState("explosion");
			world.getSoundManager().pause();

			Sound explosionSound = world.getSoundManager().getSound("ship_explosion");
			explosionSound.playNormal();

			shipModel.explode3D(15, 100);
			collided = true;
			world.getScore().setBottomMessage("Game over, kaboom!!!");
			world.getScore().stop();
			engineSound.stop();
			gameOverMessageBoard.setScore(world);
			thrusterEmitter.setEnableDraw(false);
			thrusterLight = new Light(new float[] { 100, 0, 0 }, new float[] { 1, 1, 1 }, 0.5f);
		}
	}

	@Override
	public void update(float elapsedTime, World world) {
		thrusterLight.update(elapsedTime);
		shipModel.update(elapsedTime);

		if (collided) {
			gameOverMessageBoard.update(elapsedTime, world);
			world.getScore().toggleNearMiss(false);
		} else {

			if (getCurrentState().getState().equals("normal")) {
				if (impulseOn) {
					this.setyAcceleration(impulseAcceleration);
					if (randomImpulseGeneration) {
						this.setyAcceleration(impulseAcceleration - 0.5f * random.nextFloat() * impulseAcceleration);
					}
				} else {
					this.setyAcceleration(0);
				}

				// shot missiles
				Barrier b = (Barrier) world.getDynamicCave().getObjectRefence(DynamicCave.ObjectReference.Barrier0);

				if (b != null && b.isActive() && (b.getxPosition() - getxPosition()) < 1500 && missile.hasExpired() && barrierWarning == false) {
					Sound warning = world.getSoundManager().getSound("warning_sound");
					warning.playNormal();
					warningEmitter.emit(elapsedTime, this);
					thrusterLight = new Light(new float[] { 1, 0, 0, 1 }, thrusterLight.getColor(), 0.3f);
					thrusterLight.setFlickering(true, 0.95f, 1f);

					barrierWarning = true;
				}

				if (b != null && b.isActive() && (b.getxPosition() - getxPosition()) < 1000f && missile.hasExpired()) {
					missile.fire(this, world);
					barrierWarning = false;
				}

			}

			DynamicCave cave = world.getDynamicCave();
			if (cave != null) {
				CaveCollisionData data = new CaveCollisionData();
				CaveCollision r = cave.getCollision(getxPosition(), getyPosition(), 50, data);
				if (r == CaveCollision.CollideWithTop || r == CaveCollision.CollideWithBottom) {
					nearMissEmitter.setyPosition(data.getyPosition());
					nearMissEmitter.setxPosition(data.getxPosition());
					nearMissEmitter.emit(elapsedTime, this);
					world.getScore().toggleNearMiss(true);
				} else {
					world.getScore().toggleNearMiss(false);
				}
			}

			float volume = getxSpeed() / 200f;
			if (volume > 1)
				volume = 1;
			engineSound.playLoop(volume, volume);
		}
		super.update(elapsedTime, world);
	}

	@Override
	public void setupLights(World world, Shaders shaders) {

		PerPixelShader shader = (PerPixelShader) shaders.getShaderByName("default");
		shader.load();

		float lightFade = 1f;
		if (impulseOn)
			lightFade = 0.9f;

		// set a light in front of the object
		shader.setLight(0, new float[] { getBody().getRealX(this, -1, 0, getRotation()), getBody().getRealY(this, -1.1f, 0, getRotation()), 50 }, thrusterLight,
				lightFade);

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

		nearMissEmitter.load(world, resourceMap);
		warningEmitter.load(world, resourceMap);
		engineSound = world.getSoundManager().getSound("ship_engine_sound");
	}

	@Override
	public void processControlEvent(ControlEvent event) {

		if (getCurrentState() != null && getCurrentState().getState().equals("normal") == false)
			return;

		if (event == null) {
			event = lastEvent;
		}

		if (event != null && event.getClass().equals(TouchControlEvent.class)) {
			TouchControlEvent cast = (TouchControlEvent) event;

			if (cast.isLeftSide() == false)
				return;

			if (cast.isButtonDown()) {
				impulseOn = true;
				this.setyAcceleration(impulseAcceleration);
				thrusterEmitter.setSize(20, 70);
			} else {
				impulseOn = false;
				this.setyAcceleration(0);
				thrusterEmitter.setSize(10, 50);
			}

			lastEvent = event;
		}

		super.processControlEvent(event);
	}

	public void loadDefaults() {
		impulseAcceleration = 200;
		this.randomImpulseGeneration = false;
	}

	public Ship(World world) {

		world.addGameObject(missile);

		setName("ship");
		setAttachedToCamera(true);
		setCollidable(true);
		setDynamic(true);
		setInitialState("briefing");
		setInitialXPosition(150);
		setInitialYPosition(150);
		setInitialXSpeed(0);
		setInitialXAcceleration(75f);
		setInitialYAcceleration(100f);
		setPlayable(true);

		loadDefaults();

		setBody(new Body(50, 50, 50));

		PhysicBody physicBody = new PhysicBody("points", 3);
		physicBody.setPoints(new float[][] { new float[] { -0.5f, -0.2f }, new float[] { 0.5f, 0f }, new float[] { -0.5f, 0.2f } });
		setPhysicBody(physicBody);

		State briefingState = new State();
		briefingState.setState("briefing");

		Camera briefingCamera1 = new Camera();
		briefingCamera1.setDuration(5);
		briefingCamera1.setFrom(new Point3D(-50, -50, 50));
		briefingCamera1.setTo(new Point3D(100, -50, 700));
		briefingCamera1.setLookFrom(new Point3D(0, 0, 0));
		briefingCamera1.setLookTo(new Point3D(300, 0, 0));
		briefingCamera1.setRelativeTo("gameObject");

		briefingState.setCamera(briefingCamera1);

		briefingState.addSetter(new AcceleratedSetter(true));

		thrusterEmitter = new ParticleEmitter();
		thrusterEmitter.setParticleType(Particle.ParticleType.SquareGlow);
		thrusterEmitter.setFromAlpha(0);
		thrusterEmitter.setToAlpha(1);
		thrusterEmitter.setFromDuration(0.5f);
		thrusterEmitter.setToDuration(0.5f);
		thrusterEmitter.setFromSize(10);
		thrusterEmitter.setToSize(50);
		thrusterEmitter.setTextureName("bluebloom");
		thrusterEmitter.setxPosition(-0.8f);
		thrusterEmitter.setInterval(0.0005f);
		thrusterEmitter.setColor(new float[] { 0.5f, 0.5f, 1, 1 });

		briefingState.addParticleEmitter(thrusterEmitter);

		addState(briefingState);

		State normalState = new State();
		normalState.setCopyEmitters(true);
		normalState.setState("normal");
		normalState.addSetter(new AcceleratedSetter(true));
		normalState.addSetter(new AccelerationSetter(1, 0));

		addState(normalState);

		State explosionState = new State();
		explosionState.setState("explosion");
		explosionState.setDuration(1.2f);
		explosionState.setNextState("destroyed");

		explosionState.addSetter(new DynamicSetter(false));

		ParticleEmitter explosionEmitter = new ParticleEmitter();
		explosionEmitter.setParticleType(Particle.ParticleType.SquareTextureDepthTest);
		explosionEmitter.setTextureName("cave_fragment");
		explosionEmitter.setFromAlpha(1);
		explosionEmitter.setFromDuration(10);
		explosionEmitter.setFromSize(5);
		explosionEmitter.setRotationSpeedVariation(500);
		explosionEmitter.setLimit(100);
		explosionEmitter.setParticles(100);
		explosionEmitter.setPositionVariation(5);
		explosionEmitter.setSpeedVariation(-50);
		explosionEmitter.setToAlpha(0);
		explosionEmitter.setToDuration(20);
		explosionEmitter.setToSize(0.1f);
		explosionEmitter.setyAcceleration(-70);
		explosionEmitter.setySpeed(40);
		explosionEmitter.setColor(new float[] { 1, 0.5f, 0.5f, 0.5f });

		explosionState.addParticleEmitter(explosionEmitter);

		addState(explosionState);

		State destroyedState = new State();
		destroyedState.setState("destroyed");
		addState(destroyedState);

		nearMissEmitter = new ParticleEmitter();
		nearMissEmitter.setTextureName("smoke");
		nearMissEmitter.setAlpha(1f, 0f);
		nearMissEmitter.setDuration(1f, 1f);
		nearMissEmitter.setSize(10f, 50f);
		nearMissEmitter.setInterval(0.01f);
		nearMissEmitter.setParticles(1);
		nearMissEmitter.setColor(new float[] { 1, 1, 1, 1 });
		nearMissEmitter.setMode(ParticleEmitter.Mode.Absolute);
		nearMissEmitter.setParticleType(Particle.ParticleType.SquareTextureDepthTest);
		nearMissEmitter.setPositionVariation(40);
		nearMissEmitter.setZVariation(40);

		warningEmitter = new ParticleEmitter();
		warningEmitter.setTextureName("mark");
		warningEmitter.setAlpha(1f, 0f);
		warningEmitter.setDuration(1f, 1f);
		warningEmitter.setSize(0f, 100f);
		warningEmitter.setInterval(0.01f);
		warningEmitter.setParticles(1);
		warningEmitter.setColor(new float[] { 1, 0, 0, 1 });
		warningEmitter.setParticleType(Particle.ParticleType.SquareTextureDepthTest);

		thrusterLight.setFlickering(true, 0.95f, 1f);
	}

	public void setThrusterColor(float[] color) {
		this.thrusterLight = new Light(this.thrusterLight.getColor(), color, 0.5f);
		this.thrusterLight.setFlickering(true, 0.95f, 1f);
		this.thrusterEmitter.setColor(color);
	}

	@Override
	public void drawCustom(TextureMap textureMap, Shaders shaders) {
		if (collided) {
			gameOverMessageBoard.draw(textureMap, shaders);
		}
	}

	public float getImpulseAcceleration() {
		return impulseAcceleration;
	}

	public void setImpulseAcceleration(float impulseAcceleration) {
		this.impulseAcceleration = impulseAcceleration;
	}

	public void setRandomImpulseGeneration(boolean enable) {
		this.randomImpulseGeneration = enable;

	}

	public Missile getMissile() {
		return missile;
	}
}
