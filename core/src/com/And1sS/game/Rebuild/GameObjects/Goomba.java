package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.game.Rebuild.Level;
import com.And1sS.game.Rebuild.Player;
import com.And1sS.game.Rebuild.TileId;
import com.badlogic.gdx.math.Rectangle;

public class Goomba extends GameObject {

    boolean alive = true;

    public Goomba(int mapIndxX, int mapIndxY, float cellSize) {
        this(mapIndxX * cellSize, mapIndxY * cellSize,
                cellSize);
        velocityX = -3 * cellSize;
    }

    private Goomba(float x, float y, float cellSize) {
        super(new Rectangle(x, y, cellSize, cellSize),
            new Animation("images/enemies.png", 2, 0, 16, 16, 16, 3, true));

        renderer = new ReversedObjectRenderer();
        objectCollisionDetector = new GoombaObjectsCollider();
        levelCollisionDetector = new GoombaLevelCollider();
        updater = new GoombaUpdater();
        animationUpdater = new DefaultAnimationUpdater();
    }

    private class GoombaUpdater implements IUpdatable {

        float timeAfterDeath = 0;

        @Override
        public void update(float deltaTime, Level level) {
            if (deltaTime < 0.025 && alive) {
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
            } else if (!alive) {
                timeAfterDeath += deltaTime;
                if (timeAfterDeath > 3) {
                    dispose();
                }
            }
        }
    }

    private class GoombaLevelCollider implements ILevelCollidable {
        @Override
        public void performCollisionDetectionX(Level level) {
            try {
                _performCollisionDetectionX(level);
            } catch(Exception e) {}
        }

        private void _performCollisionDetectionX(Level level) {
            int cellSize = level.getCellSize();
            for (int i = (int) (y) / cellSize; i < (y + bounds.getHeight()) / cellSize; i++) {
                for (int j = (int) (x) / cellSize; j < (x + bounds.getWidth()) / cellSize; j++) {
                    switch(level.getCell(j, i)) {
                        case TileId.TRANSPERENT_COLLIDABLE_BLOCK: case 10: case 21:
                        case 12: case 14: case 15:
                        case TileId.SECRET_BLOCK_EMPTY: case 22: case 23:
                            if (velocityX > 0) {
                                x = j * cellSize - bounds.getWidth() - 0.001;
                                velocityX *= -1;
                                bounds.setX((float) x);
                            } else if (velocityX < 0) {
                                x = (j + 1) * cellSize;
                                velocityX *= -1;
                                bounds.setX((float) x);
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        @Override
        public void performCollisionDetectionY(Level level) {
            try {
                _performCollisionDetectionY(level);
            } catch(Exception e) {}
        }

        private void _performCollisionDetectionY(Level level) {
            int cellSize = level.getCellSize();

            for (int i = (int) (y) / cellSize; i < (y + bounds.getHeight()) / cellSize; i++) {
                for (int j = (int) (x) / cellSize; j < (x + bounds.getWidth()) / cellSize; j++) {
                    int cellType = level.getCell(j, i);
                    switch(cellType) {
                        case 1: case 10: case 12:
                        case TileId.BROWN_IRON_BLOCK:
                        case TileId.BROWN_BRICK:
                        case TileId.SECRET_BLOCK_EMPTY:
                        case TileId.SECRET_BLOCK_POWERUP_SUPERMARIO:
                        case 15: case 23:
                            defaultCollisionY(i, cellSize);
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        private void defaultCollisionY(int i, float cellSize) {
            if(velocityY > 0) {
                y = i * cellSize - bounds.getHeight();
                velocityY = 0;
                bounds.setY((float) y);
            } else if (velocityY < 0) {
                y = (i + 1) * cellSize;
                velocityY = 0;
                bounds.setY((float) y);
            }
        }
    }

    private class GoombaObjectsCollider implements IGameObjectCollidable {

        @Override
        public void performCollisionDetection(GameObject object) {
            if (object instanceof Player && object.bounds.overlaps(bounds)) {
                Player player = (Player)object;
                if (player.velocityY > 0 && player.y < y) {
                    player.y = y - player.bounds.getHeight();
                    player.bounds.setY((float) player.y);
                    player.smallJump();

                    alive = false;
                    objectCollisionDetector = new NotGameObjectCollidable();
                    levelCollisionDetector = new NotLevelCollidable();

                    bounds.setHeight(bounds.getHeight() / 2);
                    y += bounds.getHeight();
                    bounds.setY((float) y);
                    animation.setSpeed(0);
                } else if (player.getType() != Player.PlayerType.INVINCIBLE_MARIO){
                    player.resetPowerUps();
                }
            }
        }
    }
}
