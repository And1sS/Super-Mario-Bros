package com.And1sS.game;

public class LifeMushroom extends Enemy {

	public LifeMushroom(float x, float y, float width, float height) {
		super(x, y, width, height, new Anim("images/objects.png", 1, 0, 0, 16, 16, -1, -1, -1, -1, (double)0, true));
	}

	@Override
	public void update(Level level, Player player) {
		// TODO: Implement this method
		super.update(level, player);
		if(player.getBody().getBounds().overlaps(getBody().getBounds())) {
			delete();
			player.superMario(level);
		}
	}
}
