package net.alcuria.online.client;

import net.alcuria.online.client.connection.GameClient;
import net.alcuria.online.client.screens.Field;
import net.alcuria.online.common.Packet.Packet7SendDamageNotification;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player extends Actor {

	public Rectangle desiredBounds;
	public boolean jumpSignal = false;

	// visual equip static values
	public static byte GENDER_MALE = 0;
	public static byte GENDER_FEMALE = 1;
	public static byte SKIN_PALE = 0;
	public static byte SKIN_TAN = 1;
	public static byte SKIN_DARK = 2;

	// a counter to calculate how long the player is invincible
	public float swingTimer = 0;						// time since last swing
	public float swingPeriod = 0.35f;					// how long the player must wait to swing again

	public boolean renderToggler = false;				// a toggler, to render the player invisible every other frame when damaged
	public boolean lightningWep = false;	
	public int lightningToggler = 0;
	public Rectangle swingBounds;

	Particle swing;
	Particle cast;

	public Sound swingSound;
	public Sound levelupSound;
	public Sound castSound;
	private Particle levelup;
	public SkillManager skills;
	private boolean levelSoundPlayed = false;

	public int statPts = 0;
	public String name;
	public String currentMap;
	public float knockback;

	public Item weapon, helmet, armor, accessory;

	public byte gender;
	public byte skin;
	public byte hair;

	VisualEquip visualHair;
	VisualEquip visualHelm;
	VisualEquip visualWeapon;
	VisualEquip visualArmor;

	public float epCounter = 0;
	public float epDelay = 4;
	
	// for networking
	public byte uid;
	public byte lastPing = 0;
	public boolean connected = false;
	public Array<Packet7SendDamageNotification> damageQueue;

	public Player(String name, int gender, int skin, int hair, int x, int y, int width, int height, Field f) {
		super(("sprites/equips/skin/" + (skin+1) + ".png"), x, y, width, height, f);

		this.damageQueue = new Array<Packet7SendDamageNotification>();
		this.desiredBounds = bounds;

		this.maxHP = Config.getMaxHP(lvl, stamina);
		this.HP = this.maxHP;
		this.name = name;

		this.walkSpeed = 100;
		this.jumpPower = 100;
		this.knockback = 100;

		swingBounds = new Rectangle(0,0,0,0);

		// TODO: really add these to an animations hash or something
		cast = new Particle("sprites/cast.png", x, y, 22, 16, 6, 3, false, f.assets);
		swing = new Particle("sprites/swing.png", x, y, 84, 84, 7, 3, false, f.assets);
		swingSound = f.assets.get("sounds/swing.wav", Sound.class);
		castSound = f.assets.get("sounds/cast.wav", Sound.class);

		levelupSound = f.assets.get("sounds/levelup.wav", Sound.class);
		levelup = new Particle("sprites/levelup.png", 32, 32, 32, 32, 27, 3, false, f.assets);

		// give our player some equipment
		weapon = new Item(Item.ID_BLANK);
		armor = new Item(Item.ID_BLANK);
		helmet = new Item(Item.ID_BLANK);
		accessory = new Item(Item.ID_BLANK);

		this.power = 5;
		this.stamina = 5;
		this.wisdom = 5;

		skills = new SkillManager(f.assets, this);

		visualHair = new VisualEquip("sprites/equips/hair/1.png", f.assets);
		visualWeapon = new VisualEquip(weapon.visualName, f.assets);
		visualHelm = new VisualEquip(helmet.visualName, f.assets);
		visualArmor = new VisualEquip(armor.visualName, f.assets);

		visible = true;

	}

	// the player's command must override the default command method, because it needs to poll the inputhandler to do proper (read: not random) input
	public void command(InputHandler inputs){
		
		if (!animation.castPose && !animation.stabPose && !animation.swingPose && !animation.itemPose){
			if (inputs.pressing[InputHandler.LEFT]){
				moveCommand[MOVE_LEFT] = true;
				networkCommand[MOVE_LEFT] = true;
			} else {
				moveCommand[MOVE_LEFT] = false;
				networkCommand[MOVE_LEFT] = false;
			}

			if (inputs.pressing[InputHandler.RIGHT]){
				moveCommand[MOVE_RIGHT] = true;
				networkCommand[MOVE_RIGHT] = true;
			} else {
				moveCommand[MOVE_RIGHT] = false;
				networkCommand[MOVE_RIGHT] = false;
			}

			if (inputs.typed[InputHandler.JUMP]){
				moveCommand[MOVE_JUMP] = true;
				networkCommand[MOVE_JUMP] = true;
				inputs.typed[InputHandler.JUMP] = false;
			} else {
				moveCommand[MOVE_JUMP] = false;
			}
		} else {
			moveCommand[MOVE_LEFT] = false;
			moveCommand[MOVE_RIGHT] = false;
			networkCommand[MOVE_RIGHT] = false;
			networkCommand[MOVE_LEFT] = false;


			moveCommand[MOVE_JUMP] = false;
		}

		// check if the player is trying to attack
		if (inputs.typed[InputHandler.ATTACK] && swingTimer >= swingPeriod && !animation.castPose && !animation.itemPose){
			moveCommand[MOVE_ATTACK] = true;
			networkCommand[MOVE_ATTACK] = true;
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

		updateSwing();

		// check if the player pressed a skill button
		if (!animation.itemPose && !animation.swingPose && !animation.stabPose && !animation.castPose && !skills.visible && EP > 0){
			if (inputs.typed[InputHandler.SKILL_1]){
				inputs.typed[InputHandler.SKILL_1] = false;
				startSkill(0);
			} else if (inputs.typed[InputHandler.SKILL_2]){
				inputs.typed[InputHandler.SKILL_2] = false;
				startSkill(1);
			} else if (inputs.typed[InputHandler.SKILL_3]){
				inputs.typed[InputHandler.SKILL_3] = false;
				startSkill(2);
			}
		}

		// update our player-specific particle effects
		levelup.update(bounds.x - 8, bounds.y, true);
		if (facingLeft) {
			cast.update(bounds.x - 8, bounds.y, true);
		} else {
			cast.update(bounds.x - 1, bounds.y, true);
		}

		skills.update();
		updateEquips();
		updateEP();


	}

	private void startSkill(int i) {
		animation.whichSkill = i;
		animation.castPose = true;
		cast.playAnimation = true;
		castSound.play(Config.sfxVol);

		EP--;
		epCounter = 0;

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

			visualHair.render(batch, bounds.x, bounds.y, animation.flipX);
			visualHelm.render(batch, bounds.x, bounds.y, animation.flipX);
			visualArmor.render(batch, bounds.x, bounds.y, animation.flipX);

			if (lightningWep){
				if ((lightningToggler/4) % 2 == 0){
					batch.flush();
					batch.setColor(1, 1, 0, 1);
				} else {
					batch.flush();
					batch.setColor(1, 1, 1, 1);
				}
			}
			visualWeapon.render(batch, bounds.x, bounds.y, animation.flipX);
			if (lightningWep){
				batch.flush();
				batch.setColor(1, 1, 1, 1);
				lightningToggler+= 1;
			}

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
		skills.render(batch);
		cast.render(batch);
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
			GameClient.sendDamage(this, null, (short) damage, false);

		} 

	}

	public void giveEXP(int expVal) {
		do {
			if (Config.notifExp) NotificationList.add("Earned " + expVal + " EXP.");
			curEXP += expVal;
			if (curEXP >= neededEXP){
				lvl++;
				if (Config.notifLevel) NotificationList.add("Congratulations! You've reached level " + lvl + ".");
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

	// resets all the equips (when a player changes gear)
	public void resetVisualEquips(){


		visualHair = new VisualEquip("sprites/equips/hair/" + (hair+1) + ".png", f.assets);

		visualWeapon.changeTexture(weapon.visualName);
		visualArmor.changeTexture(armor.visualName);



	}

	// updates the sub equips position, etc
	public void updateEquips() {

		// update the visual equips
		visualHair.update(animation.frame);
		visualWeapon.update(animation.frame);
		visualArmor.update(animation.frame);

	}

	public void updateEP(){

		if (EP < 10){
			epCounter+= Gdx.graphics.getDeltaTime();
			if (epCounter >= epDelay){
				epCounter = 0;
				EP++;
			}
		}
	}

	// the update method for OTHER players on the client
	public void networkUpdate() {


		// set network player's moving and facing flag
		if (Math.abs(desiredBounds.x - bounds.x) + Math.abs(desiredBounds.y - bounds.y) > 30){
			bounds.x = desiredBounds.x;
			bounds.y = desiredBounds.y;
		} 

		moveCommand[MOVE_LEFT] = networkCommand[MOVE_LEFT];
		moveCommand[MOVE_RIGHT] = networkCommand[MOVE_RIGHT];
		moveCommand[MOVE_JUMP] = networkCommand[MOVE_JUMP];
		networkCommand[MOVE_JUMP] = false;

		// check if the player is trying to attack
		if (networkCommand[MOVE_ATTACK] && swingTimer >= swingPeriod && !animation.castPose && !animation.itemPose){
			moveCommand[MOVE_ATTACK] = true;
			networkCommand[MOVE_ATTACK] = true;
			startSwing();
			if (this.animation.stabPose){
				swingTimer = -0.5f;
			} else {
				swingTimer = 0;
			}			
		} else {
			networkCommand[MOVE_ATTACK] = false;
			moveCommand[MOVE_ATTACK] = false;
			swingTimer += Gdx.graphics.getDeltaTime();
		}

		updateSwing();

	}

	private void updateSwing() {
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

	}
}