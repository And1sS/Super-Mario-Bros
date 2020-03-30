package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.Level;
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

    public GameObject() {}

    public GameObject(Rectangle bounds, Animation animation) {
        this.bounds = bounds;
        this.x = bounds.getX();
        this.y = bounds.getY();

        this.animation = animation;
    }

    public void dispose() { shouldBeDisposed = true; }

    public double getOffsetX() {
        return offsetX;
    }

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

    public class DefaultObjectRenderer implements GameObject.IRenderable {

        @Override
        public void render(SpriteBatch spriteBatch) {
            spriteBatch.draw(animation.getCurrentRegion(),
                    bounds.getX() - (float) offsetX,
                    bounds.getY(),
                    bounds.getWidth(), bounds.getHeight());
        }

    }
}
