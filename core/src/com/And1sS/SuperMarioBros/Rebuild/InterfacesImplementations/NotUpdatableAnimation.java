package com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations;

import com.And1sS.SuperMarioBros.Rebuild.GameObjects.GameObject;

public class NotUpdatableAnimation implements GameObject.IAnimationUpdatable {

    @Override
    public void updateAnimation(float deltaTime) {
        /* Do nothing */
    }
}
