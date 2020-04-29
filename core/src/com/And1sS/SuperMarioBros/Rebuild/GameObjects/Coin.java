package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.Level;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Coin extends GameObject {

    public Coin(int mapIndxX, int mapIndxY, Level level) {
        this(mapIndxX * level.getCellSize(), Gdx.graphics.getHeight() - mapIndxY * level.getCellSize(),
               level.getCellSize(), level.getCellSize(), level.getObectsTexture());

        velocityY = 13 * level.getCellSize();
    }

    private Coin(float x, float y, float width, float height, Texture objectsTexture) {
        super(new Rectangle(x, y, width, height),
            new Animation(objectsTexture, 4, 0, 96, 16, 16, 10, false),
                GameObjectId.COIN);

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
