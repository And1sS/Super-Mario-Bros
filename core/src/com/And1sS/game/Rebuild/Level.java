package com.And1sS.game.Rebuild;

import com.And1sS.game.Rebuild.GameObjects.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class Level {
    public static final float GRAVITATIONAL_ACCELERATION = 3000 * Gdx.graphics.getHeight() / 1080.0f;

    private int[][] map;

    private int cellSize;
    private int mapWidth;
    private int mapHeight;

    private Texture tiles;

    private float currentFrame;

    List<GameObject> objects;

    private Level(int map[][], int cellSize, Texture tiles) {
        this.map = map;
        this.cellSize = cellSize;
        this.tiles = tiles;

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

    public static Level loadFromFile(String levelPath, String texturePath) {
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
                    new Texture(Gdx.files.internal(texturePath)));
            int cellSize = loadedLevel.getCellSize();

            for(int i = 0; i < objects; i++) {
                String line = br.readLine();
                String[] tokens = line.split(del);

                Rectangle objectBounds = new Rectangle(
                        cellSize * Integer.parseInt(tokens[0]),
                        cellSize * Integer.parseInt(tokens[1]),
                        cellSize * Integer.parseInt(tokens[2]),
                        cellSize * Integer.parseInt(tokens[3]));

                Animation objectAnimation = new Animation(texturePath,
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

    private TextureRegion getTileTextureRegion(int tileId) {
        TextureRegion tileTextureRegion = new TextureRegion(tiles);
        switch(tileId) {
            case 0:
                tileTextureRegion.setRegion(80, 16, 16, 16);
                break;

                // TODO: make this transparent block look as level background
            case TileId.TRANSPARENT_COLLIDABLE_BLOCK:
                tileTextureRegion.setRegion(18 * 16, 16, 16, 16);
                break;

            case 10:
                tileTextureRegion.setRegion(0, 0, 16, 16);
                break;

            case 12:
                tileTextureRegion.setRegion(32, 0, 16, 16);
                break;

            case TileId.BROWN_BRICK:
                tileTextureRegion.setRegion(161, 32, 15, 16);
                break;

            case TileId.SECRET_BLOCK_EMPTY:
            case 22:
            case 23:
                tileTextureRegion.setRegion(80, 0, 16, 16);
                break;

            case TileId.BROWN_IRON_BLOCK:
                tileTextureRegion.setRegion(176, 0, 16, 16);
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

