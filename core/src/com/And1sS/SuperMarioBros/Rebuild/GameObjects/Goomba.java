package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Goomba extends GameObject {

    private enum Type { DEFAULT, DIED, DIED_FLIPPED }

    private Type type = Type.DEFAULT;

    public Goomba(int mapIndxX, int mapIndxY, Level level) {
        this(mapIndxX * level.getCellSize(), mapIndxY * level.getCellSize(),
                level.getCellSize(), level.getEnemiesTexture());
        velocityX = -3 * level.getCellSize();
    }

    private Goomba(float x, float y, float cellSize, Texture objectsTexture) {
        super(new Rectangle(x, y, cellSize, cellSize),
            new Animation(objectsTexture, 2, 0, 16, 16, 16, 3, true),
                GameObjectId.GOOMBA);

        renderer = new ReversedObjectRenderer();
        objectCollisionDetector = new GoombaObjectsCollider();
        levelCollisionDetector = new LeftRightBounceCollider();
        updater = new GoombaUpdater();
        animationUpdater = new DefaultAnimationUpdater();
    }

    private class GoombaUpdater implements IUpdatable {

        float timeAfterDeath = 0;

        @Override
        public void update(float deltaTime, Level level) {
            switch (type) {
                case DEFAULT: {
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
                    break;
                }


                case DIED:
                    timeAfterDeath += deltaTime;
                    if (timeAfterDeath > 3) {
                        dispose();
                    }
                    break;

                case DIED_FLIPPED:
                    throw new RuntimeException("Unimplemented yet");
            }
        }
    }

    private class GoombaObjectsCollider implements IGameObjectCollidable {

        @Override
        public void performCollisionDetection(GameObject object) {
            if (object instanceof Mario && object.bounds.overlaps(bounds)) {
                Mario mario = (Mario)object;
                if (!mario.isAlive())
                    return;

                if (mario.velocityY > 0 && mario.y < y) {
                    mario.y = y - mario.bounds.getHeight();
                    mario.bounds.setY((float) mario.y);
                    mario.smallJump();

                    type = Type.DIED;
                    objectCollisionDetector = new NotGameObjectCollidable();
                    levelCollisionDetector = new NotLevelCollidable();

                    bounds.setHeight(bounds.getHeight() / 2);
                    y += bounds.getHeight();
                    bounds.setY((float) y);
                    animation.setSpeed(0);
                } else if (mario.getType() != Mario.PlayerType.INVINCIBLE_MARIO){
                    mario.resetPowerUps();
                }
            }
        }
    }
}
