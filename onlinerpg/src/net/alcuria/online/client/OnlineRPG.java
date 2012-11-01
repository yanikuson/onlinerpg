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

		// LOAD ALL ASSETS?

				
		// SET SCREEN
		this.setScreen(new Loading(this));
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
		/*
		 * commented out because this was causing some issues.
		 */
		//		
		//		float newAspectRatio = (float)width / (float)height;
		//		float scale = 1f;
		//		Vector2 crop = new Vector2(0f,0f);
		//
		//		if(newAspectRatio > aspectRatio) {
		//			scale = (float)height/(float)h;
		//			crop.x = (width - w*scale)/2f;
		//		} else if(aspectRatio < aspectRatio) {
		//			scale = (float)width/(float)w;
		//			crop.y = (height - h*scale)/2f;
		//		} else {
		//			scale = (float)width/(float)w;
		//		}
		//
		//		float w2 = (float)w*scale;
		//		float h2 = (float)h*scale;
		//		viewport = new Rectangle(crop.x, crop.y, w2, h2);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
