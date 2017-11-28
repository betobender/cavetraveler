package com.cave.traveler;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {

	private MainView view;
	private View optionsView;
	private View highScoreView;
	private View creditsView;
	
	private ListView highScoreListView;
	private ProgressBar highScoreProgressBar;
	
	private List<ScoreListViewItem> scores = new LinkedList<ScoreListViewItem>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setting the background view
		view = new MainView(this);
		setContentView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		// adding the over view
		optionsView = getLayoutInflater().inflate(R.layout.activity_main, null);
		addContentView(optionsView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		((TextView) findViewById(R.id.titleTextView)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"),
				Typeface.BOLD_ITALIC);
		((Button) findViewById(R.id.startGameBtn)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		((Button) findViewById(R.id.viewCreditsBtn)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		((Button) findViewById(R.id.viewHighScoresBtn)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));

		// adding highscore view
		highScoreView = getLayoutInflater().inflate(R.layout.activity_highscores, null);
		highScoreView.setVisibility(View.INVISIBLE);
		addContentView(highScoreView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		((TextView) findViewById(R.id.highScoreTitleText)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"),
				Typeface.BOLD_ITALIC);
		((TextView) findViewById(R.id.highScoreSubTitleText)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		((Button) findViewById(R.id.highScoresBackButton)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		highScoreListView = (ListView) findViewById(R.id.highScoreListView);
		highScoreListView.setVisibility(View.INVISIBLE);
		highScoreProgressBar = (ProgressBar) findViewById(R.id.highscoreProgressBar);
		highScoreProgressBar.setVisibility(View.INVISIBLE);
		
		// adding credits view
		creditsView = getLayoutInflater().inflate(R.layout.activity_credits, null);
		creditsView.setVisibility(View.INVISIBLE);
		addContentView(creditsView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		((TextView) findViewById(R.id.creditsTitleText)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"),
				Typeface.BOLD_ITALIC);
		((TextView) findViewById(R.id.creditsText)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		((TextView) findViewById(R.id.creditsText)).setText(ResourceFactory.getInstance().getResourceStringByName("credits_text"));
		((Button) findViewById(R.id.creditsBackButton)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		((Button) findViewById(R.id.creditsDonateButton)).setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));

	}
	
	private Handler scoreReceivedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			highScoreListView = (ListView) findViewById(R.id.highScoreListView);
			ScoreListViewAdapter listAdapter = new ScoreListViewAdapter(MainActivity.this, R.layout.listview_item, scores);
			highScoreListView.setAdapter(listAdapter);
			highScoreListView.setVisibility(View.VISIBLE);
			highScoreProgressBar.setVisibility(View.INVISIBLE);
			super.handleMessage(msg);
		}
	};
	
	private Handler scoreNotReceivedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			highScoreProgressBar.setVisibility(View.INVISIBLE);
		}
	};

	@Override
	protected void onPause() {
		view.getEngine().pause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		view.getEngine().resume();
		super.onResume();
	}

	public void startGame(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
	}

	public void viewHighScores(View view) {

		scores.clear();
		highScoreListView.setVisibility(View.INVISIBLE);
		highScoreProgressBar.setVisibility(View.VISIBLE);
		
		class RetrieveScoreManager extends ScoreManager {

			@Override
			public void scoreRetrieved(int position, int score, int distance, String playerName, boolean mySelf) {
				scores.add(new ScoreListViewItem(score, playerName, distance, "1.0.0.0", position, mySelf));
			}

			@Override
			public void scoreRetrieved() {
				scoreReceivedHandler.sendEmptyMessage(0);
			}

			@Override
			public void errorRetrievingScore(String errorResponse) {
				scoreNotReceivedHandler.sendEmptyMessage(0);
			}

			public RetrieveScoreManager(MainActivity owner) {
				super(owner);
				scores.add(new ScoreListViewItem());
			}
		}

		RetrieveScoreManager scoreManager = new RetrieveScoreManager(this);
		scoreManager.readScore();

		optionsView.setVisibility(View.INVISIBLE);
		highScoreView.setVisibility(View.VISIBLE);
	}

	public void viewCredits(View view) {
		optionsView.setVisibility(View.INVISIBLE);
		creditsView.setVisibility(View.VISIBLE);
	}

	public void creditsDonate(View view) {
		String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=WFJU78GJG24PC&lc=BR&item_name=Cave%20Traveler&item_number=Cave%20Traveler&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
	
	public void highScoreBack(View view) {
		optionsView.setVisibility(View.VISIBLE);
		highScoreView.setVisibility(View.INVISIBLE);
	}

	public void creditsBack(View view) {
		optionsView.setVisibility(View.VISIBLE);
		creditsView.setVisibility(View.INVISIBLE);
	}

}
