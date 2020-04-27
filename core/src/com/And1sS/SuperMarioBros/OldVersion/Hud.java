package com.And1sS.SuperMarioBros.OldVersion;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.*;

public class Hud {
	public final String PLAYER = "MARIO";
	public final String WORLD = "WORLD";
	public final String TIME = "TIME";

	private BitmapFont font;
	private GlyphLayout glyphLayout;

	public Hud() {
		font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
		font.getData().setScale(Gdx.graphics.getWidth() / 1920.0f);

		glyphLayout = new GlyphLayout();
	}

	public void drawHud(SpriteBatch batch, int score, String worldName, int timeLeft) {
		glyphLayout.setText(font, PLAYER);
		int y = (int) (glyphLayout.height / 2);
		float playerTextWidth = glyphLayout.width;

		font.draw(batch, PLAYER, y * 8, Gdx.graphics.getHeight() - y);

		glyphLayout.setText(font, WORLD);
		font.draw(batch, WORLD, (Gdx.graphics.getWidth() - glyphLayout.width) / 2, Gdx.graphics.getHeight() - y);

		glyphLayout.setText(font, TIME);
		font.draw(batch, TIME, Gdx.graphics.getWidth() - 8 * y - glyphLayout.width, Gdx.graphics.getHeight() - y);

		glyphLayout.setText(font, String.valueOf(score));
		font.draw(batch, String.valueOf(score), y * 8 + (playerTextWidth - glyphLayout.width) / 2, Gdx.graphics.getHeight() - y * 4);

		glyphLayout.setText(font, worldName);
		font.draw(batch, worldName, (Gdx.graphics.getWidth() - glyphLayout.width) / 2, Gdx.graphics.getHeight() - y * 4);

		glyphLayout.setText(font, TIME);
		float timeTextWidth = glyphLayout.width;
		glyphLayout.setText(font, String.valueOf(timeLeft));
		float timeLeftTextWidth = glyphLayout.width;
		font.draw(batch, String.valueOf(timeLeft),
				Gdx.graphics.getWidth() - 8 * y - (timeTextWidth + timeLeftTextWidth / 2),
				Gdx.graphics.getHeight() - y * 4);
	}
}
