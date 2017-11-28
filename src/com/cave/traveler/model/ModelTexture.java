package com.cave.traveler.model;

import com.cave.traveler.engine.Graphics.Texture;
import com.cave.traveler.engine.Graphics.TextureMap;

public class ModelTexture {

	private String textureName;
	private Texture texture;
	
	public ModelTexture(String textureName) {
		this.textureName = textureName;
	}

	public void load(TextureMap textureMap) {
		if(textureName != null && texture == null)
			texture = textureMap.getTextureByName(textureName);
	}

	public void beginTexture(ModelObject modelObject) {
		//gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getTextureId());
		//gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		//gl.glTexCoordPointer(3, GL10.GL_FLOAT, 0, modelObject.getTextureCoordsBuffer());
	}

	public void endTexture(ModelObject modelObject) {
		//gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

}
