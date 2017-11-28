package com.cave.traveler.engine.Graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

public class GLESHelper {
	public static float[] perspectiveM(float[] projMatrix, float fovY, float fovX, float zNear, float zFar) {
		float aspect = fovX / fovY;
		fovY = (float) ((fovY / 180.0) * Math.PI); // degrees to radians
		float g = (float) (1 / Math.tan(fovY / 2));

		for (int i = 0; i < 16; i++) {
			switch (i) {

			case 0:
				projMatrix[i] = g / aspect;
				break;
			case 5:
				projMatrix[i] = g;
				break;
			case 10:
				projMatrix[i] = (zFar + zNear) / (zNear - zFar);
				break;
			case 11:
				projMatrix[i] = -1.0f;
				break;
			case 14:
				projMatrix[i] = (2 * zFar * zNear) / (zNear - zFar);
				break;
			default:
				projMatrix[i] = 0.0f;
			}
		}
		return projMatrix;
	}

	public static FloatBuffer allocateFloatBuffer(int capacity) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(capacity * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asFloatBuffer();
	}

	public static FloatBuffer allocateFloatBuffer(int capacity, float[] data) {
		FloatBuffer ret = allocateFloatBuffer(capacity);
		while (ret.hasRemaining())
			ret.put(data);
		ret.position(0);
		return ret;
	}
	
	public static FloatBuffer allocateRandomFloatBuffer(int capacity) {
		FloatBuffer ret = allocateFloatBuffer(capacity);
		Random random = new Random();
		while (ret.hasRemaining())
			ret.put(random.nextFloat());
		ret.position(0);
		return ret;
	}	
}
