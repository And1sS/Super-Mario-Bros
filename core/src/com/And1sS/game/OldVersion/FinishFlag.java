package com.And1sS.game.OldVersion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;

public class FinishFlag {
	private double x;
	private double y;
	private double width;
	private double height;
	
	private Rectangle bounds;
	
	private Texture texture;
	
	public FinishFlag(Level level) {
		texture = new Texture(Gdx.files.internal(""));
		
		x = 250 * level.getCellSize();
		y = 2 * level.getCellSize();
		
		width = level.getCellSize();
		height = 13 * level.getCellSize();
	}
	
	public void checkFinish(Player player) {
		if(player.getBody().getBounds().overlaps(bounds));
	}
	
	public void draw(SpriteBatch batch) {
		batch.draw(texture, (float)x, (float)y, (float)width, (float)height);
	}
}
