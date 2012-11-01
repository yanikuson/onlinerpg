package net.alcuria.online.client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DamageList {

	public Damage[] damages;
	public int index = 0;
	public final int MAX_ELEMS = 30;
	
	public DamageList() {

		damages = new Damage[MAX_ELEMS];
		for (int i = 0; i < damages.length; i++){
			damages[i] = new Damage();
		}
		
	}
	
	public void update() {
	
		for (int i = 0; i < damages.length; i++){
			damages[i].update();
		}
	}
	
	public void start(int damage, float x, float y, boolean facingLeft, int type) {
		
		// start a new instance of the damage object and increment our index
		damages[index].start(damage, x, y + 10, facingLeft, type);
		index =  (index + 1) % MAX_ELEMS;
		
	}
	
	public void render(SpriteBatch batcher) {
		for (int i = 0; i < damages.length; i++){
			damages[i].render(batcher);
		}
	}
	
}
