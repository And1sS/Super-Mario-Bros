
package com.And1sS.SuperMarioBros.OldVersion;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.*;

public class Bullet {
	
	public static float ENEMY_SPEED_Y = -1000;
	
	private RigidBody body;
	
	private com.And1sS.SuperMarioBros.OldVersion.Anim animation;
	
	private boolean died;
	
	public Bullet(float x, float y, int width, int height) {
		animation = new Anim("images/objects.png", 2,
				96, 144, 8, 8,
				-1, -1, -1, -1,
				(double)5 , true);

		body = new RigidBody(x, y, width, height);
	}
	
	public void update(Level level) {
		if(Gdx.graphics.getDeltaTime() < 0.025) {
			//updating velocityY
			body.setVelocityY((float) body.getVelocityY() + Level.GRAVITATIONAL_ACCELERATION * Gdx.graphics.getDeltaTime() * Level.SPEED);

			//updating X
			body.setX(body.getX() + body.getVelocityX() * Gdx.graphics.getDeltaTime() * Level.SPEED);
			checkCollision(level, 0);

			//updating Y
			body.setY(body.getY() + body.getVelocityY() * Gdx.graphics.getDeltaTime() * Level.SPEED);
			checkCollision(level, 1);

			body.getBounds().set(body.getX(), body.getY(),
				body.getWidth(), body.getHeight());

			EnemyManager enemyManager = level.getEnemyManager();

			for(Enemy enemy : enemyManager.enemies) {
				if(body.getBounds().overlaps(enemy.getBody().getBounds()) && enemy.isDied()) {
					enemy.kill();
					enemy.drop();
					enemy.getBody().setVelocityY(ENEMY_SPEED_Y * Level.SPEED);
				    enemy.flip();
					
					died = true;
				}
			}
		}
		animation.update();
	}
	
	public void checkCollision(Level level, int dir) {
			try {
				for(int i = (int) (body.getY()) / level.getCellSize(); i < (body.getY() + body.getHeight()) / level.getCellSize(); i++) {
					for(int j = (int) (body.getX()) / level.getCellSize(); j < (body.getX() + body.getWidth()) / level.getCellSize(); j++) {
						switch(level.getCell(j, i)) {

							case 0:
								break;

							case 1:
							case 10:
							case 21:
							case 12:
							case 14:
							case 15:
							case 20:
							case 22:
							case 23:
								//check X
								if(body.getVelocityX() != 0 && dir == 0) {
									died = true;
									body.setVelocityX(0);
									body.setX((j -1) * level.getCellSize());
								}

								//check Y
								if(body.getVelocityY() > 0 && dir == 1) {
									body.setY(i * level.getCellSize() - body.getHeight());
									body.setVelocityY(-900 * Gdx.graphics.getHeight() / 1080);
							    }
								break;
						}
					}
				}
			} catch(Exception e) {}
	}
	
	public void draw(SpriteBatch batch, float offsetX) {
		batch.draw(animation.getCurrentRegion(),
				   (float) (body.getX() - offsetX),
				   (float) (Gdx.graphics.getHeight() - body.getY() - body.getHeight()),
				   (float) body.getWidth(),
				   (float) body.getHeight());

	}
	
	public RigidBody getBody() {
		return body;
	}
	
	public boolean isDied() {
		return died;
	}
}
