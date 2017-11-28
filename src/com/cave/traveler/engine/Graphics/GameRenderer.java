package com.cave.traveler.engine.Graphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.cave.traveler.ResourceFactory;
import com.cave.traveler.engine.Camera;
import com.cave.traveler.engine.DynamicCave;
import com.cave.traveler.engine.DynamicLightSource;
import com.cave.traveler.engine.Engine;
import com.cave.traveler.engine.GameObject;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.TextBox.TextBoxContent;
import com.cave.traveler.engine.sound.Sound;

public class GameRenderer implements GLSurfaceView.Renderer {

	private Engine engine;
	private Camera camera;
	private Shaders shaders;
	private boolean showScores;
	private TextBox fpsTextBox;
	private TextBoxContent fpsTextBoxContent;

	// Debug information
	private int frameCounter;
	private int framesPerSecond;
	private long lastFrameCount;

	// Score and level information
	TopScore topScore = new TopScore();
	BottomScore bottomScore = new BottomScore();

	public GameRenderer() {
	}

	public void setEngine(Engine world) {
		this.engine = world;
	}

	public void onDrawFrame(GL10 unsed) {
		// drawing everything

		//StopWatch stopWatch = new StopWatch();
		
		World world = engine.getWorld();
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		DynamicCave dynamicCave = world.getDynamicCave();

		GameObject obj = world.getCameraController();
		if (obj != null) {

			// camera setup
			if (obj.getCurrentState().getCamera() != null)
				camera = obj.getCurrentState().getCamera();

			if (camera != null)
				camera.setCamera(shaders, obj);
		}
		
		//Log.i("performance", "Camera set time in ms: " + stopWatch.elapsedTime());
		//stopWatch.reset();

		for (Drawable d : world.getDrawables())
			d.setupLights(world, shaders);

		//Log.i("performance", "Lights set time in ms: " + stopWatch.elapsedTime());
		//stopWatch.reset();
		
		for (Drawable d : world.getDrawables())
			if (dynamicCave.shouldDraw(d))
				d.draw(world.getTextureMap(), shaders);
		
		//Log.i("performance", "Render time in ms: " + stopWatch.elapsedTime());
		//stopWatch.reset();
		

		for (GameObject o : world.getGameObjects())
			if (dynamicCave.shouldDraw(o))
				o.drawParticles(world.getTextureMap(), shaders);

		//Log.i("performance", "Particle render time in ms: " + stopWatch.elapsedTime());
		//stopWatch.reset();
		
		
		if (showScores) {
			topScore.draw(world.getTextureMap(), shaders);
			bottomScore.draw(world.getTextureMap(), shaders);
		}

		//Log.i("performance", "Scores render time in ms: " + stopWatch.elapsedTime());
		//stopWatch.reset();
		
		for (GameObject o : world.getGameObjects())
			o.drawCustom(world.getTextureMap(), shaders);
		
		//Log.i("performance", "Custom render time in ms: " + stopWatch.elapsedTime());
		//stopWatch.reset();
		

		if(world.getMessageBoard() != null)
			world.getMessageBoard().draw(world.getTextureMap(), shaders);
		
		//Log.i("performance", "Messageboard render time in ms: " + stopWatch.elapsedTime());
		//stopWatch.reset();

		
		//drawFramesPerSecond(world);

		// update engine
		//stopWatch.reset();
		engine.update();
		//Log.i("performance", "Engine time in ms: " + stopWatch.elapsedTime());
	}

	public void drawFramesPerSecond(World world) {
		ParticleShader shader = (ParticleShader) shaders.getShaderByName("particle");
		shader.load();

		Matrix.setIdentityM(shader.getModelMatrix(), 0);
		Matrix.translateM(shader.getModelMatrix(), 0, 0.9f, -0.9f, 0);
		fpsTextBox.draw(world.getTextureMap(), shaders);

		long currentTime = SystemClock.uptimeMillis();
		if (currentTime - lastFrameCount > 500) {
			framesPerSecond = (int) (frameCounter / ((currentTime - lastFrameCount) / 1000.0f));
			frameCounter = 0;
			lastFrameCount = currentTime;
		}
		++frameCounter;
		fpsTextBoxContent.setContent(framesPerSecond + " fps");
	}

	public void onSurfaceChanged(GL10 unsed, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		for (Shader shader : shaders.getShaders()) {

			GLESHelper.perspectiveM(shader.getProjectionMatrix(), 45, 90, 1f, 20000f);
			Matrix.orthoM(shader.getOrthoProjectionMatrix(), 0, -1, 1, -1, 1, 0f, 20000f);
		}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.3f, 0.3f, 0.3f, 1f);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		World world = engine.getWorld();
		topScore.prepare(world, world.getTextureMap());
		bottomScore.prepare(world, world.getTextureMap());
		fpsTextBox = new TextBox(128, 128, 0.2f, 0.2f);
		fpsTextBoxContent = fpsTextBox.addContent("", TextBox.createPaint(null, 28, 0xFFFFFFFF, Paint.Align.CENTER), new float[] { 64, 100 });
		fpsTextBox.prepare(world, world.getTextureMap());
		world.prepareDrawables();
		world.getTextureMap().loadTextures();

		shaders = new Shaders();
		shaders.addShader(new PerPixelShader("default", ResourceFactory.getInstance()));
		shaders.addShader(new ParticleShader("particle", ResourceFactory.getInstance()));
		shaders.addShader(new NoiseShader("noise", ResourceFactory.getInstance()));
		shaders.addShader(new ForceFieldShader("forcefield", ResourceFactory.getInstance()));

		lastFrameCount = SystemClock.uptimeMillis();

		engine.reset();
		engine.resume();
		
		DynamicLightSource.getInstance().reset();

		// game music
		Sound gameMusic = engine.getWorld().getSoundManager().getSound("game_sound");
		if (gameMusic != null)
			gameMusic.playLoop();

		Sound gameMusic2 = engine.getWorld().getSoundManager().getSound("game_sound2");
		if (gameMusic2 != null)
			gameMusic2.playLoop();

	}

	public boolean isShowScores() {
		return showScores;
	}

	public void setShowScores(boolean showScores) {
		this.showScores = showScores;
	}

}
