package com.cave.traveler.engine.cavepaths;

import com.cave.traveler.engine.DynamicCave;
import com.cave.traveler.engine.DynamicCavePath;
import com.cave.traveler.engine.DynamicCavePathGenerator;
import com.cave.traveler.objects.Modifier;

public class CaveStartPath extends DynamicCavePath {

	@Override
	public void prepare(DynamicCavePath previous, float caveXPosition) {
		Modifier m = this.owner.getModifierReference(DynamicCave.ModifierReference.LevelStart0);
		m.setxPosition(this.steps * this.owner.getVertexLength());
		m.resetCollision(this.owner);
		m.setyPosition(150);
		m.setLength(steps * owner.getVertexLength());
		super.prepare(previous, caveXPosition);
	}

	public CaveStartPath(DynamicCave owner, DynamicCavePathGenerator generator) {
		super(owner, generator);
		this.steps = 50;
	}

	@Override
	public void generate(float caveXPosition) {
		super.generate(caveXPosition);
	}
}
