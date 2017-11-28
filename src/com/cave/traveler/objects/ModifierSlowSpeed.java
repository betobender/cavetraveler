package com.cave.traveler.objects;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;

public class ModifierSlowSpeed extends Modifier {

	protected float[] modifierColor = new float[] { 0, 1, 0, 1 };
	protected float[] modifierFlickingColor = new float[] { 0, 1, 0, 0.5f };
	protected String textureName = "modifier_control_slowtime";
	private Type type = Type.FastTime;

	public enum Type {
		FastTime, SlowTime,
	}

	@Override
	public void shipCollision(float elapsedTime, World world, GameObject obj) {
		getSparks().setColor(modifierColor);
		getSparks().load(world, null);
		getSparks().emit(elapsedTime, obj);
		((Ship) obj).setThrusterColor(modifierColor);

		switch (type) {
		case FastTime:
			world.getEngine().setOneSecond(500f, 1f);
			break;
		case SlowTime:
			world.getEngine().setOneSecond(1500f, 1f);
			break;
		}

		explodeLight();
		playModifierSound(world.getSoundManager());
		world.getScore().setBottomMessage("Time anomaly detected!");
	}

	public void setType(Type type) {
		this.type = type;
		switch (type) {
		case FastTime:
			modifierFlickingColor = new float[] { 0, 1, 0, 0.5f };
			modifierColor = new float[] { 0, 1, 0, 1 };
			break;
		case SlowTime:
			modifierFlickingColor = new float[] { 0, 0, 1, 0.5f };
			modifierColor = new float[] { 0, 0, 1, 1 };
			break;
		}

		this.setFlickerColor(modifierFlickingColor);
		this.setModifierTextureIconName(textureName);
	}

	public ModifierSlowSpeed() {
		super();
		State state = new State();
		state.setState("normal");
		this.addState(state);
		this.setCollidable(false);
		this.setInitialState("normal");
		this.setFlickerColor(modifierFlickingColor);
		this.setModifierTextureIconName(textureName);

	}
}
