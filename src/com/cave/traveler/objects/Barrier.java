package com.cave.traveler.objects;

import java.io.IOException;

import android.opengl.Matrix;

import com.cave.traveler.engine.Body;
import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.NoiseShader;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Shaders;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.engine.triggers.TriggerDispatcher;
import com.cave.traveler.model.Model;
import com.cave.traveler.model.ModelFactory;

public class Barrier extends GameObject {

	private Model model;
	private float height = 50;
	private float depth = 20;
	private boolean destroyed = true;
	
	@Override
	public void update(float elapsedTime, World world) {

		setyPosition(world.getDynamicCave().getMiddle(getxPosition()));
		setHeight(world.getDynamicCave().getHeight(getxPosition()) + 10);

		model.update(elapsedTime);

		if (world.getCameraController().getClass() == Ship.class) {
			Ship ship = (Ship) world.getCameraController();
			if (ship.getxPosition() > getxPosition() && destroyed == false) {
				ship.trigger(TriggerDispatcher.CaveCollision, world);
				explode(ship, world);
			}

			Missile missile = ship.getMissile();
			if( destroyed == false && missile.getxPosition() > getxPosition() 
					&& missile.getyPosition() < getyPosition() + height / 2 
					&& missile.getyPosition() > getyPosition() - height / 2) {
				missile.trigger(TriggerDispatcher.CaveCollision, world);
				explode(missile, world);
			}
		}

		super.update(elapsedTime, world);
	}

	public boolean isActive() {
		return destroyed == false;
	}
	
	private void explode(GameObject obj, World world) {
		model.explode3DR(40f, 50f);
		destroyed = true;
	}

	private float[] color = new float[] { 0.6f, 0.6f, 2f, 1f };

	float r = 0;

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {

		NoiseShader shader = (NoiseShader) shaders.getShaderByName("noise");
		shader.load();

		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, getxPosition(), getyPosition(), 0);
		Matrix.scaleM(shader.getModelMatrix(), 0, 10, getBody().getScaleY() * (height / 2), getBody().getScaleZ()
				* (depth / 2));

		model.draw(shader);

		super.draw(textureMap, shaders);
	}

	@Override
	public void load(World world, ResourceMap resourceMap) {
		super.load(world, resourceMap);

		try {
			model = ModelFactory.getInstance().createModelFromResource(resourceMap, "glasswallmodel");
			model.load(world.getTextureMap());
			model.setColor(color);
			model.setBlend(true);
			model.setCullFace(false);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void resetCollision() {
		model.resetExplosion();
		destroyed = false;
	}

	public Barrier() {

		setName("barrier");
		setDynamic(false);
		setInitialState("normal");
		setBody(new Body(1, 1, 1));

		setInitialXPosition(-1000);
		setInitialYPosition(0f);

		State normalState = new State();
		normalState.setState("normal");
		addState(normalState);
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getDepth() {
		return depth;
	}

	public void setDepth(float depth) {
		this.depth = depth;
	}
}
