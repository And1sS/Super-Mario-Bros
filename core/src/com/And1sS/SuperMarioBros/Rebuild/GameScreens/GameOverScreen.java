package com.And1sS.SuperMarioBros.Rebuild.GameScreens;


import com.And1sS.SuperMarioBros.Rebuild.GameManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverScreen implements Screen {
    private static final float DURATION = 3.0f;

    private SpriteBatch spriteBatch;

    private float timeLeft = DURATION;

    private BitmapFont font;

    @Override
    public void show() {
        font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
        font.getData().setScale(0.75f * Gdx.graphics.getWidth() / 1920.0f);

        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        if (timeLeft > 0) {
            timeLeft -= delta;
            if (timeLeft <= 0) {
                GameManager.getManager().getGame().setScreen(new StartScreen());
            }
        }

        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, "GAME OVER");

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        font.draw(spriteBatch, "GAME OVER",
                (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
                Gdx.graphics.getHeight() / 2);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {

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
