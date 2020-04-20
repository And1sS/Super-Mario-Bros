package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.Level;
import com.And1sS.game.Rebuild.TileId;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class GameObject {
    protected double velocityX = 0;
    protected double velocityY = 0;
    protected double offsetX = 0;

    protected double x;
    protected double y;

    protected boolean flipAnimationXAxis = false;
    protected boolean shouldBeDisposed = false;

    protected Rectangle bounds;

    protected Animation animation;

    protected IUpdatable updater;
    protected IRenderable renderer;
    protected ILevelCollidable levelCollisionDetector;
    protected IGameObjectCollidable objectCollisionDetector;
    protected IAnimationUpdatable animationUpdater;

    public GameObject() {
    }

    public GameObject(Rectangle bounds, Animation animation) {
        this.bounds = bounds;
        this.x = bounds.getX();
        this.y = bounds.getY();

        this.animation = animation;
    }

    public void recalculateBounds(int oldCellSize, int newCellSize) {
        x = x / oldCellSize * newCellSize;
        y = y / oldCellSize * newCellSize;

        float newWidth = bounds.getWidth() / oldCellSize * newCellSize;
        float newHeight = bounds.getHeight() / oldCellSize * newCellSize;

        bounds.set((float) x, (float) y,  newWidth, newHeight);
    }

    public void dispose() { shouldBeDisposed = true; }

    public double getOffsetX() {
        return offsetX;
    }

    public double getX() { return x; }

    public double getY() { return y; }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public boolean shouldBeDisposed() {
        return shouldBeDisposed || !animation.isLoadedSucessful();
    }

    public void update(float deltaTime, Level level) {
        updater.update(deltaTime, level);
    }

    public void updateAnimation(float deltaTime) {
        animationUpdater.updateAnimation(deltaTime);
    }

    public void render(SpriteBatch spriteBatch) {
        renderer.render(spriteBatch);
    }

    public void performLevelCollisionDetectionX(Level level) {
        levelCollisionDetector.performCollisionDetectionX(level);
    }

    public void performLevelCollisionDetectionY(Level level) {
        levelCollisionDetector.performCollisionDetectionY(level);
    }

    public void performObjectCollisionDetection(GameObject object) {
        objectCollisionDetector.performCollisionDetection(object);
    }

    public interface IUpdatable {
        void update(float deltaTime, Level level);
    }

    public interface ILevelCollidable {
        void performCollisionDetectionX(Level level);
        void performCollisionDetectionY(Level level);
    }

    public interface IGameObjectCollidable {
        void performCollisionDetection(GameObject object);
    }

    public interface IRenderable {
        void render(SpriteBatch sp);
    }

    public interface IAnimationUpdatable {
        void updateAnimation(float deltaTime);
    }

    public class DefaultObjectRenderer implements IRenderable {

        @Override
        public void render(SpriteBatch spriteBatch) {
            if (flipAnimationXAxis) {
                spriteBatch.draw(animation.getCurrentRegion(),
                        (float) (x + bounds.getWidth() -  offsetX),
                        (float) y,
                        -bounds.getWidth(), bounds.getHeight());
            } else {
                spriteBatch.draw(animation.getCurrentRegion(),
                        (float) (x - offsetX),
                        (float) y,
                        bounds.getWidth(), bounds.getHeight());
            }
        }

    }

    public class ReversedObjectRenderer implements IRenderable {

        @Override
        public void render(SpriteBatch spriteBatch) {
            float windowHeight = Gdx.graphics.getHeight();
            if (flipAnimationXAxis) {
                spriteBatch.draw(animation.getCurrentRegion(),
                        (float) (x + bounds.getWidth() -  offsetX),
                        (float) (windowHeight - y - bounds.getHeight()),
                        -bounds.getWidth(), bounds.getHeight());
            } else {
                spriteBatch.draw(animation.getCurrentRegion(),
                        (float) (x - offsetX),
                        (float) (windowHeight - y - bounds.getHeight()),
                        bounds.getWidth(), bounds.getHeight());
            }
        }

    }

    public class DefaultAnimationUpdater implements IAnimationUpdatable {

        @Override
        public void updateAnimation(float deltaTime) {
            animation.update(deltaTime);
        }
    }

    public class LeftRightBounceCollider implements ILevelCollidable {
        @Override
        public void performCollisionDetectionX(Level level) {
            try {
                _performCollisionDetectionX(level);
            } catch(Exception e) {}
        }

        private void _performCollisionDetectionX(Level level) {
            int cellSize = level.getCellSize();
            for (int i = (int) (y) / cellSize; i < (y + bounds.getHeight()) / cellSize; i++) {
                for (int j = (int) (x) / cellSize; j < (x + bounds.getWidth()) / cellSize; j++) {
                    switch(level.getCell(j, i)) {
                        case TileId.TRANSPARENT_COLLIDABLE_BLOCK: case 10: case 21:
                        case 12: case 14: case 15:
                        case TileId.SECRET_BLOCK_EMPTY: case 22: case 23:
                            if (velocityX > 0) {
                                x = j * cellSize - bounds.getWidth() - 0.001;
                                velocityX *= -1;
                                bounds.setX((float) x);
                            } else if (velocityX < 0) {
                                x = (j + 1) * cellSize;
                                velocityX *= -1;
                                bounds.setX((float) x);
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        @Override
        public void performCollisionDetectionY(Level level) {
            try {
                _performCollisionDetectionY(level);
            } catch(Exception e) {}
        }

        private void _performCollisionDetectionY(Level level) {
            int cellSize = level.getCellSize();

            for (int i = (int) (y) / cellSize; i < (y + bounds.getHeight()) / cellSize; i++) {
                for (int j = (int) (x) / cellSize; j < (x + bounds.getWidth()) / cellSize; j++) {
                    int cellType = level.getCell(j, i);
                    switch(cellType) {
                        case TileId.TRANSPARENT_COLLIDABLE_BLOCK: case 10: case 12:
                        case TileId.BROWN_IRON_BLOCK:
                        case TileId.BROWN_BRICK:
                        case TileId.SECRET_BLOCK_EMPTY:
                        case TileId.SECRET_BLOCK_POWERUP_SUPERMARIO:
                        case 15: case 23:
                            defaultCollisionY(i, cellSize);
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        private void defaultCollisionY(int i, float cellSize) {
            if(velocityY > 0) {
                y = i * cellSize - bounds.getHeight();
                velocityY = 0;
                bounds.setY((float) y);
            } else if (velocityY < 0) {
                y = (i + 1) * cellSize;
                velocityY = 0;
                bounds.setY((float) y);
            }
        }
    }
}
