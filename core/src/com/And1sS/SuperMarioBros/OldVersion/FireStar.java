package com.And1sS.SuperMarioBros.OldVersion;

public class FireStar extends Enemy
{
	public FireStar(float x, float y, float width, float height) {
		super(x, y, width, height, new Anim("images/objects.png", 4, 0, 48, 16, 16, -1, -1, -1, -1, 15, true));
	}

	@Override
	public void update(Level level, Mario mario) {
		super.update(level, mario);

		if(mario.getBody().getBounds().overlaps(getBody().getBounds())) {
			delete();
			mario.fireMario(level);
		}
	}
}
