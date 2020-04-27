package com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations;

import com.And1sS.SuperMarioBros.Rebuild.GameObjects.GameObject;
import com.And1sS.SuperMarioBros.Rebuild.Level;

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