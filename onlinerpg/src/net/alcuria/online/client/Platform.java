package net.alcuria.online.client;

import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Platform {

	public boolean serverMode = true;
	public float radius, x, y, speed, counter;
	public float origX, origY;
	public float angle;
	public Rectangle bounds;
	public Texture texture;
	public TextureRegion region;
	
	public float dX, dY;
	
	public boolean updateCtr;

	public Platform(String line){

		// NEW: <radius>, <x tile>, <y tile>, <angle>, <speed coefficient>
		String split[] = line.split(",");
		
		// time
		radius = Float.parseFloat(split[0])*Config.TILE_WIDTH;
		
		// pos
		x = Float.parseFloat(split[1])*Config.TILE_WIDTH;
		y = Float.parseFloat(split[2])*Config.TILE_WIDTH;
		origX = x;
		origY = y;
		
		// angle (degrees -> radians) & speed
		angle = (float) (Float.parseFloat(split[3]) *
				0.0174532925); 
		speed = Float.parseFloat(split[4]);

		bounds = new Rectangle(0, 0, 52, 5);
		

		
	}
	
	public Platform(Field f, String line){
		this(line);
		
		texture = f.assets.get("sprites/platform.png", Texture.class);
		region = new TextureRegion(texture, 0, 0, 52, 5);
		serverMode = false;
	}

	public void update(Field f){

		if (serverMode){
			counter += 0.016666;
		} else {
			counter += Gdx.graphics.getDeltaTime();
		}
		// step platform
		dX = x;
		dY = y;
		
		x = origX + (float) (Math.sin(counter*speed) * Math.cos(angle)*radius);
		y = origY + (float) (Math.sin(counter*speed) * Math.sin(angle)*radius);

		// get deltaX/Y
		dX = x - dX;
		dY = y - dY;
		
		// update AABB's position
		bounds.x = x;
		bounds.y = y;

	}

	public void render(Field f){

		f.batch.draw(region, x, y);

	}
}
