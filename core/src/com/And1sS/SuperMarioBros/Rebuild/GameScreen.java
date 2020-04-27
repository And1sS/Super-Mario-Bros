package com.And1sS.SuperMarioBros.Rebuild;

import com.And1sS.SuperMarioBros.OldVersion.OnScreenController;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.CoopaTroopa;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.Platform;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private Player player;

    private Level level;

    private OnScreenController controller;

    private SpriteBatch batch;

    @Override
    public void show() {
        batch = new SpriteBatch();
        level = Level.loadFromFile("levels/test.lvl",
                "images/map.png",
                "images/objects.png",
                "images/enemies.png");
        player = new Player(100, 0, 0.85f * level.getCellSize(), level.getCellSize());
        level.addObject(new CoopaTroopa(14, 12, level.getCellSize(), level.getEnemiesTexture()));
        level.addObject(new Platform(25, 8, level, Platform.Type.LEFT_RIGHT));
        level.addObject(new Platform(190, 8, level, Platform.Type.TOP_DOWN));
        controller = new OnScreenController();
    }

    @Override
    public void render(float delta) {
        handleInput();
        updateLogic(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        level.render(batch, player);
        player.render(batch);
        controller.drawControls(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        int oldCellSize = level.getCellSize();
        level.recalculateObjectsBounds(width, height);
        player.recalculateBounds(oldCellSize, level.getCellSize());
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void updateLogic(float deltaTime) {
        if (deltaTime >= 0.025f)
            return;

        player.update(deltaTime, level);
        level.update(deltaTime, player);
        player.updateAnimation(deltaTime);
    }

    public void handleInput() {
        if (controller.isLeftPressed())
            player.moveLeft();
        else if (controller.isRightPressed())
            player.moveRight();
        if (controller.isJumpPressed() && player.getVelocityY() == 0)
            player.jump();
    }
}
