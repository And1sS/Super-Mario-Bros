package com.And1sS.SuperMarioBros.Rebuild;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Hud {
    private static final String MARIO_LABEL = "MARIO";
    private static final String WORLD_LABEL = "WORLD";
    private static final String TIME_LABEL = "TIME";
    private final GameManager gameManager;

    private final BitmapFont font;
    private final GlyphLayout glyphLayout;

    private float marioTextX;
    private float worldTextX;
    private float timeTextX;
    private float coinsTextX;

    private float marioTextY;
    private float worldTextY;
    private float timeTextY;
    private float coinsTextY;

    private float scoreValueX;
    private float worldLabelX;
    private float timeValueX;
    private float coinsValueX;

    public Hud(GameManager gameManager, BitmapFont font) {
        this.gameManager = gameManager;
        this.font = font;

        glyphLayout = new GlyphLayout();
    }

    public void render(SpriteBatch spriteBatch) {
        int score = gameManager.getMario().getScore();
        int coins = gameManager.getMario().getCoins();

        int timeLeft = gameManager.getTimeLeft();
        String levelTitle = gameManager.getLevelLabel();

        calculatePositions(score, coins, levelTitle, timeLeft);
        font.draw(spriteBatch, MARIO_LABEL, marioTextX, marioTextY);
        font.draw(spriteBatch, WORLD_LABEL, worldTextX, worldTextY);
        font.draw(spriteBatch, TIME_LABEL, timeTextX, timeTextY);

        font.draw(spriteBatch, String.valueOf(score), scoreValueX, marioTextY - 1.2f * glyphLayout.height);
        font.draw(spriteBatch, "Coins: "+ coins, coinsValueX, marioTextY - 1.2f * glyphLayout.height);
        font.draw(spriteBatch, levelTitle, worldLabelX, marioTextY - 1.2f * glyphLayout.height);
        font.draw(spriteBatch, String.valueOf(timeLeft), timeValueX, timeTextY - 1.2f * glyphLayout.height);
    }

    private void calculatePositions(int score, int coins, String label, int timeLeft) {
        glyphLayout.setText(font, MARIO_LABEL);
        float marioTextWidth = glyphLayout.width;
        glyphLayout.setText(font, WORLD_LABEL);
        float worldTextWidth = glyphLayout.width;
        glyphLayout.setText(font, TIME_LABEL);
        float timeTextWidth = glyphLayout.width;

        float padding =  (Gdx.graphics.getWidth() - marioTextWidth
                - worldTextWidth - 2 * timeTextWidth) / 5;
        marioTextX = padding;
        coinsTextX = marioTextX + marioTextWidth + padding;
        worldTextX = coinsTextX + timeTextWidth + padding;
        timeTextX = worldTextX + worldTextWidth + padding;

        float textHeight = glyphLayout.height;
        marioTextY = worldTextY = timeTextY = Gdx.graphics.getHeight() - 1.5f * textHeight;

        glyphLayout.setText(font, String.valueOf(score));
        scoreValueX = marioTextX + (marioTextWidth - glyphLayout.width) / 2;

        glyphLayout.setText(font, String.valueOf(timeLeft));
        timeValueX = timeTextX + (timeTextWidth - glyphLayout.width) / 2;

        glyphLayout.setText(font, String.valueOf(label));
        worldLabelX = worldTextX + (worldTextWidth - glyphLayout.width) / 2;

        glyphLayout.setText(font, "x" + coins);
        coinsValueX = coinsTextX + (timeTextWidth - glyphLayout.width) / 2;
    }
}
