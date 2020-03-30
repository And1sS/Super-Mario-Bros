package com.And1sS.game.Rebuild.InterfacesImplementations;

import com.And1sS.game.Rebuild.GameObjects.GameObject;
import com.And1sS.game.Rebuild.Level;

public class NotLevelCollidable implements GameObject.ILevelCollidable {

    @Override
    public void performCollisionDetectionX(Level level) {
        /* Do nothing */
    }

    @Override
    public void performCollisionDetectionY(Level level) {
        /* Do nothing */
    }
}