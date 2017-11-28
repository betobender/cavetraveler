package com.cave.traveler.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.cave.traveler.engine.Graphics.Drawable;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Shaders;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.engine.control.ControlEvent;
import com.cave.traveler.engine.triggers.Trigger;
import com.cave.traveler.engine.triggers.TriggerDispatcher;

public class GameObject implements Drawable {

	// configuration parameters

	private String name;
	private String type;
	private boolean attachedToCamera;
	private int layerId;
	private boolean dynamic;
	private boolean collidable;
	private boolean playable;
	private boolean accelerated = true;
	private PhysicBody physicBody;
	private Body body;
	private float initialXPosition;
	private float initialYPosition;
	private float initialXSpeed;
	private float initialYSpeed;
	private float initialXAcceleration;
	private float initialYAcceleration;
	private String initialState;
	private List<Trigger> triggers = new LinkedList<Trigger>();
	private Map<String, State> states = new HashMap<String, State>();
	private Queue<Particle> particles = new LinkedList<Particle>();
	private boolean allowWorldForces = true;

	// state parameters

	private float xPosition;
	private float yPosition;
	private float xSpeed;
	private float ySpeed;
	private float xMaxSpeed;
	private float xMinSpeed;
	private float xAcceleration;
	private float yAcceleration;
	private float rotation;
	private State currentState;

	public void prepare(World world, TextureMap textureMap) {

	}

	public void setupLights(World world, Shaders shaders) {
		if (world.getCameraController() == this) {
		}
	}

	public void draw(TextureMap textureMap, Shaders shaders) {
	}

	public void drawParticles(TextureMap textureMap, Shaders shaders) {
		for (Particle p : getParticles()) {
			p.draw(textureMap, shaders);
		}
	}

	public void update(float elapsedTime, World world) {
		currentState.applySetters(elapsedTime, world, this);

		if (dynamic) {

			if (accelerated) {
				
				if(allowWorldForces) {
					xSpeed += (xAcceleration + world.getResultForceX(xPosition, this)) * elapsedTime;
					ySpeed += (yAcceleration + world.getResultForceY(yPosition, this)) * elapsedTime;
				} else {
					xSpeed += (xAcceleration) * elapsedTime;
					ySpeed += (yAcceleration) * elapsedTime;
					
				}

				if (xMinSpeed != 0 && xSpeed < xMinSpeed) {
					xSpeed = xMinSpeed;
					xMinSpeed = 0;
					xAcceleration = 0;
				}

				if (xMaxSpeed != 0 && xSpeed > xMaxSpeed) {
					xSpeed = xMaxSpeed;
					xMaxSpeed = 0;
					xAcceleration = 0;
				}
			}

			xPosition += xSpeed * elapsedTime;
			yPosition += ySpeed * elapsedTime;

			float radRotation = (float) Math.atan(ySpeed / xSpeed);
			rotation = (float) (radRotation * 180 / Math.PI);
		}

		while (particles.size() > 0 && particles.element().isAlive() == false) {
			particles.remove();
		}

		for (Particle p : particles) {
			p.update(elapsedTime, this, world);
		}

		currentState.update(elapsedTime, this);
	}

