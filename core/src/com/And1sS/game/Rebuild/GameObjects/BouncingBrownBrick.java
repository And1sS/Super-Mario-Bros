package com.And1sS.game.Rebuild.GameObjects;

import com.And1sS.game.Rebuild.Animation;
import com.And1sS.game.Rebuild.TileId;

public class BouncingBrownBrick extends BouncingBlock {
    public BouncingBrownBrick(int mapIndxX, int mapIndxY, float cellSize) {
        super(mapIndxX, mapIndxY, cellSize, TileId.BROWN_BRICK,
                new Animation("images/map.png", 161, 32, 15, 16));
    }
}
