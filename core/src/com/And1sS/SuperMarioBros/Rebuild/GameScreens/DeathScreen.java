package com.And1sS.SuperMarioBros.Rebuild.GameScreens;

import com.And1sS.SuperMarioBros.Rebuild.GameManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DeathScreen implements Screen {
    private static final float DURATION = 3.0f;
    private static final float GAP_PERCENTAGE = 0.1f;

    private static final String DEATH_STRING = "You have died!";
    private static final String LIVES_STRING = "Lives remaining: ";

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
                GameManager.getManager().getGame().setScreen(new GameScreen());
            }
        }

        GlyphLayout glyphLayout = new GlyphLayout();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        glyphLayout.setText(font, DEATH_STRING);
        font.draw(spriteBatch, DEATH_STRING,
                (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
                Gdx.graphics.getHeight() * (1 + GAP_PERCENTAGE) / 2 + glyphLayout.height);
        glyphLayout.setText(font, LIVES_STRING + GameManager.getManager().getMario().getLives());
        font.draw(spriteBatch, LIVES_STRING + GameManager.getManager().getMario().getLives(),
                (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
                Gdx.graphics.getHeight() * (1 - GAP_PERCENTAGE) / 2);
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
