package com.And1sS.SuperMarioBros.Rebuild.InterfacesImplementations;

import com.And1sS.SuperMarioBros.Rebuild.GameObjects.GameObject;
import com.And1sS.SuperMarioBros.Rebuild.Level;

public class NotUpdatable implements GameObject.IUpdatable {

    @Override
    public void update(float deltaTime, Level level) {
        /* Do nothing */
    }
}
