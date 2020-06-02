package com.And1sS.SuperMarioBros.OldVersion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.ArrayList;


public class Mario {
	private com.And1sS.SuperMarioBros.OldVersion.Anim animation;

	private final com.And1sS.SuperMarioBros.OldVersion.RigidBody playerBody;
	
	private final ArrayList<com.And1sS.SuperMarioBros.OldVersion.Bullet> bullets;

	private boolean movingLeft;
	private boolean died;
	private boolean lowInvincibility;

	private double invincibilityTimer = 3;

	public enum MarioType {
		MARIO, SUPER_MARIO, FIRE_MARIO
	}

	MarioType type;

	private int lives;
	private int score;

	public Mario(String fileName, float x, float y, int width, int height) {
		animation = new com.And1sS.SuperMarioBros.OldVersion.Anim(fileName, 3, 81, 32, 16, 16, -1, -1, -1, -1, 10, true);

		playerBody = new com.And1sS.SuperMarioBros.OldVersion.RigidBody(x, y, width, height);

		bullets = new ArrayList<com.And1sS.SuperMarioBros.OldVersion.Bullet>();
		
		type = MarioType.MARIO;

		movingLeft = false;
		died = false;
		lowInvincibility = false;

		lives = 3;
		score = 0;
	}

	public void update(com.And1sS.SuperMarioBros.OldVersion.Level level) {
		playerBody.setOnGround(false);

		if(playerBody.getVelocityX() > 0) movingLeft = false;
		else if(playerBody.getVelocityX() < 0) movingLeft = true;

		if(Gdx.graphics.getDeltaTime() < 0.025) {
			//updating velocityY
			if(!playerBody.isOnGround()) {
				playerBody.setVelocityY(playerBody.getVelocityY()
						+ com.And1sS.SuperMarioBros.OldVersion.Level.GRAVITATIONAL_ACCELERATION * com.And1sS.SuperMarioBros.OldVersion.Level.SPEED * Gdx.graphics.getDeltaTime());
			}

			//updating X
			playerBody.setX(playerBody.getX() + playerBody.getVelocityX() * com.And1sS.SuperMarioBros.OldVersion.Level.SPEED * Gdx.graphics.getDeltaTime() );
			checkCollision(level, 0);

			// updating X offset for level
			if(playerBody.getX() <= playerBody.getOffsetX()) {
				playerBody.setX(playerBody.getOffsetX());
				playerBody.setVelocityX(0);
			}

			//updating Y
			playerBody.setY(playerBody.getY() + playerBody.getVelocityY() * com.And1sS.SuperMarioBros.OldVersion.Level.SPEED * Gdx.graphics.getDeltaTime());
			checkCollision(level, 1);

			playerBody.getBounds().set(
					playerBody.getX(),
					playerBody.getY(),
					playerBody.getWidth(),
					playerBody.getHeight());
		}


		manageAnimation();
		//updating velocityX;
		updateVelocityX();
		if(Math.abs(playerBody.getVelocityX()) < 100)
			playerBody.setVelocityX(0);

		//offset
		if(playerBody.getX() > Gdx.graphics.getWidth() / 2 &&
		   playerBody.getX() < level.getWidth() * level.getCellSize() - Gdx.graphics.getWidth() / 2 &&
		   playerBody.getVelocityX() > 0 && 
		   playerBody.getX() > playerBody.getOffsetX() + Gdx.graphics.getWidth() / 2)
			playerBody.setOffsetX(playerBody.getX() - Gdx.graphics.getWidth() / 2);

		animation.update();

		updateInvincibilityTimer();
	}

	public void draw(SpriteBatch batch) {
		if(movingLeft)
			batch.draw(animation.getCurrentRegion(),
					   playerBody.getX() - playerBody.getOffsetX() + playerBody.getWidth(),
					   Gdx.graphics.getHeight() - playerBody.getY() - playerBody.getHeight(),
					   -playerBody.getWidth(), playerBody.getHeight());
		else batch.draw(animation.getCurrentRegion(),
						playerBody.getX() - playerBody.getOffsetX(),
						Gdx.graphics.getHeight() - playerBody.getY() - playerBody.getHeight(),
						 playerBody.getWidth(), playerBody.getHeight());
		
      if(lowInvincibility) {



		  ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		  batch.setShader(shader);
		  
		  if(movingLeft) {
			  batch.draw(animation.getCurrentRegion(),
					  playerBody.getX() - playerBody.getOffsetX() + playerBody.getWidth(),
					  Gdx.graphics.getHeight() - playerBody.getY() - playerBody.getHeight(),
					  -playerBody.getWidth(), playerBody.getHeight());
		  } else {
			  batch.draw(animation.getCurrentRegion(),
					  playerBody.getX() - playerBody.getOffsetX(),
					  Gdx.graphics.getHeight() - playerBody.getY() - playerBody.getHeight(),
					  playerBody.getWidth(), playerBody.getHeight());
		  }
		  batch.setShader(null);		
		}
	}

