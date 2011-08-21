package com.gemserk.games.gravitymadness;

import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;

public class GameResources extends LibgdxResourceBuilder {

	public static void load(ResourceManager<String> resourceManager) {
		new GameResources(resourceManager);
	}

	public GameResources(ResourceManager<String> resourceManager) {
		super(resourceManager);
		font("FpsFont", "data/fonts/purisa-18.png", "data/fonts/purisa-18.fnt", false);
		
	}

}
