package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.game.Rebuild.Level;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Coin extends GameObject {

    public Coin(int mapIndxX, int mapIndxY, float size) {
        this(mapIndxX * size, Gdx.graphics.getHeight() - mapIndxY * size,
                size, size);
    }

    public Coin(float x, float y, float width, float height) {
        super(new Rectangle(x, y, width, height),
            new Animation("images/objects.png", 4, 0, 96, 16, 16, 10, false));

        renderer = new DefaultObjectRenderer();
        updater = new CoinUpdater();
        levelCollisionDetector = new NotLevelCollidable();
        objectCollisionDetector = new NotGameObjectCollidable();

        velocityY = 1000 * Gdx.graphics.getHeight() / 1080.0f;
    }

    private class CoinUpdater implements IUpdatable {

        @Override
        public void update(float deltaTime, Level level) {
            velocityY -= Level.GRAVITATIONAL_ACCELERATION * deltaTime;
            y += velocityY * deltaTime;
            bounds.set((float) x, (float) y, bounds.getWidth(), bounds.getHeight());

            animation.update(deltaTime);

            if (animation.isAnimationEnded()) {
                dispose();
            }
        }

    }
}
