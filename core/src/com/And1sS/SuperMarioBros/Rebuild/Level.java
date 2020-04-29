package com.And1sS.SuperMarioBros.Rebuild;

import com.And1sS.SuperMarioBros.Rebuild.GameObjects.CoopaTroopa;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.GameObject;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.Goomba;
import com.And1sS.SuperMarioBros.Rebuild.GameObjects.Platform;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class Level {
    public static final float GRAVITATIONAL_ACCELERATION = 3000 * Gdx.graphics.getHeight() / 1080.0f;

    private int[][] map;

    private int cellSize;
    private int mapWidth;
    private int mapHeight;

    private Texture tilesTexture;
    private Texture obectsTexture;
    private Texture enemiesTexture;

    private float currentFrame;
    private static boolean editorMode = false;

    private List<GameObject> objects;

    public Level(int map[][], int cellSize, Texture tiles, Texture obects, Texture enemies) {
        this.map = map;
        this.cellSize = cellSize;
        this.tilesTexture = tiles;
        this.obectsTexture = obects;
        this.enemiesTexture = enemies;

        mapWidth = map[0].length;
        mapHeight = map.length;

        objects = new ArrayList();
    }

    public void update(float deltaTime, Player player) {
        for (int i = 0; i < objects.size(); i++) {
            GameObject object = objects.get(i);
            if (Math.abs(object.getX() - player.getX()) > Gdx.graphics.getWidth())
                continue;

            object.update(deltaTime, this);
            object.updateAnimation(deltaTime);
            object.setOffsetX(player.getOffsetX());

            object.performObjectCollisionDetection(player);
            if (object.shouldBeDisposed()) {
                objects.remove(i--);
            }
        }
    }

    public void render(@NotNull SpriteBatch spriteBatch, @NotNull Player player) {

        // TODO: rewrite this background drawing
        spriteBatch.draw(getTileTextureRegion(0), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        double offsetX = player.getOffsetX();

        int startX = (int) (player.getOffsetX()) / cellSize;
        int startY = 0;
        int width = Gdx.graphics.getWidth() / cellSize + 2;
        int height = mapHeight;

        for(int y = startY; y < startY + height; y++) {
            for(int x = startX; x < startX + width; x++) {
                try {
                    if(map[y][x] != 0) {
                        spriteBatch.draw(
                            getTileTextureRegion(map[y][x]),
                            x * cellSize - (int) offsetX,
                            Gdx.graphics.getHeight() - (y - startY + 1) * cellSize,
                            cellSize, cellSize);
                    }
                } catch(Exception e) { /* who cares? no texture - undefined behaviour! don't forget to add textures! */ }
            }
        }

        for (GameObject object : objects) {
            if (Math.abs(object.getX() - player.getX()) > Gdx.graphics.getWidth())
                continue;
            object.render(spriteBatch);
        }
    }

    // This method is only for level editor, never use it outside editor!
    public void render(@NotNull SpriteBatch spriteBatch, double offsetX) {


        // TODO: rewrite this background drawing
        spriteBatch.draw(getTileTextureRegion(0), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (GameObject object: objects) {
            object.setOffsetX(offsetX);
        }

        int startX = (int) (offsetX) / cellSize;
        int startY = 0;
        int width = Gdx.graphics.getWidth() / cellSize + 2;
        int height = mapHeight;

        for(int y = startY; y < startY + height; y++) {
            for(int x = startX; x < startX + width; x++) {
                try {
                    if(map[y][x] != 0) {
                        spriteBatch.draw(
                                getTileTextureRegion(map[y][x]),
                                x * cellSize - (int) offsetX,
                                Gdx.graphics.getHeight() - (y - startY + 1) * cellSize,
                                cellSize, cellSize);
                    }
                } catch(Exception e) { /* who cares? no texture - undefined behaviour! don't forget to add textures! */ }
            }
        }

        for (GameObject object : objects) {
            object.render(spriteBatch);
        }
    }

    @Nullable
    public static Level loadFromFile(String levelPath, String tilesTexturePath, String objectsTexturePath, String enemiesObjectsPath) {
        try {
            FileHandle file = Gdx.files.local(levelPath);
            BufferedReader br = new BufferedReader(file.reader());

            int mapWidth = Integer.parseInt(br.readLine());
            int mapHeight = Integer.parseInt(br.readLine());

            int[][] map = new int[mapHeight][mapWidth];

            String del = "\\s+";

            for(int row = 0; row < mapHeight; row++) {
                String line = br.readLine();
                String[] tokens = line.split(del);

                for(int col = 0; col < mapWidth; col++) {
                    map[row][col] = Integer.parseInt(tokens[col]);
                }
            }

            Level loadedLevel = new Level(map,
                    Gdx.graphics.getHeight() / mapHeight,
                    new Texture(Gdx.files.internal(tilesTexturePath)),
                    new Texture(Gdx.files.internal(objectsTexturePath)),
                    new Texture(Gdx.files.internal(enemiesObjectsPath)));

            int objects = Integer.parseInt(br.readLine());

            for(int i = 0; i < objects; i++) {
                String line = br.readLine();
                String[] tokens = line.split(del);

                int gameObjectId = Integer.parseInt(tokens[0]);

                if (gameObjectId != GameObjectId.BACKGROUND_OBJECT) {
                    int mapIndxX = Integer.parseInt(tokens[1]);
                    int mapIndxY = Integer.parseInt(tokens[2]);
                    loadedLevel.addObject(gameObjectId, mapIndxX, mapIndxY);
                } else {
                    int cellSize = loadedLevel.getCellSize();
                    loadedLevel.addObject(new BackgroundObject(
                            new Rectangle(
                                    cellSize * Integer.parseInt(tokens[1]),
                                    cellSize * Integer.parseInt(tokens[2]),
                                cellSize * Integer.parseInt(tokens[3]),
                                    cellSize * Integer.parseInt(tokens[4])),
                            new Animation(
                                    loadedLevel.getTilesTexture(),
                                    Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]),
                                    Integer.parseInt(tokens[7]), Integer.parseInt(tokens[8]))
                    ));
                }
            }

            return loadedLevel;

        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveToFile(String levelPath) {
        try {
            FileHandle file = Gdx.files.local(levelPath);
            file.writeString(mapWidth + "\n" + mapHeight + "\n", false);

            String tmp;
            for (int i = 0; i < mapHeight; i++) {
                tmp = "";
                for (int j = 0; j < mapWidth; j++) {
                    tmp += map[i][j];
                    if (j != mapWidth - 1) {
                         tmp += " ";
                    }
                }
                tmp += "\n";
                file.writeString(tmp, true);
            }

            file.writeString(String.valueOf(objects.size()) + "\n", true);
            for (GameObject object: objects) {
                tmp = "";
                tmp += object.getId() + " ";

                if (object.getId() == GameObjectId.BACKGROUND_OBJECT) {
                    int cellX = (int) (object.getX() / cellSize);
                    int cellY = (int) (object.getY() / cellSize);
                    int cellWidth = (int) (object.getWidth() / cellSize);
                    int cellHeight = (int) (object.getHeight() / cellSize);

                    int textureX = (int) (object.getAnimation().getX());
                    int textureY = (int) (object.getAnimation().getY());
                    int textureWidth = (int) object.getAnimation().getDrawableWidth();
                    int textureHeight = (int) object.getAnimation().getDrawableHeight();

                    tmp += cellX + " " + cellY + " " + cellWidth + " " + cellHeight + " "
                            + textureX + " " + textureY + " " + textureWidth + " " + textureHeight;
                } else {
                    int cellX = (int) (object.getX() / cellSize);
                    int cellY = (int) (object.getY() / cellSize);

                    tmp += cellX + " " + cellY;
                }
                tmp += "\n";

                file.writeString(tmp, true);
            }


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setEditorMode(boolean editorMode) {
        this.editorMode = editorMode;
    }

    public int getCell(int x, int y) {
        return map[y][x];
    }

    public void setCell(int x, int y, int newCell) {
        try {
            map[y][x] = newCell;
        } catch(Exception e) {}
    }

    public int getCellSize() { return cellSize; }

    public int getWidth() { return mapWidth; }

    public int getHeight() { return mapHeight; }

    public void addObject(GameObject object) {
        objects.add(object);
    }

    public void addObject(int gameObjectId, int mapIndxX, int mapIndxY) {
        switch (gameObjectId) {
            case GameObjectId.GOOMBA:
                addObject(new Goomba(mapIndxX, mapIndxY, this));
                break;

            case GameObjectId.COOPA_TROOPA:
                addObject(new CoopaTroopa(mapIndxX, mapIndxY, this));
                break;

            case GameObjectId.PLATFORM_LEFT_RIGHT:
                addObject(new Platform(mapIndxX, mapIndxY, this, Platform.Type.LEFT_RIGHT));
                break;

            case GameObjectId.PLATFORM_TOP_DOWN:
                addObject(new Platform(mapIndxX, mapIndxY, this, Platform.Type.TOP_DOWN));
                break;
        }
    }

    public Texture getTilesTexture() {
        return tilesTexture;
    }

    public Texture getObectsTexture() {
        return obectsTexture;
    }

    public Texture getEnemiesTexture() {
        return enemiesTexture;
    }

    public TextureRegion getTileTextureRegion(int tileId) {
        TextureRegion tileTextureRegion = new TextureRegion(tilesTexture);
        switch(tileId) {
            case TileId.TRANSPARENT_NOT_COLLIDABLE_BLOCK:
                tileTextureRegion.setRegion(80, 16, 16, 16);
                break;

            case TileId.TRANSPARENT_COLLIDABLE_BLOCK:
                if (editorMode) {
                    tileTextureRegion.setRegion(18 * 16, 6 * 16, 16, 16);
                } else {
                    tileTextureRegion.setRegion(80, 16, 16, 16);
                }
                break;



            case TileId.BROWN_WAVE_BLOCK:
                tileTextureRegion.setRegion(8 * 16, 0, 16, 16);
                break;
            case TileId.BLUE_WAVE_BLOCK:
                tileTextureRegion.setRegion(9 * 16, 0, 16, 16);
                break;
            case TileId.GRAY_WAVE_BLOCK:
                tileTextureRegion.setRegion(18 * 16, 4 * 16, 16, 16);
                break;



            case TileId.BROWN_STONE_BLOCK:
                tileTextureRegion.setRegion(0, 0, 16, 16);
                break;
            case TileId.BLUE_STONE_BLOCK:
                tileTextureRegion.setRegion(16, 0, 16, 16);
                break;
            case TileId.GRAY_STONE_BLOCK:
                tileTextureRegion.setRegion(18 * 16, 3 * 16, 16, 16);
                break;



            case TileId.BROWN_IRON_BLOCK:
                tileTextureRegion.setRegion(2 * 16, 0, 16, 16);
                break;
            case TileId.BLUE_IRON_BLOCK:
                tileTextureRegion.setRegion(3 * 16, 0, 16, 16);
                break;
            case TileId.GREEN_IRON_BLOCK:
                tileTextureRegion.setRegion(16 * 16, 11 * 16, 16, 16);
                break;
            case TileId.GRAY_IRON_BLOCK:
                tileTextureRegion.setRegion(16 * 16, 6 * 16, 16, 16);
                break;



            case TileId.BROWN_BRICK_BLOCK:
                tileTextureRegion.setRegion(10 * 16 + 1, 2 * 16, 14, 16);
                break;
            case TileId.BLUE_BRICK_BLOCK:
                tileTextureRegion.setRegion(12 * 16, 2 * 16, 14, 16);
                break;
            case TileId.GRAY_BRICK_BLOCK:
                tileTextureRegion.setRegion(14 * 16, 2 * 16, 14, 16);
                break;
            case TileId.GREEN_BRICK_BLOCK:
                tileTextureRegion.setRegion(18 * 16, 10 * 16, 14, 16);
                break;



            case TileId.SECRET_BLOCK_USED_BROWN:
                tileTextureRegion.setRegion(11 * 16, 0, 16, 16);
                break;
            case TileId.SECRET_BLOCK_USED_BLUE:
                tileTextureRegion.setRegion(13 * 16, 0, 16, 16);
                break;
            case TileId.SECRET_BLOCK_USED_GRAY:
                tileTextureRegion.setRegion(15 * 16, 0, 16, 16);
                break;
            case TileId.SECRET_BLOCK_USED_GREEN:
                tileTextureRegion.setRegion(17 * 16, 9 * 16, 16, 16);
                break;



            case TileId.SECRET_BLOCK_DEFAULT_1:
            case TileId.SECRET_BLOCK_EMPTY:
                tileTextureRegion.setRegion(4 * 16, 0, 16, 16);
                break;
            case TileId.SECRET_BLOCK_POWERUP_SUPERMARIO:
                if (editorMode) {
                    tileTextureRegion.setRegion(19 * 16, 0, 16, 16);
                } else {
                    tileTextureRegion.setRegion(4 * 16, 0, 16, 16);
                }
                break;
            case TileId.SECRET_BLOCK_DEFAULT_2:
                tileTextureRegion.setRegion(5 * 16, 0, 16, 16);
                break;
            case TileId.SECRET_BLOCK_DEFAULT_3:
                tileTextureRegion.setRegion(6 * 16, 0, 16, 16);
                break;


            default:
                tileTextureRegion.setRegion(17 * 16, 6 * 16, 16, 16);
                break;
        }

        return tileTextureRegion;
    }

    // This method is only for level editor, never use it outside editor!
    public TextureRegion getGameObjectTextureRegion(int gameObjectId) {
        TextureRegion tileTextureRegion = null;

        switch (gameObjectId) {
            case GameObjectId.GOOMBA:
                tileTextureRegion = new TextureRegion(enemiesTexture);
                tileTextureRegion.setRegion(0, 16, 16, 16);
                break;

           case GameObjectId.COOPA_TROOPA:
               tileTextureRegion = new TextureRegion(enemiesTexture);
               tileTextureRegion.setRegion(96, 7, 16, 25);
               break;

            case GameObjectId.PLATFORM_LEFT_RIGHT:
            case GameObjectId.PLATFORM_TOP_DOWN:
                tileTextureRegion = new TextureRegion(obectsTexture);
                tileTextureRegion.setRegion(64, 129, 16, 7);
                break;
        }
        return tileTextureRegion;
    }

    public void recalculateObjectsBounds(int newWidth, int newHeight) {
        int oldCellSize = cellSize;
        cellSize = newHeight / mapHeight;

        for (GameObject object : objects) {
            object.recalculateBounds(oldCellSize, cellSize);
        }
    }
}

