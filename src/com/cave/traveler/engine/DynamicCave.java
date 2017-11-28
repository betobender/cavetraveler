package com.cave.traveler.engine;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cave.traveler.engine.CaveCollisionData.CaveCollision;
import com.cave.traveler.engine.Graphics.Drawable;
import com.cave.traveler.engine.Graphics.GLESHelper;
import com.cave.traveler.engine.Graphics.PerPixelShader;
import com.cave.traveler.engine.Graphics.ResourceMap;
import com.cave.traveler.engine.Graphics.Shaders;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.objects.Barrier;
import com.cave.traveler.objects.Item;
import com.cave.traveler.objects.Modifier;
import com.cave.traveler.objects.ModifierControlMalfunction;
import com.cave.traveler.objects.ModifierInvertGravity;
import com.cave.traveler.objects.ModifierLevelStart;
import com.cave.traveler.objects.ModifierNormalizer;
import com.cave.traveler.objects.ModifierSlowSpeed;

public class DynamicCave implements Drawable {

	private Random random = new Random();
	Map<ModifierReference, Modifier> modifiersMap;
	Map<ObjectReference, GameObject> objectsMap;

	public enum ModifierReference {
		LevelStart0, GravityInversor0, Normalizer0, ControlMalfunction0, SlowSpeed0,
	}

	public enum ObjectReference {
		Barrier0, Item0,
	}

	private World world;
	private int vertices;
	private int objectOffset = 30;
	private float vertexLength = 20;
	private float renderCaveTop = 2000;
	private float renderCaveBottom = -2000;
	private float renderCaveDepth = 50;
	private float renderCaveFront;
	private float renderCaveBack;
	private float backgroundSize = 2000;
	private float backgroundY = 0;
	private ParticleEmitter caveDropsEmitter;

	/*
	 * Dynamic cave generator
	 */
	DynamicCavePathGenerator dynamicCavePathGenerator;
	DynamicCavePath currentDynamicCavePath;

	/*
	 * Drawing buffers
	 */
	private FloatBuffer vertexBufferCaveTop;
	private FloatBuffer vertexBufferCaveBottom;
	private FloatBuffer vertexBufferCaveTopDepth;
	private FloatBuffer vertexBufferCaveBottomDepth;
	private FloatBuffer normalFrontBuffer;
	private FloatBuffer normalBackBuffer;
	private FloatBuffer normalToTopBuffer;
	private FloatBuffer normalToBottomBuffer;
	private FloatBuffer colorBuffer;
	private FloatBuffer backgroundVertexBuffer;
	private FloatBuffer backgroundColorBuffer;

	public DynamicCave(World world, int vertices) {
		this.vertices = vertices;
		this.world = world;

		modifiersMap = new HashMap<ModifierReference, Modifier>();
		addModifierToMap(ModifierReference.LevelStart0, new ModifierLevelStart());
		addModifierToMap(ModifierReference.Normalizer0, new ModifierNormalizer());
		addModifierToMap(ModifierReference.GravityInversor0, new ModifierInvertGravity());
		addModifierToMap(ModifierReference.ControlMalfunction0, new ModifierControlMalfunction());
		addModifierToMap(ModifierReference.SlowSpeed0, new ModifierSlowSpeed());

		objectsMap = new HashMap<DynamicCave.ObjectReference, GameObject>();
		addObjectToMap(ObjectReference.Barrier0, new Barrier());
		addObjectToMap(ObjectReference.Item0, new Item());
		

		caveDropsEmitter = new ParticleEmitter();
		caveDropsEmitter.setTextureName("water_drop");
		caveDropsEmitter.setAlpha(0.4f, 0.4f);
		caveDropsEmitter.setDuration(3f, 3f);
		caveDropsEmitter.setSize(4, 4);
		caveDropsEmitter.setInterval(0.5f);
		caveDropsEmitter.setParticles(1);
		caveDropsEmitter.setColor(new float[] { 0.5f, 0.5f, 1, 1 });
		caveDropsEmitter.setMode(ParticleEmitter.Mode.Absolute);
		caveDropsEmitter.setParticleType(Particle.ParticleType.SquareTextureDepthTest);
		caveDropsEmitter.setZVariation(renderCaveDepth / 2);
		caveDropsEmitter.setySpeed(-10);
		caveDropsEmitter.setyAcceleration(-80);
		
		renderCaveDepth = 50 + 150 * random.nextFloat();
	}

