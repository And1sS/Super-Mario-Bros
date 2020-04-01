package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotUpdatableAnimation;
import com.And1sS.game.Rebuild.Level;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Platform extends GameObject {
    public enum  Type {
        LEFT_RIGHT, TOP_DOWN
    }

    private float yStart;
    private float xStart;
    private float slidingRange;

    private Type type;

    public Platform(int mapIndxX, int mapIndxY, float cellSize, Type type) {
        this(mapIndxX * cellSize, mapIndxY * cellSize,
                cellSize * 2, cellSize / 2.5f);
        this.type = type;

        switch (type) {
            case LEFT_RIGHT:
                velocityX = 2 * cellSize * Gdx.graphics.getWidth() / 1920.0f;
                velocityY = 0;
                break;

            case TOP_DOWN:
                velocityX = 0;
                velocityY = 2 * cellSize * Gdx.graphics.getWidth() / 1920.0f;
                break;
        }

        slidingRange = 2 * cellSize;
    }

    private Platform(float x, float y, float width, float height) {
        super(new Rectangle(x, y, width, height * 2),
                new Animation("images/objects.png", 64, 129, 16, 7));

        yStart = y;
        xStart = x;

        renderer = new PlatformRenderer();
        updater = new PlatformUpdater();
        animationUpdater = new NotUpdatableAnimation();
        levelCollisionDetector = new NotLevelCollidable();
        objectCollisionDetector = new PlatformObjectCollider();
    }

    private class PlatformUpdater implements IUpdatable {

        @Override
        public void update(float deltaTime, Level level) {
            if (y > yStart + slidingRange || y < yStart - slidingRange) {
                velocityY *= -1;
            }
            if (x > xStart + slidingRange || x < xStart - slidingRange) {
                velocityX *= -1;
            }

            if (y > yStart + slidingRange) {
                y = yStart + slidingRange;
            } else if (y < yStart - slidingRange) {
                y = yStart - slidingRange;
            }

            if (x > xStart + slidingRange) {
                x = xStart + slidingRange;
            } else if (x < xStart - slidingRange) {
                x = xStart - slidingRange;
            }

            y += velocityY * deltaTime;
            x += velocityX * deltaTime;
            bounds.set((float) x, (float) y, bounds.getWidth(), bounds.getHeight());
        }
    }

    private class PlatformObjectCollider implements IGameObjectCollidable {

        @Override
        public void performCollisionDetection(GameObject object) {
            if (object.bounds.overlaps(bounds) && object.y < y && object.velocityY > 0) {
                object.y = y + bounds.getHeight() / 2 - object.bounds.getHeight();
                object.velocityY = 0;
                object.bounds.setY((float) object.y);
            }
        }
    }

    private class PlatformRenderer implements IRenderable {

        @Override
        public void render(SpriteBatch spriteBatch) {
            float windowHeight = Gdx.graphics.getHeight();
            spriteBatch.draw(animation.getCurrentRegion(),
                    (float) (x - offsetX),
                    (float) (windowHeight - y - bounds.getHeight()),
                    bounds.getWidth() / 2, bounds.getHeight() / 2);
            spriteBatch.draw(animation.getCurrentRegion(),
                    (float) (x + bounds.getWidth() / 2 - offsetX),
                    (float) (windowHeight - y - bounds.getHeight()),
                    bounds.getWidth() / 2, bounds.getHeight() / 2);
        }
    }
}
