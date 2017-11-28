package com.cave.traveler.model;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.SparseArray;

import com.cave.traveler.engine.MoveablePoint;
import com.cave.traveler.engine.Graphics.ForceFieldShader;
import com.cave.traveler.engine.Graphics.GLESHelper;
import com.cave.traveler.engine.Graphics.NoiseShader;
import com.cave.traveler.engine.Graphics.ParticleShader;
import com.cave.traveler.engine.Graphics.PerPixelShader;
import com.cave.traveler.engine.Graphics.Shader;
import com.cave.traveler.engine.Graphics.TextureMap;

public class ModelFace {

	private List<int[]> preIndexes = new LinkedList<int[]>();
	private List<FaceGroup> groupedFaces = new LinkedList<ModelFace.FaceGroup>();

	private ModelMaterial modelMaterial;
	private int faceGroupSize;
	private boolean cullFace = true;
	private boolean blend = false;

	public ModelFace(ModelMaterial modelMaterial) {
		this.setModelMaterial(modelMaterial);
		this.faceGroupSize = 5;
	}

	public void update(float elapsedTime) {
		for (FaceGroup ef : groupedFaces)
			ef.moveableOrigin.update(elapsedTime);
	}

	public void addTriangle(int av, int an, int bv, int bn, int cv, int cn) {
		preIndexes.add(new int[] { av, an, bv, bn, cv, cn });
	}

	private class FaceGroup {
		private int vertexCount;
		private FloatBuffer vertexBuffer;
		private FloatBuffer normalBuffer;
		private FloatBuffer colorBuffer;
		private MoveablePoint moveableOrigin;
		private float[] color;
	}

	public void overrideNormals(float normal[]) {
		for (FaceGroup ef : groupedFaces) {
			ef.normalBuffer = GLESHelper.allocateFloatBuffer(3 * ef.vertexCount, normal);
		}

	}

	public void load(ModelObject modelObject, TextureMap textureMap) {

		SparseArray<float[]> vertices = modelObject.getVertices();
		SparseArray<float[]> normals = modelObject.getNormals();

		for (int i = 0; i < preIndexes.size();) {

			FaceGroup ef = new FaceGroup();
			if (preIndexes.size() - i < faceGroupSize)
				ef.vertexCount = 3 * (preIndexes.size() - i);
			else
				ef.vertexCount = 3 * faceGroupSize;

			ef.vertexBuffer = GLESHelper.allocateFloatBuffer(3 * ef.vertexCount);
			ef.normalBuffer = GLESHelper.allocateFloatBuffer(3 * ef.vertexCount);
			ef.colorBuffer = GLESHelper.allocateFloatBuffer(4 * ef.vertexCount, new float[] { 1, 1, 1, 1 });

			for (int j = 0; j < ef.vertexCount / 3; ++j) {
				int index[] = preIndexes.get(i++);

				float[] v1 = vertices.get(index[0]);
				float[] v2 = vertices.get(index[2]);
				float[] v3 = vertices.get(index[4]);

				ef.vertexBuffer.put(v1);
				ef.vertexBuffer.put(v2);
				ef.vertexBuffer.put(v3);

				float[] n1 = normals.get(index[1]);
				float[] n2 = normals.get(index[3]);
				float[] n3 = normals.get(index[5]);

				ef.normalBuffer.put(n1);
				ef.normalBuffer.put(n2);
				ef.normalBuffer.put(n3);

				ef.moveableOrigin = new MoveablePoint();
				ef.moveableOrigin.reset();
			}

			ef.vertexBuffer.position(0);
			ef.normalBuffer.position(0);
			groupedFaces.add(ef);
		}
	}

	public void draw(Shader shader) {

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);

		if (blend)
			GLES20.glEnable(GLES20.GL_BLEND);
		else
			GLES20.glDisable(GLES20.GL_BLEND);

		if (cullFace)
			GLES20.glEnable(GLES20.GL_CULL_FACE);
		else
			GLES20.glDisable(GLES20.GL_CULL_FACE);

