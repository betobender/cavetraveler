package com.cave.traveler.engine.Graphics;

import android.opengl.GLES20;

public class ParticleShader extends Shader {

	public ParticleShader(String name, ResourceMap resourceMap) {
		super(name, resourceMap, "particle_vertex_shader", "particle_fragment_shader");
	}
	
	private int vertexHandle;
	private int colorHandle;
	private int textureHandle;

	@Override
	public String[] getAttributes() {
		return new String[] { "a_Position", "a_Color", "a_TexCoordinate" };
	}

	@Override
	public void bindAttributes() {
		vertexHandle = GLES20.glGetAttribLocation(getProgramHandle(), "a_Position");
		colorHandle = GLES20.glGetAttribLocation(getProgramHandle(), "a_Color");
		textureHandle = GLES20.glGetAttribLocation(getProgramHandle(), "a_TexCoordinate");
		super.bindAttributes();
	}

	@Override
	public void bindUniforms() {
		super.bindUniforms();
	}
	
	public int getVertexHandle() {
		return vertexHandle;
	}
	
	public int getColorHandle() {
		return colorHandle;
	}
	
	public int getTextureHandle() {
		return textureHandle;
	}

}
