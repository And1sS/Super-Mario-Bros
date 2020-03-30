package com.And1sS.game.Rebuild;

import com.And1sS.game.Rebuild.GameObjects.GameObject;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotGameObjectCollidable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotLevelCollidable;
import com.And1sS.game.Rebuild.InterfacesImplementations.NotUpdatable;
import com.badlogic.gdx.math.Rectangle;

public class BackgroundObject extends GameObject {

    public BackgroundObject(Rectangle bounds, Animation animation) {
        super(bounds, animation);

        renderer = new DefaultObjectRenderer();
        updater = new NotUpdatable();
        levelCollisionDetector = new NotLevelCollidable();
        objectCollisionDetector = new NotGameObjectCollidable();
    }
}
