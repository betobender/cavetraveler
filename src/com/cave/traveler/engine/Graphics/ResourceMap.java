package com.cave.traveler.engine.Graphics;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;

public interface ResourceMap {
	public int getResourceByName(String name);
	public Bitmap getBitmapByName(String name);
	public Context getResourceContext();
	public String getResourceStringByName(String name);
	public InputStream getResourceStreamByName(String name);
	public Typeface createFontFromAssets(String fontPath);
}
