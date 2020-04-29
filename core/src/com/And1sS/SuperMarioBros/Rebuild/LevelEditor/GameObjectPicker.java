package com.And1sS.SuperMarioBros.Rebuild.LevelEditor;

import com.And1sS.SuperMarioBros.Rebuild.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.GameObject;
import com.And1sS.SuperMarioBros.Rebuild.Level;
import com.And1sS.SuperMarioBros.Rebuild.TileId;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.NotNull;

public class GameObjectPicker {
    private static final int[] AVAILABLE_TILES = {
            TileId.TRANSPARENT_NOT_COLLIDABLE_BLOCK,
            TileId.TRANSPARENT_COLLIDABLE_BLOCK,
            TileId.BROWN_WAVE_BLOCK,
            TileId.BLUE_WAVE_BLOCK,
            TileId.GRAY_WAVE_BLOCK,
            TileId.BROWN_STONE_BLOCK,
            TileId.BLUE_STONE_BLOCK,
            TileId.GRAY_STONE_BLOCK,
            TileId.BROWN_IRON_BLOCK,
            TileId.BLUE_IRON_BLOCK,
            TileId.GREEN_IRON_BLOCK,
            TileId.GRAY_IRON_BLOCK,
            TileId.BROWN_BRICK_BLOCK,
            TileId.BLUE_BRICK_BLOCK,
            TileId.GRAY_BRICK_BLOCK,
            TileId.GREEN_BRICK_BLOCK,
            TileId.SECRET_BLOCK_USED_BROWN,
            TileId.SECRET_BLOCK_USED_BLUE,
            TileId.SECRET_BLOCK_USED_GRAY,
            TileId.SECRET_BLOCK_USED_GREEN,
            TileId.SECRET_BLOCK_EMPTY,
            TileId.SECRET_BLOCK_POWERUP_SUPERMARIO,
    };

    private static final int[] AVAILABLE_DYNAMIC_OBJECTS = {
            GameObjectId.GOOMBA,
            GameObjectId.COOPA_TROOPA,
            GameObjectId.PLATFORM_LEFT_RIGHT,
            GameObjectId.PLATFORM_TOP_DOWN
    };

    private static final String TITLE_TILE_SECTION = "Tiles:";
    private static final String TITLE_DYNAMIC_OBJECTS_SECTION = "Dynamic objects:";

    private static final int blocksInRow = 7;
    private int blocksInColumn;

    private static float sectionPaddingPercentage = 0.01f;
    private static float sectionWidthPercentage = 0.95f;        // sectionWidth = ScreenWidth * sectionWidthPercentage

    public enum PickedObjectType {
        TILE, STATIC_GAME_OBJECT, DYNAMIC_GAME_OBJECT, NONE
    }
    private PickedObjectType lastPickedObjectType = PickedObjectType.TILE;

    private Rectangle bounds;
    private Rectangle tileSectionBounds;
    private Rectangle dynamicGameObjectSectionBounds;

    private BitmapFont font;
    private GlyphLayout glyphLayout;

    private int lastPickedTileId = TileId.BROWN_WAVE_BLOCK;
    private int lastPickedGameObjectId = GameObjectId.GOOMBA;

    private float blockSize;
    private int sectionPadding;

    public GameObjectPicker(float x, float y, float width, float height) {
        font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
        font.getData().setScale(0.5f * Gdx.graphics.getWidth() / 1920.0f);

        glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, TITLE_TILE_SECTION);

        bounds = new Rectangle(x, y, width, height);

        sectionPadding = (int) (sectionPaddingPercentage * (sectionWidthPercentage * width));
        blockSize = (sectionWidthPercentage - 2 * sectionPaddingPercentage) * width / blocksInRow;

        // TODO: Check this calculation
        blocksInColumn = AVAILABLE_TILES.length / blocksInRow + 1;

        tileSectionBounds = new Rectangle(
                (1 - sectionWidthPercentage) / 2.0f * width,
                (1 - sectionWidthPercentage) / 2.0f * width,
                sectionWidthPercentage * width,
                blocksInColumn * blockSize + 2 * sectionPadding);

