package com.cave.traveler.objects;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;

public class ModifierLevelStart extends Modifier {

	@Override
	public void shipCollision(float elapsedTime, World world, GameObject obj) {
		getSparks().setColor(new float[] { 0.5f, 0.5f, 1, 1 });
		getSparks().load(world, null);
		getSparks().emit(elapsedTime, obj);

		world.getScore().setBottomMessage("The game has started!");
		
		playModifierSound(world.getSoundManager());

		((Ship) obj).setThrusterColor(new float[] { 0.5f, 0.5f, 1, 1 });
		((Ship) obj).setCurrentState("normal");
		((Ship) obj).processControlEvent(null);

		explodeLight();
	}

	public ModifierLevelStart() {
		super();
		State state = new State();
		state.setState("normal");
		this.addState(state);
		this.setCollidable(false);
		this.setInitialState("normal");
		this.setFlickerColor(new float[] { 0.5f, 0.5f, 1, 0.5f });
		this.setModifierTextureIconName("modifier_levelstart_icon");

	}
}
