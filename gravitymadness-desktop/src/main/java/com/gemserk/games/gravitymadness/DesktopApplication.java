package com.gemserk.games.gravitymadness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gemserk.games.gravitymadness.Game;

public class DesktopApplication {

	protected static final Logger logger = LoggerFactory.getLogger(DesktopApplication.class);

	public static void main(String[] argv) {

		System.out.println(System.getProperty("java.version"));

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 480;
		// config.width = 320;
		// config.height = 240;
		// config.width = 1024;
		// config.height = 768;
		config.fullscreen = false;
		config.title = "gravitymadness";
		config.useGL20 = false;
		config.useCPUSynch = true;
		config.forceExit = true;

		new LwjglApplication(new Game() {
			@Override
			public void create() {
				super.create();
			}
		}, config);
	}

}
