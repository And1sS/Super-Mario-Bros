package com.And1sS.game.OldVersion;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.*;

public class EditorScreen implements Screen {
	private Game game;

	private SpriteBatch batch;

	private ShapeRenderer renderer;
	private Texture tiles;
	private TextureRegion currentTile;


	private float offsetX = 0;
	private int cellSize;

	private int sizeX = 100;
	private int sizeY = 15;

	private int currentBlockId = 10;
	int[][] map;

	private OnScreenController controller;

	public EditorScreen(Game game) {
		this.game = game;

		map = new int[sizeY][sizeX];

		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		
		controller = new OnScreenController();

		cellSize = Gdx.graphics.getHeight() / sizeY;

		tiles = new Texture(Gdx.files.internal("images/map.png"));

		currentTile = new TextureRegion(tiles);
	}

	@Override
	public void show() {
		// TODO: Implement this method

	}

	@Override
	public void render(float p1) {
		// TODO: Implement this method
		
			if(controller.isLeftPressed() && offsetX > 0)
				offsetX -= 625 * Gdx.graphics.getDeltaTime();
			else if(controller.isRightPressed() && offsetX + Gdx.graphics.getWidth() < sizeX * cellSize) {
				offsetX += 625 * Gdx.graphics.getDeltaTime();
		    } else if(controller.isShootTouched()) {
				currentBlockId--;
				
				if(currentBlockId < 0)
					currentBlockId = 0;
			} else if(controller.isJumpPressed()) {
				currentBlockId++;
			} else if(Gdx.input.justTouched()) {
				try {
					if(map[Gdx.input.getY() / cellSize][(int) (Gdx.input.getX() + offsetX) / cellSize] == 0)	
					    map[Gdx.input.getY() / cellSize][(int) (Gdx.input.getX() + offsetX) / cellSize] = currentBlockId;
					else 
						map[Gdx.input.getY() / cellSize][(int) (Gdx.input.getX() + offsetX) / cellSize] = 0;
				} catch(Exception e) {}
			}



		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		batch.begin();
		

		for(int i = 0; i < sizeY; i++) {
			for(int j = 0; j < sizeX; j++) {
				try {
						setCurrentTile(map[i][j]);
						batch.draw(currentTile,
								   j * cellSize - (int) offsetX,
								   Gdx.graphics.getHeight() - (i + 1) * cellSize,
								   cellSize,
								   cellSize);


				} catch(Exception e) {}
			}
		}

		renderer.begin(ShapeRenderer.ShapeType.Filled);
		renderer.setColor(Color.BLUE);
		renderer.rect(0, 0, 2 * cellSize + 20, 2 * cellSize + 20);
		renderer.end();
		
		setCurrentTile(currentBlockId);
		batch.draw(currentTile,
				   10,
				   10,
				   2 * cellSize,
				   2 * cellSize);
		
		controller.drawControls(batch);
		batch.end();
		
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
	public void dispose() {
		// TODO: Implement this method
	}

	@Override
	public void hide() {
		// TODO: 

	}

	private void setCurrentTile(int tileId) {
		switch(tileId) {
			case 0:
				currentTile.setRegion(80, 16, 0, 0);
				break;
			case 1:
				currentTile.setRegion(80, 16, 0, 0);
				break;
			case 10:
				currentTile.setRegion(0, 0, 16, 16);
				break;
			case 14:
				currentTile.setRegion(161, 32, 15, 16);
				break;
			case 20:
				currentTile.setRegion(64, 0, 16, 16);
				break;
			case 21:
				currentTile.setRegion(176, 0, 16, 16);
				break;
		}
	}
}
