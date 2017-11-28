package com.cave.traveler.engine.Graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.cave.traveler.engine.World;

/**
 * DrawLayer class
 * 
 * @author Bender
 * 
 */
public class DrawLayer implements Drawable {

	public enum Type {
		unknown, scenario, gameObjects, cave
	};

	/* Properties and Configuration */
	private Type layerType;
	private int layerId;
	private Color color;
	private Point3D fromCoord = new Point3D();
	private Point3D toCoord = new Point3D();
	private String textureName;

	/* State properties */
	private float[] texCoords;
	private float[] vertices;
	private FloatBuffer vertexBuffer;
	private FloatBuffer texBuffer;
	private Texture texture;

	/* Implementation */

	public void prepare(World world, TextureMap textureMap) {

		texture = textureMap.getTextureByName(textureName);

		vertices = Point3D.expandToSquare(fromCoord, toCoord);
		texCoords = new float[] { 0.0f, texture.getHeight(), texture.getWidth(), texture.getHeight(), 0.0f, 0.0f, texture.getWidth(), 0.0f };

		// Setup vertex array buffer. Vertices in float. A float has 4 bytes
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder()); // Use native byte order
		vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
		vertexBuffer.put(vertices); // Copy data into buffer
		vertexBuffer.position(0); // Rewind

		// Setup texture-coords-array buffer, in float. An float has 4 bytes
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		texBuffer = tbb.asFloatBuffer();
		texBuffer.put(texCoords);
		texBuffer.position(0);

	}

	public void setupLights(World world, Shaders shaders) {
		
	}
	
	public void draw(TextureMap textureMap, Shaders shader) {
	}

	public void setLayerType(String type) {
		if (type.equals("scenario"))
			this.layerType = Type.scenario;
		else if (type.equals("gameObjects"))
			this.layerType = Type.gameObjects;
		else if (type.equals("cave"))
			this.layerType = Type.cave;
		else
			this.layerType = Type.unknown;
	}

	public Type getLayerType() {
		return layerType;
	}

	public void setLayerType(Type layerType) {
		this.layerType = layerType;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}

	public int getLayerId() {
		return layerId;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Point3D getFromCoord() {
		return fromCoord;
	}

	public void setFromCoord(Point3D fromCoord) {
		this.fromCoord = fromCoord;
	}

	public Point3D getToCoord() {
		return toCoord;
	}

	public void setToCoord(Point3D toCoord) {
		this.toCoord = toCoord;
	}

	public String getTextureName() {
		return textureName;
	}

	public void setTextureName(String textureName) {
		this.textureName = textureName;
	}

	public boolean isGameObject() {
		return false;
	}
}
