package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.game.Rebuild.Level;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Coin extends GameObject {

    public Coin(int mapIndxX, int mapIndxY, float cellSize) {
        this(mapIndxX * cellSize, Gdx.graphics.getHeight() - mapIndxY * cellSize,
                cellSize, cellSize);

        velocityY = 13 * cellSize;
    }

    private Coin(float x, float y, float width, float height) {
        super(new Rectangle(x, y, width, height),
            new Animation("images/objects.png", 4, 0, 96, 16, 16, 10, false));

        renderer = new DefaultObjectRenderer();
        updater = new CoinUpdater();
        animationUpdater = new DefaultAnimationUpdater();
        levelCollisionDetector = new NotLevelCollidable();
        objectCollisionDetector = new NotGameObjectCollidable();
    }

    private class CoinUpdater implements IUpdatable {

        @Override
        public void update(float deltaTime, Level level) {
            velocityY -= Level.GRAVITATIONAL_ACCELERATION * deltaTime;
            y += velocityY * deltaTime;
            bounds.set((float) x, (float) y, bounds.getWidth(), bounds.getHeight());

            if (animation.isAnimationEnded()) {
                dispose();
            }
        }

    }
}