	/*
	 * Reset cave initial data
	 */
	public void reset() {
		index = 0;
		bufferIndex = 0;
		renderCaveFront = renderCaveDepth / 2;
		renderCaveBack = -renderCaveDepth / 2;

		if (dynamicCavePathGenerator != null)
			dynamicCavePathGenerator.reset();

		allocateInitialData();
	}

	/*
	 * Generate cave path (incremental and circular, cannot go back)
	 */
	private int bufferIndex = 0;
	private int index = 0;

	private void generateCave(float elapsedTime) {

		// generating the cave path

		createVertex();

		// here, the draw buffers must be updated
		int prevBufferIndex = bufferIndex - 1;
		if (prevBufferIndex < 0)
			prevBufferIndex = vertices - 1;

		float x1 = (index - 1) * vertexLength;
		float x2 = (index) * vertexLength;
		float yt1 = topPath[prevBufferIndex];
		float yt2 = topPath[bufferIndex];
		float yb1 = bottomPath[prevBufferIndex];
		float yb2 = bottomPath[bufferIndex];

		// repositioning the buffers
		vertexBufferCaveTop.position(bufferIndex * 9 * 2);
		vertexBufferCaveBottom.position(bufferIndex * 9 * 2);
		vertexBufferCaveTopDepth.position(bufferIndex * 9 * 2);
		vertexBufferCaveBottomDepth.position(bufferIndex * 9 * 2);
		
		// adding points to buffer

		vertexBufferCaveTop.put(new float[] { x1, yt1, renderCaveFront, x2, yt2, renderCaveFront, x1, renderCaveTop + yt1, renderCaveFront });
		vertexBufferCaveTop.put(new float[] { x1, renderCaveTop + yt1, renderCaveFront, x2, yt2, renderCaveFront, x2, renderCaveTop + yt2, renderCaveFront });

		vertexBufferCaveBottom.put(new float[] { x1, yb1, renderCaveFront, x1, renderCaveBottom + yb1, renderCaveFront, x2, yb2, renderCaveFront });
		vertexBufferCaveBottom.put(new float[] { x2, yb2, renderCaveFront, x1, renderCaveBottom + yb1, renderCaveFront, x2, renderCaveBottom + yb2,
				renderCaveFront });

		vertexBufferCaveTopDepth.put(new float[] { x2, yt2, renderCaveBack, x2, yt2, renderCaveFront, x1, yt1, renderCaveFront });
		vertexBufferCaveTopDepth.put(new float[] { x1, yt1, renderCaveFront, x1, yt1, renderCaveBack, x2, yt2, renderCaveBack });

		vertexBufferCaveBottomDepth.put(new float[] { x1, yb1, renderCaveFront, x2, yb2, renderCaveFront, x2, yb2, renderCaveBack });
		vertexBufferCaveBottomDepth.put(new float[] { x2, yb2, renderCaveBack, x1, yb1, renderCaveBack, x1, yb1, renderCaveFront });

		++index;
		++bufferIndex;
		if (bufferIndex >= vertices)
			bufferIndex = 0;

	}

	private void createVertex() {

		// compute path values

		if (currentDynamicCavePath == null || currentDynamicCavePath.end()) {
			currentDynamicCavePath = dynamicCavePathGenerator.getNewDynamicCavePath(currentDynamicCavePath, index * vertexLength);
		}

		currentDynamicCavePath.generate(index * vertexLength);

		topPath[bufferIndex] = currentDynamicCavePath.getTopValue();
		bottomPath[bufferIndex] = currentDynamicCavePath.getBottomValue();

		// compute the line alpha coefficient

		int prevBufferIndex = bufferIndex - 1;
		if (prevBufferIndex < 0)
			prevBufferIndex = vertices - 1;

		float v0Top = topPath[prevBufferIndex];
		float v1Top = topPath[bufferIndex];

		float v0Bot = bottomPath[prevBufferIndex];
		float v1Bot = bottomPath[bufferIndex];

		topLineAlpha[prevBufferIndex] = (v1Top - v0Top) / (vertexLength);
		bottomLineAlpha[prevBufferIndex] = (v1Bot - v0Bot) / (vertexLength);
	}

