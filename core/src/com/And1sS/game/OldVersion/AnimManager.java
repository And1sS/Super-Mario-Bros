package com.And1sS.game.OldVersion;

import java.util.*;

import com.And1sS.game.OldVersion.Anim;
import com.badlogic.gdx.graphics.g2d.*;

public class AnimManager {
	private List <com.And1sS.game.OldVersion.Anim> animations;

	public AnimManager() {
		animations = new ArrayList();
	}

	public void update() {
		for(com.And1sS.game.OldVersion.Anim animation : animations) {
			if(animation.shouldBeDisposed() || !animation.isLoadedSucessful()) {
				animations.remove(animation);
				continue;
			} else {
				animation.update();
			}
		}
	}

	public void add(com.And1sS.game.OldVersion.Anim animation) {
		animations.add(animation);
	}

	public List getAnimations() { return animations; }

	public void drawAllAnims(SpriteBatch batch) {
		for(Anim animation : animations) {
			batch.draw(animation.getCurrentRegion(),
			           animation.getX(),
					   animation.getY(), 
					   animation.getDrawableWidth(),
					   animation.getDrawableHeight());
		}
	}
}
