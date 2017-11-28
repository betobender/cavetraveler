package com.cave.traveler.objects;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;

public class ModifierNormalizer extends Modifier {

	protected float[] modifierColor = new float[] { 1f, 1f, 1, 1 };
	protected float[] modifierFlickingColor = new float[] { 1f, 1f, 1, 0.5f };
	protected String textureName = "modifier_normalizer";
	
	@Override
	public void shipCollision(float elapsedTime, World world, GameObject obj) {
		getSparks().setColor(modifierColor);
		getSparks().load(world, null);
		getSparks().emit(elapsedTime, obj);

		((Ship) obj).setThrusterColor(modifierColor);
		((Ship) obj).loadDefaults();
		
		world.restoreAllForces();
		obj.getCurrentState().reApplySetters(elapsedTime, world, obj);
		
		world.getEngine().setOneSecond(1000f, 1f);
		
		((Ship) obj).processControlEvent(null);
		
		explodeLight();
		playModifierSound(world.getSoundManager());
		world.getScore().setBottomMessage("Ship controls in normal state");
	}

	public ModifierNormalizer() {
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
