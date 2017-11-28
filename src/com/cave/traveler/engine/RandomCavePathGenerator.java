package com.cave.traveler.engine;

import com.cave.traveler.engine.cavepaths.CaveStartPath;
import com.cave.traveler.engine.cavepaths.RippleRamp;
import com.cave.traveler.engine.cavepaths.SimpleRippleRamp;


public class RandomCavePathGenerator extends DynamicCavePathGenerator {

	private class DesiredPath {
		int beginningJump = -1;
		DynamicCavePath[] pathList = null;

		DesiredPath(int beginningJump, DynamicCavePath[] pathList) {
			this.beginningJump = beginningJump;
			this.pathList = pathList;
		}
	}

	private DesiredPath[] desiredPaths = null;
	private int selectedDesiredPath = -1;
	private int currentStep = 0;

	public RandomCavePathGenerator(DynamicCave owner) {

		desiredPaths = new DesiredPath[] { new DesiredPath(2, new DynamicCavePath[] {
				new CaveStartPath(owner, this),
				new SimpleRippleRamp(owner, this),
				new RippleRamp(owner, this)
		}) };

		selectedDesiredPath = this.getRandomProvider(0).nextInt(desiredPaths.length);
	}

	@Override
	public DynamicCavePath getNewDynamicCavePath(DynamicCavePath current, float caveXPosition) {
		DesiredPath desiredPath = desiredPaths[selectedDesiredPath];
		
		if(currentStep >= desiredPath.pathList.length) {
			currentStep = desiredPath.beginningJump;
		}
		
		DynamicCavePath path = desiredPath.pathList[currentStep++];
		path.reset();
		path.prepare(current, caveXPosition);
		return path;
	}
	
	@Override
	public void reset() {
		currentStep = 0;
		selectedDesiredPath = this.getRandomProvider(0).nextInt(desiredPaths.length);
		super.reset();
	}

	
}
