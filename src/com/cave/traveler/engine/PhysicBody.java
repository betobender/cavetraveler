package com.cave.traveler.engine;

public class PhysicBody {

	enum Type {
		unknown, points
	}

	private Type type;
	private int size;
	private float[][] points;

	//public void addPoint(Point)
	
	public PhysicBody(String type, int size) {
		setType(type);
		setSize(size);
		setPoints(new float[size][3]);
	}

	public float[][] getPoints() {
		return points;
	}

	public void setPoints(float[][] points) {
		this.points = points;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(String type) {
		if (type.equals("points"))
			this.type = Type.points;
		else
			this.type = Type.unknown;
	}
}
