package com.And1sS.game;

public class Goomba extends Enemy {
	
	public Goomba(float x, float y, float width, float height) {
		super(x, y, width, height, new Anim("images/enemies.png", 2, 0, 16, 16, 16, -1, -1, -1, -1, (double)5, true));
	}

	@Override
	public void update(Level level, Player player) {
		// TODO: Implement this method
		super.update(level, player);

		if(getBody().getBounds().overlaps(player.getBody().getBounds()) &&
		   		!isDied() && !player.isDied()) {
			if(player.getBody().getVelocityY() > 0 && !player.getBody().isOnGround()) {

				player.getBody().setVelocityY(player.getBody().getVelocityY() * -0.8f);
				player.setScore(player.getScore() + 100);

				smashGoomba();

			} else if(player.getType() != Player.MarioType.MARIO) {
				player.mario(level);
				player.startLowInvincibility();
			} else if(player.getType() == Player.MarioType.MARIO
			 && !player.isLowInvincibility()) {
				player.die();
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
