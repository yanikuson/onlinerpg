package net.alcuria.online.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Foreground {

	int foregroundWidth;
	int foregroundHeight;
	int numForegrounds;

	Texture foreground;
	TextureRegion layer;

	float x, y;
	int type = -1;
	public static final int FOREST = 0;

	public Foreground(Map map, AssetManager assets){

		if (map.name.equalsIgnoreCase("forest.png")){
			type = FOREST;
			foreground = assets.get("backgrounds/fog.png", Texture.class);
			foregroundWidth = Config.WIDTH;
			foregroundHeight = Config.HEIGHT;

			x = 0;
			y = 0;

			layer = new TextureRegion(foreground, 0, 0, foregroundWidth, Config.HEIGHT);

			numForegrounds = (int) (Config.WIDTH / foregroundWidth+2);
		}




	}

	public void render(SpriteBatch batch, CameraManager cameraManager) {

		if (type == FOREST){
			//for (int j = 0; j<= numForegrounds; j++){
			for (int i = 0; i<= numForegrounds; i++){
				//batch.draw(layer, (float) ((i*foregroundWidth - x + cameraManager.offsetX) % foregroundWidth), (float) ((j*foregroundHeight - y + cameraManager.offsetX) % foregroundHeight));
				batch.draw(layer, i*foregroundWidth+ (cameraManager.offsetX*0.5f) % foregroundWidth , cameraManager.offsetY);
			}	
			//}
		}
	}

	public void update(float deltaTime) {
		x += deltaTime * 600;
		y += deltaTime * 100;
		x = x % foregroundWidth;
		y = y % foregroundHeight;


	}

}
