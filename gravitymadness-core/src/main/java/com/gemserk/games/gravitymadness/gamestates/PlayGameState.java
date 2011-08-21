package com.gemserk.games.gravitymadness.gamestates;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.EntityBuilder;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.events.EventListenerManagerImpl;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.systems.ContainerSystem;
import com.gemserk.commons.artemis.systems.OwnerSystem;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.RenderableSystem;
import com.gemserk.commons.artemis.systems.ScriptSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.systems.TagSystem;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.Box2DCustomDebugRenderer;
import com.gemserk.commons.gdx.box2d.Contact;
import com.gemserk.commons.gdx.box2d.JointBuilder;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.graphics.ImmediateModeRendererUtils;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.gravitymadness.Game;
import com.gemserk.games.gravitymadness.Layers;
import com.gemserk.games.gravitymadness.Tags;
import com.gemserk.games.gravitymadness.systems.CustomGravitySystem;
import com.gemserk.games.gravitymadness.templates.EntityTemplates;
import com.gemserk.resources.ResourceManager;

public class PlayGameState extends GameStateImpl {

	private final Game game;
	private ResourceManager<String> resourceManager;
	private SpriteBatch spriteBatch;
	private EventListenerManagerImpl eventManager;
	private World physicsWorld;
	private JointBuilder jointBuilder;
	private Libgdx2dCameraTransformImpl worldCamera;
	private Libgdx2dCameraTransformImpl guiCamera;
	private RenderLayers renderLayers;
	private com.artemis.World world;
	private EntityFactoryImpl entityFactory;
	private WorldWrapper worldWrapper;
	private EntityBuilder entityBuilder;
	private Box2DCustomDebugRenderer box2dCustomDebugRenderer;
	private com.gemserk.games.gravitymadness.templates.EntityTemplates entityTemplates;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;
	private CustomGravitySystem customGravitySystem;
	protected ButtonMonitor upButton;
	protected ButtonMonitor downButton;

