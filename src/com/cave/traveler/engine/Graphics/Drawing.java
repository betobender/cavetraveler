package com.cave.traveler.engine.Graphics;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLES20;

import com.cave.traveler.engine.World;

public class Drawing implements Drawable {

	private static int drawingId = 0;
	private Bitmap bitmap;
	private Canvas canvas;
	private Texture texture;
	private float alpha = 1;
	private String textureName;

	private float modelWidth;
	private float modelHeight;

	private FloatBuffer position;
	private FloatBuffer color;
	private FloatBuffer textureCoord;

	public Drawing(int bitmapWidth, int bitmapHeight, float modelWidth, float modelHeight) {
		bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);

		this.modelWidth = modelWidth;
		this.modelHeight = modelHeight;
	}

	public Drawing(String textureName, float modelWidth, float modelHeight) {
		this.textureName = textureName;
		this.modelWidth = modelWidth;
		this.modelHeight = modelHeight;
	}

	public void setSize(float width, float height) {
		this.modelWidth = width;
		this.modelHeight = height;
		position = GLESHelper.allocateFloatBuffer(4 * 3,
				Point3D.expandToSquare(new Point3D(-modelWidth / 2, -modelHeight / 2, 0), new Point3D(modelWidth / 2, modelHeight / 2, 0)));
	}

	public void prepare(World world, TextureMap textureMap) {
		if (bitmap == null)
			texture = textureMap.getTextureByName(textureName);
		else {
			texture = Texture.createFromBitmap("drawing " + drawingId++, bitmap);
			textureMap.addTexture(texture);
		}

		position = GLESHelper.allocateFloatBuffer(4 * 3,
				Point3D.expandToSquare(new Point3D(-modelWidth / 2, -modelHeight / 2, 0), new Point3D(modelWidth / 2, modelHeight / 2, 0)));
		color = GLESHelper.allocateFloatBuffer(4 * 4, new float[] { 1, 1, 1, alpha });
		textureCoord = GLESHelper.allocateFloatBuffer(4 * 2, Point2D.expandToSquare(0, 1, 1, 0));
	}

	public void setupLights(World world, Shaders shaders) {

	}

	public void reloadTexture() {
		texture.reload();
	}

	public void setAlpha(float alpha) {

		this.alpha = alpha;

		if (this.alpha > 1)
			this.alpha = 1;
		else if (this.alpha < 0)
			this.alpha = 0;

		color.put(3, this.alpha);
		color.put(7, this.alpha);
		color.put(11, this.alpha);
		color.put(15, this.alpha);
	}

	public void draw(TextureMap textureMap, Shaders shaders) {

		ParticleShader shader = (ParticleShader) shaders.getShaderByName("particle");
		shader.load();

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_CULL_FACE);

		GLES20.glBindTexture(GL10.GL_TEXTURE_2D, texture.getTextureId());

		GLES20.glEnableVertexAttribArray(shader.getVertexHandle());
		GLES20.glEnableVertexAttribArray(shader.getColorHandle());
		GLES20.glEnableVertexAttribArray(shader.getTextureHandle());

		GLES20.glVertexAttribPointer(shader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, position);
		GLES20.glVertexAttribPointer(shader.getColorHandle(), 4, GLES20.GL_FLOAT, false, 0, color);
		GLES20.glVertexAttribPointer(shader.getTextureHandle(), 2, GLES20.GL_FLOAT, false, 0, textureCoord);

		shader.computeOrthoViewMatrix();
		GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, position.capacity() / 3);
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public boolean isGameObject() {
		return false;
	}
}