	/*
	 * Allocate buffers and all initial data
	 */
	private float[] topPath;
	private float[] bottomPath;
	private float[] topLineAlpha;
	private float[] bottomLineAlpha;

	private void allocateInitialData() {
		topPath = new float[vertices];
		bottomPath = new float[vertices];
		for (int i = 0; i < vertices; ++i) {
			topPath[i] = 1000;
			bottomPath[i] = -1000;
		}

		topLineAlpha = new float[vertices];
		bottomLineAlpha = new float[vertices];

		vertexBufferCaveTop = GLESHelper.allocateFloatBuffer(vertices * 9 * 2);
		vertexBufferCaveBottom = GLESHelper.allocateFloatBuffer(vertices * 9 * 2);
		vertexBufferCaveTopDepth = GLESHelper.allocateFloatBuffer(vertices * 9 * 2);
		vertexBufferCaveBottomDepth = GLESHelper.allocateFloatBuffer(vertices * 9 * 2);
		normalFrontBuffer = GLESHelper.allocateFloatBuffer(vertices * 9 * 2, new float[] { 0, 0, -1 });
		normalBackBuffer = GLESHelper.allocateFloatBuffer(vertices * 9 * 2, new float[] { 0, 0, 1 });
		normalToTopBuffer = GLESHelper.allocateFloatBuffer(vertices * 9 * 2, new float[] { 0, 1, 0 });
		normalToBottomBuffer = GLESHelper.allocateFloatBuffer(vertices * 9 * 2, new float[] { 0, -1, 0 });
		colorBuffer = GLESHelper.allocateFloatBuffer(vertices * 12 * 2, new float[] { 1, 1, 1, 1 });

		backgroundVertexBuffer = GLESHelper.allocateFloatBuffer(9 * 2,
				new float[] { -backgroundSize, backgroundSize, 0, backgroundSize, -backgroundSize, 0, backgroundSize, backgroundSize, 0, -backgroundSize,
						backgroundSize, 0, -backgroundSize, -backgroundSize, 0, backgroundSize, -backgroundSize, 0 });

		backgroundColorBuffer = GLESHelper.allocateFloatBuffer(vertices * 12 * 2, new float[] { 5, 5, 5, 5 });
	}

	private void addModifierToMap(ModifierReference ref, Modifier modifier) {
		modifiersMap.put(ref, modifier);
		world.addGameObject(modifier);
	}

	private void addObjectToMap(ObjectReference ref, GameObject obj) {
		objectsMap.put(ref, obj);
		world.addGameObject(obj);
	}

	/*
	 * Update buffer using camera controller position to do it so
	 */
	public void update(float elapsedTime, World world) {
		GameObject obj = world.getCameraController();
		if (obj != null) {
			int objPosition = (int) (obj.getxPosition() / vertexLength);
			
			backgroundY = obj.getyPosition();

			while (objPosition + vertices - objectOffset > (index))
				generateCave(elapsedTime);

			// dropping water drops
			float currentCaveBegin = obj.getxPosition() - 5 * vertexLength;
			float currentCaveEnd = obj.getxPosition() + 50 * vertexLength;
			float emitxPosition = currentCaveBegin + random.nextFloat() * (currentCaveEnd - currentCaveBegin);
			
			caveDropsEmitter.setxPosition(emitxPosition);
			caveDropsEmitter.setyPosition(getTop(emitxPosition));
			caveDropsEmitter.emit(elapsedTime, obj);
		}
	}

	public void prepare(World world, TextureMap textureMap) {
		reset();
	}

	public void draw(TextureMap textureMap, Shaders shaders) {
		PerPixelShader shader = (PerPixelShader) shaders.getShaderByName("default");
		shader.load();

		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);

