package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotUpdatableAnimation;
import com.And1sS.SuperMarioBros.Rebuild.Level;
import com.And1sS.SuperMarioBros.Rebuild.Player;
import com.badlogic.gdx.math.Rectangle;

public class Mushroom extends GameObject {
    private enum Type { POWERUP_SUPERMARIO }
    private Type type = Type.POWERUP_SUPERMARIO;

    public Mushroom(int mapIndxX, int mapIndxY, Level level) {
        this((float) mapIndxX * level.getCellSize(), (float) mapIndxY * level.getCellSize(),
                level);
        velocityX = 5 * level.getCellSize();
    }

    private Mushroom(float x, float y, Level level) {
        super(new Rectangle(x, y, level.getCellSize(), level.getCellSize()),
                new Animation(level.getObectsTexture(),0, 0, 16, 16));

        renderer = new ReversedObjectRenderer();
        objectCollisionDetector = new MushroomCollider();
        levelCollisionDetector = new LeftRightBounceCollider();
        updater = new MushroomUpdater();
        animationUpdater = new NotUpdatableAnimation();
    }

    private class MushroomUpdater implements IUpdatable {

        @Override
        public void update(float deltaTime, com.And1sS.SuperMarioBros.Rebuild.Level level) {
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
            if (object instanceof com.And1sS.SuperMarioBros.Rebuild.Player && object.bounds.overlaps(bounds)) {
                com.And1sS.SuperMarioBros.Rebuild.Player player = (Player) object;
                if (!player.isAlive()) {
                    return;
                }

                switch (type) {
                    case POWERUP_SUPERMARIO:
                        player.superMario();
                        dispose();
                        break;

                    default:
                        throw new RuntimeException("Unimplemented yet!");
                }
            }
        }
    }
}
