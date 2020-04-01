package com.And1sS.game.OldVersion;

public class TextureBody {
	private int x;
	private int y;
	private int width;
	private int height;

	private int textureX;
	private int textureY;
	private int textureWidth;
	private int textureHeight;

	public TextureBody(int x, int y, int width, int height,
					   int textureX, int textureY,
					   int textureWidth, int textureHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.textureX = textureX;
		this.textureY = textureY;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	int getX() { return x; }

	int getY() { return y; }

	int getWidth() { return width; }

	int getHeight() { return height; }

	int getTextureX() { return textureX; }

	int getTextureY() { return textureY; }

	int getTextureWidth() { return textureWidth; }

	int getTextureHeight() { return textureHeight; }

	void setTextureY(int textureY) { this.textureY = textureY; }

	void setTextureX(int textureX) { this.textureX = textureX; }

	void setTextureHeight(int textureHeight) { this.textureHeight = textureHeight; }

	void setX(int x) { this.x = x; }

	void setY(int y) { this.y = y; }

	void setWidth(int width) { this.width = width; }

	void setHeight(int height) { this.height = height; }
}
