package com.cave.traveler.engine.Graphics;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.Matrix;

import com.cave.traveler.engine.Score;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.TextBox.TextBoxContent;

public class TopScore extends Drawing {

	private Score score;
	private TextBox scoreTextBox;
	private TextBox speedTextBox;
	private TextBox bonusTextBox;
	private TextBoxContent scoreTextBoxContent;
	private TextBoxContent speedTextBoxContent;
	private TextBoxContent bonusTextBoxContent;
	private int resolution = 2;
	
	@Override
	public void prepare(World world, TextureMap textureMap) {
		resolution = ScreenConfig.getInstance().getResolution();
		
		score = world.getScore();

		scoreTextBox = new TextBox(256 / resolution, 128 / resolution, 0.4f, 0.2f);
		speedTextBox = new TextBox(256 / resolution, 128 / resolution, 0.4f, 0.2f);
		bonusTextBox = new TextBox(256 / resolution, 128 / resolution, 0.4f, 0.2f);

		Typeface typeFace = world.getResourceMap().createFontFromAssets("fonts/AstronBoy.ttf");

		scoreTextBox.addContent("Score", TextBox.createPaint(typeFace, 48 / resolution, 0x80FFFFFF, Paint.Align.LEFT), new float[] { 10 / resolution, 44 / resolution });
		scoreTextBoxContent = scoreTextBox.addContent("", TextBox.createPaint(typeFace, 68 / resolution, 0xFFFFFFFF, Paint.Align.LEFT), new float[] { 10 / resolution, 100 / resolution });

		speedTextBox.addContent("Speed", TextBox.createPaint(typeFace, 48 / resolution, 0x80FFFFFF, Paint.Align.CENTER), new float[] { 128 / resolution, 44 / resolution });
		speedTextBoxContent = speedTextBox.addContent("", TextBox.createPaint(typeFace, 68 / resolution, 0xFFFFFFFF, Paint.Align.CENTER), new float[] { 128 / resolution, 100 / resolution });
		
		bonusTextBox.addContent("Bonus", TextBox.createPaint(typeFace, 48 / resolution, 0x80FFFFFF, Paint.Align.RIGHT), new float[] { 246 / resolution, 44 / resolution });
		bonusTextBoxContent = bonusTextBox.addContent("", TextBox.createPaint(typeFace, 68 / resolution, 0xFFFFFFFF, Paint.Align.RIGHT), new float[] { 246 / resolution, 100 / resolution });		
		
		scoreTextBox.prepare(world, textureMap);
		speedTextBox.prepare(world, textureMap);
		bonusTextBox.prepare(world, textureMap);
		super.prepare(world, textureMap);
	}

	public TopScore() {
		super("gradient", 2, 0.2f);
	}

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {
		ParticleShader shader = (ParticleShader) shaders.getShaderByName("particle");
		shader.load();

		scoreTextBoxContent.setContent(score.getCurrentScore() + "");
		speedTextBoxContent.setContent(score.getCurrentSpeed() + " m/s");
		bonusTextBoxContent.setContent(String.format("%.2fx", score.getCurrentMultiplier()));
		
		if(score.isNearMissEnabled()) {
			bonusTextBoxContent.getPaint().setTextSize(80 / resolution);
		} else {
			bonusTextBoxContent.getPaint().setTextSize(68 / resolution);
		}

		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, 0, 0.9f, 0);
		super.draw(textureMap, shaders);

		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, -0.8f, 0.9f, 0);
		scoreTextBox.draw(textureMap, shaders);
		
		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, 0f, 0.9f, 0);		
		speedTextBox.draw(textureMap, shaders);
		
		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, 0.8f, 0.9f, 0);		
		bonusTextBox.draw(textureMap, shaders);		
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}
}
