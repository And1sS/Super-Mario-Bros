package com.And1sS.SuperMarioBros.Rebuild.LevelEditor;

import com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.TileId;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.Level;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class GameObjectPicker {

    public interface ILevelSaver {
        void save();
    }

    public interface IBackgroundChanger {
        void changeBackgroundTo(String color);
    }

    private class Point {

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int x;
        public int y;
    }

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
            TileId.BROWN_FENCE,
            TileId.BLUE_FENCE,
            TileId.NEXT_LEVEL_SIGN
    };

    private static final int[] AVAILABLE_DYNAMIC_OBJECTS = {
            GameObjectId.GOOMBA,
            GameObjectId.COOPA_TROOPA,
            GameObjectId.FLYING_COOPA_TROOPA,
            GameObjectId.PIRANA_PLANT,
            GameObjectId.PLATFORM_LEFT_RIGHT,
            GameObjectId.PLATFORM_TOP_DOWN,
            GameObjectId.COLLECTABLE_COIN
    };

    private static final String TITLE_TILE_SECTION = "Tiles:";
    private static final String TITLE_DYNAMIC_OBJECTS_SECTION = "Dynamic objects:";
    private static final String TITLE_BACKGROUNDS_SECTION = "Background colors:";

    private static final int blocksInRow = 7;
    private final int blocksInColumn;

    private static final float sectionPaddingPercentage = 0.01f;
    private static final float sectionWidthPercentage = 0.95f;        // sectionWidth = ScreenWidth * sectionWidthPercentage

    private final ILevelSaver levelSaver;
    private final IBackgroundChanger backgroundChanger;

    public enum PickedObjectType {
        TILE, STATIC_GAME_OBJECT, DYNAMIC_GAME_OBJECT, BACKGROUND, NONE
    }
    private PickedObjectType lastPickedObjectType = PickedObjectType.TILE;

    private final Rectangle bounds;
    private final Rectangle tileSectionBounds;
    private final Rectangle dynamicGameObjectSectionBounds;
    private final Rectangle backgroundSectionBounds;

    private BitmapFont font;
    private final GlyphLayout glyphLayout;

    private final TextButton saveButton;
    private final TextButton.TextButtonStyle buttonStyle;
    private final TextureAtlas textureAtlas;

    private int lastPickedTileId = TileId.BROWN_WAVE_BLOCK;
    private int lastPickedGameObjectId = GameObjectId.GOOMBA;
    private String lastPickedBackground = Level.BLACK;

    private final float blockSize;
    private final int sectionPadding;

    private final Stage stage;

    public GameObjectPicker(float x, float y, float width, float height,
                            final ILevelSaver levelSaver, final IBackgroundChanger backgroundChanger) {
        this.levelSaver = levelSaver;
        this.backgroundChanger = backgroundChanger;

        stage = new Stage();
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
                Gdx.graphics.getHeight()  - (blocksInColumn * blockSize + 2 * sectionPadding)
                        - (glyphLayout.height + 5 * sectionPadding),
                sectionWidthPercentage * width,
                blocksInColumn * blockSize + 2 * sectionPadding);

        dynamicGameObjectSectionBounds = new Rectangle(
                (1 - sectionWidthPercentage) / 2.0f * width,
                tileSectionBounds.y - (glyphLayout.height + 5 * sectionPadding)
                        - (blocksInColumn * blockSize + 2 * sectionPadding),
                sectionWidthPercentage * width,
                blocksInColumn * blockSize + 2 * sectionPadding);

        backgroundSectionBounds = new Rectangle(
                (1 - sectionWidthPercentage) / 2.0f * width,
                dynamicGameObjectSectionBounds.y - (glyphLayout.height + 5 * sectionPadding)
                        - (blockSize + 2 * sectionPadding),
                sectionWidthPercentage * width,
                blockSize + 2 * sectionPadding);

        font = new BitmapFont(Gdx.files.internal("fonts/whitetext.fnt"));
        font.getData().setScale(0.5f * Gdx.graphics.getWidth() / 1920.0f);

        textureAtlas = new TextureAtlas("ui/ui-gray.atlas");
        Skin skin = new Skin(textureAtlas);
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = skin.getDrawable("button_03");
        buttonStyle.down = skin.getDrawable("button_01");
        buttonStyle.pressedOffsetX = 1;
        buttonStyle.pressedOffsetY = -1;
        saveButton = new TextButton("Save", buttonStyle);
        saveButton.setPosition(Gdx.graphics.getWidth() - sectionPadding - sectionWidthPercentage * width / 2.0f, sectionPadding);
        saveButton.setWidth(sectionWidthPercentage * width / 2.0f);
        saveButton.setHeight(1.5f * blockSize);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                levelSaver.save();
            }
        });
        stage.addActor(saveButton);
    }

    public void render(@NotNull SpriteBatch spriteBatch, @NotNull ShapeRenderer shapeRenderer, @NotNull Level level) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        drawTileSection(spriteBatch, shapeRenderer, level);
        drawDynamicGameObjectSection(spriteBatch, shapeRenderer, level);
        drawBackgroundSection(spriteBatch, shapeRenderer);
        drawSelectedItemRect(shapeRenderer);


        spriteBatch.begin();
        saveButton.draw(spriteBatch, 1.0f);
        spriteBatch.end();
    }

    public void handleInput(float posX, float posY) {
        int x = (int) posX;
        int y = Gdx.graphics.getHeight() - (int) posY;

        if (insideTileSection(x, y)) {
            Point gridIndexes = getSectionPickedCellIndexes(
                    x, y, tileSectionBounds,
                    AVAILABLE_TILES.length,
                    blocksInRow);

            if (gridIndexes == null)
                return;

            lastPickedObjectType = PickedObjectType.TILE;
            lastPickedTileId = AVAILABLE_TILES[gridIndexes.y * blocksInRow + gridIndexes.x];
        } else if (insideDynamicGameObjectSection(x, y)) {
            Point gridIndexes = getSectionPickedCellIndexes(
                    x, y, dynamicGameObjectSectionBounds,
                    AVAILABLE_DYNAMIC_OBJECTS.length,
                    blocksInRow);

            if (gridIndexes == null)
                return;

            lastPickedObjectType = PickedObjectType.DYNAMIC_GAME_OBJECT;
            lastPickedGameObjectId = AVAILABLE_DYNAMIC_OBJECTS[gridIndexes.y * blocksInRow + gridIndexes.x];
        } else if (insideBackgroundSectionBounds(x, y)) {
            TreeMap<String, Color> colorHashMap = Level.COLORS;
            List<String> colorStrings = new ArrayList<>(colorHashMap.keySet());
            Point gridIndexes = getSectionPickedCellIndexes(x, y,
                    dynamicGameObjectSectionBounds, colorStrings.size(), 0);

            if (gridIndexes == null)
                return;

            lastPickedObjectType = PickedObjectType.BACKGROUND;
            lastPickedBackground = colorStrings.get(gridIndexes.x);
            backgroundChanger.changeBackgroundTo(lastPickedBackground);
        }
    }

    private boolean insideTileSection(int x, int y) {
        return insideSection(x, y, tileSectionBounds);
    }

    private boolean insideDynamicGameObjectSection(int x, int y) {
        return insideSection(x, y, dynamicGameObjectSectionBounds);
    }

    private boolean insideBackgroundSectionBounds(int x, int y) {
        return insideSection(x, y, backgroundSectionBounds);
    }

    private Point getSectionPickedCellIndexes(int x, int y,
                                              Rectangle sectionBounds,
                                              int availableObjectsCount,
                                              int blocksInRow) {
        int gridIndxX = (int)((x - bounds.x - sectionBounds.x - sectionPadding) / blockSize);
        int gridIndxY = (int)((y - bounds.y - sectionBounds.y - sectionPadding) / blockSize);


        if (gridIndxY * blocksInRow + gridIndxX >= availableObjectsCount) {
            return null;
        }
        return new Point(gridIndxX, gridIndxY);
    }

    private boolean insideSection(int x, int y, Rectangle sectionBounds) {
        return x > bounds.x + sectionBounds.x + sectionPadding
                && x < bounds.x + sectionBounds.x + sectionBounds.width - sectionPadding
                && y > bounds.y + sectionBounds.y + sectionPadding
                && y < bounds.y + sectionBounds.y + sectionBounds.height - sectionPadding;
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
                TextureRegion region = level.getTileTextureRegion(AVAILABLE_TILES[i * blocksInRow + j]);
                if (region != null) {
                    spriteBatch.draw(region,
                            bounds.x + tileSectionBounds.x + j * blockSize + sectionPadding,
                            bounds.y + tileSectionBounds.y + i * blockSize + sectionPadding,
                            blockSize, blockSize);
                }

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

    private void drawBackgroundSection(@NotNull SpriteBatch spriteBatch, @NotNull ShapeRenderer renderer) {
        drawSectionFrame(backgroundSectionBounds, renderer);

        TreeMap<String, Color> colorHashMap = Level.COLORS;
        List<String> colorStrings = new ArrayList<>(colorHashMap.keySet());

        boolean tilesEnded = false;
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < blocksInColumn && !tilesEnded; i++) {
            for(int j = 0; j < blocksInRow; j++) {
                if (i * blocksInRow + j >= colorStrings.size()) {
                    tilesEnded = true;
                    break;
                }

                renderer.setColor(colorHashMap.get(colorStrings.get(i * blocksInColumn + j)));

                renderer.rect(
                        bounds.x + backgroundSectionBounds.x + j * blockSize + sectionPadding,
                        bounds.y + backgroundSectionBounds.y + i * blockSize + sectionPadding,
                        blockSize, blockSize);
            }
        }
        renderer.end();

        spriteBatch.begin();
        glyphLayout.setText(font, TITLE_BACKGROUNDS_SECTION);
        font.draw(spriteBatch, TITLE_BACKGROUNDS_SECTION,
                bounds.x + backgroundSectionBounds.x,
                bounds.y + backgroundSectionBounds.y + 3 * sectionPadding
                        + blockSize + glyphLayout.height);
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

    public String getLastPickedBackground() { return lastPickedBackground; }

    public Rectangle getBounds() {
        return bounds;
    }

    public InputProcessor getInputProcessor() { return stage; }
}
