package com.cave.traveler.engine.Graphics;

import android.opengl.GLES20;

public class ForceFieldShader extends Shader {

	private int timeHandle;
	
	public ForceFieldShader(String name, ResourceMap resourceMap) {
		super(name, resourceMap, "forcefield_vertex_shader", "forcefield_fragment_shader");
	}

	private int vertexHandle;

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
		timeHandle = GLES20.glGetUniformLocation(getProgramHandle(), "u_Time");
		super.bindUniforms();
	}

	public int getVertexHandle() {
		return vertexHandle;
	}

	public void setElapsedTime(float elapsedTime) {
		GLES20.glUniform1f(timeHandle, elapsedTime);
	}
}
