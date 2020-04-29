package com.And1sS.SuperMarioBros.Rebuild.LevelEditor;

import com.And1sS.SuperMarioBros.OldVersion.OnScreenController;
import com.And1sS.SuperMarioBros.Rebuild.Level;
import com.And1sS.SuperMarioBros.Rebuild.TileId;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class EditorScreen implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer renderer;

    private Texture tiles;
    private Texture objects;
    private Texture enemies;

    private GameObjectPicker picker;
    private OnScreenController controller;

    private Level level;

    private float offsetX = 0;

    private int sizeX = 200;
    private int sizeY = 15;

    public EditorScreen() {
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        controller = new OnScreenController();
        picker = new GameObjectPicker(Gdx.graphics.getWidth() * 3 / 4.0f, 0,
                Gdx.graphics.getWidth() / 4.0f, Gdx.graphics.getHeight());

//        tiles = new Texture(Gdx.files.internal("images/map.png"));
//        objects = new Texture(Gdx.files.internal("images/objects.png"));
//        enemies = new Texture(Gdx.files.internal("images/enemies.png"));
//        level = new Level(new int[sizeY][sizeX], Gdx.graphics.getHeight() / sizeY,
//                tiles, objects, enemies);
        level = Level.loadFromFile("levels/level1.lvl",
                "images/map.png",
                "images/objects.png",
                "images/enemies.png");
        level.setEditorMode(true);
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
        batch.end();

        picker.render(batch, renderer, level);

        batch.begin();
        controller.drawControls(batch);
        batch.end();
    }

    private void handleInput() {
        if(controller.isLeftPressed() && offsetX > 0)
            offsetX -= 625 * Gdx.graphics.getDeltaTime();
        else if(controller.isRightPressed() && offsetX + Gdx.graphics.getWidth() < sizeX * level.getCellSize()) {
            offsetX += 625 * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.justTouched()) {
            if (controller.isMenuPressed()) {
                level.saveToFile("saved_levels/test.lvl");
            } else if (isPickerTouched()) {
                picker.handleInput();
            }  else {
                handleEditorInput();
            }
        }
    }

    private void handleEditorInput() {
        int pickedCellX = (int) ((Gdx.input.getX() + offsetX) / level.getCellSize());
        int pickedCellY =  Gdx.input.getY() / level.getCellSize();

        switch (picker.getLastPickedObjectType()) {
            case STATIC_GAME_OBJECT:
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

            case DYNAMIC_GAME_OBJECT: {
                level.addObject(picker.getLastPickedGameObjectId(), pickedCellX, pickedCellY);
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