	public void reset() {
		xPosition = initialXPosition;
		yPosition = initialYPosition;
		xSpeed = initialXSpeed;
		ySpeed = initialYSpeed;
		xAcceleration = initialXAcceleration;
		yAcceleration = initialYAcceleration;
		setCurrentState(states.get(initialState));
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isPlayable() {
		return playable;
	}

	public void setPlayable(boolean playable) {
		this.playable = playable;
	}

	public boolean isCollidable() {
		return collidable;
	}

	public void setCollidable(boolean collidable) {
		this.collidable = collidable;
	}

	public PhysicBody getPhysicBody() {
		return physicBody;
	}

	public void setPhysicBody(PhysicBody physicBody) {
		this.physicBody = physicBody;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public float getInitialXPosition() {
		return initialXPosition;
	}

	public void setInitialXPosition(float initialXPosition) {
		this.initialXPosition = initialXPosition;
	}

	public float getInitialYPosition() {
		return initialYPosition;
	}

	public void setInitialYPosition(float initialYPosition) {
		this.initialYPosition = initialYPosition;
	}

	public float getInitialXSpeed() {
		return initialXSpeed;
	}

	public void setInitialXSpeed(float initialXSpeed) {
		this.initialXSpeed = initialXSpeed;
	}

	public float getInitialYSpeed() {
		return initialYSpeed;
	}

	public void setInitialYSpeed(float initialYSpeed) {
		this.initialYSpeed = initialYSpeed;
	}

	public float getInitialXAcceleration() {
		return initialXAcceleration;
	}

	public void setInitialXAcceleration(float initialXAcceleration) {
		this.initialXAcceleration = initialXAcceleration;
	}

	public float getInitialYAcceleration() {
		return initialYAcceleration;
	}

	public void setInitialYAcceleration(float initialYAcceleration) {
		this.initialYAcceleration = initialYAcceleration;
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

	public void setxAcceleration(float xAcceleration, float xMinSpeed, float xMaxSpeed) {
		this.xAcceleration = xAcceleration;
		this.xMaxSpeed = xMaxSpeed;
		this.xMinSpeed = xMinSpeed;
	}

	public float getyAcceleration() {
		return yAcceleration;
	}

	public void setyAcceleration(float yAcceleration) {
		this.yAcceleration = yAcceleration;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLayerId() {
		return layerId;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}

	public void load(World world, ResourceMap resourceMap) {
		for (State s : states.values()) {
			s.load(world, resourceMap);
		}
	}

	public void setInitialState(String initialState) {
		this.initialState = initialState;

	}

	public String getInitialState() {
		return initialState;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		State lastState = getCurrentState();
		Camera lastCamera = null;

		if (lastState != null)
			lastCamera = this.currentState.getCamera();

		// setting the current state
		this.currentState = currentState;

		// copy emitters
		if (this.currentState.isCopyEmitters())
			this.currentState.setParticleEmitters(lastState.getParticleEmitters());

		// copy setters
		if (this.currentState.isCopySetters())
			this.currentState.setSetters(lastState.getSetters());

		// transfering camera knowledge
		if (this.currentState.getCamera() == null)
			this.currentState.setCamera(lastCamera);
		else
			this.currentState.getCamera().load(lastCamera);
	}

	public void setCurrentState(String currentState) {
		states.get(currentState).reset();
		setCurrentState(states.get(currentState));
	}

	public void addTrigger(Trigger trigger) {
		triggers.add(trigger);
	}

	public void addState(State state) {
		states.put(state.getState(), state);
	}

	public void trigger(TriggerDispatcher dispatcher, World world) {
		for (Trigger t : triggers) {
			t.apply(dispatcher, this);
		}
	}

	public void addParticle(Particle particle) {
		particles.add(particle);
	}

	public Queue<Particle> getParticles() {
		return particles;
	}

	public boolean isAttachedToCamera() {
		return attachedToCamera;
	}

	public void setAttachedToCamera(boolean attachedToViewPort) {
		this.attachedToCamera = attachedToViewPort;
	}

	public void processControlEvent(ControlEvent event) {

	}

	public boolean isAccelerated() {
		return accelerated;
	}

	public void setAccelerated(boolean accelerated) {
		this.accelerated = accelerated;
	}

	public void drawCustom(TextureMap textureMap, Shaders shaders) {
	}

	public boolean isGameObject() {
		return true;
	}

	public boolean isAllowWorldForces() {
		return allowWorldForces;
	}

	public void setAllowWorldForces(boolean allowWorldForces) {
		this.allowWorldForces = allowWorldForces;
	}
}
