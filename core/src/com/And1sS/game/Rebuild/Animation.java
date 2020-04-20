package com.And1sS.game.Rebuild;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.awt.*;

public class Animation {
    private TextureRegion currentRegion;

    private int numberOfFrames;

    private Rectangle bounds;

    private boolean looping;
    private boolean fileLoadingSuccessful;
    private boolean animationEnded = false;

    private double currentFrame = 0;
    private double frameChangingSpeed;

    public Animation(String fileName, int numberOfFrames,
                int frameX, int frameY,
                int frameWidth, int frameHeight,
                double frameChangingSpeed,
                boolean infinitelyRepeat) {
        this.numberOfFrames = numberOfFrames;
        this.frameChangingSpeed = frameChangingSpeed;
        this.looping = infinitelyRepeat;

        bounds = new Rectangle(frameX, frameY, frameWidth, frameHeight);

        Texture atlas = null;
        try {
            atlas = new Texture(Gdx.files.internal(fileName));
            fileLoadingSuccessful = true;
        } catch (Exception e) {
            fileLoadingSuccessful = false;
        }

        if (fileLoadingSuccessful) {
            currentRegion = new TextureRegion(atlas);
            currentRegion.setRegion(frameX, frameY, frameWidth, frameHeight);
        }
    }

    public Animation(String fileName, int frameX, int frameY,
                     int frameWidth, int frameHeight) {
        this(fileName, 1, frameX, frameY, frameWidth, frameHeight, 0, false);
    }

    public Animation(TextureRegion region, int numberOfFrames,
                     int frameX, int frameY,
                     int frameWidth, int frameHeight,
                     double frameChangingSpeed,
                     boolean looping) {
        if (region != null) {
            fileLoadingSuccessful = true;
        } else {
            fileLoadingSuccessful = false;
        }

        this.currentRegion = region;
        this.numberOfFrames = numberOfFrames;
        this.frameChangingSpeed = frameChangingSpeed;
        this.looping = looping;

        bounds = new Rectangle(frameX, frameY, frameWidth, frameHeight);
    }

    public Animation(TextureRegion region,
                     int frameX, int frameY,
                     int frameWidth, int frameHeight) {
        this(region, 1, frameX, frameY, frameWidth, frameHeight, 0, true);
    }

    public void update(float deltaTime) {
        if (deltaTime > 0.025)
            return;

        currentFrame += frameChangingSpeed * deltaTime;
        if(!looping && currentFrame > numberOfFrames) {
            animationEnded = true;
        } else if(currentFrame > numberOfFrames) {
            currentFrame -= numberOfFrames;
        }

        if(fileLoadingSuccessful) {
            currentRegion.setRegion((int) (bounds.getX() + (int) currentFrame * bounds.getWidth()),
                (int) (bounds.getY()), (int) (bounds.getWidth()), (int) (bounds.getHeight()));
        }
    }

    public void setSpeed(float speed) { this.frameChangingSpeed = speed; }

    public void setTextureX(int x) {
        bounds.setBounds(x, (int) bounds.getY(),
                (int) bounds.getWidth(), (int) bounds.getHeight());
    }

    public void setTextureY(int y) {
        bounds.setBounds((int) bounds.getWidth(), y,
                (int) bounds.getWidth(), (int) bounds.getHeight());
    }

    public void setTextureHeight(int height) {
        bounds.setSize((int) bounds.getWidth(), height);
    }

    public void set(int x, int y, int width, int height) {
        bounds.setBounds(x, y, width, height);
    }

    public void setCurrentFrame(double currentFrame) {
        this.currentFrame = currentFrame;
    }

    public boolean isLoadedSucessful() { return fileLoadingSuccessful; }

    public TextureRegion getCurrentRegion() { return currentRegion; }

    public boolean isAnimationEnded() { return animationEnded; }

    public double getX() { return bounds.getX(); }

    public double getY() { return bounds.getY(); }

    public double getDrawableWidth() { return bounds.getWidth(); }

    public double getDrawableHeight() { return bounds.getHeight(); }
}
