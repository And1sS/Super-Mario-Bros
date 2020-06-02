package com.And1sS.SuperMarioBros.OldVersion;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PauseScreen implements Screen
{
    private final Game game;
	
	private final Screen gameScreen;
	
	private final SpriteBatch batch;
	
	private final Texture buttonAtlas;
	
	private final com.And1sS.SuperMarioBros.OldVersion.Button resumeButton;
	private final com.And1sS.SuperMarioBros.OldVersion.Button restartButton;
	private final com.And1sS.SuperMarioBros.OldVersion.Button mainMenuButton;
	private final com.And1sS.SuperMarioBros.OldVersion.Button exitButton;
	
	public PauseScreen(Game game, com.And1sS.SuperMarioBros.OldVersion.GameScreen gameScreen) {
		this.game = game;
		this.gameScreen = gameScreen;
		
		batch = new SpriteBatch();
		
		buttonAtlas = new Texture(Gdx.files.internal("images/pauseMenu.png"));
		
		resumeButton = new com.And1sS.SuperMarioBros.OldVersion.Button(buttonAtlas,
		                        new Rectangle(new Rectangle(Gdx.graphics.getWidth() / 9, 
								              2 * Gdx.graphics.getHeight() / 3 - Gdx.graphics.getWidth() / 36, 
											  Gdx.graphics.getWidth() / 3, 
											  Gdx.graphics.getWidth() / 12)), 
		                        new Rectangle(0, 0, 134, 31));
								
	    restartButton = new com.And1sS.SuperMarioBros.OldVersion.Button(buttonAtlas,
								   new Rectangle(new Rectangle(5 * Gdx.graphics.getWidth() / 9, 
															   2 * Gdx.graphics.getHeight() / 3 - Gdx.graphics.getWidth() / 36, 
															   Gdx.graphics.getWidth() / 3, 
															   Gdx.graphics.getWidth() / 12)), 
								   new Rectangle(0, 31, 134, 31));
							
		mainMenuButton = new com.And1sS.SuperMarioBros.OldVersion.Button(buttonAtlas,
									new Rectangle(new Rectangle(Gdx.graphics.getWidth() / 9, 
																Gdx.graphics.getHeight() / 3 - Gdx.graphics.getWidth() / 18, 
																Gdx.graphics.getWidth() / 3, 
																Gdx.graphics.getWidth() / 12)), 
									new Rectangle(0, 62, 134, 31));
					
		exitButton = new Button(buttonAtlas,
								new Rectangle(new Rectangle(5 * Gdx.graphics.getWidth() / 9, 
															Gdx.graphics.getHeight() / 3 - Gdx.graphics.getWidth() / 18, 
															Gdx.graphics.getWidth() / 3, 
															Gdx.graphics.getWidth() / 12)), 
								new Rectangle(0, 93, 134, 31));
	}
	
	@Override
	public void show()
	{
		// TODO: Implement this method
	}

	@Override
	public void render(float deltaTime)
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(buttonAtlas,
		           0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth(),
				   135, 0, 1, 1);
				   
		resumeButton.draw(batch);
		restartButton.draw(batch);
		mainMenuButton.draw(batch);
		exitButton.draw(batch);
		batch.end();
		
		handleInput();
		// TODO: Implement this method
	}

	@Override
	public void dispose()
	{
		// TODO: Implement this method
	}

	@Override
	public void resize(int p1, int p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void hide()
	{
		// TODO: Implement this method
	}

	@Override
	public void resume()
	{
		// TODO: Implement this method
	}

	@Override
	public void pause()
	{
		// TODO: Implement this method
	}
	
	private void handleInput() {
		if(resumeButton.justTouched())
			game.setScreen(gameScreen);
		else if(restartButton.justTouched())
			game.setScreen(new GameScreen(game));
		else if(mainMenuButton.justTouched());
//			game.setScreen(new StartScreen(game));
//		else if(exitButton.justTouched())
//			System.exit(0);
	}
}
