package com.And1sS.game;

public class FireStar extends Enemy
{
	public FireStar(float x, float y, float width, float height) {
		super(x, y, width, height, new Anim("images/objects.png", 4, 0, 48, 16, 16, -1, -1, -1, -1, (double)15, true));
	}

	@Override
	public void update(Level level, Player player) {
		super.update(level, player);

		if(player.getBody().getBounds().overlaps(getBody().getBounds())) {
			delete();
			player.fireMario(level);
		}
	}
}
