package com.cave.traveler.engine.Graphics;

import com.cave.traveler.engine.World;

public interface Drawable {

	void prepare(World world, TextureMap textureMap);

	void draw(TextureMap textureMap, Shaders shaders);

	void setupLights(World world, Shaders shaders);

	boolean isGameObject();
}
