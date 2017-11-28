package com.cave.traveler.engine.Graphics;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class PerPixelShader extends Shader {

	public PerPixelShader(String name, ResourceMap resourceMap) {
		super(name, resourceMap, "per_pixel_vertex_shader", "per_pixel_fragment_shader");
	}
	
	public PerPixelShader(String name, ResourceMap resourceMap, String vertexShaderName, String fragmentShaderName) {
		super(name, resourceMap, vertexShaderName, fragmentShaderName);
	}

	private float[] lightModelMatrix = new float[16];
	private float[] lightPosInModelSpace = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	private float[] lightPosInWorldSpace = new float[4];
	private float[] lightPosInEyeSpace = new float[4];	
	private float[] ambientColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	private int lightPos1Handle;
	private int lightPos2Handle;
	private int lightColor1Handle;
	private int lightColor2Handle;
	private int lightFade1Handle;
	private int lightFade2Handle;
	private int vertexHandle;
	private int colorHandle;
	private int normalHandle;
	private int ambientColorHandle;

	@Override
	public String[] getAttributes() {
		return new String[] { "a_Position", "a_Color", "a_Normal" };
	}

	@Override
	public void bindAttributes() {
		vertexHandle = GLES20.glGetAttribLocation(getProgramHandle(), "a_Position");
		colorHandle = GLES20.glGetAttribLocation(getProgramHandle(), "a_Color");
		normalHandle = GLES20.glGetAttribLocation(getProgramHandle(), "a_Normal");
		super.bindAttributes();
	}

	@Override
	public void bindUniforms() {
		lightPos1Handle = GLES20.glGetUniformLocation(getProgramHandle(), "u_LightPos1");
		lightPos2Handle = GLES20.glGetUniformLocation(getProgramHandle(), "u_LightPos2");
		lightColor1Handle = GLES20.glGetUniformLocation(getProgramHandle(), "u_LightColor1");
		lightColor2Handle = GLES20.glGetUniformLocation(getProgramHandle(), "u_LightColor2");
		lightFade1Handle = GLES20.glGetUniformLocation(getProgramHandle(), "u_LightFade1");
		lightFade2Handle = GLES20.glGetUniformLocation(getProgramHandle(), "u_LightFade2");
		ambientColorHandle = GLES20.glGetUniformLocation(getProgramHandle(), "u_AmbientColor");
		super.bindUniforms();
	}
	
	public void setLight(int lightId, float[] position, Light light, float fade) {
		setLight(lightId, position, light.getColor(), fade);
	}
	
	public void setLight(int lightId, float[] position, float[] color, float fade) {

		Matrix.setIdentityM(lightModelMatrix, 0);
		Matrix.translateM(lightModelMatrix, 0, position[0], position[1], position[2]);
		Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
		Matrix.multiplyMV(lightPosInEyeSpace, 0, getViewMatrix(), 0, lightPosInWorldSpace, 0);

		switch (lightId) {
		case 0:
			GLES20.glUniform3f(lightPos1Handle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
			GLES20.glUniform4f(lightColor1Handle, color[0], color[1], color[2], 1f);
			GLES20.glUniform1f(lightFade1Handle, fade);
			break;

		case 1:
			GLES20.glUniform3f(lightPos2Handle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
			GLES20.glUniform4f(lightColor2Handle, color[0], color[1], color[2], 1f);
			GLES20.glUniform1f(lightFade2Handle, fade);
			break;
		}
		
		GLES20.glUniform4f(ambientColorHandle, ambientColor[0], ambientColor[1], ambientColor[2], ambientColor[3]);
	}	
	
	public int getVertexHandle() {
		return vertexHandle;
	}
	
	public int getColorHandle() {
		return colorHandle;
	}
	
	public int getNormalHandle() {
		return normalHandle;
	}
}
