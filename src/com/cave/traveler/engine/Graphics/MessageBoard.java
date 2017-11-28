package com.cave.traveler.engine.Graphics;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.opengl.Matrix;

import com.cave.traveler.engine.World;

public class MessageBoard extends Drawing {

	private MessageBoardStep[] steps = null;
	private MessageBoardStep currentStep = null;
	private int currentStepIndex = 0;
	private float elapsedTime;
	private float currentAlpha = 0;

	public MessageBoard(MessageBoardStep[] steps) {
		super(512, 512, 2, 2f);
		this.steps = steps;
	}

	public void update(float elapsedTime, World world) {

		if (currentStep == null)
			return;

		this.elapsedTime += elapsedTime;

		if (this.elapsedTime > currentStep.duration) {
			this.elapsedTime = 0;
			++currentStepIndex;

			if (currentStepIndex >= steps.length)
				currentStep = null;
			else {
				currentStep = steps[currentStepIndex];
				loadCanvas(world);
			}
		}

		if (currentStepIndex < steps.length) {
			currentAlpha = currentStep.fromAlpha + (this.elapsedTime / currentStep.duration) * (currentStep.toAlpha - currentStep.fromAlpha);
		}
	}

	private void loadCanvas(World world) {
		Canvas canvas = getCanvas();
		
		if ((currentStep.text == null || currentStep.text.length == 0) && currentStep.mode.equals(MessageBoardStep.Mode.BackgroundOnly)) {
			canvas.drawColor(currentStep.background, Mode.SRC);
			
		} else if (currentStep.text != null && currentStep.text.length == 4 && currentStep.mode.equals(MessageBoardStep.Mode.TwoSideText)) {

			Paint textPaint= new Paint();
			textPaint.setTextSize(20);
			textPaint.setColor(currentStep.color);
			textPaint.setAntiAlias(true);
			textPaint.setTextAlign(Paint.Align.CENTER);
			textPaint.setTypeface(world.getResourceMap().createFontFromAssets("fonts/AstronBoy.ttf"));
			
			Paint linePaint= new Paint();
			linePaint.setColor(currentStep.color);
			linePaint.setStrokeWidth(2);
			linePaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
			linePaint.setStyle(Style.STROKE);
			
			canvas.drawColor(currentStep.background, Mode.SRC);
			canvas.drawText(currentStep.text[0], 128, 240, textPaint);
			canvas.drawText(currentStep.text[1], 128, 260, textPaint);
			canvas.drawText(currentStep.text[2], 384, 240, textPaint);
			canvas.drawText(currentStep.text[3], 384, 260, textPaint);	
			
			canvas.drawLine(256, 0, 256, 512, linePaint);
		}

		reloadTexture();
	}

	@Override
	public void prepare(World world, TextureMap textureMap) {
		super.prepare(world, textureMap);
		currentStep = steps[0];
		loadCanvas(world);
	}

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {
		if (currentStep != null) {
			this.setAlpha(currentAlpha);
			ParticleShader shader = (ParticleShader) shaders.getShaderByName("particle");
			shader.load();
			Matrix.setIdentityM(shader.getModelMatrix(), 0);
			super.draw(textureMap, shaders);
		}
	}
}
