package com.cave.traveler.engine.Graphics;


public class Point3D {
	private float[] p = new float[] {0, 0, 0};

	public Point3D() {

	}

	public Point3D(float x, float y, float z) {
		p[0] = x;
		p[1] = y;
		p[2] = z;
	}

	public Point3D(float[] point) {
		p[0] = point[0];
		p[1] = point[1];
		p[2] = point[2];
	}

	public float[] get() {
		return p;
	}

	public float get(int index) {
		return p[index];
	}

	public float getX() {
		return p[0];
	}

	public float getY() {
		return p[1];
	}

	public float getZ() {
		return p[2];
	}

	public static float[] expandToSquare(Point3D p1, Point3D p2) {
		return new float[] { p1.p[0], p1.p[1], p1.p[2], p2.p[0], p1.p[1], p1.p[2], p1.p[0], p2.p[1], p2.p[2], p2.p[0], p2.p[1], p2.p[2] };
	}

	/*
	 *   static const GLfloat cubeVerticesStrip[] = {
        // Front face
        -1,-1,1, 1,-1,1, -1,1,1, 1,1,1,
        // Right face
        1,-1,1, 1,-1,-1, 1,1,1, 1,1,-1,
        // Back face
        1,-1,-1, -1,-1,-1, 1,1,-1, -1,1,-1,
        // Left face
        -1,-1,-1, -1,-1,1, -1,1,-1, -1,1,1,
        // Bottom face
        -1,-1,-1, 1,-1,-1, -1,-1,1, 1,-1,1,
        // Top face
        -1,1,1, 1,1,1, -1,1,-1, 1,1,-1
    };
	 */

	public static float[] expandToSquare(float s) {
		return new float[] { -s, -s, 0, s, -s, 0, -s, s, 0, s, s, 0 };
	}

	public static float[] expandToTriangle(float s) {
		return new float[] { -s, -s, 0, s, -s, 0, 0, s, 0 };
	}
}
