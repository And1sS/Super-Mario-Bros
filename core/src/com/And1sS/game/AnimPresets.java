package com.And1sS.game;

public class AnimPresets {
	public static Anim getCoin(int x, int y, int width, int height) {
	    Anim temp = new Anim("images/objects.png", 4,
				0, 80, 16, 1,
				x, y, width, height, (double)5 , false);
		temp.setSpeedY(100);
		return temp;
	}
}
