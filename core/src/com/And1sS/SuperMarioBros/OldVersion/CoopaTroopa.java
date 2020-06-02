package com.And1sS.SuperMarioBros.OldVersion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CoopaTroopa extends Enemy {
	private boolean movingLeft;

	public enum Type {
		TURTLE, SHELL, FLYING_TURTLE
	}

	private Type type;

	public CoopaTroopa(float x, float y, float width, float height) {
		super(x, y, width, height,
				new Anim("images/enemies.png", 2, 96, 7, 16, 25, -1, -1, -1, -1, 5, true));
		
		type = Type.TURTLE;
	}

	@Override
	public void update(Level level, Mario mario) {
		super.update(level, mario);

		if (getBody().getVelocityX() > 0) {
			movingLeft = false;
		} else if (getBody().getVelocityX() < 0) {
			movingLeft = true;
		}

		if (getBody().getBounds().overlaps(mario.getBody().getBounds()) &&
			!isDied() && !mario.isDied()) {
			if (type == Type.TURTLE) {
				if (mario.getBody().getVelocityY() > 0 && !mario.getBody().isOnGround()) {

					mario.getBody().setVelocityY(mario.getBody().getVelocityY() * -0.8f);
					mario.setScore(mario.getScore() + 100);
              
					shell();
				}
				else if (mario.getType() != Mario.MarioType.MARIO) {
					mario.mario(level);
					mario.startLowInvincibility();
				}
				else if (mario.getType() == Mario.MarioType.MARIO
						 && !mario.isLowInvincibility() && !mario.isDied()) {
					mario.die();
				}
			} else if(type == Type.SHELL) {
				if(getBody().getVelocityX() == 0 && mario.getBody().getVelocityY() == 0) {
					if(mario.getBody().getVelocityX() > 0) {
						getBody().setVelocityX(1700 * Gdx.graphics.getWidth() / 1920 * Level.SPEED);
						getBody().setX(mario.getBody().getX() + mario.getBody().getWidth());
					} else {
						getBody().setVelocityX(-1700 * Gdx.graphics.getWidth() / 1920 * Level.SPEED);
						getBody().setX(mario.getBody().getX() - getBody().getWidth());
					}
				} else if(getBody().getVelocityX() != 0 && mario.getBody().getVelocityY() <= 0 ) {
					mario.die();
				} else if(mario.getBody().getVelocityY() > 0 && !mario.getBody().isOnGround()) {
					mario.getBody().setVelocityY(mario.getBody().getVelocityY() * -0.8f);
					
					getBody().setVelocityX(0);
					
					kill();
					drop();
				}
			}
		}
	}

	@Override
	public void draw(SpriteBatch batch, float offsetX) {
		// TODO: Implement this method
		if (movingLeft)
			batch.draw(getAnim().getCurrentRegion(),
					getBody().getX() - offsetX,
					Gdx.graphics.getHeight() - getBody().getY() - getBody().getHeight(),
					getBody().getWidth(),
					getBody().getHeight());
		else
			batch.draw(getAnim().getCurrentRegion(),
					getBody().getX() + getBody().getWidth() - offsetX,
					Gdx.graphics.getHeight() - getBody().getY() - getBody().getHeight(),
					-getBody().getWidth(),
					getBody().getHeight());
	}
	
	private void shell() {
		type = Type.SHELL;

		getAnim().setSpeed(0);
		getAnim().setCurrentFrame(0);
		getAnim().setTextureX(160);
		getAnim().setTextureY(16);
		getAnim().setTextureHeight(14);
		
		getBody().setHeight(getBody().getHeight() / 2);
		getBody().setVelocityX(0);
	}
}
