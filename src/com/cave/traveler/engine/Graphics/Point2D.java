package com.cave.traveler.engine.Graphics;


public class Point2D {
	private float x = 0;
	private float y = 0;

	public Point2D() {

	}

	public Point2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Point2D(float[] point) {
		this.x = point[0];
		this.y = point[1];
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public static float[] expandToSquare(float p1x, float p1y, float p2x, float p2y) {
		return new float[] { p1x, p1y, p2x, p1y, p1x, p2y, p2x, p2y };
	}

	public static float[] expandToSquare(float s) {
		return new float[] { -s, -s, s, -s, -s, s, s, s };
	}

	public static float[] expandToTriangle(float p1x, float p1y, float p2x, float p2y) {
		return new float[] { p1x, p1y, p2x, p1y, (p2x - p1x), p2y };
	}
}
