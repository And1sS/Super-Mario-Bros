package com.And1sS.game.Rebuild.InterfacesImplementations;

import com.And1sS.game.Rebuild.GameObjects.GameObject;

public class NotGameObjectCollidable implements GameObject.IGameObjectCollidable {
    @Override
    public void performCollisionDetection(GameObject object) {
        /* Do nothing */
    }
}