		Matrix.setIdentityM(shader.getModelMatrix(), 0);

		GLES20.glEnableVertexAttribArray(shader.getVertexHandle());
		GLES20.glEnableVertexAttribArray(shader.getNormalHandle());
		GLES20.glEnableVertexAttribArray(shader.getColorHandle());

		shader.computeViewMatrix();

		GLES20.glVertexAttribPointer(shader.getColorHandle(), 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

		colorBuffer.position(0);
		vertexBufferCaveTop.position(0);
		vertexBufferCaveBottom.position(0);
		vertexBufferCaveTopDepth.position(0);
		vertexBufferCaveBottomDepth.position(0);
		normalFrontBuffer.position(0);
		normalToTopBuffer.position(0);
		normalToBottomBuffer.position(0);

		GLES20.glVertexAttribPointer(shader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, vertexBufferCaveTop);
		GLES20.glVertexAttribPointer(shader.getNormalHandle(), 3, GLES20.GL_FLOAT, false, 0, normalFrontBuffer);
		GLES20.glDrawArrays(GL10.GL_TRIANGLES, 0, vertices * 2 * 3);

		GLES20.glVertexAttribPointer(shader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, vertexBufferCaveBottom);
		GLES20.glVertexAttribPointer(shader.getNormalHandle(), 3, GLES20.GL_FLOAT, false, 0, normalFrontBuffer);
		GLES20.glDrawArrays(GL10.GL_TRIANGLES, 0, vertices * 2 * 3);

		GLES20.glVertexAttribPointer(shader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, vertexBufferCaveTopDepth);
		GLES20.glVertexAttribPointer(shader.getNormalHandle(), 3, GLES20.GL_FLOAT, false, 0, normalToBottomBuffer);
		GLES20.glDrawArrays(GL10.GL_TRIANGLES, 0, vertices * 2 * 3);

		GLES20.glVertexAttribPointer(shader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, vertexBufferCaveBottomDepth);
		GLES20.glVertexAttribPointer(shader.getNormalHandle(), 3, GLES20.GL_FLOAT, false, 0, normalToTopBuffer);
		GLES20.glDrawArrays(GL10.GL_TRIANGLES, 0, vertices * 2 * 3);

		drawBackground(textureMap, shaders);
	}

	public void setupLights(World world, Shaders shaders) {
	}

	/*
	 * Test collision against the cave
	 */
	public CaveCollision getCollision(GameObject obj) {
		PhysicBody physicBody = obj.getPhysicBody();
		if (physicBody.getType() == PhysicBody.Type.points) {
			for (int i = 0; i < physicBody.getSize(); ++i) {
				float[] p = physicBody.getPoints()[i];

				float oX = obj.getBody().getRealX(obj, p[0], p[1], obj.getRotation());
				float oY = obj.getBody().getRealY(obj, p[0], p[1], obj.getRotation());

				CaveCollision r = getCollision(oX, oY, 0, null);
				if (r != CaveCollision.DoesntCollide)
					return r;
			}
		}
		return CaveCollision.DoesntCollide;
	}

	/*
	 * Test point collision
	 */
	public CaveCollision getCollision(float pX, float pY, float near, CaveCollisionData data) {
		int i0 = (int) (pX / vertexLength);
		if (i0 > index || i0 < (index - vertices))
			return CaveCollision.DoesntCollide;

		int ri = i0 % vertices;

		float vtY = topLineAlpha[ri] * (pX - i0 * vertexLength) + topPath[ri];
		float vbY = bottomLineAlpha[ri] * (pX - i0 * vertexLength) + bottomPath[ri];

		if (pY + near > vtY) {
			if (data != null) {
				data.setCaveCollision(CaveCollision.CollideWithTop);
				data.setxPosition(pX);
				data.setyPosition(vtY);
				data.setDistance(vtY - pY);
			}
			return CaveCollision.CollideWithTop;
		}
		if (pY - near < vbY) {
			if (data != null) {
				data.setCaveCollision(CaveCollision.CollideWithBottom);
				data.setxPosition(pX);
				data.setyPosition(vbY);
				data.setDistance(pY - vbY);
			}
			return CaveCollision.CollideWithBottom;
		}

		return CaveCollision.DoesntCollide;
	}

