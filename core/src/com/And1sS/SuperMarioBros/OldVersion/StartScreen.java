package com.And1sS.SuperMarioBros.OldVersion;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class StartScreen implements Screen {
	private Game game;

	private final com.And1sS.SuperMarioBros.OldVersion.Level currentLevel;

	private final Mario mario;

	private final SpriteBatch batch;

	private final Texture menuTexture;
	private final Texture startTexture;

	private final BitmapFont font;

	private final GlyphLayout glyphLayout;

	private final com.And1sS.SuperMarioBros.OldVersion.Button startButton;

	public StartScreen() {
		currentLevel = Level.loadFromFile("levels/level1.lvl", "images/map.png");

		mario = new Mario("images/mario.png", 600, 5 * currentLevel.getCellSize(), currentLevel.getCellSize(), currentLevel.getCellSize());

		batch = new SpriteBatch();

		menuTexture = new Texture(Gdx.files.internal("images/menu.png"));
        startTexture = new Texture(Gdx.files.internal("images/start.png"));

		font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
		font.setColor(1, 1, 1, 1);
		font.getData().setScale(Gdx.graphics.getWidth() / 1920.0f);

		glyphLayout = new GlyphLayout();

		startButton = new Button(startTexture,
		                         new Rectangle(3 * Gdx.graphics.getWidth() / 8.0f,
								               2 * currentLevel.getCellSize(),
											   Gdx.graphics.getWidth() / 4.0f,
											   Gdx.graphics.getWidth() / 8.0f),
								 new Rectangle(0, 80, 400, 240));
	}

	@Override
	public void show() {
		// TODO: Implement this method
	}

	@Override
	public void render(float p1) {
		glyphLayout.setText(font, "M");
		float textHeight = glyphLayout.height;

		batch.begin();
		currentLevel.drawMapWithOffset(batch, 0, 0,
				currentLevel.getWidth(), currentLevel.getHeight(), mario.getBody().getOffsetX());
		mario.draw(batch);
		batch.draw(menuTexture, Gdx.graphics.getWidth() / 4, (int) textHeight * 8,
				Gdx.graphics.getWidth() / 2, Gdx.graphics.getWidth() / 4);
		startButton.draw(batch);

		batch.end();

		if(startButton.justTouched())
			game.setScreen(new GameScreen(game));
	}

	@Override
	public void pause() {
		// TODO: Implement this method
	}

	@Override
	public void resume() {
		// TODO: Implement this method

	}

	@Override
	public void resize(int p1, int p2) {
		// TODO: Implement this method
	}




	@Override
	public void hide() {
		// TODO: Implement this method
	}

	@Override
	public void dispose() {
		batch.dispose();
		menuTexture.dispose();
	}
}

//public class StartScreen implements Screen {
//	private Game game;
//
//	private com.And1sS.game.Rebuild.Level currentLevel;
//
//	private com.And1sS.game.Rebuild.Player player;
//
//	private SpriteBatch batch;
//
//	private Texture menuTexture;
//	private Texture startTexture;
//
//	private BitmapFont font;
//
//	private GlyphLayout glyphLayout;
//
//	private Button startButton;
//
//	public StartScreen(Game game) {
//		this.game = game;
//
//		currentLevel = com.And1sS.game.Rebuild.Level.loadFromFile("levels/level1.lvl", "images/map.png");
//
//		player = new com.And1sS.game.Rebuild.Player(600, 5 * currentLevel.getCellSize(), currentLevel.getCellSize(), currentLevel.getCellSize());
//
//		batch = new SpriteBatch();
////
////		menuTexture = new Texture(Gdx.files.internal("images/menu.png"));
////		startTexture = new Texture(Gdx.files.internal("images/start.png"));
////
////		font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
////		font.setColor(1, 1, 1, 1);
////		font.getData().setScale(Gdx.graphics.getWidth() / 1920.0f);
////
////		glyphLayout = new GlyphLayout();
////
////		startButton = new Button(startTexture,
////				new Rectangle(3 * Gdx.graphics.getWidth() / 8.0f,
////						2 * currentLevel.getCellSize(),
////						Gdx.graphics.getWidth() / 4.0f,
////						Gdx.graphics.getWidth() / 8.0f),
////				new Rectangle(0, 80, 400, 240));
//	}
//
//	@Override
//	public void show() {
//		// TODO: Implement this method
//	}
//
//	@Override
//	public void render(float p1) {
//		batch.begin();
//		currentLevel.render(batch, player);
//		player.render(batch);
//		batch.end();
//	}
//
//	@Override
//	public void pause() {
//		// TODO: Implement this method
//	}
//
//	@Override
//	public void resume() {
//		// TODO: Implement this method
//
//	}
//
//	@Override
//	public void resize(int p1, int p2) {
//		// TODO: Implement this method
//	}
//
//	@Override
//	public void hide() {
//		// TODO: Implement this method
//	}
//
//	@Override
//	public void dispose() {
//		batch.dispose();
//		menuTexture.dispose();
//	}
//}
