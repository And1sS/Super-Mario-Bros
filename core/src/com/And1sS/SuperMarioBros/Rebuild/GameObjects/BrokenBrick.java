package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotUpdatableAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class BrokenBrick extends GameObject {
    // TODO: rewrite this class, implement updater which will work for every part of brick
    List <GameObject> parts;

    float rotationAngle = 0;

    public BrokenBrick(int mapIndxX, int mapIndxY, Level level, Animation animation) {
        this((float) mapIndxX * level.getCellSize(),
                (float) Gdx.graphics.getHeight() - mapIndxY * level.getCellSize(),
                level, animation);
    }

    private BrokenBrick(float x, float y, Level level, Animation animation) {
        super(new Rectangle(x, y, level.getCellSize(), level.getCellSize()),
                animation, GameObjectId.BROKEN_BRICK);

        int cellSize = level.getCellSize();
        parts = new ArrayList<>();
        Rectangle rect = new Rectangle(x, y, cellSize, cellSize);
        parts.add(new GameObject(rect, null, GameObjectId.BROKEN_BRICK_PART));
        parts.add(new GameObject(rect, null, GameObjectId.BROKEN_BRICK_PART));
        parts.add(new GameObject(rect, null, GameObjectId.BROKEN_BRICK_PART));
        parts.add(new GameObject(rect, null, GameObjectId.BROKEN_BRICK_PART));

        // 0, 1 - going left, rotating left; 2, 3 - opposite

        parts.get(0).setVelocityY(5 * cellSize);
        parts.get(1).setVelocityY(18 * cellSize);
        parts.get(2).setVelocityY(18 * cellSize);
        parts.get(3).setVelocityY(5 * cellSize);

        parts.get(0).setVelocityX(-10 * cellSize);
        parts.get(1).setVelocityX(-10 * cellSize);
        parts.get(2).setVelocityX(10 * cellSize);
        parts.get(3).setVelocityX(10 * cellSize);

        renderer = new BreakingBricksRenderer();
        updater = new BreakingBricksUpdater();
        animationUpdater = new NotUpdatableAnimation();
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
