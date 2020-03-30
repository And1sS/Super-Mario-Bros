package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.TileId;

public class BouncingBrownIronBlock extends BouncingBlock{
    public BouncingBrownIronBlock(int mapIndxX, int mapIndxY, float cellSize) {
        super(mapIndxX, mapIndxY, cellSize, TileId.BROWN_IRON_BLOCK,
                new Animation("images/map.png", 176, 0, 16, 16));
    }
}
