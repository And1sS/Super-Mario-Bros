package com.And1sS.SuperMarioBros.OldVersion;

import com.And1sS.SuperMarioBros.Rebuild.GameManager;
import com.And1sS.SuperMarioBros.Rebuild.GameScreens.StartScreen;
import com.badlogic.gdx.Game;

public class Platformer extends Game {

	@Override
	public void create() {
		GameManager.instantiate(this);
		setScreen(new StartScreen());
	}

}
