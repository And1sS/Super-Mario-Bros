package com.And1sS.SuperMarioBros.Rebuild.LevelEditor;

import com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.TileId;
import com.And1sS.SuperMarioBros.Rebuild.GameManager;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.GameObject;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.Level;
import com.And1sS.SuperMarioBros.Rebuild.GameScreens.StartScreen;
import com.And1sS.SuperMarioBros.Rebuild.OnScreenController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Iterator;

public class EditorScreen implements Screen,
         GestureDetector.GestureListener,
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
        level = Level.loadFromFile("levels/level2.lvl",
                "images/map.png",
                "images/objects.png",
                "images/enemies.png");
        level.setEditorMode(true);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new GestureDetector(this));
        inputMultiplexer.addProcessor(picker.getInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void show() {
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
                //TODO: add popup asking for confirmation
                GameManager.getManager().getGame().setScreen(new StartScreen());
            }
            return;
        }

        if (offsetX < 0)
            offsetX = 0;
    }

    private void deleteGameObject(float posX, float posY) {
        int x = (int) (posX + offsetX);
        int y = (int) (Gdx.graphics.getHeight() - posY);

        boolean deleted = false;
        for (Iterator<GameObject> iterator = level.getObjects().iterator(); iterator.hasNext(); ) {
            GameObject object = iterator.next();

            if ((object.getId() != GameObjectId.BACKGROUND_OBJECT) && contains(object.getBounds(), x, (int) posY)) {
                iterator.remove();
                deleted = true;
            }
        }

        if (deleted)
            return;

        for (Iterator<GameObject> iterator = level.getObjects().iterator(); iterator.hasNext(); ) {
            GameObject object = iterator.next();

            if ((object.getId() == GameObjectId.BACKGROUND_OBJECT) && contains(object.getBounds(), x, y)) {
                iterator.remove();
                return;
            }
        }
    }

    private boolean contains(Rectangle bounds, int x, int y) {
        return x > bounds.x && x < bounds.x + bounds.width
                && y > bounds.y && y < bounds.y + bounds.height;
    }

    private void handleEditorInput(float x, float y) {
        int pickedCellX = (int) ((x + offsetX) / level.getCellSize());
        int pickedCellY =  (int) y / level.getCellSize();

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
                if (level.getCell(pickedCellX, pickedCellY) ==
                        TileId.TRANSPARENT_NOT_COLLIDABLE_BLOCK) {
                    level.addObject(picker.getLastPickedGameObjectId(), pickedCellX, pickedCellY);
                    saved = false;
                }
                break;
            }
        }
    }

    private boolean isPickerTouched(float x, float y) {
        return x > picker.getBounds().x
                && x < picker.getBounds().x + picker.getBounds().width
                && y > picker.getBounds().y
                && y < picker.getBounds().y + picker.getBounds().height;
    }

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
        level.saveToFile("saved_levels/" + text + ".lvl");
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

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (isPickerTouched(x, y)) {
            picker.handleInput(x, y);
        }  else {
            handleEditorInput(x, y);
        }
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        deleteGameObject(x, y);
        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
