package com.cave.traveler.objects;

import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.State;
import com.cave.traveler.engine.World;

public class ModifierInvertGravity extends Modifier {

	protected float[] modifierColor = new float[] { 1, 1, 0, 1 };
	protected float[] modifierFlickingColor = new float[] { 1, 1, 0, 0.5f };
	protected String textureName = "modifier_invert_gravity";
	
	@Override
	public void shipCollision(float elapsedTime, World world, GameObject obj) {
		getSparks().setColor(modifierColor);
		getSparks().load(world, null);
		getSparks().emit(elapsedTime, obj);

		((Ship) obj).setThrusterColor(modifierColor);

		world.invertAllForces();
		((Ship) obj).setImpulseAcceleration(((Ship) obj).getImpulseAcceleration() * -1);
		((Ship) obj).processControlEvent(null);
		
		explodeLight();
		playModifierSound(world.getSoundManager());
		world.getScore().setBottomMessage("Gravity anomaly detected!");
	}

	public ModifierInvertGravity() {
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
