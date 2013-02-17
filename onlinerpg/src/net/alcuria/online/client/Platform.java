package net.alcuria.online.client;

import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Platform {

	public float duration;
	public float counter;
	public float dX, dY, x, y;
	public Rectangle bounds;
	public Texture texture;
	public TextureRegion region;

	public boolean updateCtr;

	public Platform(String line){
		counter = 0;

		// NEW: <time>, <x tile>, <y tile>, <xVel>, <yVel>
		String split[] = line.split(",");
		
		// time
		duration = Float.parseFloat(split[0]);
		
		// pos
		x = Integer.parseInt(split[1])*16;
		y = Integer.parseInt(split[2])*16;
		
		// vel
		dX = Float.parseFloat(split[3]);
		dY = Float.parseFloat(split[4]);

		bounds = new Rectangle(0, 0, 52, 5);
		
	}
	
	public Platform(Field f, String line){
		this(line);
		
		texture = f.assets.get("sprites/platform.png", Texture.class);
		region = new TextureRegion(texture, 0, 0, 52, 5);
		
	}

	public void update(Field f){

		counter += Gdx.graphics.getDeltaTime();

		// step platform
		x = x + dX;
		y = y + dY;

		// check if we should reverse the platform's direction
		
		if (counter > duration){
			counter = 0;
			dX *= -1;
			dY *= -1;
		}

		// update AABB's position
		bounds.x = x;
		bounds.y = y;

	}

	public void render(Field f){

		f.batch.draw(region, x, y);

	}
}
