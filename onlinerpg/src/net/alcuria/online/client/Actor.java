package net.alcuria.online.client;

import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Actor {

	Field f;
	
	final float GRAVITY = 0.3f;
	final float GROUND_ACCEL = 30f;
	final float GROUND_DECEL = 0.40f;
	final float AIR_ACCEL = 10f;
	final float AIR_DECEL = 0.97f;
	final float TERMINAL_YVEL = -10f;
	final float STAB_SPEED = 0.5f;

	public final static int MOVE_LEFT = 1;
	public final static int MOVE_RIGHT = 2;
	public final static int MOVE_JUMP = 3;
	public final static int MOVE_ATTACK = 4;
	public final static int MOVE_INSPECT = 5;

	public float invincibilityPeriod = 1;			// how many seconds the player is invincible after being damaged
	public float hurtTimer = 0;		
	public float criticalHealthTimer = 0;

	public Rectangle bounds;						// physical representation of our player, for collisions
	public Rectangle feet;							// a rectangle just at the feet (for platforms)
	public Animator animation;						// graphical representation of our player, for "pretty" gameplay
	protected Texture debugPoint;					// a small point to display for the sensor points
	public StatusEffects effects;					// all of the actor's status effects
	public Platform curPlatform;					// current platform player is touching
	public SkillManager skills;
	
	public Texture hudCopy;
	public TextureRegion hpBack;
	public TextureRegion hpFront;					// front and back of the hp bar
	public boolean showHP = true;
	
	public Sound jump;
	public Sound shoot;
	public Sound hurt;
	public Sound hurtEnemy;
	public Sound kill;

	public float xVel;								// xVelocity of player
	public float yVel;								// yVelocity of player
	public boolean onGround = false;				// is our player touching the ground?
	public boolean renderSensorPoints = false;		// do we render our pink sensor pixels?
	public boolean facingLeft = false;				// is our player facing left?
	public boolean moving = false;					// is the player moving left or right?
	public int celWidth = 14;						// width and 
	public int celHeight = 22;						// height of our actor

	// character stats
	public float jumpPower = 80f;					// player's jump strength
	public float walkSpeed = 50f;					// players max walking speed
	public float attackSpeed = 9f;					// player's attack speed (more is faster)
	public int lvl = 1;
	public int maxHP = 100;
	public int HP = 100;
	public int maxEP = 10;
	public int EP = 10;
	public int curEXP = 0;
	public int neededEXP = Config.getNextLvl(lvl);
	public int power = 1;
	public int stamina = 1;
	public int wisdom = 1;
	public int atk = 0;
	public int matk = 0;
	public int def = 0;
	public int mdef = 0;

	public boolean[] moveCommand;					// used for actor movement types
	public boolean[] networkCommand;					// used for actor movement types
	public float[] sensorX, sensorY;				// array of sensor points: 0 = bot left, 1 = top right, 2 = mid left, 3 = mid right, etc.
	public float oldY;								// saving off the old Y prior to stepping Y to check for one-way platform collisions

	public boolean playJump = false;
	public boolean visible = false;					// do we render the Actor?

	public boolean lastFrameInAir = false;			
	public boolean flash = false;					// are we flashing?
	public float flashTime = 0;						// number of seconds to flash
	public float curFlash = 0;						// current flash time
	public float[] targetRGBA = {1f,1f,1f,1f};
	public float[] currentRGBA = {1f,1f,1f,1f};
	
	// for networking
	public boolean networkingPlayer = false;
	public Actor(String filename, int x, int y, int width, int height, Field f){
		if (f == null){
			return;
		}
		this.f = f;
				
		celWidth = width;
		celHeight = height;

		// create the bounding box and assign it the passed in position
		this.bounds = new Rectangle(x+6, y+6, celWidth-3, celHeight-3);
		this.feet = new Rectangle(x+6, y+6, celWidth-3, 5);

		// create new sensor point texture
		this.debugPoint = f.assets.get("sprites/point.png", Texture.class);
		sensorX = new float[6];
		sensorY = new float[6];
		for(int i=0; i<sensorY.length; i++){
			sensorX[i] = 0f;
			sensorY[i] = 0f;
		}

		// create our graphical player
		animation = new Animator(filename, celWidth, celHeight, f.assets);

		// create the movement array
		moveCommand = new boolean[10];
		networkCommand = new boolean[10];
		clearMoveCommands();

		// load sound assets
		hurt = f.assets.get("sounds/hurt.wav", Sound.class);
		jump = f.assets.get("sounds/jump.wav", Sound.class);
		hurtEnemy = f.assets.get("sounds/hurt_enemy.wav", Sound.class);
		kill = f.assets.get("sounds/kill.wav",Sound.class);
		shoot = f.assets.get("sounds/shoot.wav", Sound.class);

		// status effects handler
		effects = new StatusEffects(this, f);
		
		// HP bar variables
		hudCopy = f.assets.get("ui/hud-player.png", Texture.class);
		hpBack = new TextureRegion(hudCopy, 26, 11, 1, 1);
		hpFront = new TextureRegion(hudCopy, 0, 32, 1, 1);

	}

	// clears all of the move command arrays
	public void clearMoveCommands() {
		
		for (int i=0; i<moveCommand.length; i++){
			moveCommand[i] = false;
			networkCommand[i] = false;
		}
		
	}

	public void knockback(boolean facingLeft, float kb){
		// calculate KB
		if (facingLeft){
			xVel = kb * -1;
		} else {
			xVel = kb;
		}
		yVel = kb;
	}
	
	public void checkIfOnMovingPlatform(Map map){

		curPlatform = null;
		if (map.platforms != null) {

			// iterate through each of the platforms
			for (int i = 0; i < map.platforms.length; i++){
				if (map.platforms[i] != null && yVel <= 0 && feet.overlaps(map.platforms[i].bounds)) {

					if (curPlatform == null) {
						yVel = 0;
						onGround = true;
						curPlatform = map.platforms[i];

						while (bounds.overlaps(map.platforms[i].bounds)){
							bounds.y++;
						}
						bounds.y--;
						return;
					}

				}
			}

		}
	}

	// handles actor movement, jumping, collision, etc. 
	public void update(Map map){

		// do not update if actor is dead
		if (HP <= 0){
			visible = false;
			return;
		}

		// ############################ STEP X #################################
		
		bounds.x = bounds.x + xVel;
		if (curPlatform != null) {
			bounds.x += curPlatform.dX;
		}
		setAllSensors(bounds.x, bounds.y);


		// Do LEFT movement handler
		if (!sensorTouchesLeftSide(map)){

			if(moveCommand[MOVE_LEFT] && !animation.swingPose && !animation.stabPose){
				moving = true;

				if (onGround){
					facingLeft = true;
					xVel -= GROUND_ACCEL * Gdx.graphics.getDeltaTime();
				} else {
					xVel -= AIR_ACCEL * Gdx.graphics.getDeltaTime();
				}
				xVel = Math.max(xVel, (0 - walkSpeed - effects.speedOffset)/50);
			} else if (animation.stabPose){
				if (facingLeft){
					xVel = -1 * STAB_SPEED;
				} else {
					xVel = STAB_SPEED;
				}
			}

		} else {

			// player is in a wall, let's push him back out
			while (sensorTouchesLeftSide(map)){
				bounds.x++;
				moveAllSensors(1, 0);
			}
			bounds.x--;
			moveAllSensors(-1, 0);


		}

		// Do RIGHT movement handler
		if (!sensorTouchesRightSide(map)){

			if(moveCommand[MOVE_RIGHT] && !animation.swingPose && !animation.stabPose){
				moving = true;

				if (onGround) {
					facingLeft = false;
					xVel += GROUND_ACCEL * Gdx.graphics.getDeltaTime();
				} else {
					xVel += AIR_ACCEL * Gdx.graphics.getDeltaTime();

				}
				xVel = Math.min(xVel, (walkSpeed + effects.speedOffset)/50);
			} else if (animation.stabPose){
				if (facingLeft){
					xVel = -1 * STAB_SPEED;
				} else {
					xVel = STAB_SPEED;
				}
			}

		} else {

			// player is in a wall, let's push him back out
			while (sensorTouchesRightSide(map)){
				bounds.x--;
				moveAllSensors(-1, 0);
			}
			bounds.x++;
			moveAllSensors(1, 0);


		}

		// update x velocity due to friction
		if (animation.swingPose || (!moveCommand[MOVE_LEFT] && !moveCommand[MOVE_RIGHT])){
			moving = false;
			if (onGround){
				xVel *= GROUND_DECEL;
			} else if (!animation.swingPose){
				xVel *= AIR_DECEL;
			}
		}

		if (xVel < 0.05 && xVel > -0.05){
			xVel = 0;
		}

		// ############################ STEP Y #################################

		oldY = bounds.y;
		bounds.y = bounds.y + yVel;
		if (curPlatform != null) {
			bounds.y += curPlatform.dY;
		}
		setAllSensors(bounds.x, bounds.y);

		// add gravity's acceleration
		if (onGround || curPlatform != null){
			yVel = 0;
		} else if (yVel > TERMINAL_YVEL) {
			yVel -= GRAVITY;
		}


		// this check is to see if the prior frame the player was in the air
		lastFrameInAir = false;
		if (map.getTileAtPoint(sensorX[0], oldY) == 0 || map.getTileAtPoint(sensorX[1], oldY) == 0){
			lastFrameInAir = true;

		}

		// check if either sensor point A OR B touches the ground
		if (yVel <= 0 && (map.getSubTileAtPoint(sensorX[0], sensorY[0], yVel) != map.COLL_EMPTY || map.getSubTileAtPoint(sensorX[1], sensorY[1], yVel) != map.COLL_EMPTY)){

			if (map.getTileAtPoint(sensorX[0], sensorY[0]) == map.COLL_DOWNBLOCKING){

				/*
				if (!lastFrameInAir){
					if ((map.getTileAtPoint(sensorX[0], sensorY[0]+1) == map.COLL_EMPTY || map.getTileAtPoint(sensorX[1], sensorY[0]+1) == map.COLL_EMPTY )){
						onGround = true;
						yVel = 0;
					} else {
						return;
					}
				}*/

			}
			// set boundsY to the height of the TALLER of the two sensors
			if(bounds.y - getHigherSensor(map) < 5){
				bounds.y = getHigherSensor(map);
				setAllSensors(bounds.x, bounds.y);

				onGround = true;
				yVel = 0;
			}

		}

		// check if the top sensors hit a ceiling
		if (yVel > 0 && eitherSensorTouchesTopSide(map)){
			yVel = 0;
			while (eitherSensorTouchesTopSide(map)){
				bounds.y--;
				moveAllSensors(0, -1);
			}
		}

		// check if we fall off an edge
		if (curPlatform == null){
			if (onGround && (map.getSubTileAtPoint(sensorX[0], sensorY[0], yVel) == map.COLL_EMPTY && map.getSubTileAtPoint(sensorX[1], sensorY[1], yVel) == map.COLL_EMPTY)){
				if(bounds.y - getHigherSensor(map) > 2){
					onGround = false;
				} else {
					bounds.y = getHigherSensor(map);
					setAllSensors(bounds.x, bounds.y);
				}
			}
		} else {
			onGround = true;
		}

		// check for a JUMP
		if (onGround && moveCommand[MOVE_JUMP] && !animation.swingPose && !animation.stabPose){
			moveCommand[MOVE_JUMP] = false;
			onGround = false;
			yVel = jumpPower/17;
			if (playJump){
				jump.play(Config.sfxVol);
			}
			// boost player up
			bounds.y = bounds.y + yVel;
			setAllSensors(bounds.x, bounds.y);
		}
		
		// check for an ATTACK
		if (moveCommand[MOVE_ATTACK] && !animation.swingPose && !animation.stabPose){
			moveCommand[MOVE_ATTACK] = false;			
			moving = false;
		}

		// ALL OTHER PROCESSING 
		if (effects.timer[StatusEffects.FREEZE] <= 0) {
			animation.update(this);
		}

		if (hurtTimer < invincibilityPeriod){
			hurtTimer += Gdx.graphics.getDeltaTime();
		} 

		// if we are below 25% health, flash sprite...
		if (HP*100/maxHP <= 25){
			criticalHealthTimer+= Gdx.graphics.getDeltaTime();
			if (criticalHealthTimer > 1){
				flash(1,0,0,1,1);
				criticalHealthTimer = 0;
			}
		}
		// this is temporary -- it resets the player if they fall off! :)
		if (bounds.y < -500){
			bounds.y = 5*16;
			bounds.x = 5*16;
			facingLeft = false;
		}

		// update the feet bounds for the moving platform collision check
		feet.x = bounds.x;
		feet.y = bounds.y;
		
		effects.update(this);

	}


	public void flash(float r, float g, float b, float a, float duration) {

		this.curFlash = 0;
		this.flashTime = duration;
		this.targetRGBA[0] = r;
		this.targetRGBA[1] = g;
		this.targetRGBA[2] = b;
		this.targetRGBA[3] = a;
		this.currentRGBA[0] = r;
		this.currentRGBA[1] = g;
		this.currentRGBA[2] = b;
		this.currentRGBA[3] = a;
		this.flash = true;


	}

	public float getHigherSensor(Map map) {
		// set the height to the height value discovered, plus the number of tiles*16
		return Math.max(((int) ((sensorY[0]-1)/Config.TILE_WIDTH))*Config.TILE_WIDTH + map.getSubTileAtPoint(sensorX[0], sensorY[0], yVel), ((int) ((sensorY[1]-1)/Config.TILE_WIDTH))*Config.TILE_WIDTH + map.getSubTileAtPoint(sensorX[1], sensorY[1], yVel) );

	}

	public boolean bottomSensorTouchesGround(int id, Map map){
		return !(map.getSubTileAtPoint(sensorX[id], sensorY[id], yVel) == map.COLL_EMPTY);
	}

	// a helper function to see if there's a blocking tile on the top of the player
	public boolean eitherSensorTouchesTopSide(Map map) {
		return map.getTileAtPoint(sensorX[4], sensorY[4]) == map.COLL_BLOCKING || map.getTileAtPoint(sensorX[5], sensorY[5]) == map.COLL_BLOCKING;
	}

	// a helper function to see if there's a blocking tile on the right side of the player
	public boolean sensorTouchesRightSide(Map map) {
		return map.getTileAtPoint(sensorX[3], sensorY[3]) == map.COLL_BLOCKING;
	}

	// a helper function to see if there's a blocking tile on the left side of the player
	public boolean sensorTouchesLeftSide(Map map) {
		return map.getTileAtPoint(sensorX[2], sensorY[2]) == map.COLL_BLOCKING;
	}

	// a helper function to adjust all sensors simultaneously
	public void moveAllSensors(float x, float y) {
		for(int i=0; i< sensorX.length; i++){
			sensorX[i] += x;
			sensorY[i] += y;
		}

	}

	// a helper function to set all sensors simultaneously
	public void setAllSensors(float boundsX, float boundsY) {

		sensorX[0] = bounds.x + 4;
		sensorY[0] = bounds.y + 0;
		sensorX[1] = bounds.x + 10;
		sensorY[1] = bounds.y + 0;

		sensorX[2] = bounds.x + 2;
		sensorY[2] = bounds.y + 5;
		sensorX[3] = bounds.x + 12;
		sensorY[3] = bounds.y + 5;

		sensorX[4] = bounds.x + 3;
		sensorY[4] = bounds.y + celHeight - 5;
		sensorX[5] = bounds.x + 11;
		sensorY[5] = bounds.y + celHeight - 5;	

	}

	public void flashPrecheck(SpriteBatch batch){

		for (int i = 0; i < currentRGBA.length; i++){
			currentRGBA[i] += curFlash/flashTime * (1 - targetRGBA[i]);
			if (currentRGBA[i] > 1){
				currentRGBA[i] = 1;
			}
		}
		curFlash += Gdx.graphics.getDeltaTime();
		batch.flush();
		batch.setColor(currentRGBA[0], currentRGBA[1], currentRGBA[2], currentRGBA[3]);

	}

	public void flashPostcheck(SpriteBatch batch){
		batch.flush();
		batch.setColor(1,1,1,1);
		if (curFlash >= Config.lowHPFlashFrequency){
			flash = false;
			curFlash = 0;
		}
	}

	// takes as input a reference to a batcher and renders out the sprite
	public void render(SpriteBatch batch){


		if (visible){

			if (flash) flashPrecheck(batch);

			animation.render(batch, bounds.x, bounds.y);

			if (renderSensorPoints){
				for(int i=0; i<sensorY.length; i++){
					batch.draw(debugPoint, sensorX[i], sensorY[i]);
				}
			}

			if (flash) flashPostcheck(batch);

			effects.render(batch);


		}


	}
	
	// draw the hp bar below the char
	public void drawHP(SpriteBatch batch){
		batch.draw(hpBack, bounds.x, bounds.y - 5, bounds.width+2, 4);
		batch.draw(hpFront, bounds.x + 1, bounds.y - 4, ((HP * (bounds.width)) / maxHP), 2);
	}

	// dispose of all assets related to the player object
	public void dispose(){

		debugPoint.dispose();
		animation.dispose();
	}
}

