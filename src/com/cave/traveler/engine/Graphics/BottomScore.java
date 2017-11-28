package com.cave.traveler.engine.Graphics;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.Matrix;

import com.cave.traveler.engine.Score;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.TextBox.TextBoxContent;

public class BottomScore extends Drawing {

	private TextBox messageTextBox;
	private TextBoxContent messageTextBoxContent;
	private Score score;
	private int resolution = 2;

	@Override
	public void prepare(World world, TextureMap textureMap) {
		resolution = ScreenConfig.getInstance().getResolution();
		score = world.getScore();

		messageTextBox = new TextBox(1024 / resolution, 128 / resolution, 1.6f, 0.2f);
		Typeface typeFace = world.getResourceMap().createFontFromAssets("fonts/AstronBoy.ttf");
		messageTextBoxContent = messageTextBox.addContent("", TextBox.createPaint(typeFace, 68 / resolution, 0xFFFFFFFF, Paint.Align.CENTER), new float[] {
				512 / resolution, 80 / resolution });

		messageTextBox.prepare(world, textureMap);
		super.prepare(world, textureMap);
	}

	public BottomScore() {
		super("gradient", 2, 0.2f);
	}

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {

		ParticleShader shader = (ParticleShader) shaders.getShaderByName("particle");
		shader.load();

		messageTextBoxContent.setContent(score.getBottomMessage());

		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, 0, -0.9f, 0);
		super.draw(textureMap, shaders);

		messageTextBox.draw(textureMap, shaders);
	}
}
