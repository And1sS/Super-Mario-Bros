package com.And1sS.game.OldVersion;

import com.And1sS.game.OldVersion.Anim;

public class AnimPresets {
	public static com.And1sS.game.OldVersion.Anim getCoin(int x, int y, int width, int height) {
	    com.And1sS.game.OldVersion.Anim temp = new Anim("images/objects.png", 4,
				0, 80, 16, 1,
				x, y, width, height, (double)5 , false);
		temp.setSpeedY(100);
		return temp;
	}
}
