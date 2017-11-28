package com.cave.traveler.objects;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;

public class ModifierControlMalfunction extends Modifier {

	protected float[] modifierColor = new float[] { 1, 0, 0, 1 };
	protected float[] modifierFlickingColor = new float[] { 1, 0, 0, 0.5f };
	protected String textureName = "modifier_control_malfunction";
	
	@Override
	public void shipCollision(float elapsedTime, World world, GameObject obj) {
		getSparks().setColor(modifierColor);
		getSparks().load(world, null);
		getSparks().emit(elapsedTime, obj);

		((Ship) obj).setThrusterColor(modifierColor);
		((Ship) obj).setRandomImpulseGeneration(true);
		((Ship) obj).processControlEvent(null);
		
		explodeLight();
		playModifierSound(world.getSoundManager());
		world.getScore().setBottomMessage("Ship malfunction");
	}

	public ModifierControlMalfunction() {
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
