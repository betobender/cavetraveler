package com.cave.traveler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class ScoreSubmitDialog extends Activity {

	class SubmitScoreManager extends ScoreManager {
		
		public SubmitScoreManager(ScoreSubmitDialog owner) {
			super(owner);
		}

		@Override
		public void scoreSaved() {
			getActivity().finish();
		}

		@Override
		public void errorSavingScore(String errorResponse) {
			cannotSubmitHandler.sendEmptyMessage(0);
		}
	}

	private final String ScorePlayerName = "ScorePlayerName";
	private EditText playerNameEditText;
	private Button submitButton;
	private Button skipButton;
	private ProgressBar progressBar;
	private SubmitScoreManager scoreManager = new SubmitScoreManager(this);
	private int currentScore = 0;
	private int currentDistance = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_submit_dialog);
		
		((TextView) findViewById(R.id.title)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"), Typeface.BOLD);

		playerNameEditText = (EditText) findViewById(R.id.playerName);
		playerNameEditText.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));

		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		playerNameEditText.setText(sharedPref.getString(ScorePlayerName, "Anonymous"));
		
		submitButton = (Button) findViewById(R.id.submit);
		submitButton.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		
		skipButton = (Button) findViewById(R.id.skip);
		skipButton.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		
		currentScore = getIntent().getExtras().getInt("score");
		currentDistance = getIntent().getExtras().getInt("distance");
		
		progressBar = (ProgressBar) findViewById(R.id.submitingProgressBar);
		progressBar.setVisibility(View.INVISIBLE);
	}

	private Handler cannotSubmitHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressBar.setVisibility(View.INVISIBLE);
			playerNameEditText.setEnabled(true);
			submitButton.setEnabled(true);
			skipButton.setEnabled(true);
		}
	};	
	
	public void submitScoreClick(View view) {

		if(playerNameEditText.getText() == null || playerNameEditText.getText().length() == 0)
			return;

		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(ScorePlayerName, playerNameEditText.getText().toString());
		editor.commit();
		
		scoreManager.saveScore(playerNameEditText.getText().toString(), currentScore, currentDistance, "1.0.0.3");
		
		progressBar.setVisibility(View.VISIBLE);
		playerNameEditText.setEnabled(false);
		submitButton.setEnabled(false);
		skipButton.setEnabled(false);
	}

	public void skipScoreClick(View view) {
		this.finish();
	}
}
