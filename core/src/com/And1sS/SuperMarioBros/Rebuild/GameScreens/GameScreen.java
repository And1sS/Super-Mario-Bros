package com.And1sS.SuperMarioBros.Rebuild.GameScreens;

import com.And1sS.SuperMarioBros.Rebuild.GameCamera;
import com.And1sS.SuperMarioBros.Rebuild.GameManager;
import com.And1sS.SuperMarioBros.Rebuild.Hud;
import com.And1sS.SuperMarioBros.Rebuild.OnScreenController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameScreen implements Screen {
    private static final float FLASH_DURATION = 1f;

    private OnScreenController controller;
    private Hud hud;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;


    private GameCamera gameCamera;

    private BitmapFont font;

    private float flashTimeLeft = FLASH_DURATION;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.BLACK);
        spriteBatch = new SpriteBatch();
        controller = new OnScreenController();
        gameCamera = new GameCamera();

        if (!GameManager.getManager().isGameRunning()) {
            GameManager.getManager().restartGame();
        }
        GameManager.getManager().getMario().subscribe(gameCamera);

        font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
        font.getData().setScale(0.75f * Gdx.graphics.getWidth() / 1920.0f);

        hud = new Hud(GameManager.getManager(), font);
    }

    @Override
    public void render(float delta) {
        handleInput();
        updateLogic(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        GameManager.getManager().getLevel().render(spriteBatch, gameCamera.offsetX);
        GameManager.getManager().getMario().render(spriteBatch);
        controller.drawControls(spriteBatch);
        hud.render(spriteBatch);
        spriteBatch.end();

        if (flashTimeLeft > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, flashTimeLeft / FLASH_DURATION);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }



    public void updateLogic(float deltaTime) {
        if (flashTimeLeft > 0) {
            flashTimeLeft -= deltaTime;
            if (flashTimeLeft < 0) {
                flashTimeLeft = 0;
            }
            return;
        }

        GameManager.getManager().executeGame(deltaTime);
    }

    public void handleInput() {
        if (controller.isLeftPressed())
            GameManager.getManager().getMario().moveLeft();
        else if (controller.isRightPressed())
            GameManager.getManager().getMario().moveRight();
        if (controller.isJumpPressed() && GameManager.getManager().getMario().getVelocityY() == 0)
            GameManager.getManager().getMario().jump();
        if (controller.isMenuPressed()) {
            GameManager.getManager().getGame().setScreen(new StartScreen());
            GameManager.getManager().restartGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        GameManager.getManager().recalculateObjectsBounds(width, height);
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
}
