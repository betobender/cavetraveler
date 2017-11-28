package com.cave.traveler.model;

import java.util.LinkedList;
import java.util.List;

import com.cave.traveler.engine.Graphics.Shader;
import com.cave.traveler.engine.Graphics.TextureMap;

public class Model {

	List<ModelObject> modelObjects = new LinkedList<ModelObject>();

	public void update(float elapsedTime) {
		for (ModelObject modelObject : modelObjects)
			modelObject.update(elapsedTime);
	}

	public void explode3D(float speed, float rotationSpeed) {
		for (ModelObject modelObject : modelObjects)
			modelObject.explode3D(speed, rotationSpeed);
	}

	public void addModelObject(ModelObject modelObject) {
		modelObjects.add(modelObject);
	}

	public void load(TextureMap textureMap) {
		for (ModelObject modelObject : modelObjects)
			modelObject.load(textureMap);
	}

	public void draw(Shader shader) {
		for (ModelObject modelObject : modelObjects) {
			modelObject.draw(shader);
		}
	}

	public void setColor(float[] color) {
		for (ModelObject modelObject : modelObjects)
			modelObject.setColor(color);

	}

	public void resetExplosion() {
		for (ModelObject modelObject : modelObjects)
			modelObject.resetExplosion();
		
	}
	
	public void setCullFace(boolean cullFace) {
		for (ModelObject modelObject : modelObjects)
			modelObject.setCullFace(cullFace);
	}
	
	public void setBlend(boolean blend) {
		for (ModelObject modelObject : modelObjects)
			modelObject.setBlend(blend);
	}	
	
	public void overrideNormals(float normal[]) {
		for (ModelObject modelObject : modelObjects)
			modelObject.overrideNormals(normal);
	}

	public void explode3DR(float speed, float rotationSpeed) {
		for (ModelObject modelObject : modelObjects)
			modelObject.explode3DR(speed, rotationSpeed);
	}
	
}
