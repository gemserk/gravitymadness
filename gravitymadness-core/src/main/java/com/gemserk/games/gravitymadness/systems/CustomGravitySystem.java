package com.gemserk.games.gravitymadness.systems;

import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.PhysicsComponent;

public class CustomGravitySystem extends EntitySystem{

	private Vector2 gravity = new Vector2(0,0);
	private Vector2 bodyGravity = new Vector2(0,0);

	public CustomGravitySystem() {
		super(PhysicsComponent.class);
	}
	
	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
			Body body = physicsComponent.getBody();
			bodyGravity.set(gravity);
			bodyGravity.mul(body.getMass());
			body.applyForce(bodyGravity, body.getPosition());
		}
	}

	@Override
	protected boolean checkProcessing() {
		return true;
	}
	
	public Vector2 getGravity() {
		return gravity;
	}

}
