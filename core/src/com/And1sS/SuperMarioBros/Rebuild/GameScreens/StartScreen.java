package com.And1sS.SuperMarioBros.Rebuild.GameScreens;

import com.And1sS.SuperMarioBros.Rebuild.GameManager;
import com.And1sS.SuperMarioBros.Rebuild.LevelEditor.EditorScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class StartScreen implements Screen {
    private TextButton startButton;
    private TextButton levelEditorButton;

    private Stage stage;

    private SpriteBatch spriteBatch;

    @Override
    public void show() {
        stage = new Stage();
        spriteBatch = new SpriteBatch();

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
        font.getData().setScale(0.5f * Gdx.graphics.getWidth() / 1920.0f);

        TextureAtlas textureAtlas = new TextureAtlas("ui/ui-gray.atlas");
        Skin skin = new Skin(textureAtlas);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = skin.getDrawable("button_03");
        buttonStyle.down = skin.getDrawable("button_01");
        buttonStyle.pressedOffsetX = 1;
        buttonStyle.pressedOffsetY = -1;
        startButton = new TextButton("Start", buttonStyle);
        float buttonHeight = 0.1f;
        float buttonWidth = 0.5f;
        startButton.setPosition((1 - buttonWidth) / 2 * Gdx.graphics.getWidth(), (2 * (1 - 2 * buttonHeight) / 3 + buttonHeight) * Gdx.graphics.getHeight());
        startButton.setWidth(Gdx.graphics.getWidth() * buttonWidth);
        startButton.setHeight(Gdx.graphics.getHeight() * buttonHeight);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManager.getManager().getGame().setScreen(new GameScreen());
                stage.clear();
            }
        });

        levelEditorButton = new TextButton("Level Editor", buttonStyle);
        levelEditorButton.setPosition((1 - buttonWidth) / 2 * Gdx.graphics.getWidth(), (1 - 2 * buttonHeight) / 3 * Gdx.graphics.getHeight());
        levelEditorButton.setWidth(Gdx.graphics.getWidth() * buttonWidth);
        levelEditorButton.setHeight(Gdx.graphics.getHeight() * buttonHeight);
        levelEditorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManager.getManager().getGame().setScreen(new EditorScreen());
                stage.clear();
            }
        });

        stage.addActor(startButton);
        stage.addActor(levelEditorButton);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Color color = Color.LIGHT_GRAY;
        Gdx.gl.glClearColor(color.r, color.g, color.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        startButton.draw(spriteBatch, 1);
        levelEditorButton.draw(spriteBatch, 1);
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
