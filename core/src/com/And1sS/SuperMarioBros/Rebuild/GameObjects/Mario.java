package com.And1sS.SuperMarioBros.Rebuild.GameObjects;

import com.And1sS.SuperMarioBros.Rebuild.Animation;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.PlayerShaders;
import com.And1sS.SuperMarioBros.Rebuild.GameConstants.TileId;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Mario extends GameObject {

    // callback for changing level
    public interface ILevelChanger {
        void levelChanged();
    }

    // callback for changing camera offset
    public interface IOffsetChangeable {
        void onOffsetChanged(double newOffsetX);
    }

    private final ILevelChanger levelChanger;
    private final List<IOffsetChangeable> offsetSubscribers;

    public enum PlayerType {
        MARIO, SUPER_MARIO, FIRE_MARIO, INVINCIBLE_MARIO
    }

    private PlayerType playerType = PlayerType.MARIO;

    private int score = 0;
    private int coins = 0;
    private int lives = 3;

    private final int defaultMarioHeight;

    private boolean alive = true;

    public Mario(float x, float y, float width, float height, ILevelChanger levelChanger) {
        bounds = new Rectangle(x, y, width, height);
        this.x = x;
        this.y = y;
        this.levelChanger = levelChanger;

        defaultMarioHeight = (int) height;
        defaultMario();

        updater = new PlayerUpdater();
        animationUpdater = new PlayerAnimationUpdater();
        levelCollisionDetector = new PlayerCollider();
        renderer = new PlayerRenderer();

        offsetSubscribers = new ArrayList<>();
    }

    public void die() {
        velocityY = -25 * defaultMarioHeight;

        alive = false;
        lives--;
    }

    public void resetPowerUps() {
        if (playerType == PlayerType.MARIO) {
            die();
        } else {
            invincibleMario();
        }
    }

    public void moveLeft() {
        velocityX = -7 * defaultMarioHeight;
    }

    public void moveRight() {
        velocityX = 7 * defaultMarioHeight;
    }

    public void jump() {
        velocityY = -21 * defaultMarioHeight;
    }

    public void smallJump() {
        velocityY = -15 * defaultMarioHeight;
    }

    public void invincibleMario() {
        defaultMario();
        playerType = PlayerType.INVINCIBLE_MARIO;
    }

    public void defaultMario() {
        playerType = PlayerType.MARIO;

        animation = new Animation("images/mario.png", 3, 81, 32, 16, 16, 0, true);
        if (bounds.getHeight() == 2 * defaultMarioHeight) {
            y += defaultMarioHeight;
        }
        bounds.setY((float) y);
        bounds.setHeight(defaultMarioHeight);
    }

    public void superMario() {
        if(playerType == PlayerType.MARIO) {
            playerType = PlayerType.SUPER_MARIO;

            animation.setTextureY(0);
            animation.setTextureHeight(31);

            velocityX = 0;
            bounds.setHeight(2 * defaultMarioHeight);
            y -= defaultMarioHeight;
            bounds.setY((float) y);

            score += 100;
        }
    }

    public void respawn(float x, float y) {
        if (lives > 0) {
            flipAnimationXAxis = false;
            alive = true;
            this.x = x;
            this.y = y;
            velocityX = 0;
            velocityY = 0;
            offsetX = 0;

            for (IOffsetChangeable subscriber : offsetSubscribers) {
                subscriber.onOffsetChanged(offsetX);
            }
        }
    }

    public void subscribe(IOffsetChangeable subscriber) {
        offsetSubscribers.add(subscriber);
    }

    public boolean isAlive() { return alive; }

    public PlayerType getType() { return playerType; }

    public Rectangle getBounds() { return bounds; }

    public int getLives() { return lives; }

    public int getScore() { return score; }

    public int getCoins() { return coins; }

    private class PlayerUpdater implements IUpdatable {

        float invincibilityTimer = 3;

        @Override
        public void update(float deltaTime, Level level) {
           if (alive) {
               if (velocityX > 0) {
                   flipAnimationXAxis = false;
               } else if (velocityX < 0) {
                   flipAnimationXAxis = true;
               }

               //updating velocityY
               velocityY += Level.GRAVITATIONAL_ACCELERATION * deltaTime;

               //updating velocityX;
               if (velocityX > 100) {
                   velocityX -= 3000 * deltaTime;
               } else if (velocityX < -100) {
                   velocityX += 3000 * deltaTime;
               }

               //updating X
               x += velocityX * deltaTime;
               bounds.setX((float) x);
               performLevelCollisionDetectionX(level);

               // updating X offset for level
               if(x <= offsetX) {
                   x = offsetX;
                   bounds.setX((float) x);
                   velocityX = 0;
               }

               //updating Y
               y += velocityY * deltaTime;
               bounds.setY((float) y);
               performLevelCollisionDetectionY(level);

               if (Math.abs(velocityX) < 100)
                   velocityX = 0;

               //offset
               float screenCenterX = Gdx.graphics.getWidth() / 2;
               float levelWidth = level.getWidth() * level.getCellSize();
               if (x < levelWidth - screenCenterX && x > offsetX + screenCenterX
                       && velocityX > 0) {
                   offsetX = x - screenCenterX;
                   for (IOffsetChangeable subscriber : offsetSubscribers) {
                       subscriber.onOffsetChanged(offsetX);
                   }
               }

               if (playerType == PlayerType.INVINCIBLE_MARIO) {
                   updateInvincibilityTimer();
               }
           } else {
               velocityY += Level.GRAVITATIONAL_ACCELERATION * deltaTime;
               y += velocityY * deltaTime;
               bounds.setY((float) y);
           }
        }

        private void updateInvincibilityTimer() {
            invincibilityTimer -= Gdx.graphics.getDeltaTime();

            if(invincibilityTimer <= 0) {
                invincibilityTimer = 3;

                defaultMario();
            }
        }
    }

    private class PlayerCollider implements ILevelCollidable {
        @Override
        public void performCollisionDetectionX(Level level) {
            if (alive) {
                if (y > level.getHeight() * level.getCellSize()) {
                    die();
                }

                _performCollisionDetectionX(level);
            }
        }

        private void _performCollisionDetectionX(Level level) {
            int cellSize = level.getCellSize();
            for (int i = (int) (y) / cellSize; i < (y + bounds.getHeight()) / cellSize; i++) {
                for (int j = (int) (x) / cellSize; j < (x + bounds.getWidth()) / cellSize; j++) {
                    try {
                        switch(level.getCell(j, i)) {
                            case TileId.TRANSPARENT_COLLIDABLE_BLOCK:
                            case TileId.BROWN_BRICK_BLOCK:
                            case TileId.BLUE_BRICK_BLOCK:
                            case TileId.GREEN_BRICK_BLOCK:
                            case TileId.GRAY_BRICK_BLOCK:
                            case TileId.BROWN_IRON_BLOCK:
                            case TileId.BLUE_IRON_BLOCK:
                            case TileId.BROWN_STONE_BLOCK:
                            case TileId.BLUE_STONE_BLOCK:
                            case TileId.GRAY_STONE_BLOCK:
                            case TileId.BROWN_WAVE_BLOCK:
                            case TileId.BLUE_WAVE_BLOCK:
                            case TileId.SECRET_BLOCK_USED_BROWN:
                            case TileId.SECRET_BLOCK_USED_BLUE:
                            case TileId.SECRET_BLOCK_USED_GREEN:
                            case TileId.SECRET_BLOCK_USED_GRAY:
                            case TileId.SECRET_BLOCK_EMPTY:
                                if (velocityX > 0) {
                                    x = j * cellSize - bounds.getWidth() - 0.001;
                                    velocityX = 0;
                                    bounds.setX((float) x);
                                } else if (velocityX < 0) {
                                    x = (j + 1) * cellSize + 0.001;
                                    velocityX = 0;
                                    bounds.setX((float) x);
                                }
                                break;

                            case TileId.NEXT_LEVEL_SIGN:
                                levelChanger.levelChanged();

                            default:
                                break;
                        }
                    } catch (Exception e) {}
                }
            }
        }

        @Override
        public void performCollisionDetectionY(Level level) {
            if(alive) {
                _performCollisionDetectionY(level);
            }
        }

        private void _performCollisionDetectionY(Level level) {
            int cellSize = level.getCellSize();

            for (int i = (int) (y) / cellSize; i < (y + bounds.getHeight()) / cellSize; i++) {
                for (int j = (int) (x) / cellSize; j < (x + bounds.getWidth()) / cellSize; j++) {
                    try {
                        int cellType = level.getCell(j, i);
                        switch(cellType) {
                            case TileId.TRANSPARENT_COLLIDABLE_BLOCK:
                            case TileId.GREEN_BRICK_BLOCK:
                            case TileId.GRAY_BRICK_BLOCK:
                            case TileId.BROWN_IRON_BLOCK:
                            case TileId.BLUE_IRON_BLOCK:
                            case TileId.BROWN_STONE_BLOCK:
                            case TileId.BLUE_STONE_BLOCK:
                            case TileId.GRAY_STONE_BLOCK:
                            case TileId.BROWN_WAVE_BLOCK:
                            case TileId.BLUE_WAVE_BLOCK:
                            case TileId.SECRET_BLOCK_USED_BROWN:
                            case TileId.SECRET_BLOCK_USED_BLUE:
                            case TileId.SECRET_BLOCK_USED_GREEN:
                            case TileId.SECRET_BLOCK_USED_GRAY:
                                defaultCollisionY(i, cellSize);
                                break;

                            case TileId.BROWN_BRICK_BLOCK:
                                brickBlockCollisionY(i, j, level, TileId.BROWN_BRICK_BLOCK);
                                break;

                            case TileId.BLUE_BRICK_BLOCK:
                                brickBlockCollisionY(i, j, level, TileId.BLUE_BRICK_BLOCK);
                                break;

                            case TileId.SECRET_BLOCK_EMPTY:
                                defaultCollisionY(i, cellSize);
                                if (y >= (i + 1) * cellSize) {
                                    score += 100;
                                    coins += 1;
                                    level.setCell(j, i, 0);
                                    level.addObject(new Coin(j, i, level));
                                    level.addObject(new BouncingBrownSecretBlock(j, i, level));
                                }
                                break;

                            case TileId.SECRET_BLOCK_POWERUP_SUPERMARIO:
                                defaultCollisionY(i, cellSize);
                                if (y >= (i + 1) * cellSize) {
                                    level.setCell(j, i, TileId.TRANSPARENT_COLLIDABLE_BLOCK);
                                    level.addObject(new BouncingBrownSecretBlock(j, i, level));
                                    level.addObject(new Mushroom(j, i - 1, level));
                                }
                                break;

                            default:
                                break;
                        }
                    } catch (Exception e) {}
                }
            }
        }

        private void brickBlockCollisionY(int i, int j, Level level, int id) {
            int cellSize = level.getCellSize();
            defaultCollisionY(i, cellSize);
            if (y >= (i + 1) * cellSize) {
                //TODO: add other bouncing blocks classes
                switch (playerType) {
                    case INVINCIBLE_MARIO:
                    case MARIO:
                        level.setCell(j, i, TileId.TRANSPARENT_COLLIDABLE_BLOCK);
                        switch (id) {
                            case TileId.BROWN_BRICK_BLOCK:
                                level.addObject(new BouncingBrownBrick(j, i, level));
                                break;
                            case TileId.BLUE_BRICK_BLOCK:
                                level.addObject(new BouncingBlueBrick(j, i, level));
                                break;

                            default:
                                throw new RuntimeException("Invalid brick id");
                        }

                        break;
                    case FIRE_MARIO:
                    case SUPER_MARIO:
                        level.setCell(j, i, TileId.TRANSPARENT_NOT_COLLIDABLE_BLOCK);
                        switch (id) {
                            case TileId.BROWN_BRICK_BLOCK:
                                level.addObject(new BrownBrokenBrick(j, i, level));
                                break;
                            case TileId.BLUE_BRICK_BLOCK:
                                level.addObject(new BlueBrokenBrick(j, i, level));
                                break;
                            default:
                                throw new RuntimeException("Invalid brick id");
                        }
                        break;

                }
            }
        }

        private void defaultCollisionY(int i, float cellSize) {
            if(velocityY > 0) {
                y = i * cellSize - bounds.getHeight();
                velocityY = 0;
                bounds.setY((float) y);
            } else if (velocityY < 0) {
                y = (i + 1) * cellSize;
                velocityY = 0;
                bounds.setY((float) y);
            }
        }
    }

    private class PlayerRenderer implements IRenderable {

        @Override
        public void render(SpriteBatch spriteBatch) {
            _render(spriteBatch);

            if(playerType == PlayerType.INVINCIBLE_MARIO) {
                ShaderProgram shader = new ShaderProgram(com.And1sS.SuperMarioBros.Rebuild.GameConstants.PlayerShaders.VERTEX_SHADER, PlayerShaders.FRAGMENT_SHADER);
                spriteBatch.setShader(shader);

                _render(spriteBatch);
                spriteBatch.setShader(null);
            }
        }

        private void _render(SpriteBatch spriteBatch) {
            float windowHeight = Gdx.graphics.getHeight();
            if (flipAnimationXAxis) {
                spriteBatch.draw(animation.getCurrentRegion(),
                        (float) (x + bounds.getWidth() -  offsetX),
                        (float) (windowHeight - y - bounds.getHeight()),
                        -bounds.getWidth() * 1.1f, bounds.getHeight());
            } else {
                spriteBatch.draw(animation.getCurrentRegion(),
                        (float) (x - offsetX),
                        (float) (windowHeight - y - bounds.getHeight()),
                        bounds.getWidth() * 1.1f, bounds.getHeight());
            }
        }
    }

    private class PlayerAnimationUpdater implements IAnimationUpdatable {

        @Override
        public void updateAnimation(float deltaTime) {
            manageAnimation();
            animation.update(deltaTime);
        }

        private void manageAnimation() {
            if(!alive) {                                            // Died
                animation.setTextureX(176);
                animation.setSpeed(0);
                animation.setCurrentFrame(0);
            } else if (Math.abs(velocityY) > 0.001) {              // flying
                animation.setTextureX(160);
                animation.setSpeed(0);
                animation.setCurrentFrame(0);
            } else if (Math.abs(velocityX + velocityY) <= 0.001) { // Staying
                animation.setTextureX(80);
                animation.setSpeed(0);
                animation.setCurrentFrame(0);
            } else if(Math.abs(velocityX) > 0.001 && Math.abs(velocityY) <= 10) { // Running
                animation.setTextureX(96);
                animation.setSpeed(10);
            }
        }
    }
}