	public void checkCollision(com.And1sS.SuperMarioBros.OldVersion.Level level, int dir) {
		if(!died)
			try {
			for(int i = (int) (playerBody.getY()) / level.getCellSize(); i < (playerBody.getY() + playerBody.getHeight()) / level.getCellSize(); i++) {
				for(int j = (int) (playerBody.getX()) / level.getCellSize(); j < (playerBody.getX() + playerBody.getWidth()) / level.getCellSize(); j++) {
					switch(level.getCell(j, i)) {

						case 0:
							break;

						case 1:
						case 10:
						case 21:
						case 12:
							//check X
							if(playerBody.getVelocityX() > 0 && dir == 0) {
								playerBody.setX(j * level.getCellSize() - playerBody.getWidth());
								playerBody.setVelocityX(0);
							} else if(playerBody.getVelocityX() < 0 && dir == 0) {
								playerBody.setX((j + 1) * level.getCellSize());
								playerBody.setVelocityX(0);
							}

							//check Y
							if(playerBody.getVelocityY() > 0 && dir == 1) {
								playerBody.setY(i * level.getCellSize() - playerBody.getHeight());
								playerBody.setVelocityY(0);
								playerBody.setOnGround(true);
							} else if(playerBody.getVelocityY() < 0 && dir == 1) {
								playerBody.setY((i + 1) * level.getCellSize());
								playerBody.setVelocityY(0);
							}
							break;

						case 14:
						case 15:
						case 20:
						case 22:
						case 23:
							
							//check X
							if(playerBody.getVelocityX() > 0 && dir == 0) {
								playerBody.setX(j * level.getCellSize() - playerBody.getWidth());
								playerBody.setVelocityX(0);
							} else if(playerBody.getVelocityX() < 0 && dir == 0) {
								playerBody.setX((j + 1) * level.getCellSize());
								playerBody.setVelocityX(0);
							}

							//check Y
							if(playerBody.getVelocityY() > 0 && dir == 1) {
								playerBody.setY(i * level.getCellSize() - playerBody.getHeight());
								playerBody.setVelocityY(0);
								playerBody.setOnGround(true);
							} else if(playerBody.getVelocityY() < 0 && dir == 1) {
								playerBody.setY((i + 1) * level.getCellSize());
								playerBody.setVelocityY(0);

								if(level.getCell(j, i) == 20 || 
								   level.getCell(j, i) == 14) {
									if(level.getCell(j, i) == 20) level.setCell(j, i, 21);
									else level.setCell(j, i, 0);

									com.And1sS.SuperMarioBros.OldVersion.Anim temp = new com.And1sS.SuperMarioBros.OldVersion.Anim("images/objects.png", 4, 0, 96, 16, 16, j * level.getCellSize(), (Gdx.graphics.getHeight() - i * level.getCellSize()), level.getCellSize(), level.getCellSize(), 10, false);
									temp.setSpeedY(600 * Gdx.graphics.getHeight() / 1080);

									level.addAnim(temp);

									score += 100;
								} else if(level.getCell(j, i) == 22) {
									level.setCell(j, i, 21);
									level.getEnemyManager().addEnemy(new LifeMushroom(j * level.getCellSize(), (i - 1) * level.getCellSize(), level.getCellSize(), level.getCellSize()));
								} else if(level.getCell(j, i) == 23) {
									level.setCell(j, i, 21);
									level.getEnemyManager().addEnemy(new FireStar(j * level.getCellSize(), (i - 1) * level.getCellSize(), level.getCellSize(), level.getCellSize()));
								}
							}
							break;
					}
				}
			}
		} catch(Exception e) {}
	}

	private void manageAnimation() {
		if(playerBody.getVelocityY() != 0 && !playerBody.isOnGround()) {
			animation.setTextureX(160);
			animation.setSpeed(0);
			animation.setCurrentFrame(0);
		}
		if(playerBody.getVelocityY() == 0 && playerBody.getVelocityX() == 0) {
			animation.setTextureX(80);
			animation.setSpeed(0);
			animation.setCurrentFrame(0);
		} 
		if(playerBody.getVelocityX() != 0 && playerBody.getVelocityY() == 0) {
			animation.setTextureX(96);
			animation.setSpeed(10);
		}
		if(died) {
			animation.setTextureX(176);
			animation.setSpeed(0);
			animation.setCurrentFrame(0);
		}
	}
	
	public void fireMario(com.And1sS.SuperMarioBros.OldVersion.Level level) {

		animation.setTextureY(96);
		animation.setTextureHeight(31);

		playerBody.setVelocityX(0);
		
		if(type == MarioType.MARIO) {
			playerBody.setHeight(2 * level.getCellSize());
			playerBody.setY(playerBody.getY() - level.getCellSize());
		}

		type = MarioType.FIRE_MARIO;
		
		score += 100;
	}

