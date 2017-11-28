package com.cave.traveler.engine;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.os.SystemClock;
import android.util.SparseArray;

import com.cave.traveler.engine.CaveCollisionData.CaveCollision;
import com.cave.traveler.engine.Graphics.DrawLayer;
import com.cave.traveler.engine.Graphics.Drawable;
import com.cave.traveler.engine.Graphics.MessageBoard;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.engine.control.ControlEvent;
import com.cave.traveler.engine.sound.SoundManager;
import com.cave.traveler.engine.triggers.TriggerDispatcher;

public class World {
	private String name;
	private long updateTime;

	private DynamicCave dynamicCave;
	private Score score = new Score();
	private List<Dynamic> forces = new LinkedList<Dynamic>();
	private List<GameObject> gameObjects = new LinkedList<GameObject>();
	private List<GameObject> dynamicObjects = new LinkedList<GameObject>();
	private List<GameObject> collidableObjects = new LinkedList<GameObject>();
	private List<GameObject> playableObjects = new LinkedList<GameObject>();
	private SparseArray<List<GameObject>> gameObjectByLayerId = new SparseArray<List<GameObject>>();
	private SparseArray<List<GameObject>> collidableObjectsByLayerId = new SparseArray<List<GameObject>>();
	private List<DrawLayer> layers = new LinkedList<DrawLayer>();
	private GameObject cameraController;
	private List<Drawable> drawables = new LinkedList<Drawable>();
	private TextureMap textureMap;
	private SoundManager soundManager;
	private ResourceMap resourceMap;
	private MessageBoard messageBoard;
	private Engine engine;

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public List<Dynamic> getForces() {
		return forces;
	}

	public void setForces(List<Dynamic> forces) {
		this.forces = forces;
	}

	public void addForce(Dynamic force) {
		forces.add(force);
	}

	public float getResultForceX(double x, GameObject obj) {
		float r = 0;
		for (Dynamic dynamic : forces) {
			r += dynamic.getValueX(x, obj);
		}
		return r;
	}

	public float getResultForceY(double y, GameObject obj) {
		float r = 0;
		for (Dynamic dynamic : forces) {
			r += dynamic.getValueY(y, obj);
		}
		return r;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void update(float elapsedTime, Engine engine) {

		long beginTime = SystemClock.uptimeMillis();

		// update objects position
		for (GameObject obj : gameObjects) {
			obj.update(elapsedTime, this);
		}

		// update cave position
		if (dynamicCave != null) {
			dynamicCave.update(elapsedTime, this);

			// detect objects collision
			for (int i = 0; i < collidableObjectsByLayerId.size(); ++i) {
				for (GameObject obj : collidableObjectsByLayerId.valueAt(i)) {

					// testing collision with cave
					if (dynamicCave.getCollision(obj) != CaveCollision.DoesntCollide) {
						obj.trigger(TriggerDispatcher.CaveCollision, this);
					}
				}
			}
		}

		// update store
		score.update(elapsedTime, this);

		// update message board
		if (messageBoard != null)
			messageBoard.update(elapsedTime, this);

		updateTime = SystemClock.uptimeMillis() - beginTime;
	}

	public void load(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;

		if (textureMap != null)
			textureMap.readTextures(resourceMap);

		if (soundManager != null)
			soundManager.initialize(resourceMap);

		if (dynamicCave != null)
			dynamicCave.load(this, resourceMap);

		for (GameObject o : gameObjects)
			o.load(this, resourceMap);
	}

	public void addLayer(DrawLayer layer) {
		layers.add(layer);
		drawables.add(layer);
	}

	public List<DrawLayer> getLayers() {
		return null;
	}

	public void addGameObject(GameObject obj) {
		if (obj.isPlayable()) {
			playableObjects.add(obj);
		}

		if (obj.isDynamic()) {
			dynamicObjects.add(obj);
		}

		if (obj.isCollidable()) {
			collidableObjects.add(obj);

			if (collidableObjectsByLayerId.get(obj.getLayerId()) == null) {
				collidableObjectsByLayerId.put(obj.getLayerId(), new LinkedList<GameObject>());
			}
			collidableObjectsByLayerId.get(obj.getLayerId()).add(obj);
		}

		gameObjects.add(obj);

		if (gameObjectByLayerId.get(obj.getLayerId()) == null) {
			gameObjectByLayerId.put(obj.getLayerId(), new LinkedList<GameObject>());
		}
		gameObjectByLayerId.get(obj.getLayerId()).add(obj);

		if (obj.isAttachedToCamera())
			cameraController = obj;

		drawables.add(obj);
	}

	public Collection<GameObject> getGameObjects() {
		return gameObjects;
	}

	public List<GameObject> getGameObjectsByLayerId(int layerId) {
		return gameObjectByLayerId.get(layerId);
	}

	public void reset() {
		if (soundManager != null)
			soundManager.reset();

		for (GameObject obj : gameObjects) {
			obj.reset();
		}

		if (dynamicCave != null)
			dynamicCave.reset();
	}

	public void processControlEvent(ControlEvent event) {
		for (GameObject obj : playableObjects) {
			if (obj.isPlayable()) {
				obj.processControlEvent(event);
			}
		}
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public List<Drawable> getDrawables() {
		return drawables;
	}

	public void setTextureMap(TextureMap textureMap) {
		this.textureMap = textureMap;
	}

	public TextureMap getTextureMap() {
		return this.textureMap;
	}

	public void prepareDrawables() {
		for (Drawable d : drawables) {
			d.prepare(this, textureMap);
		}

		if (messageBoard != null)
			messageBoard.prepare(this, textureMap);
	}

	public GameObject getCameraController() {
		return cameraController;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}

	public void setSoundManager(SoundManager soundManager) {
		this.soundManager = soundManager;
	}

	public ResourceMap getResourceMap() {
		return resourceMap;
	}

	public void pause() {
		if (soundManager != null)
			soundManager.pause();
	}

	public DynamicCave getDynamicCave() {
		return dynamicCave;
	}

	public void setDynamicCave(DynamicCave dynamicCave) {
		if (this.dynamicCave != null) {
			drawables.remove(this.dynamicCave);
		}
		this.dynamicCave = dynamicCave;
		drawables.add(0, dynamicCave);
	}

	public void invertAllForces() {
		for (Dynamic force : forces) {
			force.setMultiplier(-1);
		}
	}

	public void restoreAllForces() {
		for (Dynamic force : forces) {
			force.setMultiplier(1);
		}
	}

	public void resume() {
		if (soundManager != null)
			soundManager.resume();
	}

	public MessageBoard getMessageBoard() {
		return messageBoard;
	}

	public void setMessageBoard(MessageBoard messageBoard) {
		this.messageBoard = messageBoard;
	}
}
