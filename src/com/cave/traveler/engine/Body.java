package com.cave.traveler.engine;

import android.util.FloatMath;

public class Body {

	private float scaleX;
	private float scaleY;
	private float scaleZ;

	public Body() {

	}

	public Body(float scaleX, float scaleY, float scaleZ) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getScaleZ() {
		return scaleZ;
	}

	public void setScaleZ(float scaleZ) {
		this.scaleZ = scaleZ;
	}

	public float getRealX(GameObject obj, float rX, float rY, float rotation) {
		float rad = (rotation * 3.14f) / 180f;
		return obj.getxPosition() + (scaleX * rX) * FloatMath.cos(rad) - (scaleY * rY) * FloatMath.sin(rad);
	}

	public float getRealX(float rX, float rY, float rotation) {
		float rad = (rotation * 3.14f) / 180f;
		return (scaleX * rX) * FloatMath.cos(rad) - (scaleY * rY) * FloatMath.sin(rad);
	}

	public float getRealY(GameObject obj, float rX, float rY, float rotation) {
		float rad = (rotation * 3.14f) / 180f;
		return obj.getyPosition() + (scaleX * rX) * FloatMath.sin(rad) + (scaleY * rY) * FloatMath.cos(rad);
	}

	public float getRealY(float rX, float rY, float rotation) {
		float rad = (rotation * 3.14f) / 180f;
		return (scaleX * rX) * FloatMath.sin(rad) + (scaleY * rY) * FloatMath.cos(rad);
	}

	public boolean isInside(GameObject a, GameObject b) {

		float left = getRealX(a, -1, 1, a.getRotation());
		float top = getRealY(a, -1, 1, a.getRotation());
		float right = getRealX(a, 1, -1, a.getRotation());
		float bottom = getRealY(a, 1, -1, a.getRotation());

		boolean ret = false;

		PhysicBody physicBody = b.getPhysicBody();
		if (physicBody != null && physicBody.getType() == PhysicBody.Type.points) {
			for (int i = 0; i < physicBody.getSize(); ++i) {
				float[] p = physicBody.getPoints()[i];

				float oX = b.getBody().getRealX(b, p[0], p[1], b.getRotation());
				float oY = b.getBody().getRealY(b, p[0], p[1], b.getRotation());

				ret = oX > left && oX < right && oY > bottom && oY < top;
				if (ret)
					break;
			}
		}

		return ret;
	}

}
