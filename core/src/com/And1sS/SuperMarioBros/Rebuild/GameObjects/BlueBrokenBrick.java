package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;

public class BlueBrokenBrick extends BrokenBrick {
    public BlueBrokenBrick(int mapIndxX, int mapIndxY, Level level) {
        super(mapIndxX, mapIndxY, level,
                new Animation(level.getObjectsTexture(), 13 * 16, 16, 16, 16));
    }
}
