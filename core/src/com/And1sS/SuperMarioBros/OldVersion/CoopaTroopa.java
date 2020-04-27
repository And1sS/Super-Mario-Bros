package com.And1sS.SuperMarioBros.OldVersion;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.*;

public class CoopaTroopa extends Enemy {
	private boolean movingLeft;

	public enum Type {
		TURTLE, SHELL, FLYING_TURTLE
	}

	private Type type;

	public CoopaTroopa(float x, float y, float width, float height) {
		super(x, y, width, height, new Anim("images/enemies.png", 2, 96, 7, 16, 25, -1, -1, -1, -1, (double)5, true));
		
		type = Type.TURTLE;
	}

	@Override
	public void update(Level level, Player player) {
		super.update(level, player);

		if (getBody().getVelocityX() > 0) {
			movingLeft = false;
		} else if (getBody().getVelocityX() < 0) {
			movingLeft = true;
		}

		if (getBody().getBounds().overlaps(player.getBody().getBounds()) &&
			!isDied() && !player.isDied()) {
			if (type == Type.TURTLE) {
				if (player.getBody().getVelocityY() > 0 && !player.getBody().isOnGround()) {

					player.getBody().setVelocityY(player.getBody().getVelocityY() * -0.8f);
					player.setScore(player.getScore() + 100);
              
					shell();
				}
				else if (player.getType() != Player.MarioType.MARIO) {
					player.mario(level);
					player.startLowInvincibility();
				}
				else if (player.getType() == Player.MarioType.MARIO
						 && !player.isLowInvincibility() && !player.isDied()) {
					player.die();
				}
			} else if(type == Type.SHELL) {
				if(getBody().getVelocityX() == 0 && player.getBody().getVelocityY() == 0) {
					if(player.getBody().getVelocityX() > 0) {
						getBody().setVelocityX(1700 * Gdx.graphics.getWidth() / 1920 * Level.SPEED);
						getBody().setX(player.getBody().getX() + player.getBody().getWidth());
					} else {
						getBody().setVelocityX(-1700 * Gdx.graphics.getWidth() / 1920 * Level.SPEED);
						getBody().setX(player.getBody().getX() - getBody().getWidth());
					}
				} else if(getBody().getVelocityX() != 0 && player.getBody().getVelocityY() <= 0 ) {
					player.die();
				} else if(player.getBody().getVelocityY() > 0 && !player.getBody().isOnGround()) {
					player.getBody().setVelocityY(player.getBody().getVelocityY() * -0.8f);
					
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
					   (float) (getBody().getX() - offsetX),
					   (float) (Gdx.graphics.getHeight() - getBody().getY() - getBody().getHeight()),
					   (float) getBody().getWidth(),
					   (float) getBody().getHeight());
		else
			batch.draw(getAnim().getCurrentRegion(),
					   (float) (getBody().getX() + getBody().getWidth() - offsetX),
					   (float) (Gdx.graphics.getHeight() - getBody().getY() - getBody().getHeight()),
					   (float) -getBody().getWidth(),
					   (float) getBody().getHeight());
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
