package com.cave.traveler.objects;

import com.cave.traveler.engine.Body;
import com.cave.traveler.engine.DynamicLightSource;
import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.PerPixelShader;
import com.cave.traveler.engine.Graphics.Shaders;

public class LightPoint extends GameObject {

	@Override
	public void update(float elapsedTime, World world) {

		DynamicLightSource.getInstance().updateLight(1, world, this);
		super.update(elapsedTime, world);
	}

	private float[] color = new float[] { 0, 1, 0, 1 };

	@Override
	public void setupLights(World world, Shaders shaders) {

		if (DynamicLightSource.getInstance().isLightSource(1, this)) {
			PerPixelShader shader = (PerPixelShader) shaders.getShaderByName("default");
			shader.load();

			shader.setLight(1, new float[] { getxPosition(), getyPosition(), 50 }, color, 1f);
		}

		super.setupLights(world, shaders);
	}

	public LightPoint() {

		setName("light");
		setDynamic(false);
		setInitialState("normal");
		setBody(new Body(1, 1, 1));

		State normalState = new State();
		normalState.setState("normal");
		addState(normalState);
	}
}
