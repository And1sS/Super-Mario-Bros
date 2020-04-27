package com.And1sS.SuperMarioBros.OldVersion;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

public class GameOverScreen implements Screen {
	
	Game game;
	
	SpriteBatch batch;
	
	BitmapFont font;
	
	double timer;
	
	public GameOverScreen(Game game) {
		this.game = game;
		
		batch = new SpriteBatch();
		
		font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
		font.setColor(Color.WHITE);
		
		timer = 2;
	}

	@Override
	public void show() {
		// TODO: Implement this method
	}

	@Override
	public void render(float p1) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		GlyphLayout glyphLayout = new GlyphLayout();
		glyphLayout.setText(font, "GAME OVER");

		batch.begin();
		font.draw(batch, "GAME OVER", 
				  (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
				  (Gdx.graphics.getHeight() + glyphLayout.width) / 2);
		batch.end();
		
		updateTimer();
	}

	@Override
	public void resize(int p1, int p2) {
		// TODO: Implement this method
	}

	@Override
	public void resume() {
		// TODO: Implement this method
	}

	@Override
	public void pause() {
		// TODO: Implement this method
	}

	@Override
	public void dispose() {
		// TODO: Implement this method
	}

	@Override
	public void hide() {
		// TODO: Implement this method
	}
	
	private void updateTimer() {
		timer -= Gdx.graphics.getDeltaTime();
		
		if(timer <= 0)
			game.setScreen(new StartScreen(game));
	}
}
