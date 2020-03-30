package com.And1sS.game;

import java.util.*;
import com.badlogic.gdx.graphics.g2d.*;

public class AnimManager {
	private List <Anim> animations;

	public AnimManager() {
		animations = new ArrayList();
	}

	public void update() {
		for(Anim animation : animations) {
			if(animation.shouldBeDisposed() || !animation.isLoadedSucessful()) {
				animations.remove(animation);
				continue;
			} else {
				animation.update();
			}
		}
	}

	public void add(Anim animation) {
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
