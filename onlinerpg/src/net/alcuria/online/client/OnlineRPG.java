package net.alcuria.online.client;

import net.alcuria.online.client.screens.Loading;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Rectangle;

public class OnlineRPG extends Game {

	public static final int WIDTH = 416;
	public static final int HEIGHT = 240;
	
	public Rectangle viewport;
	
	@Override
	public void create() {
				
		// SET SCREEN
		this.setScreen(new Loading(this));
		
		// TODO: create a thread that saves the game every 20 seconds or so.
	}
	
	@Override
	public void dispose() {

	}

	@Override
	public void render() {	
		super.render();

	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
