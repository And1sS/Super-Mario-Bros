package com.And1sS.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;

public class Enemy {
	private Anim animation;

	private RigidBody body;

	private boolean shouldBeDeleted = false;
	private boolean died = false;
	private boolean shouldDrop = false;
	private boolean flipped = false;
	
	private float timer = 2;

	public Enemy(float x, float y, float width, float height, Anim animation) {
		this.animation = animation;

		body = new RigidBody(x, y, width, height);
		body.setVelocityX(500 * Gdx.graphics.getWidth() / 1920.0f);
	}

	public void update(Level level, Player p) {
		body.setOnGround(false);

		if(Gdx.graphics.getDeltaTime() < 0.025) {
			//updating X
			body.setX(body.getX() + body.getVelocityX() * Level.SPEED * Gdx.graphics.getDeltaTime());
			if(!shouldDrop) {
				checkCollision(level, 0);
			}

			//updating Y
			body.setY(body.getY() + body.getVelocityY()  * Level.SPEED * Gdx.graphics.getDeltaTime());
			if (!shouldDrop) {
				checkCollision(level, 1);
			}

			body.getBounds().set(body.getX(), body.getY(),
					body.getWidth(), body.getHeight());
		}

		//updating velocityY
		if (!body.isOnGround()) {
			body.setVelocityY(body.getVelocityY()
					+ Level.GRAVITATIONAL_ACCELERATION * Level.SPEED * Gdx.graphics.getDeltaTime());
		}

		if (!died) {
			animation.update();
		} else {
			timer -= Gdx.graphics.getDeltaTime();
			
			if(timer <= 0) {
				shouldBeDeleted = true;
			}
		}
	}

	public void checkCollision(Level level, int dir) {
		try {
			for(int i = (int) (body.getY()) / level.getCellSize(); i < (body.getY() + body.getHeight()) / level.getCellSize(); i++) {
				for(int j = (int) (body.getX()) / level.getCellSize(); j < (body.getX() + body.getWidth()) / level.getCellSize(); j++) {
					switch(level.getCell(j, i)) {

						case 0:
							break;

						case 1:
						case 10:
						case 12:

						case 14: 
						case 15:
						case 20:
						case 21:
						case 22:
							//check X
							if(body.getVelocityX() > 0 && dir == 0) {
								body.setX(j * level.getCellSize() - body.getWidth());
								body.setVelocityX(-1 * body.getVelocityX());
							} else if(body.getVelocityX() < 0 && dir == 0) {
								body.setX((j + 1) * level.getCellSize());
								body.setVelocityX(-1 * body.getVelocityX());
							}

							//check Y
							if(body.getVelocityY() > 0 && dir == 1) {
								body.setY(i * level.getCellSize() - body.getHeight());
								body.setVelocityY(0);
								body.setOnGround(true);
							}
							break;
					}	
				}
			}
		} catch(Exception e) {}
	}

	public void draw(SpriteBatch batch, float offsetX) {
		if (!flipped) {
			batch.draw(animation.getCurrentRegion(),
					body.getX() - offsetX,Gdx.graphics.getHeight() - body.getY() - body.getHeight(),
					body.getWidth(), body.getHeight());
		}  else {
			batch.draw(animation.getCurrentRegion(),
					body.getX() - offsetX, Gdx.graphics.getHeight() - body.getY(),
					body.getWidth(), -body.getHeight());
		}
	}

	public RigidBody getBody() { return body; }

	public Anim getAnim() { return animation; }

	public void kill() { died = true; }

	public void delete() { shouldBeDeleted = true; }

	public void drop() { shouldDrop = true; }
	
	public void flip() { flipped = true; }
	
	public boolean isDied() { return died; }
	
	public boolean shouldBeDeleted() { return shouldBeDeleted; }

}
