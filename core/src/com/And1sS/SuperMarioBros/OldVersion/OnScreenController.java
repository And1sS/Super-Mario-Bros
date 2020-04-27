package com.And1sS.SuperMarioBros.OldVersion;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;

public class OnScreenController {
	private Texture buttonAtlas;

	private Button leftButton;
	private Button rightButton;
	private Button bButton;
	private Button shootButton;
	private Button pauseButton;

	private int buttonWidth;
	private int buttonHeight;

	public OnScreenController() {

		buttonAtlas = new Texture(Gdx.files.internal("images/buttons.png"));

		buttonWidth = Gdx.graphics.getWidth() / 10;
		buttonHeight = buttonWidth;

		leftButton = new Button(buttonAtlas, new Rectangle(buttonWidth / 4, buttonHeight / 20, buttonWidth, buttonHeight), new Rectangle(0, 0, 512, 512));
		rightButton = new Button(buttonAtlas, new Rectangle(6 * buttonWidth / 4, buttonHeight / 20, buttonWidth, buttonHeight), new Rectangle(512, 0, -512, 512));
		bButton = new Button(buttonAtlas, new Rectangle(Gdx.graphics.getWidth() - buttonWidth * 5 / 4, buttonHeight / 20, buttonWidth, buttonHeight), new Rectangle(512, 0, 512, 512));
	    shootButton = new Button(buttonAtlas, new Rectangle(Gdx.graphics.getWidth() - buttonWidth * 10 / 4, buttonHeight / 20, buttonWidth, buttonHeight), new Rectangle(1536, 0, 512, 512));
		pauseButton = new Button(buttonAtlas, new Rectangle(Gdx.graphics.getWidth() - buttonWidth * 2 / 3, Gdx.graphics.getHeight() - buttonHeight * 2 / 3, buttonWidth * 2 / 3, buttonHeight * 2 / 3), new Rectangle(1024, 0, 512, 512));
	}

	public boolean isLeftPressed() {
		switch(Gdx.app.getType()) {

			case Android:
				if (leftButton.isTouched()) {
					leftButton.getDrawableRegion().setY(512);
					return true;
				} else {
					leftButton.getDrawableRegion().setY(0);
				}
				break;

			case Desktop:
				if (Gdx.input.isKeyPressed(Input.Keys.A))
					return true;
				break;

			default:
				break;

		}
		return false;
	}

	public boolean isRightPressed() {
		switch(Gdx.app.getType()) {

			case Android:
				if(rightButton.isTouched()) {
					rightButton.getDrawableRegion().setY(512);
					return true;
				} else {
					rightButton.getDrawableRegion().setY(0);
				}
				break;

			case Desktop:
				if (Gdx.input.isKeyPressed(Input.Keys.D))
					return true;
				break;

			default:
				break;

		}
		return false;
	}

	public boolean isJumpPressed() {
		switch(Gdx.app.getType()) {

			case Android:
				if (bButton.justTouched()) {
					bButton.getDrawableRegion().setY(512);
					return true;
				} else {
					bButton.getDrawableRegion().setY(0);
				}
				break;

			case Desktop:
				if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
					return true;
				break;

			default:
				break;
		}
		return false;
	}

	public boolean isMenuPressed() {
		if(pauseButton.justTouched()) {
			pauseButton.getDrawableRegion().setY(512);
			return true;
		} else pauseButton.getDrawableRegion().setY(0);

		return false;
	}
	
	public boolean isShootTouched() {
		shootButton.getDrawableRegion().setY(512);
		
		if(shootButton.justTouched()) {
			return true;
		} else if(!shootButton.justTouched())
		shootButton.getDrawableRegion().setY(0);

		return false;
	}


	public void drawControls(SpriteBatch batch) {
		if (Gdx.app.getType() == ApplicationType.Android)
		{
			leftButton.draw(batch);
			rightButton.draw(batch);
			bButton.draw(batch);
			shootButton.draw(batch);
		}
		pauseButton.draw(batch);
	}
}