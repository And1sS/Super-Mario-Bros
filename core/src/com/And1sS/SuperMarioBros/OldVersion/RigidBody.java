package com.And1sS.SuperMarioBros.OldVersion;

import com.badlogic.gdx.math.*;

public class RigidBody {
	private float x;
	private float y;

	private float velocityX;
	private float velocityY;

	private float width;
	private float height;

	private float offsetX;
	private float offsetY;

	private boolean onGround;

	private Rectangle bounds;

	public RigidBody(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;

		this.width = width;
		this.height = height;

		bounds = new Rectangle();

		onGround = false;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public void setVelocityX(float velocityX) {
		this.velocityX = velocityX;
	}

	public void setVelocityY(float velocityY) {
		this.velocityY = velocityY;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getX() { return x; }

	public float getY() { return y; }

	public float getVelocityX() { return velocityX; }

	public float getVelocityY() { return velocityY; }

	public float getWidth() { return width; }

	public float getHeight() { return height; }

	public float getOffsetX() { return offsetX; }

	public float getOffsetY() { return offsetY; }

	public Rectangle getBounds() { return bounds; }

	public void setOnGround(boolean onGround) {
		this.onGround  = onGround;
	}

	public boolean isOnGround() { return onGround; }
}
