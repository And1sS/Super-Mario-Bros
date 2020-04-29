package com.And1sS.SuperMarioBros.Rebuild;

import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotUpdatable;
import com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations.NotUpdatableAnimation;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.GameObject;
import com.badlogic.gdx.math.Rectangle;

public class BackgroundObject extends GameObject {

    public BackgroundObject(Rectangle bounds, Animation animation) {
        super(bounds, animation, GameObjectId.BACKGROUND_OBJECT);

        renderer = new DefaultObjectRenderer();
        updater = new NotUpdatable();
        levelCollisionDetector = new NotLevelCollidable();
        objectCollisionDetector = new NotGameObjectCollidable();
        animationUpdater = new NotUpdatableAnimation();
    }
}
