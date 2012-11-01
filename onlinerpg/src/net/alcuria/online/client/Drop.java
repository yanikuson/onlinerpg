package net.alcuria.online.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Actor {

	public static final int DROP_MONEY = 1;

	private Particle dropParticle;
	
	public int particleID = 0;			// which row in the particle should we draw? 
	public int value = 0;				// value: for gold this is the amount, for items it is the item's ID!
	

	public Drop(String filename, int x, int y, int width, int height, AssetManager assets) {
		super(filename, x, y, width, height, assets);

		this.dropParticle = new Particle(filename, x, y, width, height, 4, 5, false, assets);

		this.dropParticle.loop = true;
		this.maxHP = 1;
		this.bounds.width -= 8;

		this.visible = false;
		this.bounds.x = -30;
		this.bounds.y = -30;
		
	}

	@Override
	public void update(Map map){

		if (visible) {
			dropParticle.update(bounds.x-4, bounds.y, true);
			if (onGround){
				dropParticle.rotate = false;
			} else {
				dropParticle.rotate = true;
				if (xVel > 0){
					dropParticle.clockwise = true;
				} else {
					dropParticle.clockwise = false;
				}
			}
			super.update(map);
		}
		
	}

	@Override
	public void render(SpriteBatch batch){
		
		dropParticle.render(batch);
		if (renderSensorPoints){
			for(int i=0; i<sensorY.length; i++){
				batch.draw(debugPoint, bounds.x, bounds.y);
				batch.draw(debugPoint, bounds.x + bounds.width, bounds.y);
				batch.draw(debugPoint, bounds.x, bounds.y + bounds.height);
				batch.draw(debugPoint, bounds.x + bounds.width, bounds.y + bounds.height);
			}

		}
		
	}

	@Override
	public void dispose(){
		dropParticle.dispose();

	}

	public void start(Monster m) {
		visible = true;
		dropParticle.row = particleID;
		bounds.x = m.bounds.x;
		bounds.y = m.bounds.y + 12;
		yVel = 4;
		xVel = m.xVel/4;
		onGround = false;
		dropParticle.start(bounds.x, bounds.y, false);
		dropParticle.playAnimation = true;
	}
	
	public void start(Player p) {
		visible = true;
		dropParticle.row = particleID;
		bounds.y = p.bounds.y + 12;
		yVel = 4;
		if (p.facingLeft) {
			xVel = -4;
			bounds.x = p.bounds.x - 10;
		} else {
			xVel = 4;
			bounds.x = p.bounds.x + 20;
		}
		
		onGround = false;
		dropParticle.start(bounds.x, bounds.y, false);
		dropParticle.playAnimation = true;
	}

	public void collect(ItemManager inventory) {
		visible = false;
		dropParticle.playAnimation = false;
		bounds.x = -30;
		bounds.y = -30;
		
		if (particleID == 0){
			inventory.money += value;
		} else {
			inventory.addItem(new Item(value));
		}

	}
}