	public void superMario(com.And1sS.SuperMarioBros.OldVersion.Level level) {
		if(type == MarioType.MARIO) {
			type = MarioType.SUPER_MARIO;

			animation.setTextureY(0);
			animation.setTextureHeight(31);

			playerBody.setVelocityX(0);
			playerBody.setHeight(2 * level.getCellSize());
			playerBody.setY(playerBody.getY() - level.getCellSize());

			score += 100;
		}
	}

	public void mario(com.And1sS.SuperMarioBros.OldVersion.Level level) {
		type = MarioType.MARIO;

		animation.setTextureY(32);
		animation.setTextureHeight(16);

		playerBody.setHeight(level.getCellSize());
		playerBody.setY(playerBody.getY() + playerBody.getHeight());
	}

	public void die() {
		died = true;

		playerBody.setVelocityY(-2100 * Gdx.graphics.getHeight() / 1080);
	}

	public int showGameOverScreen(com.And1sS.SuperMarioBros.OldVersion.Level level) {
		if(playerBody.getY() > level.getHeight() * level.getCellSize() && died) {
			lives--;

			playerBody.setX(3 * level.getCellSize());
			playerBody.setY(11 * level.getCellSize());
			playerBody.setOffsetX(0);

			died = false;
			mario(level);

			if(lives <= 0) {
				return 1;  	// 	1 - GAME OVER SCREEN
			} else {
				return 2;   // 	2 - JUST DIE SCREEN
			}
		} else if((playerBody.getY() > level.getHeight() * level.getCellSize() ||
				level.getTime() <= 0) && !died) {
			die();
			level.resetTime();
		}
		return -1;		    // -1 - DON'T SHOW ANYTHING
	}

	public void updateInvincibilityTimer() {
		if(lowInvincibility) {
			invincibilityTimer -= Gdx.graphics.getDeltaTime();

			if(invincibilityTimer <= 0) {
				invincibilityTimer = 3;

				lowInvincibility = false;
			}
		}
	}

	private void updateVelocityX() {
		if(playerBody.getVelocityX() > 0)
			playerBody.setVelocityX(playerBody.getVelocityX() - 50);
		else if(playerBody.getVelocityX() < 0)
			playerBody.setVelocityX(playerBody.getVelocityX() + 50);
	}

	public void setType(MarioType type) { this.type = type; }

	public void setLives(int lives) { this.lives = lives; }

	public void setScore(int score) { this.score = score; }

	public void setAnim(com.And1sS.SuperMarioBros.OldVersion.Anim animation) { this.animation = animation; }

	public void startLowInvincibility() { lowInvincibility = true; }

	public Anim getAnimation() { return animation; }

	public RigidBody getBody() { return playerBody; }

	public MarioType getType() { return type; }

	public int getLives() { return lives; }

	public int getScore() { return score; }

	public boolean isDied() { return died; }

	public boolean isLowInvincibility() { return lowInvincibility; }
	
	public void shoot(com.And1sS.SuperMarioBros.OldVersion.Level level) {
		if(type != MarioType.FIRE_MARIO)
			return;
			
		com.And1sS.SuperMarioBros.OldVersion.Bullet bullet = new com.And1sS.SuperMarioBros.OldVersion.Bullet(playerBody.getX() + playerBody.getWidth() - level.getCellSize()  / 2, playerBody.getY() + playerBody.getHeight() / 2, level.getCellSize() / 2, level.getCellSize() / 2);
		
		if(movingLeft) {
			bullet.getBody().setVelocityX(-600 * Gdx.graphics.getWidth() / 1920.0f);
			bullet.getBody().setX(playerBody.getX());
		} else {
			bullet.getBody().setVelocityX(600 * Gdx.graphics.getWidth() / 1920.0f);
		}
			
		bullets.add(bullet);
	}
	
    public void updateBullets(Level level) {
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update(level);
			
			if(bullets.get(i).isDied()) {
				bullets.remove(i--);
			}
		}
	}
	
	public void drawBullets(SpriteBatch batch) {
		for(Bullet bullet : bullets){
		    bullet.draw(batch, playerBody.getOffsetX());
		}
	}

	private final String vertexShader =
			"attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
					+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
					+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
					+ "uniform mat4 u_projTrans;\n" //
					+ "varying vec4 v_color;\n" //
					+ "varying vec2 v_texCoords;\n" //
					+ "\n" //
					+ "void main()\n" //
					+ "{\n" //
					+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
					+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
					+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
					+ "}\n";

	private final String fragmentShader =
			"#ifdef GL_ES\n" //
					+ "#define LOWP lowp\n" //
					+ "precision mediump float;\n" //
					+ "#else\n" //
					+ "#define LOWP \n" //
					+ "#endif\n" //
					+ "varying LOWP vec4 v_color;\n" //
					+ "varying vec2 v_texCoords;\n" //
					+ "uniform sampler2D u_texture;\n" //
					+ "void main()\n"//
					+ "{\n" //
					+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords).a * vec4(1, 0.1, 0.1, 0.35);\n" //
					+ "}";
}
