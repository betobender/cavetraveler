package com.cave.traveler;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.cave.traveler.engine.DynamicCave;
import com.cave.traveler.engine.Engine;
import com.cave.traveler.engine.Loader;
import com.cave.traveler.engine.LoaderException;
import com.cave.traveler.engine.SimpleRandomCavePathGenerator;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.GameRenderer;
import com.cave.traveler.engine.Graphics.MessageBoard;
import com.cave.traveler.engine.Graphics.MessageBoardStep;
import com.cave.traveler.engine.Graphics.ScreenConfig;
import com.cave.traveler.engine.sound.Sound;
import com.cave.traveler.engine.sound.SoundManager;

public class MainView extends GLSurfaceView {

	private World world;
	private Engine engine;

	public MainView(Context context) {
		super(context);

		ScreenConfig.getInstance().setScreenResolution(getWidth(), getHeight());

		try {
			ResourceFactory.getInstance().setResourceContext(context);
			world = Loader.getInstance().createWorldFromXml(context, R.raw.worlds, "mainMenu");
			SoundManager soundManager = new SoundManager();
			soundManager.addSound(new Sound("game_sound", Sound.Type.Music, "cave_sound"));
			soundManager.addSound(new Sound("game_sound2", Sound.Type.Music, "music_two"));
			soundManager.addSound(new Sound("ship_engine_sound", Sound.Type.SoundEffect, "engine_sound"));
			world.setSoundManager(soundManager);

			world.setDynamicCave(new DynamicCave(world, 100));
			world.getDynamicCave().setGenerator(new SimpleRandomCavePathGenerator(world.getDynamicCave()));

			world.load(ResourceFactory.getInstance());

			MessageBoardStep fadeStep = new MessageBoardStep(null, 0, 0xFF000000, 2, 1, 0, MessageBoardStep.Mode.BackgroundOnly);
			MessageBoard msgBoardInfo = new MessageBoard(new MessageBoardStep[] { fadeStep });

			world.setMessageBoard(msgBoardInfo);

		} catch (LoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		engine = new Engine();
		engine.setWorld(world);
		world.setEngine(engine);

		GameRenderer renderer = new GameRenderer();
		renderer.setEngine(engine);
		renderer.setShowScores(false);

		setEGLContextClientVersion(2);
		setRenderer(renderer);

	}

	public Engine getEngine() {
		return engine;
	}
}
