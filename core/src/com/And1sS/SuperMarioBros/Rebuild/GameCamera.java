package com.And1sS.SuperMarioBros.Rebuild;

import com.And1sS.SuperMarioBros.Rebuild.GameObjects.Mario;

public class GameCamera implements Mario.IOffsetChangeable {

    public double offsetX = 0;

    @Override
    public void onOffsetChanged(double newOffsetX) {
        offsetX = newOffsetX;
    }
}


