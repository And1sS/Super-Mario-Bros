package com.And1sS.game.Rebuild.InterfacesImplementations;

import com.And1sS.game.Rebuild.GameObjects.GameObject;
import com.And1sS.game.Rebuild.Level;

public class NotUpdatable implements GameObject.IUpdatable {

    @Override
    public void update(float deltaTime, Level level) {
        /* Do nothing */
    }
}
