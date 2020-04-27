package com.And1sS.SuperMarioBros.OldVersion;

import com.And1sS.SuperMarioBros.Rebuild.LevelEditor.GameObjectPicker;
import com.And1sS.SuperMarioBros.Rebuild.TileId;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.And1sS.SuperMarioBros.Rebuild.Level;

public class EditorScreen implements Screen {
	private Game game;

	private SpriteBatch batch;
	private ShapeRenderer renderer;

	private Texture tiles;
	private Texture objects;
	private Texture enemies;
	private TextureRegion currentTile;

	private GameObjectPicker picker;
	private OnScreenController controller;

	private Level level;

	private float offsetX = 0;

	private int sizeX = 200;
	private int sizeY = 15;

	public EditorScreen(Game game) {
		this.game = game;

		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		
		controller = new OnScreenController();
		picker = new GameObjectPicker(Gdx.graphics.getWidth() * 3 / 4.0f, Gdx.graphics.getHeight() * 2 / 3.0f,
				Gdx.graphics.getWidth() / 4.0f, Gdx.graphics.getHeight() / 3.0f);

		tiles = new Texture(Gdx.files.internal("images/map.png"));
		objects = new Texture(Gdx.files.internal("images/objects.png"));
		enemies = new Texture(Gdx.files.internal("images/enemies.png"));
		level = new Level(new int[sizeY][sizeX], Gdx.graphics.getHeight() / sizeY,
				tiles, objects, enemies);

		currentTile = new TextureRegion(tiles);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float p1) {
		handleInput();

		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		batch.begin();
		level.render(batch, offsetX);
		controller.drawControls(batch);
		batch.end();

		picker.render(batch, renderer, level);
	}

	private void handleInput() {
		if(controller.isLeftPressed() && offsetX > 0)
			offsetX -= 625 * Gdx.graphics.getDeltaTime();
		else if(controller.isRightPressed() && offsetX + Gdx.graphics.getWidth() < sizeX * level.getCellSize()) {
			offsetX += 625 * Gdx.graphics.getDeltaTime();
		}

		if (Gdx.input.justTouched()) {
			if (isPickerTouched()) {
				picker.handleInput();
			} else {
				handleEditorInput();
			}
		}
	}

	private void handleEditorInput() {
		int pickedCellX = (int) ((Gdx.input.getX() + offsetX) / level.getCellSize());
		int pickedCellY =  Gdx.input.getY() / level.getCellSize();

		switch (picker.getLastPickedObjectType()) {
			case STATIC_GAME_OBJECT:
			case DYNAMIC_GAME_OBJECT:
			case NONE:
				return;

			case TILE: {
				try {
					int pickedCell = level.getCell(pickedCellX, pickedCellY);
					if (pickedCell == picker.getLastPickedTileId()){
						level.setCell(pickedCellX, pickedCellY, TileId.TRANSPARENT_NOT_COLLIDABLE_BLOCK);
					} else {
						level.setCell(pickedCellX, pickedCellY, picker.getLastPickedTileId());
					}
				} catch (Exception e) {}
				break;
			}
		}
	}

	private boolean isPickerTouched() {
		int x = Gdx.input.getX();
		int y = Gdx.graphics.getHeight() - Gdx.input.getY();

		if (x > picker.getBounds().x
			&& x < picker.getBounds().x + picker.getBounds().width
			&& y > picker.getBounds().y
			&& y < picker.getBounds().y + picker.getBounds().height) {
			return true;
		}

		return false;
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
}
