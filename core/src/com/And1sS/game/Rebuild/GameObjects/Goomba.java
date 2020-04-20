package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.game.Rebuild.Level;
import com.And1sS.game.Rebuild.Player;
import com.And1sS.game.Rebuild.TileId;
import com.badlogic.gdx.math.Rectangle;

public class Goomba extends GameObject {

    private enum Type { DEFAULT, DIED, DIED_FLIPPED };
    private Type type = Type.DEFAULT;

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
            if (object instanceof Player && object.bounds.overlaps(bounds)) {
                Player player = (Player)object;
                if (player.velocityY > 0 && player.y < y) {
                    player.y = y - player.bounds.getHeight();
                    player.bounds.setY((float) player.y);
                    player.smallJump();

                    type = Type.DIED;
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
