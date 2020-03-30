package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.GameObjects.GameObject;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.game.Rebuild.Level;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class BrokenBrick extends GameObject {
    // TODO: rewrire this class, implement updater which will work for every part of brick

    List <GameObject> parts;

    float rotationAngle = 0;

    public BrokenBrick(int mapIndxX, int mapIndxY, float size) {
        this(mapIndxX * size, Gdx.graphics.getHeight() - mapIndxY * size,
                size, size);
    }

    public BrokenBrick(float x, float y, float width, float height) {
        super(new Rectangle(x, y, width, height),
                new Animation("images/objects.png", 64, 16, 16, 16));

        parts = new ArrayList<>();
        parts.add(new GameObject(new Rectangle(x, y, width, height),
                new Animation("images/objects.png", 64, 16, 16, 16)));
        parts.add(new GameObject(new Rectangle(x, y, width, height),
                new Animation("images/objects.png", 64, 16, 16, 16)));
        parts.add(new GameObject(new Rectangle(x, y, width, height),
                new Animation("images/objects.png", 64, 16, 16, 16)));
        parts.add(new GameObject(new Rectangle(x, y, width, height),
                new Animation("images/objects.png", 64, 16, 16, 16)));

        // 0, 1 - going left, rotating left; 2, 3 - opposite

        parts.get(0).setVelocityY(80 * Gdx.graphics.getHeight() / 1080.0f);
        parts.get(1).setVelocityY(1300 * Gdx.graphics.getHeight() / 1080.0f);
        parts.get(2).setVelocityY(1300 * Gdx.graphics.getHeight() / 1080.0f);
        parts.get(3).setVelocityY(80 * Gdx.graphics.getHeight() / 1080.0f);

        parts.get(0).setVelocityX(-500 * Gdx.graphics.getHeight() / 1080.0f);
        parts.get(1).setVelocityX(-500 * Gdx.graphics.getHeight() / 1080.0f);
        parts.get(2).setVelocityX(500 * Gdx.graphics.getHeight() / 1080.0f);
        parts.get(3).setVelocityX(500 * Gdx.graphics.getHeight() / 1080.0f);

        renderer = new BreakingBricksRenderer();
        updater = new BreakingBricksUpdater();
        levelCollisionDetector = new NotLevelCollidable();
        objectCollisionDetector = new NotGameObjectCollidable();
    }

    private class BreakingBricksUpdater implements IUpdatable {

        @Override
        public void update(float deltaTime, Level level) {
            for (GameObject object : parts) {
                object.x += object.velocityX * deltaTime;
                object.velocityY -= Level.GRAVITATIONAL_ACCELERATION * deltaTime;
                object.y += object.velocityY * deltaTime;
                object.bounds.set((float) x, (float) y, bounds.getWidth(), bounds.getHeight());
            }

            rotationAngle += 1000 * deltaTime;

            if (parts.get(0).y < 0 && parts.get(1).y < 0
                    && parts.get(2).y < 0 && parts.get(3).y < 0) {
                dispose();
            }
        }
    }

    private class BreakingBricksRenderer implements IRenderable {

        @Override
        public void render(SpriteBatch spriteBatch) {

            spriteBatch.draw(animation.getCurrentRegion(),
                    (float) (parts.get(0).x -  offsetX), (float) parts.get(0).y,
                    bounds.getWidth() / 2.0f, bounds.getWidth() / 2.0f,
                    bounds.getWidth(), bounds.getHeight(), 1, 1, rotationAngle);
            spriteBatch.draw(animation.getCurrentRegion(),
                    (float) (parts.get(1).x -  offsetX), (float) parts.get(1).y,
                    bounds.getWidth() / 2.0f, bounds.getWidth() / 2.0f,
                    bounds.getWidth(), bounds.getHeight(), 1, 1, rotationAngle);
            spriteBatch.draw(animation.getCurrentRegion(),
                    (float) (parts.get(2).x -  offsetX), (float) parts.get(2).y,
                    bounds.getWidth() / 2.0f, bounds.getWidth() / 2.0f,
                    bounds.getWidth(), bounds.getHeight(), 1, 1, -rotationAngle);
            spriteBatch.draw(animation.getCurrentRegion(),
                    (float) (parts.get(3).x -  offsetX), (float) parts.get(3).y,
                    bounds.getWidth() / 2.0f, bounds.getWidth() / 2.0f,
                    bounds.getWidth(), bounds.getHeight(), 1, 1, -rotationAngle);
        }
    }
}
