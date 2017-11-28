package com.cave.traveler.engine;

import java.util.LinkedList;
import java.util.List;

import com.cave.traveler.engine.Graphics.Effect;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Texture;
import com.cave.traveler.engine.setters.Setter;

public class State {
	private String state;
	private float duration;
	private String nextState;
	private List<Setter> setters = new LinkedList<Setter>();
	private List<ParticleEmitter> emitters = new LinkedList<ParticleEmitter>();
	private List<Effect> effects = new LinkedList<Effect>();
	private Camera camera;
	private boolean copyEmitters = false;
	private boolean copySetters = false;
	private float xPosition;
	private String textureName;
	private Texture texture;

	private float elapsedTime;

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getNextState() {
		return nextState;
	}

	public void setNextState(String nextState) {
		this.nextState = nextState;
	}

	public void load(World world, ResourceMap resourceMap) {
		for (ParticleEmitter pe : emitters)
			pe.load(world, resourceMap);

		if (textureName != null) {
			texture = world.getTextureMap().getTextureByName(textureName);
		}
	}

	public void addSetter(Setter setter) {
		setters.add(setter);
	}

	public void applySetters(float elapsedTime, World world, GameObject gameObject) {
		for (Setter s : setters)
			if (s.isApplied() == false) {
				s.apply(elapsedTime, world, gameObject);
				s.markAsApplied();
			}
	}

	public void reApplySetters(float elapsedTime, World world, GameObject gameObject) {
		for (Setter s : setters) {
			s.apply(elapsedTime, world, gameObject);
			s.markAsApplied();
		}
	}

	public void update(float elapsedTime, GameObject gameObject) {

		this.elapsedTime += elapsedTime;

		for (ParticleEmitter pe : emitters)
			pe.emit(elapsedTime, gameObject);

		if (camera != null)
			camera.update(elapsedTime);

		if (duration > 0 && this.elapsedTime > duration) {
			this.elapsedTime = 0;
			gameObject.setCurrentState(nextState);
		}

		if (xPosition > 0 && gameObject.getxPosition() > xPosition) {
			this.elapsedTime = 0;
			gameObject.setCurrentState(nextState);
		}
	}

	public void addParticleEmitter(ParticleEmitter emitter) {
		emitters.add(emitter);
	}

	public void addEffect(Effect effect) {
		effects.add(effect);
	}

	public List<Effect> getEffects() {
		return effects;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public boolean isCopyEmitters() {
		return copyEmitters;
	}

	public void setCopyEmitters(boolean copyEmitters) {
		this.copyEmitters = copyEmitters;
	}

	public float getXPosition() {
		return xPosition;
	}

	public void setXPosition(float position) {
		this.xPosition = position;
	}

	public boolean isCopySetters() {
		return copySetters;
	}

	public void setCopySetters(boolean copySetters) {
		this.copySetters = copySetters;
	}

	public List<ParticleEmitter> getParticleEmitters() {
		return emitters;
	}

	public List<Setter> getSetters() {
		return setters;
	}

	public void setParticleEmitters(List<ParticleEmitter> emitters) {
		this.emitters = emitters;

	}

	public void setSetters(List<Setter> setters) {
		this.setters = setters;
	}

	public String getTextureName() {
		return textureName;
	}

	public void setTextureName(String textureName) {
		this.textureName = textureName;
	}

	public Texture getTexture() {
		return texture;
	}

	public void reset() {
		this.elapsedTime = 0;
		if (camera != null)
			camera.reset();

	}
}
