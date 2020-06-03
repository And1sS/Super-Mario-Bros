package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotUpdatable;
import com.badlogic.gdx.math.Rectangle;

public class CollectableCoin extends GameObject {
    public CollectableCoin(int mapIndxX, int mapIndxY, Level level) {
        super(new Rectangle(mapIndxX * level.getCellSize(), mapIndxY * level.getCellSize(),
                        level.getCellSize(), level.getCellSize()),
                new Animation(level.getObjectsTexture(), 4, 0, 80, 16, 16, 5, true),
                GameObjectId.COLLECTABLE_COIN);

        levelCollisionDetector = new NotLevelCollidable();
        objectCollisionDetector = new CollectableCoinGameObjectCollider();
        updater = new NotUpdatable();
        animationUpdater = new DefaultAnimationUpdater();
        renderer = new ReversedObjectRenderer();
    }

    private class CollectableCoinGameObjectCollider implements IGameObjectCollidable {

        @Override
        public void performCollisionDetection(GameObject object) {
            if (object instanceof Mario && object.bounds.overlaps(bounds)) {
                dispose();
                ((Mario) object).addCoin();
            }
        }
    }
}
