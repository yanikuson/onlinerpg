package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Damage {

	final static int TYPE_DAMAGE = 0;
	final static int TYPE_HURT = 1;
	final static int TYPE_HEAL = 2;

	private BitmapFont damageNums;
	private BitmapFont healNums;
	private BitmapFont hurtNums;
	private float x = 0;
	private float y = 0;
	private float vX = 0;
	private float vY = 0;
	private int damage = 0;
	private boolean visible = false;
	private float frameTime = 0;
	private int type;

	public Damage(){

		damageNums = new BitmapFont(Gdx.files.internal("fonts/damage.fnt"), false);
		healNums = new BitmapFont(Gdx.files.internal("fonts/heal.fnt"), false);
		hurtNums = new BitmapFont(Gdx.files.internal("fonts/hurt.fnt"), false);


	}
	
	public void dispose(){
		damageNums.dispose();
		healNums.dispose();
		hurtNums.dispose();
	}

	public void start(int damage, float x, float y, boolean facingLeft, int type){

		if (!visible){
			this.type = type;
			this.damage = damage;
			this.x = x;
			this.y = y;
			this.vY = 2.5f;
			if (facingLeft){
				this.vX = -0.5f;
			} else {
				this.vX = 0.5f;
			}
			visible = true;
		}
	}


	public void update(){
		if (visible){
			x = x + vX;
			y = y + vY;
			vY = vY - 0.08f;
			frameTime += Gdx.graphics.getDeltaTime();
			if (frameTime > 1.5){
				frameTime = 0;
				visible = false;
			}

		}
	}

	public void render(SpriteBatch batch){

		if (visible){

			
			switch (type){
			case TYPE_DAMAGE:
				damageNums.draw(batch, Integer.toString(damage), x, y);
				break;

			case TYPE_HURT:
				hurtNums.draw(batch, Integer.toString(damage), x, y);
				break;

			case TYPE_HEAL:
				healNums.draw(batch, Integer.toString(damage), x, y);
				break;
			}
			

		}

	}

}
