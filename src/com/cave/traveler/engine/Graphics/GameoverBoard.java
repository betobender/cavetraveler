package com.cave.traveler.engine.Graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.opengl.Matrix;

import com.cave.traveler.engine.World;

public class GameoverBoard extends Drawing {

	private String message;
	private float elapsedTime;
	private float fadeInTime;
	private float fadeOutTime;
	private float duration;
	private float currentAlpha = 0;

	@Override
	public void prepare(World world, TextureMap textureMap) {
		super.prepare(world, textureMap);

		Paint titlePaint= new Paint();
		titlePaint.setTextSize(80);
		titlePaint.setColor(0xFFFFFFFF);
		titlePaint.setAntiAlias(true);
		titlePaint.setTextAlign(Paint.Align.CENTER);
		titlePaint.setTypeface(world.getResourceMap().createFontFromAssets("fonts/AstronBoy.ttf"));

		Canvas canvas = getCanvas();
		canvas.drawColor(0xCC000000, Mode.SRC);
		canvas.drawText(message, 512, 400, titlePaint);
		reloadTexture();

	}

	public GameoverBoard(String message, float fadeInTime, float duration, float fadeOutTime) {
		super(1024, 1024, 2, 2);

		this.message = message;
		this.fadeInTime = fadeInTime;
		this.fadeOutTime = fadeOutTime;
		this.duration = duration;
	}

	public void update(float elapsedTime, World world) {
		this.elapsedTime += elapsedTime;
		if (this.elapsedTime <= fadeInTime) {
			currentAlpha = this.elapsedTime / fadeInTime;
		} else if (this.elapsedTime <= fadeInTime + duration || duration < 0) {
			currentAlpha = 1f;
			if(duration < 0) {
				world.getEngine().setGameOver(true);
			}
		} else {
			currentAlpha = 1f - Math.min(this.elapsedTime / (fadeInTime + fadeOutTime + duration), 1f);
			world.getEngine().setGameOver(true);
		}
	}
	
	public void setScore(World world) {
		Paint scorePaint= new Paint();
		scorePaint.setTextSize(60);
		scorePaint.setColor(0xFFFFFFFF);
		scorePaint.setAntiAlias(true);
		scorePaint.setTextAlign(Paint.Align.CENTER);
		scorePaint.setTypeface(world.getResourceMap().createFontFromAssets("fonts/AstronBoy.ttf"));
		
		Paint scorePaint2= new Paint();
		scorePaint2.setTextSize(40);
		scorePaint2.setColor(0xDDDDDDDD);
		scorePaint2.setAntiAlias(true);
		scorePaint2.setTextAlign(Paint.Align.CENTER);
		scorePaint2.setTypeface(world.getResourceMap().createFontFromAssets("fonts/AstronBoy.ttf"));		
		
		Canvas canvas = getCanvas();
		canvas.drawText(String.format("Current Score: %d", world.getScore().getCurrentScore()), 512, 500, scorePaint);
		canvas.drawText(String.format("Touch the Screen to Continue..."), 512, 560, scorePaint2);
		reloadTexture();
	}

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {

		this.setAlpha(currentAlpha);

		ParticleShader shader = (ParticleShader) shaders.getShaderByName("particle");
		shader.load();

		Matrix.setIdentityM(shader.getModelMatrix(), 0);

		super.draw(textureMap, shaders);
	}
}
