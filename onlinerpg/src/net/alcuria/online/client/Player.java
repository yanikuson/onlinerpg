package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Actor {
	// a counter to calculate how long the player is invincible
	public float swingTimer = 0;						// time since last swing
	public float swingPeriod = 0.35f;						// how long the player must wait to swing again

	public boolean renderToggler = false;				// a toggler, to render the player invisible every other frame when damaged
	public Rectangle swingBounds;
	Particle swing;
	
	public Sound swingSound;
	public Sound levelupSound;
	private Particle levelup;
	public NotificationList notifications;
	private boolean levelSoundPlayed = false;
	
	public int statPts = 0;
	
	public float knockback;
	
	public Item weapon, helmet, armor, accessory;
	
	public Player(String filename, int x, int y, int width, int height, NotificationList notifications, AssetManager assets) {
		super(filename, x, y, width, height, assets);
		
		this.maxHP = Config.getMaxHP(lvl, stamina);
		this.HP = this.maxHP;
		
		this.walkSpeed = 100;
		this.jumpPower = 100;
		this.knockback = 100;
		
		swingBounds = new Rectangle(0,0,0,0);
		this.notifications = notifications;
		
		// TODO: really add these to an animations hash or something
		swing = new Particle("sprites/swing.png", x, y, 84, 84, 7, 3, false, assets);
		swingSound = assets.get("sounds/swing.wav", Sound.class);
		
		levelupSound = assets.get("sounds/levelup.wav", Sound.class);
		levelup = new Particle("sprites/levelup.png", 32, 32, 32, 32, 27, 3, false, assets);
		
		// give our player some equipment
		weapon = new Item(Item.ID_BLANK);
		helmet = new Item(Item.ID_BLANK);
		armor = new Item(Item.ID_BLANK);
		accessory = new Item(Item.ID_BLANK);
		
		this.power = 5;
		this.stamina = 5;
		this.wisdom = 5;
		
		visible = true;

	}

	// the player's command must override the default command method, because it needs to poll the inputhandler to do proper (read: not random) input
	public void command(InputHandler inputs){
		if (inputs.pressing[InputHandler.LEFT]){
			moveCommand[MOVE_LEFT] = true;
		} else {
			moveCommand[MOVE_LEFT] = false;

		}

		if (inputs.pressing[InputHandler.RIGHT]){
			moveCommand[MOVE_RIGHT] = true;
		} else {
			moveCommand[MOVE_RIGHT] = false;
		}

		if (inputs.typed[InputHandler.JUMP]){
			moveCommand[MOVE_JUMP] = true;
			inputs.typed[InputHandler.JUMP] = false;
		} else {
			moveCommand[MOVE_JUMP] = false;
		}

		if (inputs.typed[InputHandler.ATTACK] && swingTimer >= swingPeriod){

			moveCommand[MOVE_ATTACK] = true;
			inputs.typed[InputHandler.ATTACK] = false;
			startSwing();
			if (this.animation.stabPose){
				swingTimer = -0.5f;
			} else {
				swingTimer = 0;
			}
			
			
		} else {
			moveCommand[MOVE_ATTACK] = false;
			swingTimer += Gdx.graphics.getDeltaTime();
		}

		// update the swing bounds (collision rectangle) while the animation is being played.
		if (swing.playAnimation){
			swingBounds.x += xVel;
			if (facingLeft) {
				swing.update(bounds.x - 45, bounds.y - 20, true);
				swingBounds.x += -230 * Gdx.graphics.getDeltaTime();
			} else {
				swing.update(bounds.x - 25, bounds.y - 20, true);
				swingBounds.x += 230 * Gdx.graphics.getDeltaTime();
			}
			swingBounds.y = bounds.y;
		}
		// check to stop pushing out the collision swing rectangle thing
		if (this.animation.stabPose && swingTimer > -0.3f){
			swing.row = 1;
			swingBounds.x = - 100;
			swingBounds.y = - 100;
		} else if (swingTimer > 0.2f) {
			swing.row = 0;
			swingBounds.x = - 100;
			swingBounds.y = - 100;
		}

		// update our level up animation guy
		levelup.update(bounds.x - 8, bounds.y, true);
	}

	public void startSwing(){
		animation.startAttack(facingLeft);
		
		swingSound.play(Config.sfxVol);
		if (facingLeft){
			swingBounds.x = bounds.x + 20;
		} else {
			swingBounds.x = bounds.x - 20;
		}

		swingBounds.y = bounds.y;
		swingBounds.width = bounds.width;
		if (animation.stabPose) {
			swingBounds.height = (float) (bounds.height*1.2);
			swing.row = 1;
		} else {
			swingBounds.height = bounds.height*2;
			swing.row = 0;
		}
		
		swing.start(bounds.x, bounds.y, facingLeft);
	}

	public void render(SpriteBatch batch){

		if (visible){
			if (renderToggler){
				batch.flush();
				batch.setColor(1, 1, 1, 0.5f);
			}
			if (flash) flashPrecheck(batch);
			animation.render(batch, bounds.x, bounds.y);

			if (renderSensorPoints){
				for(int i=0; i<sensorY.length; i++){
					batch.draw(debugPoint, sensorX[i], sensorY[i]);
				}
				batch.draw(debugPoint, swingBounds.x, swingBounds.y);
				batch.draw(debugPoint, swingBounds.x + swingBounds.width, swingBounds.y);
				batch.draw(debugPoint, swingBounds.x, swingBounds.y + swingBounds.height);
				batch.draw(debugPoint, swingBounds.x + swingBounds.width, swingBounds.y + swingBounds.height);

			}
			
			if (flash) flashPostcheck(batch);
		}
		swing.render(batch);

		// do player-specific flash effect
		if (renderToggler){
			
			if (hurtTimer >= invincibilityPeriod){
				renderToggler = false;
				
			} 
			
			batch.flush();
			batch.setColor(1, 1, 1, 1);
		}
		effects.render(batch);
		levelup.render(batch);
		

	}

	public void damage(float enemyX, int damage, DamageList damageList){

		// if it has been at least invincibilityPeriod seconds, damage hero
		if (hurtTimer >= invincibilityPeriod){
			damageList.start(damage, bounds.x, bounds.y, !facingLeft, Damage.TYPE_HURT);
			hurt.play(Config.sfxVol);
			hurtTimer = 0;
			renderToggler = true;
			if (enemyX > bounds.x){
				xVel = -2;
			} else {
				xVel = 2;
			}
			yVel = 2;
			
			HP -= damage;
			if (HP <= 0){
				
				// TODO: game over handler?
				kill.play(Config.sfxVol);
				HP = maxHP;
				xVel = yVel = 0;
				bounds.y = 5*16;
				bounds.x = 5*16;
				facingLeft = false;
				visible = true;
			}

		} 


	}

	public void giveEXP(int expVal) {
		do {
			if (Config.notifExp) notifications.add("Earned " + expVal + " EXP.");
			curEXP += expVal;
			if (curEXP >= neededEXP){
				lvl++;
				if (Config.notifLevel) notifications.add("Congratulations! You've reached level " + lvl + ".");
				statPts += 2;
				
				// set new max HP
				maxHP = Config.getMaxHP(lvl, stamina);
				HP = maxHP;
				
				// update XP
				curEXP -= neededEXP;
				neededEXP = Config.getNextLvl(lvl);
				
				// to ensure we don't play the level up sound later twice
				if (!levelSoundPlayed){
					flash(1, 1, 0, 1, 4);
					levelup.start(bounds.x, bounds.y, false);
					levelupSound.play(Config.sfxVol);
					levelSoundPlayed = true;					
				}
			}
		} while (curEXP > neededEXP);
		levelSoundPlayed = false;
		
	}
	
	public boolean allocateStatPoint(int selection){
		
		if (statPts > 0){
			if (selection == 0){
				power++;					
			}
			if (selection == 1){
				stamina++;	
				int oldHP = maxHP;
				maxHP = Config.getMaxHP(lvl, stamina);
				HP += maxHP - oldHP;
			}
			if (selection == 2){
				wisdom++;					
			}
			statPts--;
			return true;
		}
		return false;
	}

}