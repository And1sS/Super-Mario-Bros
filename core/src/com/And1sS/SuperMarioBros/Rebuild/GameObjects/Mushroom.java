package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotUpdatableAnimation;
import com.badlogic.gdx.math.Rectangle;

public class Mushroom extends GameObject {
    private enum Type { POWERUP_SUPERMARIO }
    private final Type type = Type.POWERUP_SUPERMARIO;

    public Mushroom(int mapIndxX, int mapIndxY, Level level) {
        this((float) mapIndxX * level.getCellSize(), (float) mapIndxY * level.getCellSize(),
                level);
        velocityX = 5 * level.getCellSize();
    }

    private Mushroom(float x, float y, Level level) {
        super(new Rectangle(x, y, level.getCellSize(), level.getCellSize()),
                new Animation(level.getObjectsTexture(),0, 0, 16, 16),
                GameObjectId.MUSHROOM);

        renderer = new ReversedObjectRenderer();
        objectCollisionDetector = new MushroomCollider();
        levelCollisionDetector = new LeftRightBounceCollider();
        updater = new MushroomUpdater();
        animationUpdater = new NotUpdatableAnimation();
    }

    private class MushroomUpdater implements IUpdatable {

        @Override
        public void update(float deltaTime, Level level) {
            //updating velocityY
            velocityY += Level.GRAVITATIONAL_ACCELERATION * deltaTime;

            //updating X
            x += velocityX * deltaTime;
            bounds.setX((float) x);
            performLevelCollisionDetectionX(level);

            //updating Y
            y += velocityY * deltaTime;
            bounds.setY((float) y);
            performLevelCollisionDetectionY(level);

            if (y > level.getCellSize() * level.getHeight()) {
                dispose();
            }
        }
    }

    private class MushroomCollider implements IGameObjectCollidable {

        @Override
        public void performCollisionDetection(GameObject object) {
            if (object instanceof Mario && object.bounds.overlaps(bounds)) {
                Mario mario = (Mario) object;
                if (!mario.isAlive()) {
                    return;
                }

                switch (type) {
                    case POWERUP_SUPERMARIO:
                        mario.superMario();
                        dispose();
                        break;

                    default:
                        throw new RuntimeException("Unimplemented yet!");
                }
            }
        }
    }
}
