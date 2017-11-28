package com.cave.traveler.model;

import java.util.LinkedList;
import java.util.List;

import android.util.SparseArray;

import com.cave.traveler.engine.Graphics.Shader;
import com.cave.traveler.engine.Graphics.TextureMap;

public class ModelObject {

	private String name;
	private List<ModelFace> modelFaces = new LinkedList<ModelFace>();

	private SparseArray<float[]> vertices = new SparseArray<float[]>();
	private SparseArray<float[]> normals = new SparseArray<float[]>();

	public void addVertex(int index, float x, float y, float z) {
		vertices.put(index, new float[] { x, y, z });
	}

	public void addNormal(int index, float x, float y, float z) {
		normals.put(index, new float[] { x, y, z });
	}

	public SparseArray<float[]> getVertices() {
		return vertices;
	}

	public SparseArray<float[]> getNormals() {
		return normals;
	}

	public ModelObject(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void load(TextureMap textureMap) {
		for (ModelFace f : modelFaces)
			f.load(this, textureMap);
	}

	public void draw(Shader shader) {
		for (ModelFace modelFace : modelFaces)
			modelFace.draw(shader);
	}

	public void update(float elapsedTime) {
		for (ModelFace modelFace : modelFaces)
			modelFace.update(elapsedTime);
	}

	public void addModelFace(ModelFace modelFace) {
		modelFaces.add(modelFace);
	}

	public List<ModelFace> getModelFaces() {
		return modelFaces;
	}

	public void explode3D(float speed, float rotationSpeed) {
		for (ModelFace modelFace : modelFaces)
			modelFace.explode3D(speed, rotationSpeed);

	}

	public void setColor(float[] color) {
		for (ModelFace modelFace : modelFaces)
			modelFace.setColor(color);

	}

	public void resetExplosion() {
		for (ModelFace modelFace : modelFaces)
			modelFace.resetExplosion();
		
	}
	
	public void setCullFace(boolean cullFace) {
		for (ModelFace modelFace : modelFaces)
			modelFace.setCullFace(cullFace);
	}
	
	public void setBlend(boolean blend) {
		for (ModelFace modelFace : modelFaces)
			modelFace.setBlend(blend);
	}	

	public void overrideNormals(float[] normal) {
		for (ModelFace modelFace : modelFaces)
			modelFace.overrideNormals(normal);
		
	}

	public void explode3DR(float speed, float rotationSpeed) {
		for (ModelFace modelFace : modelFaces)
			modelFace.explode3DR(speed, rotationSpeed);
	}
}
