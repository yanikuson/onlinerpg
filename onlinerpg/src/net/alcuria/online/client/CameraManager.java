package net.alcuria.online.client;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraManager {

	public OrthographicCamera camera;

	
	private float camX;
	private float camY;
	private float camW;
	private float camH;
	
	public float offsetX;			// camera's X offset (for getting absolute coordinates!)
	public float offsetY;			// camera's X offset (for getting absolute coordinates!)
	
	float screenX, screenY;
	
	public CameraManager(){
		camW = Config.WIDTH;
		camH = Config.HEIGHT;
		camX = Config.WIDTH/2;
		camY = Config.HEIGHT/2;
		
		camera = new OrthographicCamera(Config.WIDTH, Config.HEIGHT);
		camera.setToOrtho(false, Config.WIDTH, Config.HEIGHT);
		camera.viewportHeight = Config.HEIGHT;
		camera.viewportWidth = Config.WIDTH;
		camera.position.set(camX, camY, 0); // start at middle of screen
		camera.update();
		
	}
	
	// takes as input the map-relative player coordinates, converts it to screen-relative coordinates and shifts the camera
	public void update(float playerX, float playerY, int mapWidth, int mapHeight) {
		
		// get screen-relative player coords and translate map
		playerX = playerX - camX;
		playerY = playerY - camY;
		if (playerX > 10){
			camX += (playerX - 10);
		} else if (playerX < -30){
			camX += playerX + 30;
		}
		if (playerY > 40){
			camY += (playerY - 40);
		} else if (playerY < -40){
			camY += playerY + 40;
		}
		
		// if map exceeds edges, push camera back!
		if (camX - camW/2 < 0){
			camX -= camX - camW/2;
		} else if (camX + camW/2 > mapWidth){
			camX = mapWidth - camW/2;
		}
		if (camY - camH/2 < 0){
			camY -= camY - camH/2;
		} else if (camY + camH/2 > mapHeight){
			camY = mapHeight - camW/2;
		}
		
		// update our screenX and screenY
		offsetX = camX - camW/2;
		offsetY = camY - camH/2;
		
		camera.position.set((int)camX, (int)camY, 0);
		camera.update();
		
	}
	
	
}
