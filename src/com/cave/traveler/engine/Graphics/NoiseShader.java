package com.cave.traveler.engine.Graphics;

import android.opengl.GLES20;

public class NoiseShader extends Shader {

	public NoiseShader(String name, ResourceMap resourceMap) {
		super(name, resourceMap, "noise_vertex_shader", "noise_fragment_shader");
	}

	private int vertexHandle;
	private int noiseColorHandle;
	private int seedHandle;

	@Override
	public String[] getAttributes() {
		return new String[] { "a_Position" };
	}

	@Override
	public void bindAttributes() {
		vertexHandle = GLES20.glGetAttribLocation(getProgramHandle(), "a_Position");
		super.bindAttributes();
	}

	@Override
	public void bindUniforms() {
		seedHandle = GLES20.glGetUniformLocation(getProgramHandle(), "u_Seed");
		noiseColorHandle = GLES20.glGetUniformLocation(getProgramHandle(), "u_NoiseColor");
		super.bindUniforms();
	}
	
	public int getVertexHandle() {
		return vertexHandle;
	}
	
	public void setSeed(float seed) {
		GLES20.glUniform1f(seedHandle, seed);
	}
	
	public void setNoiseColor(float[] color) {
		GLES20.glUniform4f(noiseColorHandle, color[0], color[1], color[2], color[3]);
	}

}
