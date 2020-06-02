package com.And1sS.SuperMarioBros.Rebuild;

import com.And1sS.SuperMarioBros.Rebuild.GameObjects.Level;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.Mario;
import com.And1sS.SuperMarioBros.Rebuild.GameScreens.DeathScreen;
import com.And1sS.SuperMarioBros.Rebuild.GameScreens.GameOverScreen;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager implements Mario.ILevelChanger {
    private static final int LEVEL_DURATION = 300; // in seconds

    private static GameManager gameManager;

    public static final List<String> levels = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("levels/level1.lvl");
                add("levels/level2.lvl");
            }});

    private Game game;

    private Level level;
    private Mario mario;

    private final int playerStartX = 3;
    private final int playerStartY = 12;

    private int currentLevel = 0;

    private float timeFromStart = 0;

    private boolean gameRunning = false;

    private GameManager() {
        restartGame();
    }

    public static void instantiate(Game game) {
        if (gameManager == null) {
            gameManager = new GameManager();
            gameManager.game = game;
        }
    }

    public void restartGame() {
        currentLevel = 0;
        gameRunning = true;
        level = Level.loadFromFile(levels.get(currentLevel),
                "images/map.png",
                "images/objects.png",
                "images/enemies.png");
        mario = new Mario(
                playerStartX * level.getCellSize(),
                playerStartY * level.getCellSize(),
                0.85f * level.getCellSize(),
                level.getCellSize(),
                this);
    }

    public void executeGame(float deltaTime) {
        if (deltaTime >= 0.025f)
            return;

        mario.update(deltaTime, level);

        if (mario.isAlive()) {
            timeFromStart += deltaTime;
            if (timeFromStart >= LEVEL_DURATION) {
                mario.die();
            }
            level.update(deltaTime, mario);
        } else if (!mario.isAlive() && mario.getY() > level.getCellSize() * level.getHeight()) {
            if (mario.getLives() <= 0) {
                game.setScreen(new GameOverScreen());
                gameRunning = false;
            } else {
                game.setScreen(new DeathScreen());
                timeFromStart = 0;
                restartLevel();
                mario.defaultMario();
            }
        }

        mario.updateAnimation(deltaTime);
    }

    public void recalculateObjectsBounds(int width, int height) {
        int oldCellSize = level.getCellSize();
        level.recalculateObjectsBounds(width, height);
        mario.recalculateBounds(oldCellSize, level.getCellSize());
    }

    public void restartLevel() {
        timeFromStart = 0;
        mario.respawn(playerStartX * level.getCellSize(), playerStartY * level.getCellSize());
        level = Level.loadFromFile(levels.get(currentLevel),
                "images/map.png",
                "images/objects.png",
                "images/enemies.png");
    }

    public boolean isGameRunning() { return gameRunning; }

    public static GameManager getManager() {
        return gameManager;
    }

    public Game getGame() { return game; }

    public Level getLevel() { return level; }

    public String getLevelLabel() { return String.valueOf(currentLevel + 1); }

    public Mario getMario() { return mario; }

    public int getTimeLeft() { return LEVEL_DURATION - (int) timeFromStart; }

    @Override
    public void levelChanged() {
        currentLevel++;
        restartLevel();
    }
}
