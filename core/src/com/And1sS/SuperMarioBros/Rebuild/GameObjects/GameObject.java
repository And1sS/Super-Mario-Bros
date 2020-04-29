package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Level;
import com.And1sS.SuperMarioBros.Rebuild.TileId;
import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class GameObject {
    protected int id;

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

    public GameObject(Rectangle bounds, Animation animation, int id) {
        this.bounds = bounds;
        this.x = bounds.getX();
        this.y = bounds.getY();

        this.animation = animation;
        this.id = id;
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

    public double getWidth() { return bounds.width; }

    public double getHeight() { return bounds.height; }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public int getId() {
        return id;
    }

    public Animation getAnimation() { return animation; }

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

    public void update(float deltaTime, com.And1sS.SuperMarioBros.Rebuild.Level level) {
        updater.update(deltaTime, level);
    }

    public void updateAnimation(float deltaTime) {
        animationUpdater.updateAnimation(deltaTime);
    }

    public void render(SpriteBatch spriteBatch) {
        renderer.render(spriteBatch);
    }

    public void performLevelCollisionDetectionX(com.And1sS.SuperMarioBros.Rebuild.Level level) {
        levelCollisionDetector.performCollisionDetectionX(level);
    }

    public void performLevelCollisionDetectionY(com.And1sS.SuperMarioBros.Rebuild.Level level) {
        levelCollisionDetector.performCollisionDetectionY(level);
    }

    public void performObjectCollisionDetection(GameObject object) {
        objectCollisionDetector.performCollisionDetection(object);
    }

    public interface IUpdatable {
        void update(float deltaTime, com.And1sS.SuperMarioBros.Rebuild.Level level);
    }

    public interface ILevelCollidable {
        void performCollisionDetectionX(com.And1sS.SuperMarioBros.Rebuild.Level level);
        void performCollisionDetectionY(com.And1sS.SuperMarioBros.Rebuild.Level level);
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
        public void performCollisionDetectionX(com.And1sS.SuperMarioBros.Rebuild.Level level) {
            try {
                _performCollisionDetectionX(level);
            } catch(Exception e) {}
        }

        private void _performCollisionDetectionX(Level level) {
            int cellSize = level.getCellSize();
            for (int i = (int) (y) / cellSize; i < (y + bounds.getHeight()) / cellSize; i++) {
                for (int j = (int) (x) / cellSize; j < (x + bounds.getWidth()) / cellSize; j++) {
                    switch(level.getCell(j, i)) {
                        case TileId.TRANSPARENT_COLLIDABLE_BLOCK:
                        case TileId.BROWN_BRICK_BLOCK:
                        case TileId.BLUE_BRICK_BLOCK:
                        case TileId.GREEN_BRICK_BLOCK:
                        case TileId.GRAY_BRICK_BLOCK:
                        case TileId.BROWN_IRON_BLOCK:
                        case TileId.BLUE_IRON_BLOCK:
                        case TileId.BROWN_STONE_BLOCK:
                        case TileId.BLUE_STONE_BLOCK:
                        case TileId.GRAY_STONE_BLOCK:
                        case TileId.BROWN_WAVE_BLOCK:
                        case TileId.BLUE_WAVE_BLOCK:
                        case TileId.SECRET_BLOCK_USED_BROWN:
                        case TileId.SECRET_BLOCK_USED_BLUE:
                        case TileId.SECRET_BLOCK_USED_GREEN:
                        case TileId.SECRET_BLOCK_USED_GRAY:
                        case TileId.SECRET_BLOCK_EMPTY:
                        case TileId.SECRET_BLOCK_POWERUP_SUPERMARIO: {
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
                        }

                        default:
                            break;
                    }
                }
            }
        }

        @Override
        public void performCollisionDetectionY(com.And1sS.SuperMarioBros.Rebuild.Level level) {
            try {
                _performCollisionDetectionY(level);
            } catch(Exception e) {}
        }

        private void _performCollisionDetectionY(Level level) {
            int cellSize = level.getCellSize();

            for (int i = (int) (y) / cellSize; i < (y + bounds.getHeight()) / cellSize; i++) {
                for (int j = (int) (x) / cellSize; j < (x + bounds.getWidth()) / cellSize; j++) {
                    int cellType = level.getCell(j, i);
                    switch (cellType) {
                        case TileId.TRANSPARENT_COLLIDABLE_BLOCK:
                        case TileId.BROWN_BRICK_BLOCK:
                        case TileId.BLUE_BRICK_BLOCK:
                        case TileId.GREEN_BRICK_BLOCK:
                        case TileId.GRAY_BRICK_BLOCK:
                        case TileId.BROWN_IRON_BLOCK:
                        case TileId.BLUE_IRON_BLOCK:
                        case TileId.BROWN_STONE_BLOCK:
                        case TileId.BLUE_STONE_BLOCK:
                        case TileId.GRAY_STONE_BLOCK:
                        case TileId.BROWN_WAVE_BLOCK:
                        case TileId.BLUE_WAVE_BLOCK:
                        case TileId.SECRET_BLOCK_USED_BROWN:
                        case TileId.SECRET_BLOCK_USED_BLUE:
                        case TileId.SECRET_BLOCK_USED_GREEN:
                        case TileId.SECRET_BLOCK_USED_GRAY:
                        case TileId.SECRET_BLOCK_EMPTY:
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
