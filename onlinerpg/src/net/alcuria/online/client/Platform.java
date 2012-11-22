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

	public float aX;
	public float aY;
	public float maxX;
	public float maxY;

	public boolean updateCtr;

	public Platform(Field f, String line){

		texture = f.assets.get("sprites/platform.png", Texture.class);
		region = new TextureRegion(texture, 0, 0, 52, 5);
		bounds = new Rectangle(0, 0, 52, 5);

		counter = 0;
		
		// File format: <time>, <x tile>, <y tile>, <max x vel>, <max y vel>, <acc x>, <acc y>
		String split[] = line.split(",");
		
		// time
		duration = Float.parseFloat(split[0]);
		
		// pos
		x = Float.parseFloat(split[1])*16;
		y = Float.parseFloat(split[2])*16;
		
		// vel
		maxX = Float.parseFloat(split[3]);
		maxY = Float.parseFloat(split[4]);
		
		// acc
		aX = Float.parseFloat(split[5]);
		aY = Float.parseFloat(split[6]);

	}

	public void update(Field f){

		// smoothing acceleration apply
		dX += aX;
		dY += aY;
		updateCtr = false;

		if (dX >= maxX){
			dX = maxX;
			updateCtr = true;
		}
		if (dX <= 0 - maxX){
			dX = 0 - maxX;
			updateCtr = true;			
		}

		if (dY >= maxY){
			dY = maxY;
			updateCtr = true;				
		}
		if (dY <= 0 - maxY){
			dY = 0 - maxY;
			updateCtr = true;			
		}

		if (updateCtr){
			counter += Gdx.graphics.getDeltaTime();
		}

		// step platform
		x = x + dX;
		y = y + dY;

		// check if we should reverse the platform's direction
		if (counter > duration){
			counter = 0;
			aX *= -1;
			aY *= -1;
		}

		// update AABB's position
		bounds.x = x;
		bounds.y = y;

	}

	public void render(Field f){

		f.batch.draw(region, x, y);

	}
}
