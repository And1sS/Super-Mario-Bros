package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.TileId;

public class BouncingBlueBrick extends BouncingBlock {
    public BouncingBlueBrick(int mapIndxX, int mapIndxY, Level level) {
        super(mapIndxX, mapIndxY, level.getCellSize(), TileId.BLUE_BRICK_BLOCK,
                new Animation(level.getTileTextureRegion(TileId.BLUE_BRICK_BLOCK), 161, 32, 15, 16));
    }
}