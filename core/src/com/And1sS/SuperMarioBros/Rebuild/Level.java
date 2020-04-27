package com.And1sS.SuperMarioBros.Rebuild;

import com.And1sS.SuperMarioBros.Rebuild.GameObjects.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.io.BufferedReader;
import java.io.PrintWriter;
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
    private static boolean editorMode = true;

    List<GameObject> objects;

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

    public void render(SpriteBatch spriteBatch, Player player) {

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
    public void render(SpriteBatch spriteBatch, double offsetX) {

        // TODO: rewrite this background drawing
        spriteBatch.draw(getTileTextureRegion(0), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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

    public static Level loadFromFile(String levelPath, String tilesTexturePath, String objectsTexturePath, String enemiesObjectsPath) {
        try {
            FileHandle file = Gdx.files.internal(levelPath);
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

            int objects = Integer.parseInt(br.readLine());
            Level loadedLevel = new Level(map,
                    Gdx.graphics.getHeight() / mapHeight,
                    new Texture(Gdx.files.internal(tilesTexturePath)),
                    new Texture(Gdx.files.internal(objectsTexturePath)),
                    new Texture(Gdx.files.internal(enemiesObjectsPath)));
            int cellSize = loadedLevel.getCellSize();

            for(int i = 0; i < objects; i++) {
                String line = br.readLine();
                String[] tokens = line.split(del);

                Rectangle objectBounds = new Rectangle(
                        cellSize * Integer.parseInt(tokens[0]),
                        cellSize * Integer.parseInt(tokens[1]),
                        cellSize * Integer.parseInt(tokens[2]),
                        cellSize * Integer.parseInt(tokens[3]));

                com.And1sS.SuperMarioBros.Rebuild.Animation objectAnimation = new Animation(tilesTexturePath,
                        Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]),
                        Integer.parseInt(tokens[6]), Integer.parseInt(tokens[7]));

                loadedLevel.addObject(new BackgroundObject(objectBounds, objectAnimation));
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

            file.writeString(String.valueOf(0), true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
            case TileId.SECRET_BLOCK_POWERUP_SUPERMARIO:
                tileTextureRegion.setRegion(4 * 16, 0, 16, 16);
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

    public void recalculateObjectsBounds(int newWidth, int newHeight) {
        int oldCellSize = cellSize;
        cellSize = newHeight / mapHeight;

        for (GameObject object : objects) {
            object.recalculateBounds(oldCellSize, cellSize);
        }
    }
}

