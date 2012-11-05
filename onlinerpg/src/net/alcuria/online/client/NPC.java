package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NPC extends Actor {

	float timeSinceSpawn = 0;
	float commandTimer = 0;
	float commandFrequency = 2;
	int rndCommand = 0;

	int type=0;						// type of monster as defined in Config.java (e.g., slime = 1)

	public NPC(String filename, int x, int y, int width, int height, int type, AssetManager assets) {
		super(filename, x, y, width, height, assets);
		
		this.maxHP = 1;
		this.visible = true;
		this.bounds.x = x;
		this.bounds.y = y;
		
		// monster-specific updates
		switch (type){
		case Config.NPC_WELCOME:
			
			// TODO: assign an array of NPCCommand objects
			// and add to these command objects certain values, such as talk, shop, heal, whatever.
			break;

		case Config.NPC_SHOP:

			break;
		default:

		}
		this.type = type;
	}

	public void render(SpriteBatch batch){
		super.render(batch);
	}

	// NPC AI, since it will probably be big, is going here
	public void command(Map map, Player player){

		if (HP > 0){

			// UPDATE AI or something here
			commandTimer += Gdx.graphics.getDeltaTime();
			timeSinceSpawn += Gdx.graphics.getDeltaTime();

			// check if we can issue a new command
			if (commandTimer > commandFrequency){

				// clear all previous commands
				for (int i = 0; i < moveCommand.length; i++) {
					moveCommand[i] = false;
				}

				// determine WHICH TYPE OF ENEMY
				switch (type) {
				default:

					// randomly choose a new command
					rndCommand = (int) (Math.random()*4);
					moveCommand[rndCommand] = true;			

					break;
				}

				// reset stuff
				commandTimer = 0;
				commandFrequency *= 1 + Math.random()/4;

			}

		}



		// also check if anything can change the command (for instance, enemy walks to a cliff or bumps a wall)

		// if we're moving right and we bump a wall on the right, let's turn the enemy around
		if (moveCommand[MOVE_RIGHT] && sensorTouchesRightSide(map)){
			moveCommand[MOVE_LEFT] = true;
			moveCommand[MOVE_RIGHT] = false;
		}

		// if we're moving left and we bump a wall on the left, let's turn the enemy around
		if (moveCommand[MOVE_LEFT] && sensorTouchesLeftSide(map)){
			moveCommand[MOVE_LEFT] = false;
			moveCommand[MOVE_RIGHT] = true;
		}

		// if the bottom left sensor is off the ground and we're moving left, turn around so we don't suicide!	
		if (moveCommand[MOVE_LEFT] && !bottomSensorTouchesGround(0, map)){
			moveCommand[MOVE_LEFT] = false;
			moveCommand[MOVE_RIGHT] = true;
		}

		// if the bottom right sensor is off the ground and we're moving right, turn around
		if (moveCommand[MOVE_RIGHT] && !bottomSensorTouchesGround(1, map)){
			moveCommand[MOVE_LEFT] = true;
			moveCommand[MOVE_RIGHT] = false;
		}
	}

}