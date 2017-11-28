package com.cave.traveler;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.widget.TextView;

import com.cave.traveler.engine.DynamicCave;
import com.cave.traveler.engine.Engine;
import com.cave.traveler.engine.Loader;
import com.cave.traveler.engine.LoaderException;
import com.cave.traveler.engine.RandomCavePathGenerator;
import com.cave.traveler.engine.World;
import com.cave.traveler.engine.Graphics.GameRenderer;
import com.cave.traveler.engine.Graphics.MessageBoard;
import com.cave.traveler.engine.Graphics.MessageBoardStep;
import com.cave.traveler.engine.control.TouchControlEvent;
import com.cave.traveler.engine.sound.Sound;
import com.cave.traveler.engine.sound.SoundManager;

public class OpenGLGameView extends GLSurfaceView {

	private World world;
	private Engine engine;
	private int buttonsPressed = 0;

	public Engine getEngine() {
		return engine;
	}

	public OpenGLGameView(Context context) {
		super(context);

		Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		dialog.setContentView(R.layout.loading_dialog);
		((TextView) dialog.findViewById(R.id.loadingTextView)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		dialog.show();

		try {
			ResourceFactory.getInstance().setResourceContext(context);
			world = Loader.getInstance().createWorldFromXml(context, R.raw.worlds, "world01");

			MessageBoardStep infoStep = new MessageBoardStep(new String[] {"Touch the left side", "to control the ship", "Touch the right side", "to control any fired missile"}, 0xFFFFFFFF, 0xFF000000, 4, 0.8f, 0, MessageBoardStep.Mode.TwoSideText);
			MessageBoard msgBoardInfo = new MessageBoard(new MessageBoardStep[] { infoStep });
			world.setMessageBoard(msgBoardInfo);

			SoundManager soundManager = new SoundManager();
			soundManager.addSound(new Sound("ship_explosion", Sound.Type.SoundEffect, "ship_explosion"));
			soundManager.addSound(new Sound("modifier_sound", Sound.Type.SoundEffect, "modifier_sound"));
			soundManager.addSound(new Sound("bonus_sound", Sound.Type.SoundEffect, "bonus_sound"));
			soundManager.addSound(new Sound("game_sound", Sound.Type.Music, "music_one"));
			soundManager.addSound(new Sound("ship_engine_sound", Sound.Type.SoundEffect, "engine_sound"));
			soundManager.addSound(new Sound("warning_sound", Sound.Type.SoundEffect, "warning_sound"));
			soundManager.addSound(new Sound("missile_sound", Sound.Type.SoundEffect, "missile_sound"));

			world.setSoundManager(soundManager);

			world.setDynamicCave(new DynamicCave(world, 100));
			world.getDynamicCave().setGenerator(new RandomCavePathGenerator(world.getDynamicCave()));

			world.load(ResourceFactory.getInstance());
		} catch (LoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		engine = new Engine();
		engine.setWorld(world);
		world.setEngine(engine);

		GameRenderer renderer = new GameRenderer();
		renderer.setShowScores(true);
		renderer.setEngine(engine);

		setEGLContextClientVersion(2);
		setRenderer(renderer);

		dialog.dismiss();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (engine.isGameOver() == true) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				((GameActivity) getContext()).finish();
				Intent intent = new Intent(getContext(), ScoreSubmitDialog.class);
				intent.putExtra("score", world.getScore().getCurrentScore());
				intent.putExtra("distance", world.getScore().getCurrentDistance());

				getContext().startActivity(intent);
			}
			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			engine.processControlEvent(new TouchControlEvent(true, buttonsPressed, event.getX() < getWidth() / 2));
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			engine.processControlEvent(new TouchControlEvent(false, buttonsPressed, event.getX() < getWidth() / 2));
		} else if ((event.getAction() & MotionEvent.ACTION_POINTER_DOWN) == MotionEvent.ACTION_POINTER_DOWN) {
			int pointerId = event.getPointerId(event.getActionIndex());
			engine.processControlEvent(new TouchControlEvent(true, buttonsPressed, event.getX(pointerId) < getWidth() / 2));
		} else if ((event.getAction() & MotionEvent.ACTION_POINTER_UP) == MotionEvent.ACTION_POINTER_UP) {
			int pointerId = event.getPointerId(event.getActionIndex());
			engine.processControlEvent(new TouchControlEvent(false, buttonsPressed, event.getX(pointerId) < getWidth() / 2));
		}

		return true;
	}

}
