package com.gemserk.games.gravitymadness.templates;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.artemis.EntityBuilder;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.PhysicsImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.gdx.graphics.Mesh2dBuilder;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.gravitymadness.Tags;
import com.gemserk.resources.ResourceManager;

public class EntityTemplates {

	private final BodyBuilder bodyBuilder;
	private final ResourceManager<String> resourceManager;
	private final EntityBuilder entityBuilder;
	private final Mesh2dBuilder mesh2dBuilder;
	private final World physicsWorld;
	private final EntityFactory entityFactory;

	private final Parameters parameters = new ParametersWrapper();
	private final com.artemis.World world;

	public EntityTemplates(World physicsWorld, com.artemis.World world, ResourceManager<String> resourceManager, EntityBuilder entityBuilder, EntityFactoryImpl entityFactory) {
		this.physicsWorld = physicsWorld;
		this.world = world;
		// TODO Auto-generated constructor stub
		this.resourceManager = resourceManager;
		this.entityBuilder = entityBuilder;
		this.entityFactory = entityFactory;
		this.bodyBuilder = new BodyBuilder(physicsWorld);
		this.mesh2dBuilder = new Mesh2dBuilder();
	}

	public final EntityTemplate groundTemplate = new EntityTemplateImpl() {

		@Override
		public void apply(Entity entity) {
			Spatial spatial = parameters.get("spatial");
			Body body = boxBody(spatial.getX(), spatial.getY(), spatial.getWidth(), spatial.getHeight(), entity);

			entity.addComponent(new PhysicsComponent(new PhysicsImpl(body)));
			entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial.getWidth(), spatial.getHeight())));
		}
	};

	private Body boxBody(float x, float y, float width, float height, Object userdata) {
		float halfwidth = width/2;
		float halfheight = height/2;
		float centerX = x + halfwidth;
		float centerY = y + halfheight;

		Body body = bodyBuilder //
				.fixedRotation() //
				.userData(userdata) //
				.position(centerX, centerY) //
				.type(BodyType.StaticBody) //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.boxShape(halfwidth, halfheight)//
						.density(1f) //
						.build()) //
				.build();

		return body;
	}

	public final EntityTemplate playerTemplate = new EntityTemplateImpl() {

		@Override
		public void apply(Entity entity) {

			Spatial spatial = parameters.get("spatial");

			Body body = bodyBuilder //
					.fixedRotation() //
					.userData(entity) //
					.position(spatial.getX(), spatial.getY()) //
					.type(BodyType.DynamicBody) //
					.fixture(bodyBuilder.fixtureDefBuilder() //
							.circleShape(0.1f)//
							.density(1f) //
							.build()) //
					.fixture(bodyBuilder.fixtureDefBuilder()//
							.sensor()//
							.circleShape(new Vector2(0,-0.1f),0.01f)//
							.build(), "feet")//
					.build();

			entity.addComponent(new TagComponent(Tags.PLAYER_TAG));
			entity.addComponent(new PhysicsComponent(new PhysicsImpl(body)));
			entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, 1f, 1f)));
		}
	};

}
