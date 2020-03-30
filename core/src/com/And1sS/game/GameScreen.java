package com.And1sS.game;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

public class GameScreen implements Screen {
	
	private Game game;

	private SpriteBatch batch;

	private Player player;

	private Level currentLevel;

	private OnScreenController controller;

	private Hud hud;

	private BitmapFont font;

	private GlyphLayout glyphLayout;

	private double timer = 3;
	
	private boolean showLives;
	private boolean blackScreen;

	private float livesTextWidth;
	private float levelTextWidth;

	public GameScreen(Game game) {
		this.game = game;
		
		batch = new SpriteBatch();

		font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
		font.setColor(Color.WHITE);

		currentLevel = Level.loadFromFile("levels/level1.lvl", "images/map.png");

		player = new Player("images/mario.png",
				7 * currentLevel.getCellSize(), 12 * currentLevel.getCellSize(),
				currentLevel.getCellSize(), currentLevel.getCellSize());

		controller = new OnScreenController();
		hud = new Hud();
		
		blackScreen = true;

		glyphLayout = new GlyphLayout();
		glyphLayout.setText(font, "   x 1");
		livesTextWidth = glyphLayout.width;

		glyphLayout.setText(font, "Level 00-00");
		levelTextWidth = glyphLayout.width;
	}


	@Override
	public void render(float deltaTime) { 


		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if(showLives) {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


			font.draw(batch, "   x " + player.getLives(), 
					  (Gdx.graphics.getWidth() - livesTextWidth) / 2,
					  (Gdx.graphics.getHeight() + livesTextWidth) / 2);
			batch.draw(player.getAnimation().getCurrentRegion(),
			           (Gdx.graphics.getWidth() - livesTextWidth) / 2,
					   (Gdx.graphics.getHeight() - (float)player.getBody().getHeight()) / 2,
					   (float)player.getBody().getWidth(),
					   (float)player.getBody().getHeight());
		} else if(blackScreen) {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			font.draw(batch, "Level 1-1", 
					  Gdx.graphics.getWidth() / 2 - levelTextWidth / 2,
					  Gdx.graphics.getHeight() / 2 + levelTextWidth / 2);
		} else {
			drawMap(batch);
		    player.drawBullets(batch);
		    player.draw(batch);
		
		    font.draw(batch, String.valueOf(Gdx.graphics.getFramesPerSecond()), 0, Gdx.graphics.getHeight());
		              hud.drawHud(batch, player.getScore(), "1 - 1", currentLevel.getTime());
		              controller.drawControls(batch);
		} 
			
		batch.end();

		if(!showLives && !blackScreen)
			handleInput();
		if(!blackScreen)
			updateLogic();
			
		updateTimer();
	}

	@Override
	public void show() { }

	@Override
	public void dispose() { }

	@Override
	public void resize(int width, int height) { }

	@Override
	public void hide() { }



	@Override
	public void pause() {
		game.setScreen(new PauseScreen(game, this));
	}

	@Override
	public void resume() { }

	private void drawMap(SpriteBatch batch) {
		currentLevel.drawMapWithOffset(batch,
				(int) (player.getBody().getOffsetX()) / currentLevel.getCellSize(), 0,
				Gdx.graphics.getWidth() / currentLevel.getCellSize() + 2, currentLevel.getHeight(),
				(float) player.getBody().getOffsetX());
	}

	private void updateLogic() {
		player.update(currentLevel);
		
		int result = player.showGameOverScreen(currentLevel);
		
		if(result != -1) {
			showLives = true;
		} 
		
		if(!player.isDied() && !showLives) {
			currentLevel.updateTime();
			player.updateBullets(currentLevel);
		    currentLevel.getEnemyManager().updateEnemies(currentLevel, player);
	    }
	}
	
	private void handleInput() {
		if(controller.isRightPressed() && !player.isDied()) {
			player.getBody().setVelocityX(700 * Gdx.graphics.getWidth() / 1920);
		}
		if(controller.isLeftPressed() && !player.isDied()) {
			player.getBody().setVelocityX(-700 * Gdx.graphics.getWidth() / 1920);
		}
		if(controller.isJumpPressed() && player.getBody().isOnGround() && !player.isDied()) {
			player.getBody().setVelocityY(-1800 * Gdx.graphics.getHeight() / 1080);
		}
		if(controller.isMenuPressed())
			game.setScreen(new PauseScreen(game, this));
		
		if(controller.isShootTouched())
			player.shoot(currentLevel);

	}
	
	private void updateTimer() {
		if(showLives && !blackScreen) {
			timer -= Gdx.graphics.getDeltaTime();
			if(timer <= 0) {
				timer = 3;
				
				showLives = false;
				currentLevel = Level.loadFromFile("levels/level1.lvl", "images/map.png");
				
				if(player.getLives() <= 0) {
					game.setScreen(new GameOverScreen(game));
				}
			}
		} else if(blackScreen && !showLives) {
			timer -= Gdx.graphics.getDeltaTime();
			if(timer <= 0) {
				timer = 3;
				blackScreen = false;
			}
		}
	}
}
