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

	public boolean smoothing = true;
	public float aX;
	public float aY;
	public float maxX;
	public float maxY;

	public boolean updateCtr;

	public Platform(Field f){

		texture = f.assets.get("sprites/platform.png", Texture.class);
		region = new TextureRegion(texture, 0, 0, 52, 5);
		bounds = new Rectangle(0, 0, 52, 5);

		counter = 0;
		duration = 0.50f;
		dX = 0;
		dY = 0;
		x = 9*16;
		y = 7*16;
		maxX = 5;
		maxY = 5;
		aX = 0.1f;
		aY = 0f;

	}

	public void update(Field f){

		// smoothing acceleration apply
		if (smoothing){
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
		} else {
			counter+= Gdx.graphics.getDeltaTime();
		}

		// step platform
		x = x + dX;
		y = y + dY;


		// check if we should reverse the platform's direction
		if (counter > duration){
			counter = 0;
			if (!smoothing) {
				dX = -1 * dX;
				dY = -1 * dY;
			} else {
				aX *= -1;
				aY *= -1;
			}
		}

		// update AABB's position
		bounds.x = x;
		bounds.y = y;

	}

	public void render(Field f){

		f.batch.draw(region, x, y);

	}
}
