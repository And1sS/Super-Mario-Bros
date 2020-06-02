package com.And1sS.SuperMarioBros.Rebuild.LevelEditor;

import com.And1sS.SuperMarioBros.OldVersion.OnScreenController;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.TileId;
import com.And1sS.SuperMarioBros.Rebuild.GameManager;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.Level;
import com.And1sS.SuperMarioBros.Rebuild.GameScreens.StartScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class EditorScreen implements Screen,
        Input.TextInputListener,
        GameObjectPicker.ILevelSaver,
        GameObjectPicker.IBackgroundChanger {
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;

    private final GameObjectPicker picker;
    private final OnScreenController controller;

    private final Level level;

    private float offsetX = 0;

    private final int sizeX = 200;
    private final int sizeY = 15;

    private boolean saved = true;

    public EditorScreen() {
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        controller = new OnScreenController();
        picker = new GameObjectPicker(Gdx.graphics.getWidth() * 3 / 4.0f, 0,
                Gdx.graphics.getWidth() / 4.0f, Gdx.graphics.getHeight(),
                this,
                this);

//        Texture tiles = new Texture(Gdx.files.internal("images/map.png"));
//        Texture objects = new Texture(Gdx.files.internal("images/objects.png"));
//        Texture enemies = new Texture(Gdx.files.internal("images/enemies.png"));
//        level = new Level(new int[sizeY][sizeX], Gdx.graphics.getHeight() / sizeY,
//                tiles, objects, enemies);
        level = Level.loadFromFile("levels/level1.lvl",
                "images/map.png",
                "images/objects.png",
                "images/enemies.png");
        level.setEditorMode(true);
    }

    @Override
    public void render(float deltaTime) {
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
        if (controller.isLeftPressed() && offsetX > 0) {
            offsetX -= 625 * Gdx.graphics.getDeltaTime();
            return;
        } else if(controller.isRightPressed()
                && offsetX + Gdx.graphics.getWidth() < sizeX * level.getCellSize()) {
            offsetX += 625 * Gdx.graphics.getDeltaTime();
            return;
        } else if (controller.isMenuPressed()) {
            if (saved) {
                GameManager.getManager().getGame().setScreen(new StartScreen());
            } else {


            }
            return;
        }

        if (offsetX < 0)
            offsetX = 0;

        if (Gdx.input.justTouched()) {
            if (isPickerTouched()) {
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
            case BACKGROUND:
                return;

            case TILE: {
                try {
                    int pickedCell = level.getCell(pickedCellX, pickedCellY);
                    if (pickedCell == picker.getLastPickedTileId()){
                        level.setCell(pickedCellX, pickedCellY, TileId.TRANSPARENT_NOT_COLLIDABLE_BLOCK);
                    } else {
                        level.setCell(pickedCellX, pickedCellY, picker.getLastPickedTileId());
                    }
                    saved = false;
                } catch (Exception e) {}
                break;
            }

            case DYNAMIC_GAME_OBJECT: {
                level.addObject(picker.getLastPickedGameObjectId(), pickedCellX, pickedCellY);
                saved = false;
                break;
            }
        }
    }

    private boolean isPickerTouched() {
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();

        return x > picker.getBounds().x
                && x < picker.getBounds().x + picker.getBounds().width
                && y > picker.getBounds().y
                && y < picker.getBounds().y + picker.getBounds().height;
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void resize(int p1, int p2) {}

    @Override
    public void dispose() {}

    @Override
    public void hide() {}

    @Override
    public void input(String text) {
        if (text.equals(""))
            text = "savedlevel";
        level.saveToFile("levels/" + text + ".lvl");
        saved = true;
    }

    @Override
    public void canceled() {

    }

    @Override
    public void save() {
        Gdx.input.getTextInput(this, "Save level", "", "Level name");
    }

    @Override
    public void changeBackgroundTo(String color) {
        level.setBackgroundColor(color);
        saved = false;
    }
}
