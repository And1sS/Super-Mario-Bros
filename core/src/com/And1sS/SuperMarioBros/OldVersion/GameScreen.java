package com.And1sS.SuperMarioBros.OldVersion;


import com.And1sS.SuperMarioBros.Rebuild.GameManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
	
	private final Game game;

	private final SpriteBatch batch;

	private final Mario mario;

	private Level currentLevel;

	private final OnScreenController controller;

	private final Hud hud;

	private final BitmapFont font;

	private final GlyphLayout glyphLayout;

	private double timer = 3;
	
	private boolean showLives;
	private boolean blackScreen;

	private final float livesTextWidth;
	private final float levelTextWidth;

	public GameScreen(Game game) {
		this.game = game;
		
		batch = new SpriteBatch();

		font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
		font.setColor(Color.WHITE);

		currentLevel = Level.loadFromFile("levels/level1.lvl", "images/map.png");

		mario = new Mario("images/mario.png",
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


			font.draw(batch, "   x " + mario.getLives(),
					  (Gdx.graphics.getWidth() - livesTextWidth) / 2,
					  (Gdx.graphics.getHeight() + livesTextWidth) / 2);
			batch.draw(mario.getAnimation().getCurrentRegion(),
			           (Gdx.graphics.getWidth() - livesTextWidth) / 2,
					   (Gdx.graphics.getHeight() - mario.getBody().getHeight()) / 2,
					mario.getBody().getWidth(),
					mario.getBody().getHeight());
		} else if(blackScreen) {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			font.draw(batch, "Level 1-1", 
					  Gdx.graphics.getWidth() / 2 - levelTextWidth / 2,
					  Gdx.graphics.getHeight() / 2 + levelTextWidth / 2);
		} else {
			drawMap(batch);
		    mario.drawBullets(batch);
		    mario.draw(batch);
		
		    font.draw(batch, String.valueOf(Gdx.graphics.getFramesPerSecond()),
					0, Gdx.graphics.getHeight());
		  	hud.drawHud(batch, mario.getScore(), GameManager.getManager().getLevelLabel(), currentLevel.getTime());
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
				(int) (mario.getBody().getOffsetX()) / currentLevel.getCellSize(), 0,
				Gdx.graphics.getWidth() / currentLevel.getCellSize() + 2, currentLevel.getHeight(),
				mario.getBody().getOffsetX());
	}

	private void updateLogic() {
		mario.update(currentLevel);
		
		int result = mario.showGameOverScreen(currentLevel);
		
		if(result != -1) {
			showLives = true;
		} 
		
		if(!mario.isDied() && !showLives) {
			currentLevel.updateTime();
			mario.updateBullets(currentLevel);
		    currentLevel.getEnemyManager().updateEnemies(currentLevel, mario);
	    }
	}
	
	private void handleInput() {
		if(controller.isRightPressed() && !mario.isDied()) {
			mario.getBody().setVelocityX(700 * Gdx.graphics.getWidth() / 1920);
		}
		if(controller.isLeftPressed() && !mario.isDied()) {
			mario.getBody().setVelocityX(-700 * Gdx.graphics.getWidth() / 1920);
		}
		if(controller.isJumpPressed() && mario.getBody().isOnGround() && !mario.isDied()) {
			mario.getBody().setVelocityY(-1800 * Gdx.graphics.getHeight() / 1080);
		}
		if(controller.isMenuPressed())
			game.setScreen(new PauseScreen(game, this));
		
		if(controller.isShootTouched())
			mario.shoot(currentLevel);

	}
	
	private void updateTimer() {
		if(showLives && !blackScreen) {
			timer -= Gdx.graphics.getDeltaTime();
			if(timer <= 0) {
				timer = 3;
				
				showLives = false;
				currentLevel = Level.loadFromFile("levels/level1.lvl", "images/map.png");
				
				if(mario.getLives() <= 0) {
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
