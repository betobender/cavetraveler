package com.cave.traveler.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.cave.traveler.engine.Graphics.TextureMap;

public class ModelMaterial {

	private String name;
	private float[] ambient = new float[] { 1, 1, 1, 1 };
	private float[] difuse = new float[] { 1, 1, 1, 1 };
	private float[] specular = new float[] { 1, 1, 1, 1 };
	private float transparent = 1;
	private ModelTexture difuseTexture;

	private FloatBuffer difuseBuffer;
	private FloatBuffer ambientBuffer;
	private FloatBuffer specularBuffer;

	public ModelMaterial(String name) {
		this.setName(name);
	}

	public void beginMaterial(ModelObject modelObject) {

		//GLES20.glVertexAttribPointer(renderer.getColorHandle(), renderer.getColorDataSize(), GLES20.GL_FLOAT, false, 0, difuseBuffer);
		//GLES20.glEnableVertexAttribArray(renderer.getColorHandle());

		if (difuseTexture != null)
			difuseTexture.beginTexture(modelObject);
	}

	public void endMaterial(ModelObject modelObject) {

		if (difuseTexture != null)
			difuseTexture.endTexture(modelObject);
	}

	public void load(TextureMap textureMap) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(difuse.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		difuseBuffer = vbb.asFloatBuffer();
		difuseBuffer.put(difuse);
		difuseBuffer.position(0);

		vbb = ByteBuffer.allocateDirect(ambient.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		ambientBuffer = vbb.asFloatBuffer();
		ambientBuffer.put(ambient);
		ambientBuffer.position(0);

		vbb = ByteBuffer.allocateDirect(specular.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		specularBuffer = vbb.asFloatBuffer();
		specularBuffer.put(specular);
		specularBuffer.position(0);

		if (difuseTexture != null)
			difuseTexture.load(textureMap);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAmbient(float r, float g, float b) {
		ambient = new float[] { r, g, b, 1 };
	}

	public float[] getAmbient() {
		return ambient;
	}

	public void setAmbient(float[] ambient) {
		this.ambient = ambient;
	}

	public void setDifuse(float r, float g, float b) {
		difuse = new float[] { r, g, b, 1 };
	}

	public float[] getDifuse() {
		return difuse;
	}

	public void setDifuse(float[] difuse) {
		this.difuse = difuse;
	}

	public void setSpecular(float r, float g, float b) {
		specular = new float[] { r, g, b, 1 };
	}

	public float[] getSpecular() {
		return specular;
	}

	public void setSpecular(float[] specular) {
		this.specular = specular;
	}

	public float getTransparent() {
		return transparent;
	}

	public void setTransparent(float transparent) {
		this.transparent = transparent;
	}

	public ModelTexture getDifuseTexture() {
		return difuseTexture;
	}

	public void setDifuseTexture(ModelTexture difuseTexture) {
		this.difuseTexture = difuseTexture;
	}

}