	public float getRenderCaveBottom() {
		return renderCaveBottom;
	}

	public void setRenderCaveBottom(float renderCaveBottom) {
		this.renderCaveBottom = renderCaveBottom;
	}

	public float getRenderCaveDepth() {
		return renderCaveDepth;
	}

	public void setRenderCaveDepth(float renderCaveDepth) {
		this.renderCaveDepth = renderCaveDepth;
	}

	public float getRenderCaveTop() {
		return renderCaveTop;
	}

	public void setRenderCaveTop(float renderCaveTop) {
		this.renderCaveTop = renderCaveTop;
	}

	public Modifier getModifierReference(ModifierReference reference) {
		return modifiersMap.get(reference);
	}

	public float getVertexLength() {
		return vertexLength;
	}

	public void setVertexLength(float vertexLength) {
		this.vertexLength = vertexLength;
	}

	public GameObject getObjectRefence(ObjectReference reference) {
		return objectsMap.get(reference);
	}

	public boolean shouldDraw(Drawable d) {
		if (d.isGameObject() == false)
			return true;

		GameObject obj = (GameObject) d;
		return obj.getxPosition() >= (index - vertices) * vertexLength && obj.getxPosition() <= index * vertexLength;
	}

	public boolean isGameObject() {
		return false;
	}

	public float getHeight(float pX) {
		int i0 = (int) (pX / vertexLength);
		if (i0 > index || i0 < (index - vertices) || i0 < 0)
			return 0;

		int ri = i0 % vertices;
		float vtY = topLineAlpha[ri] * (pX - i0 * vertexLength) + topPath[ri];
		float vbY = bottomLineAlpha[ri] * (pX - i0 * vertexLength) + bottomPath[ri];
		return vtY - vbY;
	}

	public float getMiddle(float pX) {
		int i0 = (int) (pX / vertexLength);
		if (i0 > index || i0 < (index - vertices) || i0 < 0)
			return 0;

		int ri = i0 % vertices;
		float vtY = topLineAlpha[ri] * (pX - i0 * vertexLength) + topPath[ri];
		float vbY = bottomLineAlpha[ri] * (pX - i0 * vertexLength) + bottomPath[ri];
		return (vtY - vbY) / 2 + vbY;
	}

	public float getTop(float pX) {
		int i0 = (int) (pX / vertexLength);
		if (i0 > index || i0 < (index - vertices) || i0 < 0)
			return 0;

		int ri = i0 % vertices;
		return topLineAlpha[ri] * (pX - i0 * vertexLength) + topPath[ri];
	}

	public float getBottom(float pX) {
		int i0 = (int) (pX / vertexLength);
		if (i0 > index || i0 < (index - vertices) || i0 < 0)
			return 0;

		int ri = i0 % vertices;
		return bottomLineAlpha[ri] * (pX - i0 * vertexLength) + bottomPath[ri];
	}

	void drawBackground(TextureMap textureMap, Shaders shaders) {

		PerPixelShader shader = (PerPixelShader) shaders.getShaderByName("default");
		shader.load();

		Matrix.translateM(shader.getModelMatrix(), 0, (index - this.vertices / 2) * vertexLength, backgroundY, -renderCaveDepth / 2);
		shader.computeViewMatrix();

		GLES20.glVertexAttribPointer(shader.getVertexHandle(), 3, GLES20.GL_FLOAT, false, 0, backgroundVertexBuffer);
		GLES20.glVertexAttribPointer(shader.getNormalHandle(), 3, GLES20.GL_FLOAT, false, 0, normalBackBuffer);
		GLES20.glVertexAttribPointer(shader.getColorHandle(), 4, GLES20.GL_FLOAT, false, 0, backgroundColorBuffer);

		GLES20.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);
	}

	public void setGenerator(DynamicCavePathGenerator generator) {
		dynamicCavePathGenerator = generator;
	}

	public void load(World world, ResourceMap resourceMap) {
		caveDropsEmitter.load(world, resourceMap);
	}
}