		for (FaceGroup ef : groupedFaces) {

			MoveablePoint origin = ef.moveableOrigin.getAnimatedPoint();

			shader.saveModelMatrix();

			Matrix.translateM(shader.getModelMatrix(), 0, origin.getX(), origin.getY(), origin.getZ());
			Matrix.rotateM(shader.getModelMatrix(), 0, origin.getRotationX(), 1, 0, 0);
			Matrix.rotateM(shader.getModelMatrix(), 0, origin.getRotationY(), 0, 1, 0);
			Matrix.rotateM(shader.getModelMatrix(), 0, origin.getRotationZ(), 0, 0, 1);

			if (shader.getClass() == PerPixelShader.class) {

				PerPixelShader perPixelShader = (PerPixelShader) shader;

				GLES20.glVertexAttribPointer(perPixelShader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, ef.vertexBuffer);
				GLES20.glEnableVertexAttribArray(perPixelShader.getVertexHandle());

				GLES20.glVertexAttribPointer(perPixelShader.getNormalHandle(), 3, GLES20.GL_FLOAT, false, 0, ef.normalBuffer);
				GLES20.glEnableVertexAttribArray(perPixelShader.getNormalHandle());

				GLES20.glVertexAttribPointer(perPixelShader.getColorHandle(), 4, GLES20.GL_FLOAT, false, 0, ef.colorBuffer);
				GLES20.glEnableVertexAttribArray(perPixelShader.getColorHandle());

			} else if (shader.getClass() == ParticleShader.class) {

				ParticleShader particleShader = (ParticleShader) shader;
				
				GLES20.glVertexAttribPointer(particleShader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, ef.vertexBuffer);
				GLES20.glEnableVertexAttribArray(particleShader.getVertexHandle());

				GLES20.glVertexAttribPointer(particleShader.getColorHandle(), 4, GLES20.GL_FLOAT, false, 0, ef.colorBuffer);
				GLES20.glEnableVertexAttribArray(particleShader.getColorHandle());

			} else if (shader.getClass() == ForceFieldShader.class) {

				ForceFieldShader selectedShader = (ForceFieldShader) shader;
				
				GLES20.glVertexAttribPointer(selectedShader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, ef.vertexBuffer);
				GLES20.glEnableVertexAttribArray(selectedShader.getVertexHandle());
			}

			else if (shader.getClass() == NoiseShader.class) {

				NoiseShader selectedShader = (NoiseShader) shader;
				
				selectedShader.setNoiseColor(ef.color);
				
				GLES20.glVertexAttribPointer(selectedShader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, ef.vertexBuffer);
				GLES20.glEnableVertexAttribArray(selectedShader.getVertexHandle());
			}

			shader.computeViewMatrix();
			GLES20.glDrawArrays(GL10.GL_TRIANGLES, 0, ef.vertexCount);

			shader.restoreModelMatrix();
		}
	}

	public ModelMaterial getModelMaterial() {
		return modelMaterial;
	}

	public void setModelMaterial(ModelMaterial modelMaterial) {
		this.modelMaterial = modelMaterial;
	}

	public void explode3D(float speed, float rotationSpeed) {
		Random random = new Random();
		for (FaceGroup ef : groupedFaces) {
			ef.moveableOrigin.setSpeedX(-speed + random.nextFloat() * speed * 2);
			ef.moveableOrigin.setSpeedY(-speed + random.nextFloat() * speed * 2);
			ef.moveableOrigin.setRotationSpeedX(-rotationSpeed + random.nextFloat() * rotationSpeed * 2);
			ef.moveableOrigin.setRotationSpeedY(-rotationSpeed + random.nextFloat() * rotationSpeed * 2);
			ef.moveableOrigin.setRotationSpeedZ(-rotationSpeed + random.nextFloat() * rotationSpeed * 2);
			ef.moveableOrigin.reset();
		}
		this.setCullFace(false);
	}

	public boolean isCullFace() {
		return cullFace;
	}

	public boolean isBlend() {
		return blend;
	}

	public void setBlend(boolean blend) {
		this.blend = blend;
	}

	public void setCullFace(boolean cullFace) {
		this.cullFace = cullFace;
	}

	public void setColor(float[] color) {
		for (FaceGroup ef : groupedFaces) {
			ef.colorBuffer = GLESHelper.allocateFloatBuffer(4 * ef.vertexCount, color);
			ef.color = color;
		}
	}

	public void resetExplosion() {
		for (FaceGroup ef : groupedFaces) {
			ef.moveableOrigin = new MoveablePoint();
			ef.moveableOrigin.reset();
		}
	}

	public void explode3DR(float speed, float rotationSpeed) {
		Random random = new Random();
		for (FaceGroup ef : groupedFaces) {
			ef.moveableOrigin.setSpeedX(random.nextFloat() * speed);
			ef.moveableOrigin.setSpeedY(random.nextFloat() * speed);
			ef.moveableOrigin.setRotationSpeedX(-rotationSpeed + random.nextFloat() * rotationSpeed * 2);
			ef.moveableOrigin.setRotationSpeedY(-rotationSpeed + random.nextFloat() * rotationSpeed * 2);
			ef.moveableOrigin.setRotationSpeedZ(-rotationSpeed + random.nextFloat() * rotationSpeed * 2);
			ef.moveableOrigin.reset();
		}
		this.setCullFace(false);
	}
}
