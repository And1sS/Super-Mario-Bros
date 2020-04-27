package com.And1sS.SuperMarioBros.Rebuild.LevelEditor;

import com.And1sS.SuperMarioBros.Rebuild.Level;
import com.And1sS.SuperMarioBros.Rebuild.TileId;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameObjectPicker {
    private static final int blocksInRow = 7;
    private static final int blocksInColumn = 5;

    private static float sectionPaddingPercentage = 0.01f;
    private static float sectionWidthPercentage = 0.95f;        // sectionWidth = ScreenWidth * sectionWidthPercentage

    public enum PickedObjectType {
        TILE, STATIC_GAME_OBJECT, DYNAMIC_GAME_OBJECT, NONE
    }
    private PickedObjectType lastPickedObjectType = PickedObjectType.TILE;

    private Rectangle bounds;
    private Rectangle tileSectionBounds;

    private int lastPickedTileId = TileId.BROWN_WAVE_BLOCK;

    private float blockSize;
    private float sectionPadding;


    public GameObjectPicker(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);

        sectionPadding = sectionPaddingPercentage * (sectionWidthPercentage * width);
        blockSize = (sectionWidthPercentage - 2 * sectionPaddingPercentage) * width / blocksInRow;

        tileSectionBounds = new Rectangle((1 - sectionWidthPercentage) / 2.0f * width, 2 * (1 - sectionWidthPercentage) / 2.0f * width,
                sectionWidthPercentage * width, blocksInColumn * blockSize + 2 * sectionPadding);
    }

    public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, Level level) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(Color.BLACK);
        drawRectWithOffset(tileSectionBounds, shapeRenderer);
        shapeRenderer.end();

        drawTileSection(spriteBatch, shapeRenderer, level);
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            int x = Gdx.input.getX();
            int y = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (insideTileSection(x, y)) {
                int gridIndxX = (int)((x - bounds.x - tileSectionBounds.x - sectionPadding) / blockSize);
                int gridIndxY = (int)((y - bounds.y - tileSectionBounds.y - sectionPadding) / blockSize);

                lastPickedObjectType = PickedObjectType.TILE;
                lastPickedTileId = gridIndxY * blocksInRow + gridIndxX;
            }
        }
    }

    private boolean insideTileSection(int x, int y) {
        return x > bounds.x + tileSectionBounds.x + sectionPadding
                && x < bounds.x + tileSectionBounds.x + tileSectionBounds.width - sectionPadding
                && y > bounds.y + tileSectionBounds.y + sectionPadding
                && y < bounds.y + tileSectionBounds.y + tileSectionBounds.height - sectionPadding;
    }

    private void drawTileSection(SpriteBatch spriteBatch, ShapeRenderer renderer, Level level) {
        spriteBatch.begin();
        for (int i = 0; i < blocksInColumn; i++) {
            for(int j = 0; j < blocksInRow; j++) {
                spriteBatch.draw(level.getTileTextureRegion(i * blocksInRow + j),
                        bounds.x + tileSectionBounds.x + j * blockSize + sectionPadding,
                        bounds.y + tileSectionBounds.y + i * blockSize + sectionPadding,
                        blockSize, blockSize);
            }
        }
        spriteBatch.end();

        float pickedTileX = lastPickedTileId % blocksInRow * blockSize;
        float pickedTileY = lastPickedTileId / blocksInRow * blockSize;

        float leftX = bounds.x + tileSectionBounds.x + sectionPadding + pickedTileX;
        float rightX = leftX + blockSize;
        float bottomY = bounds.y + tileSectionBounds.y + sectionPadding + pickedTileY;
        float topY = bottomY + blockSize;

        renderer.setColor(Color.RED);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rectLine(new Vector2(leftX, bottomY), new Vector2(leftX, topY), 10);
        renderer.rectLine(new Vector2(rightX, bottomY), new Vector2(rightX, topY), 10);
        renderer.rectLine(new Vector2(leftX, bottomY), new Vector2(rightX, bottomY), 10);
        renderer.rectLine(new Vector2(leftX, topY), new Vector2(rightX, topY), 10);
        renderer.end();
    }

    private void drawRectWithOffset(Rectangle rectangle, ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(bounds.x + rectangle.x, bounds.y + rectangle.y,
                rectangle.width, rectangle.height);
    }

    public PickedObjectType getLastPickedObjectType() {
        return lastPickedObjectType;
    }

    public int getLastPickedTileId() {
        return lastPickedTileId;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
