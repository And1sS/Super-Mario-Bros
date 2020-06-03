package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.TileId;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Level {
    public static final float GRAVITATIONAL_ACCELERATION = 3000 * Gdx.graphics.getHeight() / 1080.0f;

    public static final String BLUE = "BLUE";
    public static final String BLACK = "BLACK";

    public static final TreeMap<String, Color> COLORS = new TreeMap<>();

    static {
        COLORS.put(BLUE, new Color(107 / 255f, 140 / 255f, 255 / 255f, 1f));
        COLORS.put(BLACK, new Color(0, 0, 0, 1f));
    }

    private final int[][] map;

    private int cellSize;
    private final int mapWidth;
    private final int mapHeight;

    private final Texture tilesTexture;
    private final Texture objectsTexture;
    private final Texture enemiesTexture;

    private boolean editorMode = false;
    private String backgroundColor = BLACK;

    private final List<GameObject> objects;

    public Level(int[][] map, int cellSize, Texture tiles, Texture obects, Texture enemies) {
        this.map = map;
        this.cellSize = cellSize;
        this.tilesTexture = tiles;
        this.objectsTexture = obects;
        this.enemiesTexture = enemies;

        mapWidth = map[0].length;
        mapHeight = map.length;

        objects = new ArrayList();
    }

    public void update(float deltaTime, Mario mario) {
        for (int i = 0; i < objects.size(); i++) {
            GameObject object = objects.get(i);
            if (Math.abs(object.getX() - mario.getX()) > Gdx.graphics.getWidth())
                continue;

            object.update(deltaTime, this);
            object.updateAnimation(deltaTime);
            object.setOffsetX(mario.getOffsetX());

            object.performObjectCollisionDetection(mario);

            if (object.shouldBeDisposed()) {
                objects.remove(i--);
            }
        }

        for (GameObject object : objects) {
            if (Math.abs(object.getX() - mario.getX()) > Gdx.graphics.getWidth())
                continue;

            for (GameObject anotherObject : objects) {
                if (Math.abs(object.getX() - mario.getX()) > Gdx.graphics.getWidth()
                        || object.equals(anotherObject))
                    continue;

                object.performObjectCollisionDetection(anotherObject);
            }
        }
    }

    @Nullable
    public static Level loadFromFile(String levelPath, String tilesTexturePath, String objectsTexturePath, String enemiesObjectsPath) {
        try {
            FileHandle file = Gdx.files.internal(levelPath);
            BufferedReader br = new BufferedReader(file.reader());

            int mapWidth = Integer.parseInt(br.readLine());
            int mapHeight = Integer.parseInt(br.readLine());

            String backgroundColor = br.readLine();
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

            if (!colorExists(backgroundColor)) {
                loadedLevel.backgroundColor = BLACK;
            } else {
                loadedLevel.backgroundColor = backgroundColor;
            }

            int objects = Integer.parseInt(br.readLine());

            for(int i = 0; i < objects; i++) {
                String line = br.readLine();
                String[] tokens = line.split(del);

                int gameObjectId = Integer.parseInt(tokens[0]);

                if (gameObjectId != com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId.BACKGROUND_OBJECT) {
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
            file.writeString(backgroundColor + "\n", true);

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

            file.writeString(objects.size() + "\n", true);
            for (GameObject object: objects) {
                tmp = "";
                tmp += object.getId() + " ";

                if (object.getId() == com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId.BACKGROUND_OBJECT) {
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
            System.out.println("HERE!");
            return false;
        }
    }

    public void setEditorMode(boolean editorMode) {
        this.editorMode = editorMode;
    }

    public void setBackgroundColor(String color) {
        if (colorExists(color)) {
            backgroundColor = color;
        }
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
            case com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId.GOOMBA:
                addObject(new Goomba(mapIndxX, mapIndxY, this));
                break;

            case com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId.COOPA_TROOPA:
                addObject(new CoopaTroopa(mapIndxX, mapIndxY, this));
                break;

            case com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId.FLYING_COOPA_TROOPA:
                addObject(new CoopaTroopa(mapIndxX, mapIndxY, this, CoopaTroopa.Type.FLYING));
                break;

            case com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId.PLATFORM_LEFT_RIGHT:
                addObject(new Platform(mapIndxX, mapIndxY, this, Platform.Type.LEFT_RIGHT));
                break;

            case com.And1sS.SuperMarioBros.Rebuild.GameConstants.GameObjectId.PLATFORM_TOP_DOWN:
                addObject(new Platform(mapIndxX, mapIndxY, this, Platform.Type.TOP_DOWN));
                break;

            case GameObjectId.COLLECTABLE_COIN:
                addObject(new CollectableCoin(mapIndxX, mapIndxY, this));
                break;

            case GameObjectId.PIRANA_PLANT:
                addObject(new PiranaPlant(mapIndxX, mapIndxY, this));
                break;
        }
    }

    public Texture getTilesTexture() {
        return tilesTexture;
    }

    public Texture getObjectsTexture() {
        return objectsTexture;
    }

    public Texture getEnemiesTexture() {
        return enemiesTexture;
    }

    public List<GameObject> getObjects() { return objects; }

    //TODO: move this to texture provider class, rewrite to constant hashmap
    public TextureRegion getTileTextureRegion(int tileId) {
        TextureRegion tileTextureRegion = new TextureRegion(tilesTexture);
        switch(tileId) {
            case TileId.TRANSPARENT_NOT_COLLIDABLE_BLOCK:
                return null;

            case TileId.TRANSPARENT_COLLIDABLE_BLOCK:
                if (editorMode) {
                    tileTextureRegion.setRegion(18 * 16, 6 * 16, 16, 16);
                } else {
                    return null;
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



            case TileId.BROWN_FENCE:
                tileTextureRegion.setRegion(8 * 16, 8 * 16, 16, 16);
                break;
            case TileId.BLUE_FENCE:
                tileTextureRegion.setRegion(9 * 16, 8 * 16, 16, 16);
                break;



            case TileId.NEXT_LEVEL_SIGN:
                tileTextureRegion.setRegion(19 * 16, 2 * 16, 16, 16);
                break;

            default:
                tileTextureRegion.setRegion(17 * 16, 6 * 16, 16, 16);
                break;
        }

        return tileTextureRegion;
    }

    //TODO: move this to texture provider class, rewrite to constant hashmap
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

           case GameObjectId.FLYING_COOPA_TROOPA:
                tileTextureRegion = new TextureRegion(enemiesTexture);
                tileTextureRegion.setRegion(128, 8, 16, 24);
                break;

           case GameObjectId.PLATFORM_LEFT_RIGHT:
           case GameObjectId.PLATFORM_TOP_DOWN:
                tileTextureRegion = new TextureRegion(objectsTexture);
                tileTextureRegion.setRegion(64, 129, 16, 7);
                break;

           case GameObjectId.COLLECTABLE_COIN:
                tileTextureRegion = new TextureRegion(objectsTexture);
                tileTextureRegion.setRegion(0, 80, 16, 16);
                break;

           case GameObjectId.PIRANA_PLANT:
                tileTextureRegion = new TextureRegion(enemiesTexture);
                tileTextureRegion.setRegion(12 * 16, 8, 16, 24);
        }
        return tileTextureRegion;
    }

    //TODO: rewrite or delete
    public void recalculateObjectsBounds(int newWidth, int newHeight) {
        int oldCellSize = cellSize;
        cellSize = newHeight / mapHeight;

        for (GameObject object : objects) {
            object.recalculateBounds(oldCellSize, cellSize);
        }
    }

    public void render(SpriteBatch spriteBatch, double offsetX) {
        Color background = COLORS.get(backgroundColor);
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int startX = (int) (offsetX) / cellSize;
        int startY = 0;
        int width = Gdx.graphics.getWidth() / cellSize + 2;
        int height = mapHeight;

        for (GameObject object : objects) {
            if (Math.abs(object.getX() - offsetX) > Gdx.graphics.getWidth())
                continue;
            object.setOffsetX(offsetX);
            object.render(spriteBatch);
        }

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
    }

    private static boolean colorExists(String color) {
        return COLORS.containsKey(color);
    }
}