	public PlayGameState(Game game) {
		this.game = game;
	}

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void init() {
		super.init();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		float centerX = width / 2;
		float centerY = height / 2;
		spriteBatch = new SpriteBatch();

		eventManager = new EventListenerManagerImpl();

		physicsWorld = new World(new Vector2(0, 0), false);

		jointBuilder = new JointBuilder(physicsWorld);

		worldCamera = new Libgdx2dCameraTransformImpl();
		worldCamera.center(centerX, centerY);
		float zoom = 100;
		worldCamera.zoom(zoom);
		worldCamera.move(centerX / zoom, centerY / zoom);

		guiCamera = new Libgdx2dCameraTransformImpl();

		Libgdx2dCamera backgroundLayerCamera = new Libgdx2dCameraTransformImpl();

		renderLayers = new RenderLayers();

		renderLayers.add(Layers.Background, new RenderLayerSpriteBatchImpl(-10000, -500, backgroundLayerCamera, spriteBatch));
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-500, 100, worldCamera));

		world = new com.artemis.World();
		entityFactory = new EntityFactoryImpl(world);
		worldWrapper = new WorldWrapper(world);

		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));
		customGravitySystem = new CustomGravitySystem();
		worldWrapper.addUpdateSystem(customGravitySystem);
		worldWrapper.addUpdateSystem(new ScriptSystem());
		worldWrapper.addUpdateSystem(new TagSystem());
		worldWrapper.addUpdateSystem(new ContainerSystem());
		worldWrapper.addUpdateSystem(new OwnerSystem());

		worldWrapper.addRenderSystem(new SpriteUpdateSystem());
		worldWrapper.addRenderSystem(new RenderableSystem(renderLayers));

		worldWrapper.init();

		entityBuilder = new EntityBuilder(world);

		box2dCustomDebugRenderer = new Box2DCustomDebugRenderer((Libgdx2dCameraTransformImpl) worldCamera, physicsWorld);
		entityTemplates = new EntityTemplates(physicsWorld, world, resourceManager, entityBuilder, entityFactory, eventManager);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKeys("pause", Keys.BACK, Keys.ESCAPE);
				monitorKeys("switchControls", Keys.MENU, Keys.R);
				monitorKey("left", Keys.LEFT);
				monitorKey("right", Keys.RIGHT);
				monitorKey("up", Keys.UP);
				monitorKey("down", Keys.DOWN);
			}
		};

		loadLevel();
	}

	private void loadLevel() {
		customGravitySystem.getGravity().set(0, -10);
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(0, 0, 8, 0.1f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(0, 4.8f, 8, 0.1f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(0, 0, 0.1f, 4.8f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(8, 0, 0.1f, 4.8f, 0)));

		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(4.510000f, 0.060000f, 0.550000f, 1.420000f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(3.010000f, 1.120000f, 1.500000f, 0.350000f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(1.130000f, 0.080000f, 0.440000f, 1.430000f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(0.070000f, 2.100000f, 1.410000f, 0.420000f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(0.680000f, 3.090000f, 2.520000f, 0.420000f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(1.710000f, 4.040000f, 0.380000f, 0.730000f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(4.380000f, 2.650000f, 1.090000f, 1.530000f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(6.290000f, 2.340000f, 0.620000f, 2.410000f, 0)));
		entityFactory.instantiate(entityTemplates.groundTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(6.320000f, 0.040000f, 0.710000f, 1.260000f, 0)));

		entityFactory.instantiate(entityTemplates.playerTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(4, 1, 1, 1, 0)));

		entityBuilder.component(new ScriptComponent(new ScriptJavaImpl() {
			Class<PhysicsComponent> physicscomponentclass = PhysicsComponent.class;

			@Override
			public void update(com.artemis.World world, Entity e) {
				Entity entity = world.getTagManager().getEntity(Tags.PLAYER_TAG);
				PhysicsComponent physicsComponent = entity.getComponent(physicscomponentclass);
				Contact contact = physicsComponent.getContact();
				if (contact.isInContact()) {
					// Body body = physicsComponent.getBody();
					// Vector2 contactForce = contact.getNormal().cpy().mul(10);
					// body.applyForce(contactForce, body.getPosition());
				}

			}

			@Override
			public void init(com.artemis.World world, Entity e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose(com.artemis.World world, Entity e) {
				// TODO Auto-generated method stub

			}
		})).build();

		entityBuilder.component(new ScriptComponent(new ScriptJavaImpl() {
			Class<PhysicsComponent> physicscomponentclass = PhysicsComponent.class;
			private ButtonMonitor leftButton;
			private ButtonMonitor rightButton;

			private Vector2 direction = new Vector2();

			@Override
			public void update(com.artemis.World world, Entity e) {
				direction.set(0, 0);
				if (upButton.isPressed())
					direction.add(0, 1);
				if (downButton.isPressed())
					direction.add(0, -1);
				if (leftButton.isPressed())
					direction.add(-1, 0);
				if (rightButton.isPressed())
					direction.add(1, 0);

				if (direction.len2() != 0) {
					Vector2 gravity = customGravitySystem.getGravity();
					gravity.set(direction.mul(gravity.len()));
				}
			}

			@Override
			public void init(com.artemis.World world, Entity e) {
				leftButton = inputDevicesMonitor.getButton("left");
				rightButton = inputDevicesMonitor.getButton("right");
				upButton = inputDevicesMonitor.getButton("up");
				downButton = inputDevicesMonitor.getButton("down");
			}

			@Override
			public void dispose(com.artemis.World world, Entity e) {
				// TODO Auto-generated method stub

			}
		})).build();
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		worldWrapper.render();

		// worldCamera.apply();

		ImmediateModeRendererUtils.drawHorizontalAxis(Gdx.graphics.getHeight() / 2f, Color.RED);
		ImmediateModeRendererUtils.drawVerticalAxis(Gdx.graphics.getWidth() / 2f, Color.RED);

		// if (Game.isShowBox2dDebug())
		box2dCustomDebugRenderer.render();

		// guiCamera.apply(spriteBatch);
		// spriteBatch.begin();
		// container.draw(spriteBatch);
		// spriteBatch.end();
	}

	@Override
	public void update() {

		inputDevicesMonitor.update();
		Synchronizers.synchronize(getDelta());
		worldWrapper.update(getDeltaInMs());
	}

	@Override
	public void dispose() {
		worldWrapper.dispose();
		spriteBatch.dispose();
		physicsWorld.dispose();
	}
}
