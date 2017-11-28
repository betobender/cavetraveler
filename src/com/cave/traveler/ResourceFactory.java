package com.cave.traveler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import com.cave.traveler.engine.Graphics.ResourceMap;

public class ResourceFactory implements ResourceMap {

	private Context context;
	private static ResourceFactory instance = new ResourceFactory();
	private Map<String, Integer> resourceMap = new HashMap<String, Integer>();
	private Map<String, Typeface> fontMap = new HashMap<String, Typeface>();
	private Map<String, Bitmap> bitmapMap = new HashMap<String, Bitmap>();

	private ResourceFactory() {
		if (resourceMap.isEmpty() == true) {

			// images

			resourceMap.put("bluebloom", R.drawable.bluebloom);
			resourceMap.put("modifier_fragment", R.drawable.modifier_fragment);
			resourceMap.put("cave_fragment", R.drawable.cave_fragment);
			resourceMap.put("mark", R.drawable.mark);
			resourceMap.put("gradient", R.drawable.gradient);
			resourceMap.put("smoke", R.drawable.smoke);
			resourceMap.put("circle", R.drawable.circle);
			resourceMap.put("water_drop", R.drawable.water_drop);

			// sounds
			resourceMap.put("ship_explosion", R.raw.ship_explosion);
			resourceMap.put("modifier_sound", R.raw.modifier_sound);
			resourceMap.put("bonus_sound", R.raw.bonus_sound);
			resourceMap.put("cave_sound", R.raw.cave_sound);
			resourceMap.put("engine_sound", R.raw.engine_sound);
			resourceMap.put("music_one", R.raw.music_one);
			resourceMap.put("music_two", R.raw.music_two);
			resourceMap.put("warning_sound", R.raw.warning_sound);
			resourceMap.put("missile_sound", R.raw.missile_sound);

			// models

			resourceMap.put("spaceshipmodel", R.raw.spaceshipmodel);
			resourceMap.put("spaceshipmaterial", R.raw.spaceshipmaterial);
			resourceMap.put("cubemodel", R.raw.cubemodel);
			resourceMap.put("cubematerial", R.raw.cubematerial);
			resourceMap.put("spheremodel", R.raw.spheremodel);
			resourceMap.put("spherematerial", R.raw.spherematerial);
			resourceMap.put("stalactitemodel", R.raw.stalactitemodel);
			resourceMap.put("stalactitematerial", R.raw.stalactitematerial);
			resourceMap.put("missilemodel", R.raw.missilemodel);
			resourceMap.put("missilematerial", R.raw.missilematerial);			
			resourceMap.put("wallmodel", R.raw.wallmodel);
			resourceMap.put("wallmaterial", R.raw.wallmaterial);			
			resourceMap.put("glasswallmodel", R.raw.glasswallmodel);
			resourceMap.put("glasswallmaterial", R.raw.glasswallmaterial);

			// shaders

			resourceMap.put("fragment_shader", R.raw.fragment_shader);
			resourceMap.put("vertex_shader", R.raw.vertex_shader);			
			resourceMap.put("per_pixel_fragment_shader", R.raw.per_pixel_fragment_shader);
			resourceMap.put("per_pixel_vertex_shader", R.raw.per_pixel_vertex_shader);
			resourceMap.put("particle_fragment_shader", R.raw.particle_fragment_shader);
			resourceMap.put("particle_vertex_shader", R.raw.particle_vertex_shader);
			resourceMap.put("forcefield_fragment_shader", R.raw.forcefield_fragment_shader);
			resourceMap.put("forcefield_vertex_shader", R.raw.forcefield_vertex_shader);
			resourceMap.put("noise_fragment_shader", R.raw.noise_fragment_shader);
			resourceMap.put("noise_vertex_shader", R.raw.noise_vertex_shader);
			
			// texts
			resourceMap.put("credits_text", R.raw.credits);
		}
	}

	public static ResourceFactory getInstance() {
		return instance;
	}

	public Bitmap getBitmapByName(String name) {
		
		if(bitmapMap.containsKey(name)) {
			return bitmapMap.get(name);
		}
		
		if (resourceMap.containsKey(name)) {
			InputStream is = context.getResources().openRawResource(resourceMap.get(name));
			try {
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
				Bitmap retBitmap = BitmapFactory.decodeStream(is, null, bitmapOptions);
				bitmapMap.put(name, retBitmap);
				return retBitmap;
			} catch (Exception ex) {
			}
		}
		return null;
	}

	public int getResourceByName(String name) {
		return resourceMap.get(name);
	}

	public InputStream getResourceStreamByName(String name) {
		return context.getResources().openRawResource(getResourceByName(name));
	}

	public String getResourceStringByName(String name) {

		final InputStream inputStream = getResourceStreamByName(name);
		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try {
			while ((nextLine = bufferedReader.readLine()) != null) {
				body.append(nextLine);
				body.append('\n');
			}
		} catch (IOException e) {
			return null;
		}

		return body.toString();
	}

	public Context getResourceContext() {
		return context;
	}

	public void setResourceContext(Context context) {
		this.context = context;
	}

	public Typeface createFontFromAssets(String fontPath) {
		if(fontMap.containsKey(fontPath) == false) {
			fontMap.put(fontPath, Typeface.createFromAsset(context.getAssets(), fontPath));
		}
		return fontMap.get(fontPath);
	}
}
