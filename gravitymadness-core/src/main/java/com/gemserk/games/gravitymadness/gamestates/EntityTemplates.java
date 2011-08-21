package com.gemserk.games.gravitymadness.gamestates;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.artemis.EntityBuilder;
import com.gemserk.commons.artemis.events.EventListenerManagerImpl;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.graphics.Mesh2dBuilder;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.resources.ResourceManager;

public class EntityTemplates {
	
	private final BodyBuilder bodyBuilder;
	private final ResourceManager<String> resourceManager;
	private final EntityBuilder entityBuilder;
	private final Mesh2dBuilder mesh2dBuilder;
	private final World physicsWorld;
	private final EntityFactory entityFactory;

	private final Parameters parameters = new ParametersWrapper();
	private final EventManager eventManager;
	private final com.artemis.World world;
	
	public EntityTemplates(World physicsWorld, com.artemis.World world, ResourceManager<String> resourceManager, EntityBuilder entityBuilder, EntityFactoryImpl entityFactory, EventListenerManagerImpl eventManager) {
		this.physicsWorld = physicsWorld;
		this.world = world;
		// TODO Auto-generated constructor stub
		this.resourceManager = resourceManager;
		this.entityBuilder = entityBuilder;
		this.entityFactory = entityFactory;
		this.eventManager = eventManager;
		this.bodyBuilder = new BodyBuilder(physicsWorld);
		this.mesh2dBuilder = new Mesh2dBuilder();
	}

	
	private EntityTemplate groundTemplate = new EntityTemplateImpl() {
		
		@Override
		public void apply(Entity entity) {
			
		}
	};
	
	
}
