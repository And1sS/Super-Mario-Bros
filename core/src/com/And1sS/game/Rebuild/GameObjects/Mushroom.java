package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotUpdatable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotUpdatableAnimation;
import com.And1sS.game.Rebuild.Level;
import com.And1sS.game.Rebuild.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Mushroom extends GameObject {
    private enum Type { POWERUP_SUPERMARIO }
    private Type type = Type.POWERUP_SUPERMARIO;

    public Mushroom(int mapIndxX, int mapIndxY, float cellSize) {
        this(mapIndxX * cellSize, mapIndxY * cellSize,
                cellSize);
        velocityX = 5 * cellSize;
    }

    private Mushroom(float x, float y, float cellSize) {
        super(new Rectangle(x, y, cellSize, cellSize),
                new Animation("images/objects.png",0, 0, 16, 16));

        renderer = new ReversedObjectRenderer();
        objectCollisionDetector = new MushroomCollider();
        levelCollisionDetector = new LeftRightBounceCollider();
        updater = new MushroomUpdater();
        animationUpdater = new NotUpdatableAnimation();
    }

    private class MushroomUpdater implements IUpdatable {

        @Override
        public void update(float deltaTime, Level level) {
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

                if (y > level.getCellSize() * level.getHeight()) {
                    dispose();
                }
            }
        }
    }

    private class MushroomCollider implements IGameObjectCollidable {

        @Override
        public void performCollisionDetection(GameObject object) {
            if (object instanceof Player && object.bounds.overlaps(bounds)) {
                Player player = (Player) object;
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
