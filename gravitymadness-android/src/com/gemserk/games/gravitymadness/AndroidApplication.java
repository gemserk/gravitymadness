package com.gemserk.games.gravitymadness;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.dmurph.tracking.VisitorData;
import com.gemserk.analytics.googleanalytics.android.AnalyticsStoredConfig;
import com.gemserk.games.gravitymadness.Game;

public class AndroidApplication extends com.badlogic.gdx.backends.android.AndroidApplication  {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		
		config.useGL20 = false;
		config.useAccelerometer = true;
		config.useCompass = true;
		config.useWakelock = true;
		
		initialize(new Game(), config);

	}
}