package com.cave.traveler.engine.cavepaths;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.cave.traveler.engine.DynamicCave;
import com.cave.traveler.engine.DynamicCavePath;
import com.cave.traveler.engine.DynamicCavePathGenerator;
import com.cave.traveler.objects.Barrier;
import com.cave.traveler.objects.Item;
import com.cave.traveler.objects.Modifier;
import com.cave.traveler.objects.ModifierSlowSpeed;

public class RippleRamp extends DynamicCavePath {

	private class RippleState {
		private List<RippleState> possibleStates = new LinkedList<RippleState>();
		private List<RippleState> randomizedStates = new LinkedList<RippleState>();
		private List<RippleState> temporaryStates = new LinkedList<RippleState>();

		public void addPossibleState(RippleState state, int chances) {
			for (int i = 0; i < chances; ++i)
				possibleStates.add(state);
		}

		public void randomizeStates(Random random) {
			temporaryStates.addAll(possibleStates);
			while (temporaryStates.size() > 0) {
				int p = random.nextInt(temporaryStates.size());
				randomizedStates.add(temporaryStates.get(p));
				temporaryStates.remove(p);
			}
		}

		public RippleState getNextState(Random random) {
			if (randomizedStates.size() == 0)
				randomizeStates(random);
			return randomizedStates.remove(0);
		}

		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
		}
	}

	private class NormalState extends RippleRamp.RippleState {
		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
			rippleRamp.loadDefaults();
		}
	}

	private class RollbackState extends RippleRamp.RippleState {
		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
			Modifier m = rippleRamp.owner.getModifierReference(DynamicCave.ModifierReference.Normalizer0);
			if (m.hasExpired(caveXPosition)) {
				m.setLength(rippleRamp.steps * rippleRamp.owner.getVertexLength());
				m.setxPosition(caveXPosition);
				m.resetCollision(rippleRamp.owner);
			}
		}
	}

	private class InvertGravityState extends RippleRamp.RippleState {
		public InvertGravityState(RollbackState rollbackState) {
			ItemState itemState = new ItemState();
			itemState.addPossibleState(rollbackState, 1);
			addPossibleState(rollbackState, 1);
			addPossibleState(itemState, 1);
		}

		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
			Modifier m = rippleRamp.owner.getModifierReference(DynamicCave.ModifierReference.GravityInversor0);
			if (m.hasExpired(caveXPosition)) {
				m.setLength(rippleRamp.steps * rippleRamp.owner.getVertexLength());
				m.setxPosition(caveXPosition);
				m.resetCollision(rippleRamp.owner);
				steps = 200;
			}
		}
	}

	private class ControlMalfuntionState extends RippleRamp.RippleState {
		public ControlMalfuntionState(RollbackState rollbackState) {
			ItemState itemState = new ItemState();
			itemState.addPossibleState(rollbackState, 1);
			addPossibleState(rollbackState, 1);
			addPossibleState(itemState, 1);
		}

		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
			Modifier m = rippleRamp.owner.getModifierReference(DynamicCave.ModifierReference.ControlMalfunction0);
			if (m.hasExpired(caveXPosition)) {
				m.setLength(rippleRamp.steps * rippleRamp.owner.getVertexLength());
				m.setxPosition(caveXPosition);
				m.resetCollision(rippleRamp.owner);
				steps = 200;
			}
		}
	}

	private class SlowSpeedState extends RippleRamp.RippleState {
		public SlowSpeedState(RollbackState rollbackState) {
			ItemState itemState = new ItemState();
			itemState.addPossibleState(rollbackState, 1);
			addPossibleState(rollbackState, 1);
			addPossibleState(itemState, 1);
		}

		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
			Modifier m = rippleRamp.owner.getModifierReference(DynamicCave.ModifierReference.SlowSpeed0);
			if (m.hasExpired(caveXPosition)) {

				switch (generator.getRandomProvider(1).nextInt(2)) {
				case 0:
					((ModifierSlowSpeed) m).setType(ModifierSlowSpeed.Type.FastTime);
					break;
				case 1:
					((ModifierSlowSpeed) m).setType(ModifierSlowSpeed.Type.SlowTime);
					break;
				}

				m.setLength(rippleRamp.steps * rippleRamp.owner.getVertexLength());
				m.setxPosition(caveXPosition);
				m.resetCollision(rippleRamp.owner);
				steps = 200;
			}
		}
	}

	private class BarrierState extends RippleRamp.RippleState {
		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {

			Barrier b = (Barrier) rippleRamp.owner.getObjectRefence(DynamicCave.ObjectReference.Barrier0);
			b.setDepth(rippleRamp.owner.getRenderCaveDepth() - 10f);
			b.setxPosition(caveXPosition);
			b.resetCollision();
		}
	}

	private class ItemState extends RippleRamp.RippleState {
		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {

			Item b = (Item) rippleRamp.owner.getObjectRefence(DynamicCave.ObjectReference.Item0);
			b.setxPosition(caveXPosition);
			b.setyRelativePosition(generator.getRandomProvider(1).nextFloat() * 0.8f + 0.1f);
			b.resetCollision();
		}
	}

	private class BigRampState extends RippleRamp.RippleState {
		public BigRampState(NormalState normalState) {
			ItemState itemState = new ItemState();
			itemState.addPossibleState(normalState, 1);
			addPossibleState(normalState, 1);
			addPossibleState(itemState, 1);
		}

		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
			rippleRamp.minPathVariation = 0;
			rippleRamp.maxPathVariation = 300;
		}
	}

	private class BiggerRampState extends RippleRamp.RippleState {
		public BiggerRampState(NormalState normalState) {
			ItemState itemState = new ItemState();
			itemState.addPossibleState(normalState, 1);
			addPossibleState(normalState, 1);
			addPossibleState(itemState, 1);
		}

		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
			rippleRamp.minPathVariation = 100;
			rippleRamp.maxPathVariation = 500;
		}
	}

	private class BigDropState extends RippleRamp.RippleState {
		public BigDropState(NormalState normalState) {
			ItemState itemState = new ItemState();
			itemState.addPossibleState(normalState, 1);
			addPossibleState(normalState, 1);
			addPossibleState(itemState, 1);
		}

		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
			rippleRamp.minPathVariation = -300;
			rippleRamp.maxPathVariation = 0;
		}
	}

	private class BiggerDropState extends RippleRamp.RippleState {
		public BiggerDropState(NormalState normalState) {
			ItemState itemState = new ItemState();
			itemState.addPossibleState(normalState, 1);
			addPossibleState(normalState, 1);
			addPossibleState(itemState, 1);
		}

		public void prepareEnvironment(RippleRamp rippleRamp, float caveXPosition) {
			rippleRamp.minPathVariation = -500;
			rippleRamp.maxPathVariation = -100;
		}
	}

	@Override
	public void prepare(DynamicCavePath previous, float caveXPosition) {
		super.prepare(previous, caveXPosition);

		this.steps = 50;

		float nextPath = currentPath + minPathVariation + generator.getRandomProvider(0).nextFloat() * (maxPathVariation - minPathVariation);
		pathVariation = (nextPath - currentPath) / (float) steps;

		state.prepareEnvironment(this, caveXPosition);
		state = state.getNextState(generator.getRandomProvider(1));
	}

	public RippleRamp(DynamicCave owner, DynamicCavePathGenerator generator) {
		super(owner, generator);
		this.steps = 100;

		// creating possible states //
		RippleRamp.NormalState normalState = new NormalState();
		RippleRamp.RollbackState rollbackState = new RollbackState();
		RippleRamp.InvertGravityState invertGravityState = new InvertGravityState(rollbackState);
		RippleRamp.ControlMalfuntionState controlMalfunctionState = new ControlMalfuntionState(rollbackState);
		RippleRamp.SlowSpeedState slowSpeedState = new SlowSpeedState(rollbackState);
		RippleRamp.BigRampState bigRampState = new BigRampState(normalState);
		RippleRamp.BiggerRampState biggerRampState = new BiggerRampState(normalState);
		RippleRamp.BigDropState bigDropState = new BigDropState(normalState);
		RippleRamp.BiggerDropState biggerDropState = new BiggerDropState(normalState);
		RippleRamp.BarrierState barrierState = new BarrierState();
		RippleRamp.ItemState itemState = new ItemState();

		normalState.addPossibleState(normalState, 2);
		normalState.addPossibleState(barrierState, 3);
		normalState.addPossibleState(invertGravityState, 1);
		normalState.addPossibleState(controlMalfunctionState, 2);
		normalState.addPossibleState(slowSpeedState, 1);
		normalState.addPossibleState(bigDropState, 2);
		normalState.addPossibleState(biggerDropState, 2);
		normalState.addPossibleState(bigRampState, 1);
		normalState.addPossibleState(biggerRampState, 1);
		normalState.addPossibleState(itemState, 4);

		rollbackState.addPossibleState(normalState, 1);

		itemState.addPossibleState(normalState, 1);
		
		barrierState.addPossibleState(normalState, 1);

		state = normalState;
	}

	private float minPathVariation = -100;
	private float maxPathVariation = 100;
	private float minAppertureSize = 200;
	private float maxAppertureSize = 400;
	private float minAppertureVariation = -10;
	private float maxAppertureVariation = 10;
	private float currentPath = 150;
	private float pathVariation = 0;
	private RippleRamp.RippleState state = null;

	public void loadDefaults() {
		minPathVariation = -100;
		maxPathVariation = 100;
		minAppertureSize = 200;
		maxAppertureSize = 400;
		minAppertureVariation = -10;
		maxAppertureVariation = 10;
	}

	@Override
	public void generate(float caveXPosition) {

		float variation1 = minAppertureVariation + generator.getRandomProvider(0).nextFloat() * (maxAppertureVariation - minAppertureVariation);
		float variation2 = minAppertureVariation + generator.getRandomProvider(0).nextFloat() * (maxAppertureVariation - minAppertureVariation);

		topValue += pathVariation + variation1;
		bottomValue += pathVariation + variation2;

		while ((topValue - bottomValue) > maxAppertureSize) {
			topValue -= 0.5;
			bottomValue += 0.5;
		}

		while ((topValue - bottomValue) < minAppertureSize) {
			topValue += 0.5;
			bottomValue -= 0.5;
		}

		super.generate(caveXPosition);
	}

	public float getMinPathVariation() {
		return minPathVariation;
	}

	public void setMinPathVariation(float minPathVariation) {
		this.minPathVariation = minPathVariation;
	}

	public float getMaxPathVariation() {
		return maxPathVariation;
	}

	public void setMaxPathVariation(float maxPathVariation) {
		this.maxPathVariation = maxPathVariation;
	}

	public float getMinAppertureSize() {
		return minAppertureSize;
	}

	public void setMinAppertureSize(float minAppertureSize) {
		this.minAppertureSize = minAppertureSize;
	}

	public float getMaxAppertureSize() {
		return maxAppertureSize;
	}

	public void setMaxAppertureSize(float maxAppertureSize) {
		this.maxAppertureSize = maxAppertureSize;
	}

	public float getMaxAppertureVariation() {
		return maxAppertureVariation;
	}

	public void setMaxAppertureVariation(float maxAppertureVariation) {
		this.maxAppertureVariation = maxAppertureVariation;
	}
}
