package com.And1sS.game.Rebuild.InterfacesImplementations;

import com.And1sS.game.Rebuild.GameObjects.GameObject;

public class NotUpdatableAnimation implements GameObject.IAnimationUpdatable {

    @Override
    public void updateAnimation(float deltaTime) {
        /* Do nothing */
    }
}
