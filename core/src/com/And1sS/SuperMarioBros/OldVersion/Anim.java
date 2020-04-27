package com.And1sS.SuperMarioBros.OldVersion;

import java.lang.String;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

public class Anim {
	private Texture atlas;
	private TextureRegion currentRegion;

	private String fileName;

	private int numberOfFrames;

	private TextureBody bounds;

	private boolean infinitelyRepeat;
	private boolean fileLoadingSucessful;
	private boolean shouldBeDisposed;

	private double currentFrame;
	private double frameChangingSpeed;
	private double speedY;

	public Anim(String fileName,
				int numberOfFrames,
				int frameStartX,
				int frameStartY,
				int frameWidth,
				int frameHeight,
				int posX,
				int posY,
				int drawableWidth,
				int drawableHeight,
				double frameChangingSpeed,
				boolean infinitelyRepeat) {

		currentFrame = 0;
		speedY = 0;

		shouldBeDisposed = false;

		this.fileName = fileName;

		this.numberOfFrames = numberOfFrames;

		bounds = new TextureBody(posX,
								   posY, 
								   drawableWidth,
								   drawableHeight,
								   frameStartX,
								   frameStartY,
								   frameWidth,
								   frameHeight);

		this.frameChangingSpeed = frameChangingSpeed;

		this.infinitelyRepeat = infinitelyRepeat;

		try {
			atlas = new Texture(Gdx.files.internal(fileName));
			fileLoadingSucessful = true;
		} catch(Exception e) {
			fileLoadingSucessful = false;
		}

		if(fileLoadingSucessful) {
			currentRegion = new TextureRegion(atlas);
			currentRegion.setRegion(frameStartX, frameStartY, frameWidth, frameHeight);
		}
	}

	public Anim(String fileName,
				int numberOfFrames,
				int frameWidth,
				int frameHeight,
				int drawableWidth,
				int drawableHeight,
				double frameChangingSpeed,
				boolean infinitelyRepeat) {
		this(fileName,
			 numberOfFrames,
			 0,
			 0,
			 frameWidth,
			 frameHeight,
			 0,
			 0,
			 drawableWidth,
			 drawableHeight,
			 frameChangingSpeed,
			 infinitelyRepeat);
	}

	public Anim(String fileName,
				int numberOfFrames,
				int frameWidth,
				int frameHeight,
				int posX,
				int posY,
				int drawableWidth,
				int drawableHeight,
				double frameChangingSpeed,
				boolean infinitelyRepeat) {
		this(fileName,
			 numberOfFrames,
			 0,
			 0,
			 frameWidth,
			 frameHeight,
			 posX,
			 posY,
			 drawableWidth,
			 drawableHeight,
			 frameChangingSpeed,
			 infinitelyRepeat);

	}

	public void update() {
		currentFrame += frameChangingSpeed * Gdx.graphics.getDeltaTime();

		bounds.setY((int) (bounds.getY() + Gdx.graphics.getDeltaTime() * speedY));
		if(!infinitelyRepeat && currentFrame > numberOfFrames) 
			shouldBeDisposed = true;
		else if(currentFrame > numberOfFrames) currentFrame -= numberOfFrames;

		if(fileLoadingSucessful)
			currentRegion.setRegion(bounds.getTextureX() + (int) currentFrame * bounds.getTextureWidth(),
									bounds.getTextureY(),
									bounds.getTextureWidth(),
									bounds.getTextureHeight());
	}

	public void setSpeed(double speed) { this.frameChangingSpeed = speed; }

	public void setTextureX(int x) { bounds.setTextureX(x); }

	public void setTextureY(int y) { bounds.setTextureY(y); }

	public void setTextureHeight(int height) { bounds.setTextureHeight(height); }

	public void setCurrentFrame(double currentFrame) { this.currentFrame = currentFrame; }

	public boolean isLoadedSucessful() { return fileLoadingSucessful; }

	public TextureRegion getCurrentRegion() { return currentRegion; }

	public boolean shouldBeDisposed() { return shouldBeDisposed; }

	public float getX() { return bounds.getX(); }

	public float getY() { return bounds.getY(); }

	public float getDrawableWidth() { return bounds.getWidth(); }

	public float getDrawableHeight() { return bounds.getHeight(); }

	public void set(float x, float y, float width, float height) {
		bounds.setX((int) x);
		bounds.setY((int) y);
		bounds.setWidth((int) width);
		bounds.setHeight((int) height);
	}	

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}
}

