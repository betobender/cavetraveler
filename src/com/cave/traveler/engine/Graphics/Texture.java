package com.cave.traveler.engine.Graphics;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.PorterDuff.Mode;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {

	private String name;
	private String bitmapName;
	private Bitmap bitmap;
	private String text;
	private int textWidth;
	private int textHeight;
	private int textSize;
	private int textureId = -1;
	private float width = 10;
	private float height = 2;

	protected Texture(String name) {
		this.name = name;
	}

	public static Texture createFromBitmap(String name, Bitmap bitmap) {
		Texture t = new Texture(name);
		t.bitmap = bitmap;
		return t;
	}

	public static Texture createFromBitmap(String name, String bitmap) {
		Texture t = new Texture(name);
		t.bitmapName = bitmap;
		return t;
	}

	public static Texture createFromText(String name, String text, int size, int width, int height) {
		Texture t = new Texture(name);
		t.text = text;
		t.textSize = size;
		t.textWidth = width;
		t.textHeight = height;
		return t;
	}

	public int getTextureId() {
		return textureId;
	}

	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void loadBitmap(ResourceMap resourceMap) {
		if (bitmap == null && bitmapName != null) {
			bitmap = resourceMap.getBitmapByName(bitmapName);
		}

		if (bitmap == null && text != null) {
			bitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawColor(0x00000000, Mode.SRC);
			Typeface typeFace = resourceMap.createFontFromAssets("fonts/Promethean.ttf");
			Paint p = TextBox.createPaint(typeFace, textSize, 0xFFFFFFFF, Paint.Align.CENTER);
			canvas.drawText(text, textWidth / 2, textHeight / 2 + textSize / 2, p);
		}
	}

	public float[] alignTexture(float[] points) {
		for (int i = 0; i < points.length; i += 2)
			points[i] /= width;

		for (int i = 1; i < points.length; i += 2)
			points[i] /= height;

		return points;
	}

	public float[] alignTextureX(float[] points, float width, float height) {
		for (int i = 0; i < points.length; i += 2)
			points[i] /= width;

		for (int i = 1; i < points.length; i += 2)
			points[i] /= height;

		return points;
	}

	public void reload() {
		GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	}

}
