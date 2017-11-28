package com.cave.traveler.objects;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.World;

public class GameObjectFactory {

	private static GameObjectFactory instance = new GameObjectFactory();

	private GameObjectFactory() {

	}

	public static GameObjectFactory getInstance() {
		return instance;
	}

	public GameObject createFromType(String type, World world) {
		GameObject obj = null;
		if (type.equals("generic")) {
			obj = new GameObject();
		} else if (type.equals("ship")) {
			obj = new Ship(world);
		} else if (type.equals("menuShip")) {
			obj = new MenuShip();
		} else if (type.equals("lightPoint")) {
			obj = new LightPoint();
		}
		obj.setType(type);
		return obj;
	}
}
