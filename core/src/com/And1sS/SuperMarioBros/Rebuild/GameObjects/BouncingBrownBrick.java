package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.Level;
import com.And1sS.SuperMarioBros.Rebuild.TileId;

public class BouncingBrownBrick extends BouncingBlock {
    public BouncingBrownBrick(int mapIndxX, int mapIndxY, Level level) {
        super(mapIndxX, mapIndxY, level.getCellSize(), TileId.BROWN_BRICK_BLOCK,
                new Animation(level.getTileTextureRegion(TileId.BROWN_BRICK_BLOCK), 161, 32, 15, 16));
    }
}
