package com.And1sS.SuperMarioBros.OldVersion;

import com.And1sS.SuperMarioBros.Rebuild.GameConstants.TileId;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class Level {
	public static final float GRAVITATIONAL_ACCELERATION = 5000 * Gdx.graphics.getHeight() / 1080;
	public static final float SPEED = 0.7f;
    public static final int TIME = 300;
	
	private int[][] map;

	private final int cellSize;
	private final int mapWidth;
	private final int mapHeight;

	private double time = TIME;
	private double currentFrame = 0;

	private final TextureRegion currentTile;

	List <TextureBody> sprites;
	List <com.And1sS.SuperMarioBros.OldVersion.Anim> animations;

	private final com.And1sS.SuperMarioBros.OldVersion.EnemyManager enemies;

	public Level(int[][] map, int cellSize, Texture tiles) {
		this.map = map;
		this.cellSize = cellSize;

		mapWidth = map[0].length;
		mapHeight = map.length;

		currentTile = new TextureRegion(tiles);

		sprites = new ArrayList();
		animations = new ArrayList();

		enemies = new com.And1sS.SuperMarioBros.OldVersion.EnemyManager(cellSize);
	}

	public void drawMap(SpriteBatch batch, int startX, int startY, int width, int height) {
		for(int y = startY; y < startY + height; y++) {
			for(int x = startX; x < startX + width; x++) {
				try {
					setCurrentTile(map[y][x]);
					batch.draw(currentTile,
							   (x - startX) * cellSize,
							   Gdx.graphics.getHeight() - (y - startY + 1) * cellSize,
							   cellSize,
							   cellSize);
				} catch(Exception e) {}
			}
		}
	}

	public void drawMapWithOffset(SpriteBatch batch, int startX, int startY, int width, int height, float offsetX) {
		// drawing sky
		setCurrentTile(0);
		batch.draw(currentTile, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		for(int y = startY; y < startY + height; y++) {
			for(int x = startX; x < startX + width; x++) {
				try {
					if(map[y][x] != 0) {
						setCurrentTile(map[y][x]);
						batch.draw(currentTile,
								   x * cellSize - (int) offsetX,
								   Gdx.graphics.getHeight() - (y - startY + 1) * cellSize,
								   cellSize, cellSize);
					}
				} catch(Exception e) { /* who cares? no texture - undefined behaviour! don't forget to add textures! */ }
			}
		}

		for(int i = 0; i < sprites.size(); i++) {
			TextureBody sprite = sprites.get(i);
			if(Math.abs(sprite.getX() - offsetX) <= Gdx.graphics.getWidth()) {
				currentTile.setRegion(sprite.getTextureX(), sprite.getTextureY(),
						              sprite.getTextureWidth(), sprite.getTextureHeight());

				batch.draw(currentTile, sprite.getX() - offsetX, sprite.getY(),
						sprite.getWidth(), sprite.getHeight());
			}
		}

		// animated sprites
		for(int i = 0; i < animations.size(); i++) {
			com.And1sS.SuperMarioBros.OldVersion.Anim animation = animations.get(i);

			animation.update();
			batch.draw(animation.getCurrentRegion(), animation.getX() - offsetX, animation.getY(),
					animation.getDrawableWidth(), animation.getDrawableWidth());

			if(!animation.isLoadedSucessful() || animation.shouldBeDisposed()) {
				animations.remove(i--);
			}
		}

		// enemies
		enemies.drawEnemies(batch, offsetX);
	}

    public static Level loadFromFile(String fileName, String textureName) {
		try {
			FileHandle file = Gdx.files.internal(fileName);
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
			Level temp = new Level(map, Gdx.graphics.getHeight() / mapHeight, new Texture(Gdx.files.internal(textureName)));
            int cellSize = temp.getCellSize();

			for(int i = 0; i < objects; i++) {
				String line = br.readLine();
				String[] tokens = line.split(del);

				temp.addSprite(new TextureBody(
                        cellSize * Integer.parseInt(tokens[0]), cellSize * Integer.parseInt(tokens[1]),
                        cellSize * Integer.parseInt(tokens[2]), cellSize * Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]),
                        Integer.parseInt(tokens[6]), Integer.parseInt(tokens[7]))
                );
			}
			return temp;

		} catch(Exception e) {
		    e.printStackTrace();
		}
		return null;
	}

    private void setCurrentTile(int tileId) {
        switch(tileId) {
            case TileId.TRANSPARENT_COLLIDABLE_BLOCK:
            case TileId.TRANSPARENT_NOT_COLLIDABLE_BLOCK:
                currentTile.setRegion(80, 16, 16, 16);
                break;

            case 10:
                currentTile.setRegion(0, 0, 16, 16);
                break;

            case TileId.BROWN_IRON_BLOCK:
                currentTile.setRegion(32, 0, 16, 16);
                break;

            case 14:
                currentTile.setRegion(161, 32, 15, 16);
                break;
//            case TileId.SECRET_BLOCK_EMPTY_BROWN:
//            case 22:
//            case 23:
//                currentTile.setRegion(64 + (int) currentFrame * 16, 0, 16, 16);
//                break;
//            case TileId.SECRET_BLOCK_USED_BROWN:
//                currentTile.setRegion(176, 0, 16, 16);
//                break;
        }
    }

    public int[][] getMap() { return map; }

    public void setMap(int[][] map) {
        this.map = map;
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

	public void addAnim(Anim animation) {
		animations.add(animation);
	}

	public void addSprite(TextureBody bounds) {
		sprites.add(bounds);
	}

	public void updateTime() {
		time -= Gdx.graphics.getDeltaTime();
		currentFrame += Gdx.graphics.getDeltaTime() * 5;

		if(currentFrame >= 3)
			currentFrame -= 3;
	}

	public EnemyManager getEnemyManager() { return enemies; }

	public int getTime() { return (int) time; }

	public void resetTime() {
		time = TIME;
	}
}
