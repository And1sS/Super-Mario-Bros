package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotUpdatableAnimation;
import com.And1sS.SuperMarioBros.Rebuild.Level;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class BouncingBlock extends GameObject {
    private int tileId;

    private int mapIndxX;
    private int mapIndxY;

    public BouncingBlock(int mapIndxX, int mapIndxY, float cellSize, int tileId, Animation animation) {
        super(new Rectangle(mapIndxX * cellSize, Gdx.graphics.getHeight() - (mapIndxY + 1) * cellSize, cellSize, cellSize),
                animation);
        this.mapIndxX = mapIndxX;
        this.mapIndxY = mapIndxY;
        this.tileId = tileId;

        levelCollisionDetector = new NotLevelCollidable();
        objectCollisionDetector = new NotGameObjectCollidable();
        updater = new BouncingBlockUpdater();
        animationUpdater = new NotUpdatableAnimation();
        renderer = new DefaultObjectRenderer();

        velocityX = 0;
        velocityY = 350 * Gdx.graphics.getHeight() / 1080.0f;
    }

    private class BouncingBlockUpdater implements IUpdatable {

        @Override
        public void update(float deltaTime, Level level) {
            velocityY -= Level.GRAVITATIONAL_ACCELERATION * deltaTime;
            y += velocityY * deltaTime;
            bounds.set((float) x, (float) y, bounds.getWidth(), bounds.getHeight());

            if (y < Gdx.graphics.getHeight() - (mapIndxY + 1) * bounds.getWidth()) {
                dispose();
                level.setCell(mapIndxX, mapIndxY, tileId);
            }
        }
    }
}
