package com.cave.traveler.engine.Graphics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.opengl.GLES20;

public class Shaders {

	private Map<String, Shader> shadersMap = new HashMap<String, Shader>();
	private Shader activeShader;

	public Shaders() {
	}

	public void addShader(Shader shader) {
		int vertexShaderHandle = GLSLHelper.compileShader(shader.getVertexShader(), GLES20.GL_VERTEX_SHADER);
		int fragmentShaderHandle = GLSLHelper.compileShader(shader.getFragmentShader(), GLES20.GL_FRAGMENT_SHADER);
		int programHandle = GLSLHelper.createProgram(vertexShaderHandle, fragmentShaderHandle, shader.getAttributes());

		shader.setCreator(this);
		shader.setProgramHandle(programHandle);
		shader.bindUniforms();
		shader.bindAttributes();

		shadersMap.put(shader.getName(), shader);
	}

	public Shader getShaderByName(String shaderName) {
		return shadersMap.get(shaderName);
	}

	public Shader getActiveShader() {
		return activeShader;
	}

	public void setActiveShader(Shader activeShader) {
		this.activeShader = activeShader;
	}

	public Collection<Shader> getShaders() {
		return shadersMap.values();
	}
}
