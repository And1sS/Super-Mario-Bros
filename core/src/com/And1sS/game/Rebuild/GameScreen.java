package com.And1sS.game.Rebuild;

import com.And1sS.game.OldVersion.OnScreenController;
import com.And1sS.game.Rebuild.GameObjects.Goomba;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private Player player;

    private Level level;

    private OnScreenController controller;

    private SpriteBatch batch;

    private boolean running = true;

    @Override
    public void show() {
        batch = new SpriteBatch();
        level = Level.loadFromFile("levels/level1.lvl", "images/map.png");
        player = new Player(100, 0, 0.85f * level.getCellSize(), level.getCellSize());
        level.addObject(new Goomba(14, 12, level.getCellSize()));
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
