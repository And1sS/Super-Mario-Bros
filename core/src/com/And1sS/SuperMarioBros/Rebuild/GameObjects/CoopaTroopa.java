package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class CoopaTroopa extends GameObject {

    public enum Type { DEFAULT, FLYING, SHELL, DIED_SHELL }

    private Type type = Type.DEFAULT;

    private float shellVelocity;

    public CoopaTroopa(int mapIndxX, int mapIndxY, Level level, Type type) {
        this(mapIndxX * level.getCellSize(), mapIndxY * level.getCellSize(),
                level.getCellSize(), level.getEnemiesTexture(), type);

        velocityX = -6 * level.getCellSize();
        shellVelocity = 12 * level.getCellSize();
    }

    public CoopaTroopa(int mapIndxX, int mapIndxY, Level level) {
        this(mapIndxX, mapIndxY, level, Type.DEFAULT);
    }

    private CoopaTroopa(float x, float y, float cellSize, Texture objectsTexture) {
        this(x, y, cellSize, objectsTexture, Type.DEFAULT);
    }

    private CoopaTroopa(float x, float y, float cellSize, Texture objectsTexture, Type type) {
        super(new Rectangle(x, y, cellSize, 1.5f * cellSize), null, GameObjectId.TEMPRORARY_ID);

        Animation animation;
        switch (type) {
            case DEFAULT:
                this.id = GameObjectId.COOPA_TROOPA;
                animation = new Animation("images/enemies.png", 2, 96, 8, 16, 24, 3, true);
                break;

            case FLYING:
                this.id = GameObjectId.FLYING_COOPA_TROOPA;
                animation = new Animation("images/enemies.png", 2, 128, 8, 16, 24, 3, true);
                break;

            default:
                throw new IllegalStateException("Unexpected value of CoopaTroopa type: " + type);
        }

        this.animation = animation;
        this.type = type;

        renderer = new ReversedObjectRenderer();
        objectCollisionDetector = new CoopaTroopaGameObjectCollider();
        levelCollisionDetector = new LeftRightBounceCollider();
        updater = new CoopaTroopaUpdater();
        animationUpdater = new DefaultAnimationUpdater();
    }

    private class CoopaTroopaUpdater implements IUpdatable {

        private float timeOnFloor = 0;

        private static final float MAX_TIME_ON_FLOOR = 1;
        @Override
        public void update(float deltaTime,  Level level) {
            switch (type) {
                case FLYING:
                    if (velocityY == 0) {
                        timeOnFloor += deltaTime;

                        if (timeOnFloor >= MAX_TIME_ON_FLOOR) {
                            velocityY = -15 * level.getCellSize();
                            timeOnFloor = 0;
                        }
                    }

                case DEFAULT:
                    flipAnimationXAxis = velocityX > 0;

                case SHELL: {
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

                case DIED_SHELL: {
                    //updating velocityY
                    velocityY += Level.GRAVITATIONAL_ACCELERATION * deltaTime;

                    y += velocityY * deltaTime;
                    bounds.setY((float) y);

                    if (y > level.getHeight() * level.getCellSize()) {
                        dispose();
                    }
                    break;
                }
            }
        }
    }

    private class CoopaTroopaGameObjectCollider implements IGameObjectCollidable {

        @Override
        public void performCollisionDetection(GameObject object) {
            if (object instanceof Mario && object.bounds.overlaps(bounds)) {
                Mario mario = (Mario)object;
                if (!mario.isAlive())
                    return;

                switch (type) {
                    case DEFAULT:
                    case FLYING: {
                        if (mario.velocityY > 0 && mario.y < y) { // player kills coopa troopa
                            mario.y = y - mario.bounds.getHeight();
                            mario.bounds.setY((float) mario.y);
                            mario.smallJump();

                            type = Type.SHELL;

                            bounds.setHeight(bounds.getHeight() / 2);
                            y += bounds.getHeight();
                            bounds.setY((float) y);
                            animation.set(160, 16, 16, 16);
                            animation.setSpeed(0);
                            animation.setCurrentFrame(0);

                            velocityX = 0;
                        } else if (mario.getType() != Mario.PlayerType.INVINCIBLE_MARIO){ // coopa troopa kills the player
                            mario.resetPowerUps();
                        }

                        break;
                    }

                    case SHELL: {
                        if (mario.velocityY > 0 && mario.y < y) { // player kills shell
                            mario.y = y - mario.bounds.getHeight();
                            mario.bounds.setY((float) mario.y);
                            mario.smallJump();

                            type = Type.DIED_SHELL;
                            objectCollisionDetector = new NotGameObjectCollidable();
                            levelCollisionDetector = new NotLevelCollidable();
                            velocityX = 0;
                        } else if (mario.getType() != Mario.PlayerType.INVINCIBLE_MARIO && velocityX != 0){ // shell kills player
                            mario.resetPowerUps();
                        } else if (velocityX == 0) {
                            if (mario.x < x && mario.velocityX > 0) {
                                x = mario.x + mario.bounds.getWidth();
                                velocityX = shellVelocity;
                            } else if (mario.x > x && mario.velocityX < 0) {
                                x = mario.x - bounds.getWidth();
                                velocityX = -shellVelocity;
                            }
                        } // player tries to move shell

                        break;
                    }
                }
            }
        }
    }
}
