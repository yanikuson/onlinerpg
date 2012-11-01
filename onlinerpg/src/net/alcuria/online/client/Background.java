package net.alcuria.online.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Background {

	int backgroundWidth;
	int numBackgrounds;
	
	Texture background;
	TextureRegion layerA;
	TextureRegion layerB;
	TextureRegion layerC;
	
	
	public Background(String filename, AssetManager assets){
		background = assets.get(filename, Texture.class);	
		backgroundWidth = Config.WIDTH - 2;
		
		layerC = new TextureRegion(background, 0, 0, backgroundWidth, Config.HEIGHT);
		layerB = new TextureRegion(background, 0, Config.HEIGHT, backgroundWidth, Config.HEIGHT);
		layerA = new TextureRegion(background, 0, Config.HEIGHT*2, backgroundWidth, Config.HEIGHT);
		
		numBackgrounds = (int) (Config.WIDTH / backgroundWidth+2);
	
		
	}

	public void render(SpriteBatch batch, CameraManager cameraManager) {
		for (int i = 0; i<= numBackgrounds; i++){
			batch.draw(layerA, i*backgroundWidth + (cameraManager.offsetX/4) % backgroundWidth, cameraManager.offsetY);
		
		}
		for (int i = 0; i<= numBackgrounds; i++){
			batch.draw(layerB, i*backgroundWidth + (cameraManager.offsetX/8) % backgroundWidth, cameraManager.offsetY);
		
		}
		for (int i = 0; i<= numBackgrounds; i++){
			batch.draw(layerC, i*backgroundWidth + (cameraManager.offsetX/16) % backgroundWidth, cameraManager.offsetY);
		
		}		
	}
	
}
