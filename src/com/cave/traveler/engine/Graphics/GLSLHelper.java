package com.cave.traveler.engine.Graphics;

import android.opengl.GLES20;

public class GLSLHelper {

	public static int compileShader(String shaderSource, int shaderType) {
		int shaderHandle = GLES20.glCreateShader(shaderType);
		String logInfo = "";

		if (shaderHandle != 0) {
			GLES20.glShaderSource(shaderHandle, shaderSource);
			GLES20.glCompileShader(shaderHandle);

			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			if (compileStatus[0] == 0) {
				logInfo = GLES20.glGetShaderInfoLog(shaderHandle);
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
			throw new RuntimeException("Error compiling shader: " + shaderSource + ". [" + logInfo + "]");

		return shaderHandle;
	}

	public static int createProgram(int vertexShaderHandle, int fragmentShaderHandle, String[] attributes) {
		int programHandle = GLES20.glCreateProgram();
		String logInfo = "";
		
		if (programHandle != 0) {
			GLES20.glAttachShader(programHandle, vertexShaderHandle);
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			for(int i = 0; i < attributes.length; ++i)
				GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
			
			GLES20.glLinkProgram(programHandle);
			
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			if (linkStatus[0] == 0) 
			{
				logInfo = GLES20.glGetProgramInfoLog(programHandle);
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}

		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program." + " [" + logInfo + "]");
		}
		
		return programHandle;
	}
}
