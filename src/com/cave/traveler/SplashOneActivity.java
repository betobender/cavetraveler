package com.cave.traveler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashOneActivity extends Activity {

	private static final int SPLASH_DISPLAY_TIME = 4000; // splash screen delay
	private boolean showMainActivity = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_one);

		ResourceFactory.getInstance().setResourceContext(this);

		TextView titleTextView1 = (TextView) findViewById(R.id.splashTextLine1);
		titleTextView1.setText("Lazy Goat Games");
		titleTextView1.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));
		
		TextView titleTextView2 = (TextView) findViewById(R.id.splashTextLine2);
		titleTextView2.setText("presents...");
		titleTextView2.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"));

		new Handler().postDelayed(new Runnable() {
			public void run() {

				if (showMainActivity) {

					Intent intent = new Intent();
					intent.setClass(SplashOneActivity.this, MainActivity.class);

					SplashOneActivity.this.startActivity(intent);
					SplashOneActivity.this.finish();

					// transition from splash to main menu
					overridePendingTransition(R.anim.activityfadein, R.anim.activityfadeout);
				}
			}
		}, SPLASH_DISPLAY_TIME);
	}

	@Override
	protected void onStop() {
		super.onStop();
		showMainActivity = false;
	}

}
