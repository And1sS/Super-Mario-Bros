package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.TileId;

public class BouncingBrownSecretBlock extends BouncingBlock{
    public BouncingBrownSecretBlock(int mapIndxX, int mapIndxY, Level level) {
        super(mapIndxX, mapIndxY, level.getCellSize(), TileId.SECRET_BLOCK_USED_BROWN,
                new Animation(level.getTileTextureRegion(TileId.SECRET_BLOCK_DEFAULT_1), 176, 0, 16, 16));
    }
}
