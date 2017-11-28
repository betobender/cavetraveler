package com.cave.traveler;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {

	private OpenGLGameView view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new OpenGLGameView(this);
        setContentView(view);
    }
    
	@Override
	protected void onDestroy() {
		view.getEngine().stop();
		super.onDestroy();
	}
}
