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

	int type = -1;
	public static final int FOREST = 0;
	public static final int BEACH = 1;
	public static final int VILLAGE = 2;

	public Background(Map map, AssetManager assets){

		backgroundWidth = 1;
		if (map.name.equalsIgnoreCase("forest.png")){
			type = FOREST;
			background = assets.get("backgrounds/forest.png", Texture.class);	
			backgroundWidth = Config.WIDTH - 2;

			layerC = new TextureRegion(background, 0, 0, backgroundWidth, Config.HEIGHT);
			layerB = new TextureRegion(background, 0, Config.HEIGHT, backgroundWidth, Config.HEIGHT);
			layerA = new TextureRegion(background, 0, Config.HEIGHT*2, backgroundWidth, Config.HEIGHT);
		} else if (map.name.equalsIgnoreCase("beach.png")){
			type = BEACH;
			background = assets.get("backgrounds/sky.png", Texture.class);	
			backgroundWidth = 256;
		} else if (map.name.equalsIgnoreCase("village.png")){
			type = VILLAGE;
			background = assets.get("backgrounds/sky.png", Texture.class);	
			backgroundWidth = 256;
		}


		numBackgrounds = (int) (Config.WIDTH / backgroundWidth+2);


	}

	public void render(SpriteBatch batch, CameraManager cameraManager) {

		switch (type){
		case FOREST:

			for (int i = 0; i<= numBackgrounds; i++){
				batch.draw(layerA, i*backgroundWidth + (cameraManager.offsetX/4) % backgroundWidth, cameraManager.offsetY);

			}
			for (int i = 0; i<= numBackgrounds; i++){
				batch.draw(layerB, i*backgroundWidth + (cameraManager.offsetX/8) % backgroundWidth, cameraManager.offsetY);

			}
			for (int i = 0; i<= numBackgrounds; i++){
				batch.draw(layerC, i*backgroundWidth + (cameraManager.offsetX/16) % backgroundWidth, cameraManager.offsetY);

			}		
			break;
			
		case BEACH:
			for (int i = 0; i<= numBackgrounds; i++){
				batch.draw(background, i*backgroundWidth + (cameraManager.offsetX/4) % backgroundWidth, cameraManager.offsetY);

			}
			break;
			
		case VILLAGE:
			for (int i = 0; i<= numBackgrounds; i++){
				batch.draw(background, i*backgroundWidth + (cameraManager.offsetX/4) % backgroundWidth, cameraManager.offsetY);

			}
			break;
		}

	}

}
