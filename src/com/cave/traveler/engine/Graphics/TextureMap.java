package com.cave.traveler.engine.Graphics;

import java.util.HashMap;
import java.util.Map;

import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureMap {

	private int[] texturesRefence;
	private Map<String, Texture> textures = new HashMap<String, Texture>();

	public TextureMap() {
	}

	public void addTexture(Texture texture) {
		textures.put(texture.getName(), texture);
	}

	public void readTextures(ResourceMap resourceMap) {
		for(Texture t : textures.values()) {
			if(t.getBitmap() == null) {
				t.loadBitmap(resourceMap);
			}
		}
	}
	
	public void loadTextures() {
		texturesRefence = new int[textures.size()];

		// Generate texture-ID array
		GLES20.glGenTextures(texturesRefence.length, texturesRefence, 0);

		// Bind to texture ID
		int index = 0;
		for (Texture t : textures.values()) {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesRefence[index]);

			// Set up texture filters
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

			// Build Texture from loaded bitmap for the currently-bind texture ID
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, t.getBitmap(), 0);

			// Reference the texture
			t.setTextureId(texturesRefence[index]);
			++index;
		}
	}

	public Texture getTextureByName(String name) {
		return textures.get(name);
	}
}
