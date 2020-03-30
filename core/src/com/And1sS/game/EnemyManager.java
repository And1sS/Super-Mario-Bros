package com.And1sS.game;

import java.util.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.*;

public class EnemyManager {
	List<Enemy> enemies;

	public EnemyManager(int cellSize) {
		enemies = new ArrayList<Enemy>();

		enemies.add(new Goomba(35 * cellSize, 12 * cellSize, cellSize, cellSize));
		enemies.add(new Goomba(40 * cellSize, 12 * cellSize, cellSize, cellSize));	
        enemies.add(new CoopaTroopa(25 * cellSize, 12 * cellSize, cellSize * 1.1f, cellSize * 1.3f));
		
		enemies.add(new Goomba(174 * cellSize, 12 * cellSize, cellSize, cellSize));
		enemies.add(new Goomba(177 * cellSize, 12 * cellSize, cellSize, cellSize));	
        enemies.add(new CoopaTroopa(160 * cellSize, 12 * cellSize, cellSize * 1.1f, cellSize * 1.3f));
		
		enemies.add(new Goomba(90 * cellSize, 12 * cellSize, cellSize, cellSize));
		enemies.add(new Goomba(100 * cellSize, 12 * cellSize, cellSize, cellSize));	
        enemies.add(new CoopaTroopa(144 * cellSize, 8 * cellSize, cellSize * 1.1f, cellSize * 1.3f));
	}

	public void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	};

	public void updateEnemies(Level level, Player player) {
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);

			float distanceToPlayer =
					Math.abs(enemy.getBody().getX() - player.getBody().getOffsetX());

			if(distanceToPlayer <= Gdx.graphics.getWidth()) {
				enemy.update(level, player);

				if(!enemy.getAnim().isLoadedSucessful() || enemy.shouldBeDeleted()) {
					enemies.remove(enemy);
					i--;
				}
			}
		}
	}

	public void drawEnemies(SpriteBatch batch, float offsetX) {
		for(Enemy enemy : enemies) {
			if(Math.abs(enemy.getBody().getX() - offsetX) <= Gdx.graphics.getWidth()) {
				enemy.draw(batch, offsetX);
			}
		}
	}
}

