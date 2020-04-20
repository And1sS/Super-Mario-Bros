package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.game.Rebuild.Level;
import com.And1sS.game.Rebuild.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class CoopaTroopa extends GameObject {

    private enum Type { DEFAULT, FLYING, SHELL, DIED_SHELL };
    private Type type = Type.DEFAULT;

    private float shellVelocity;

    public CoopaTroopa(int mapIndxX, int mapIndxY, float cellSize) {
        this(mapIndxX * cellSize, mapIndxY * cellSize,
                cellSize);
        velocityX = -6 * cellSize;
        shellVelocity = 12 * cellSize;
    }

    private CoopaTroopa(float x, float y, float cellSize) {
        super(new Rectangle(x, y, cellSize, 1.5f * cellSize),
                new Animation("images/enemies.png", 2, 96, 8, 16, 24, 3, true));

        renderer = new ReversedObjectRenderer();
        objectCollisionDetector = new CoopaTroopaGameObjectCollider();
        levelCollisionDetector = new LeftRightBounceCollider();
        updater = new CoopaTroopaUpdater();
        animationUpdater = new DefaultAnimationUpdater();
    }

    private class CoopaTroopaUpdater implements IUpdatable {

        @Override
        public void update(float deltaTime, Level level) {
            switch (type) {
                case DEFAULT:
                    if (velocityX > 0) {
                        flipAnimationXAxis = true;
                    } else {
                        flipAnimationXAxis = false;
                    }

                case SHELL: {
                    if (deltaTime < 0.025) {
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
                    }
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

                case FLYING:
                    throw new RuntimeException("Unimplemented yet!");
            }
        }
    }

    private class CoopaTroopaGameObjectCollider implements IGameObjectCollidable {

        @Override
        public void performCollisionDetection(GameObject object) {
            if (object instanceof Player && object.bounds.overlaps(bounds)) {
                Player player = (Player)object;
                if (!player.isAlive())
                    return;

                switch (type) {
                    case DEFAULT: {
                        if (player.velocityY > 0 && player.y < y) { // player kills coopa troopa
                            player.y = y - player.bounds.getHeight();
                            player.bounds.setY((float) player.y);
                            player.smallJump();

                            type = Type.SHELL;

                            bounds.setHeight(bounds.getHeight() / 2);
                            y += bounds.getHeight();
                            bounds.setY((float) y);
                            animation.set(160, 16, 16, 16);
                            animation.setSpeed(0);
                            animation.setCurrentFrame(0);

                            velocityX = 0;
                        } else if (player.getType() != Player.PlayerType.INVINCIBLE_MARIO){ // coopa troopa kills the player
                            player.resetPowerUps();
                        }

                        break;
                    }

                    case SHELL: {
                        if (player.velocityY > 0 && player.y < y) { // player kills shell
                            player.y = y - player.bounds.getHeight();
                            player.bounds.setY((float) player.y);
                            player.smallJump();

                            type = Type.DIED_SHELL;
                            objectCollisionDetector = new NotGameObjectCollidable();
                            levelCollisionDetector = new NotLevelCollidable();
                            velocityX = 0;
                        } else if (player.getType() != Player.PlayerType.INVINCIBLE_MARIO && velocityX != 0){ // shell kills player
                            player.resetPowerUps();
                        } else if (velocityX == 0) {
                            if (player.x < x && player.velocityX > 0) {
                                x = player.x + player.bounds.getWidth();
                                velocityX = shellVelocity;
                            } else if (player.x > x && player.velocityX < 0) {
                                x = player.x - bounds.getWidth();
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
