package com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations;

import com.And1sS.SuperMarioBros.Rebuild.GameObjects.GameObject;

public class NotGameObjectCollidable implements GameObject.IGameObjectCollidable {
    @Override
    public void performCollisionDetection(GameObject object) {
        /* Do nothing */
    }
}