        dynamicGameObjectSectionBounds = new Rectangle(
                (1 - sectionWidthPercentage) / 2.0f * width,
                tileSectionBounds.y + tileSectionBounds.height + glyphLayout.height + 10 * sectionPadding,
                sectionWidthPercentage * width,
                blocksInColumn * blockSize + 2 * sectionPadding);
    }

    public void render(@NotNull SpriteBatch spriteBatch, @NotNull ShapeRenderer shapeRenderer, @NotNull Level level) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        drawTileSection(spriteBatch, shapeRenderer, level);
        drawDynamicGameObjectSection(spriteBatch, shapeRenderer, level);
        drawSelectedItemRect(shapeRenderer);
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            int x = Gdx.input.getX();
            int y = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (insideTileSection(x, y)) {
                int gridIndxX = (int)((x - bounds.x - tileSectionBounds.x - sectionPadding) / blockSize);
                int gridIndxY = (int)((y - bounds.y - tileSectionBounds.y - sectionPadding) / blockSize);

                if (gridIndxY * blocksInRow + gridIndxX >= AVAILABLE_TILES.length) {
                    return;
                }

                lastPickedObjectType = PickedObjectType.TILE;
                lastPickedTileId = AVAILABLE_TILES[gridIndxY * blocksInRow + gridIndxX];
            } else if (insideDynamicGameObjectSection(x, y)) {
                int gridIndxX = (int)((x - bounds.x - dynamicGameObjectSectionBounds.x - sectionPadding) / blockSize);
                int gridIndxY = (int)((y - bounds.y - dynamicGameObjectSectionBounds.y - sectionPadding) / blockSize);


                if (gridIndxY * blocksInRow + gridIndxX >= AVAILABLE_DYNAMIC_OBJECTS.length) {
                    return;
                }

                lastPickedObjectType = PickedObjectType.DYNAMIC_GAME_OBJECT;
                lastPickedGameObjectId = AVAILABLE_DYNAMIC_OBJECTS[gridIndxY * blocksInRow + gridIndxX];
            }
        }
    }

    private boolean insideTileSection(int x, int y) {
        return x > bounds.x + tileSectionBounds.x + sectionPadding
                && x < bounds.x + tileSectionBounds.x + tileSectionBounds.width - sectionPadding
                && y > bounds.y + tileSectionBounds.y + sectionPadding
                && y < bounds.y + tileSectionBounds.y + tileSectionBounds.height - sectionPadding;
    }

    private boolean insideDynamicGameObjectSection(int x, int y) {
        return x > bounds.x + dynamicGameObjectSectionBounds.x + sectionPadding
                && x < bounds.x + dynamicGameObjectSectionBounds.x + dynamicGameObjectSectionBounds.width - sectionPadding
                && y > bounds.y + dynamicGameObjectSectionBounds.y + sectionPadding
                && y < bounds.y + dynamicGameObjectSectionBounds.y + dynamicGameObjectSectionBounds.height - sectionPadding;
    }

    private int indexOf(@NotNull int[] array, int value) {
        for (int i = 0; i < array.length; i++)
            if (value == array[i])
                return i;

        return -1;
    }

    private void drawSectionFrameRectWithOffset(@NotNull Rectangle rectangle, @NotNull ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(bounds.x + rectangle.x, bounds.y + rectangle.y,
                rectangle.width, rectangle.height);
    }

    private void drawSectionFrameRectWithOffset(int x, int y, int width, int height, @NotNull ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(bounds.x + x, bounds.y + y, width, height);
    }

    private void drawSelectedObjectFrame(float x, float y, float width, float height, @NotNull ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rectLine(new Vector2(x, y), new Vector2(x, y + height), 10);
        shapeRenderer.rectLine(new Vector2(x + width, y), new Vector2(x + width, y + height), 10);
        shapeRenderer.rectLine(new Vector2(x, y), new Vector2(x + width, y), 10);
        shapeRenderer.rectLine(new Vector2(x, y + height), new Vector2(x + width, y + height), 10);
        shapeRenderer.end();
    }

    private void drawTileSection(@NotNull SpriteBatch spriteBatch, @NotNull ShapeRenderer renderer, Level level) {
        drawSectionFrame(tileSectionBounds, renderer);

        boolean tilesEnded = false;
        spriteBatch.begin();
        for (int i = 0; i < blocksInColumn && !tilesEnded; i++) {
            for(int j = 0; j < blocksInRow; j++) {
                if (i * blocksInRow + j >= AVAILABLE_TILES.length) {
                    tilesEnded = true;
                    break;
                }
                spriteBatch.draw(level.getTileTextureRegion(AVAILABLE_TILES[i * blocksInRow + j]),
                        bounds.x + tileSectionBounds.x + j * blockSize + sectionPadding,
                        bounds.y + tileSectionBounds.y + i * blockSize + sectionPadding,
                        blockSize, blockSize);
            }
        }
        glyphLayout.setText(font, TITLE_TILE_SECTION);
        font.draw(spriteBatch, TITLE_TILE_SECTION,
                bounds.x + tileSectionBounds.x,
                bounds.y + tileSectionBounds.y + 3 * sectionPadding + blocksInColumn * blockSize + glyphLayout.height);
        spriteBatch.end();
    }

    private void drawDynamicGameObjectSection(@NotNull SpriteBatch spriteBatch, @NotNull ShapeRenderer renderer, Level level) {
        drawSectionFrame(dynamicGameObjectSectionBounds, renderer);

        boolean tilesEnded = false;
        spriteBatch.begin();
        for (int i = 0; i < blocksInColumn && !tilesEnded; i++) {
            for(int j = 0; j < blocksInRow; j++) {
                if (i * blocksInRow + j >= AVAILABLE_DYNAMIC_OBJECTS.length) {
                    tilesEnded = true;
                    break;
                }
                spriteBatch.draw(level.getGameObjectTextureRegion(AVAILABLE_DYNAMIC_OBJECTS[i * blocksInRow + j]),
                        bounds.x + dynamicGameObjectSectionBounds.x + j * blockSize + sectionPadding,
                        bounds.y + dynamicGameObjectSectionBounds.y + i * blockSize + sectionPadding,
                        blockSize, blockSize);
            }
        }
        glyphLayout.setText(font, TITLE_DYNAMIC_OBJECTS_SECTION);
        font.draw(spriteBatch, TITLE_DYNAMIC_OBJECTS_SECTION,
                bounds.x + dynamicGameObjectSectionBounds.x,
                bounds.y + dynamicGameObjectSectionBounds.y + 3 * sectionPadding + blocksInColumn * blockSize + glyphLayout.height);
        spriteBatch.end();
    }

    private void drawSelectedItemRect(@NotNull ShapeRenderer shapeRenderer) {
        switch (lastPickedObjectType) {
            case TILE:
                float pickedTileX = indexOf(AVAILABLE_TILES, lastPickedTileId) % blocksInRow * blockSize;
                float pickedTileY = indexOf(AVAILABLE_TILES, lastPickedTileId) / blocksInRow * blockSize;
                drawSelectedObjectFrame(
                        bounds.x + tileSectionBounds.x + sectionPadding + pickedTileX,
                        bounds.y + tileSectionBounds.y + sectionPadding + pickedTileY,
                        blockSize, blockSize, shapeRenderer);
                break;

            case DYNAMIC_GAME_OBJECT:
                float pickedObjectX = indexOf(AVAILABLE_DYNAMIC_OBJECTS, lastPickedGameObjectId) % blocksInRow * blockSize;
                float pickedObjectY = indexOf(AVAILABLE_DYNAMIC_OBJECTS, lastPickedGameObjectId) / blocksInRow * blockSize;
                drawSelectedObjectFrame(
                        bounds.x + dynamicGameObjectSectionBounds.x + sectionPadding + pickedObjectX,
                        bounds.y + dynamicGameObjectSectionBounds.y + sectionPadding + pickedObjectY,
                        blockSize, blockSize, shapeRenderer);
                break;
            default:
                break;
        }
    }

    private void drawSectionFrame(@NotNull Rectangle sectionBounds, @NotNull ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.BLACK);
        drawSectionFrameRectWithOffset(sectionBounds, renderer);
        renderer.setColor(Color.LIGHT_GRAY);
        drawSectionFrameRectWithOffset(
                (int) (sectionBounds.x + sectionPadding),
                (int) (sectionBounds.y + sectionPadding),
                (int) (sectionBounds.width - 2 * sectionPadding),
                (int) (sectionBounds.height - 2 * sectionPadding),
                renderer);
        renderer.end();
    }

    public PickedObjectType getLastPickedObjectType() {
        return lastPickedObjectType;
    }

    public int getLastPickedTileId() {
        return lastPickedTileId;
    }

    public int getLastPickedGameObjectId() {
        return lastPickedGameObjectId;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
