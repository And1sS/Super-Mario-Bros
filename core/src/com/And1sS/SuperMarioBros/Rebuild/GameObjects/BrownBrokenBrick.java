package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;

public class BrownBrokenBrick extends BrokenBrick {
    public BrownBrokenBrick(int mapIndxX, int mapIndxY, Level level) {
        super(mapIndxX, mapIndxY, level,
                new Animation(level.getObjectsTexture(), 4 * 16, 16, 16, 16));
    }
}
