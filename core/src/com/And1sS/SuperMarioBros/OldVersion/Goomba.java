package com.And1sS.SuperMarioBros.OldVersion;

public class Goomba extends Enemy {
	
	public Goomba(float x, float y, float width, float height) {
		super(x, y, width, height, new Anim("images/enemies.png", 2, 0, 16, 16, 16, -1, -1, -1, -1, 5, true));
	}

	@Override
	public void update(Level level, Mario mario) {
		// TODO: Implement this method
		super.update(level, mario);

		if(getBody().getBounds().overlaps(mario.getBody().getBounds()) &&
		   		!isDied() && !mario.isDied()) {
			if(mario.getBody().getVelocityY() > 0 && !mario.getBody().isOnGround()) {

				mario.getBody().setVelocityY(mario.getBody().getVelocityY() * -0.8f);
				mario.setScore(mario.getScore() + 100);

				smashGoomba();

			} else if(mario.getType() != Mario.MarioType.MARIO) {
				mario.mario(level);
				mario.startLowInvincibility();
			} else if(mario.getType() == Mario.MarioType.MARIO
			 && !mario.isLowInvincibility()) {
				mario.die();
            }
		}
	}

	private void smashGoomba() {
		getBody().setVelocityX(0);
		getBody().setHeight(getBody().getHeight() / 2);
		getAnim().setSpeed(0);
		getBody().setY(getBody().getY() + getBody().getHeight());
		kill();
	}
}
