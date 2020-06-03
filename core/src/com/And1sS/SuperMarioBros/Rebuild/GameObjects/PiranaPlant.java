package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PiranaPlant extends GameObject {
    private float defaultHeight;
    private float defaultPosY;

    public PiranaPlant(int mapIndxX, int mapIndxY, Level level) {
        this(mapIndxX * level.getCellSize(), mapIndxY * level.getCellSize(),
                level.getCellSize(), level.getEnemiesTexture());

        defaultHeight = level.getCellSize();
        defaultPosY = mapIndxY * level.getCellSize();
        velocityY = level.getCellSize();
    }

    private PiranaPlant(float x, float y, float cellSize, Texture objectsTexture) {
        super(new Rectangle(x, y, cellSize, cellSize),
                new Animation(objectsTexture, 2, 12 * 16, 8, 16, 24, 1, true),
                GameObjectId.PIRANA_PLANT);

        renderer = new PiranaPlantRenderer();
        objectCollisionDetector = new PiranaPlantGameObjectCollider();
        levelCollisionDetector = new NotLevelCollidable();
        updater = new PiranaPlantUpdater();
        animationUpdater = new DefaultAnimationUpdater();
    }

    private class PiranaPlantUpdater implements IUpdatable {
        private static final float WAITING_TIME = 2f;

        private float waitingTime = 0;

        @Override
        public void update(float deltaTime, Level level) {
            if (waitingTime > 0) {
                waitingTime -= deltaTime;
                if (waitingTime < 0) {
                    waitingTime = 0;
                }
                return;
            }

            y += velocityY * deltaTime;

            if (y > defaultPosY + defaultHeight) {
                y = defaultPosY + defaultHeight;
                velocityY *= -1;
                waitingTime = WAITING_TIME;
            } else if (y < defaultPosY) {
                y = defaultPosY;
                velocityY *= -1;
                waitingTime = WAITING_TIME;
            }
            float newHeight = defaultHeight - (float) Math.abs(defaultPosY - y);
            bounds.set(bounds.x, (float) y, bounds.width, newHeight);
        }
    }

    private class PiranaPlantGameObjectCollider implements IGameObjectCollidable {

        @Override
        public void performCollisionDetection(GameObject object) {
            Rectangle collidableBounds =
                    new Rectangle(bounds.x, bounds.y + bounds.height / 4.0f,
                            bounds.width, bounds.height * 3 / 4);
            if (object instanceof Mario && object.bounds.overlaps(collidableBounds)) {
                Mario player = ((Mario) object);
                if (player.getType() != Mario.PlayerType.INVINCIBLE_MARIO)
                    player.resetPowerUps();
            }
        }
    }

    private class PiranaPlantRenderer implements IRenderable {

        @Override
        public void render(SpriteBatch sp) {
            float textureRegionHeight = 24 * bounds.height / defaultHeight;
            animation.set((int) animation.getX(),
                    (int) animation.getY(),
                    (int) animation.getDrawableWidth(),
                    (int) textureRegionHeight);

            sp.draw(animation.getCurrentRegion(),
                    (float) (x - offsetX),
                    (float) (Gdx.graphics.getHeight() - y - bounds.height),
                    bounds.getWidth(), bounds.height);
        }
    }
}
