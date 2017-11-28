package com.cave.traveler.engine.Graphics;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Shader {
	
	private String vertexShader;
	private String fragmentShader;

	private int programHandle;
	private Shaders creator;
	private String name;
	
	private float[] viewMatrix = new float[16];
	private float[] modelMatrix = new float[16];
	private float[] modelMatrixCpy = new float[16];
	private float[] projectionMatrix = new float[16];
	private float[] orthoProjectionMatrix = new float[16];
	private float[] mvMatrix = new float[16];
	private float[] mvpMatrix = new float[16];

	private int mvpMatrixHandle;
	private int mvMatrixHandle;
	
	public Shader(String name, ResourceMap resourceMap, String vertexShaderName, String fragmentShaderName) {
		this.name = name;
		this.loadSource(resourceMap, vertexShaderName, fragmentShaderName);
	}
	
	public void loadSource(ResourceMap resourceMap, String vertexShaderName, String fragmentShaderName) {
		vertexShader = resourceMap.getResourceStringByName(vertexShaderName);
		fragmentShader = resourceMap.getResourceStringByName(fragmentShaderName);
	}
	
	public String[] getAttributes() {
		return null;
	}
	
	public String getVertexShader() {
		return vertexShader;
	}
	
	public String getFragmentShader() {
		return fragmentShader;
	}
	
	public void bindAttributes() {
		
	}
	
	public void bindUniforms() {
		mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
		mvMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVMatrix");
	}
	
	public void computeViewMatrix() {
		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);

		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);
		GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
	}

	public void computeOrthoViewMatrix() {

		float[] viewMatrix = new float[16];
		Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);

		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);

		Matrix.multiplyMM(mvpMatrix, 0, orthoProjectionMatrix, 0, mvMatrix, 0);
		GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
	}	
	
	public String getName() {
		return name;
	}

	public void setProgramHandle(int programHandle) {
		this.programHandle = programHandle;
	}
	
	public int getProgramHandle() {
		return programHandle;
	}
	
	public void load() {
		GLES20.glUseProgram(programHandle);
		this.creator.setActiveShader(this);		
	}
	
	public void setCreator(Shaders creator) {
		this.creator = creator;
	}

	public float[] getViewMatrix() {
		return viewMatrix;
	}
	
	public float[] getModelMatrix() {
		return modelMatrix;
	}

	public float[] getProjectionMatrix() {
		return projectionMatrix;
	}

	public float[] getOrthoProjectionMatrix() {
		return orthoProjectionMatrix;
	}
	
	public void saveModelMatrix() {
		System.arraycopy(modelMatrix, 0, modelMatrixCpy, 0, modelMatrix.length);
	}
	
	public void restoreModelMatrix() {
		System.arraycopy(modelMatrixCpy, 0, modelMatrix, 0, modelMatrix.length);
	}

}
