package com.And1sS.game;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.*;

public class Button {
	private Rectangle bounds;
	private Rectangle drawableRegion;

	private Texture texture;
	TextureRegion region;

	public Button(Texture texture, Rectangle bounds, Rectangle drawableRegion) {
		this.texture = texture;

		this.bounds = bounds;
		this.drawableRegion = drawableRegion;

		region = new TextureRegion(texture);

	}

	public boolean isTouched() {
		for(int i = 0; i < 5; i++) {
			boolean inside = inside(Gdx.input.getX(i),
					Gdx.graphics.getHeight() - Gdx.input.getY(i), bounds);

			if(Gdx.input.isTouched(i) && inside) {
				return true;
			}
		}

		return false;
	}
	
	public boolean justTouched() {
		for(int i = 0; i < 5; i++) {
			boolean inside = inside(Gdx.input.getX(i),
					Gdx.graphics.getHeight() - Gdx.input.getY(i), bounds);

			if(Gdx.input.justTouched() && inside) {
				return true;
			}
		}

		return false;
	}

	private boolean inside(int x, int y, Rectangle bounds) {
		return bounds.contains(x, y);
	}

	public void draw(SpriteBatch batch) {
		// setting region here cuz it could be updated
		region.setRegion((int) drawableRegion.getX(), (int) drawableRegion.getY(),
				(int) drawableRegion.getWidth(), (int) drawableRegion.getHeight());

		batch.draw(region, bounds.getX(), bounds.getY(),
				   bounds.getWidth(), bounds.getHeight());
	}

	public Rectangle getDrawableRegion() { return drawableRegion; }
}
