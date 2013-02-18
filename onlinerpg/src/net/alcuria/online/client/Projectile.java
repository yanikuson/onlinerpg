package net.alcuria.online.client;

import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Projectile extends Actor {

	float launchTime = 0;
	TextureRegion projectileTexture;
	
	public Projectile(String filename, int x, int y, int width, int height,	Field f) {
		super(filename, x, y, width, height, f);

		//projectileTexture = new TextureRegion(new Texture(Gdx.files.internal(filename)));
	}

	public void update(Map map){
		
		// do not update if actor is dead
		if (HP <= 0){
			visible = false;
			launchTime = 0;
			return;
		}

		// ############################ STEP X #################################
		bounds.x = bounds.x + xVel;
		setAllSensors(bounds.x, bounds.y);

		// Do LEFT movement handler
		if (xVel < 0 && sensorTouchesLeftSide(map)){

			bounds.x = -10;
			bounds.y = -10;
			HP = 0;
			visible = false;
			launchTime = 0;

		} else if (xVel > 0 && sensorTouchesRightSide(map)){
			HP = 0;
			bounds.x = -10;
			bounds.y = -10;
			visible = false;
			launchTime = 0;

		}
		
		// check to see if it's too far
		launchTime+=Gdx.graphics.getDeltaTime();
		if(launchTime > 3){
			HP = 0;
			bounds.x = -10;
			bounds.y = -10;
			visible = false;
			launchTime = 0;
		}

	}
	
	public void render(SpriteBatch batch){		
		if (visible){		
			
			batch.draw(projectileTexture, bounds.x, bounds.y);
		
		}


	}
	// method to shoot the projectile
	public void shoot(float x, float y, float xVel){
		bounds.x = x;
		bounds.y = y;
		HP = 10;
		visible = true;
		this.xVel = xVel;
	}
	
}
