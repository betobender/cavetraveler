package com.cave.traveler.engine;

import android.util.SparseArray;

public class DynamicLightSource {

	private static DynamicLightSource instance = new DynamicLightSource();

	public static DynamicLightSource getInstance() {
		return instance;
	}

	private DynamicLightSource() {

	}

	private SparseArray<GameObject> lightControllers = new SparseArray<GameObject>();

	public void updateLight(int lightId, World world, GameObject obj) {

		GameObject lightController = lightControllers.get(lightId);
		GameObject cameraController = world.getCameraController();

		if (cameraController != null) {
			if (lightController == null) {
				lightController = obj;
			} else {
				float distance = obj.getxPosition() - cameraController.getxPosition();
				if (distance > 0) {
					float ldistance = lightController.getxPosition() - cameraController.getxPosition();
					if (ldistance > 0) {
						if (distance < ldistance) {
							lightController = obj;
						}
					} else {
						lightController = obj;
					}
				}
			}
		}

		lightControllers.put(lightId, lightController);
	}

	public void reset() {
		lightControllers.clear();
	}

	public GameObject getLightController(int lightId) {
		return lightControllers.get(lightId);
	}

	public boolean isLightSource(int lightId, GameObject obj) {
		return lightControllers.get(lightId) == obj;
	}
}
