package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Monster extends Actor {

	float timeSinceSpawn = 0;
	float commandTimer = 0;
	float commandFrequency = 2;
	int rndCommand = 0;
	int expVal;						// value awarded for killing
	int money;						// money the enemy has
	Item drop;						// item the enemy drops
	Item rareDrop;					// rare item the enemy drops
	int type=0;						// type of monster as defined in Config.java (e.g., slime = 1)

	public Projectile projectile;			// monster's projectile

	public Monster(String filename, int width, int height, int type, AssetManager assets) {
		super(filename, -60, -60, width, height, assets);

		// monster-specific updates
		switch (type){
		case Config.MON_EYE:

			this.maxHP = 15;

			this.atk = 1;
			this.def = 1;
			this.matk = 3;
			this.mdef = 3;

			this.power = 5;
			this.stamina = 5;
			this.wisdom = 5;

			this.expVal = 8;
			this.walkSpeed = 0;
			this.jumpPower = 0;

			this.commandFrequency = 1;
			this.projectile = new Projectile("sprites/projectile.png", -10, -10, 4, 4, assets);

			break;

		case Config.MON_SLIME:

			this.maxHP = 20;

			this.atk = 3;
			this.def = 0;
			this.matk = 0;
			this.mdef = 0;

			this.power = 5;
			this.stamina = 5;
			this.wisdom = 5;

			this.expVal = 6;
			this.walkSpeed = 50;
			this.jumpPower = 80;
			this.commandFrequency = 1;

			break;
		default:

		}

		this.invincibilityPeriod = 0.5f;
		this.type = type;
		this.HP = maxHP;
	}

	public void render(SpriteBatch batch){
		super.render(batch);
	}

	// monster AI, since it will probably be big, is going here
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
				case Config.MON_EYE:

					// always face the hero
					if (player.bounds.x > bounds.x){
						facingLeft = false;
					} else {
						facingLeft = true;
					}

					// if player is in line-of sight, attack
					if (Math.abs(player.bounds.x - bounds.x) < 200 && Math.abs(player.bounds.y - bounds.y) < 25)  {
						moveCommand[MOVE_ATTACK] = true;
						shoot.play(Config.sfxVol);
						if (facingLeft){
							projectile.shoot(bounds.x + 10, bounds.y + 5, -2);
						} else {
							projectile.shoot(bounds.x + 10, bounds.y + 5, 2);
						}
					}
					break;

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

		// update projectiles
		if (projectile != null){
			projectile.update(map);
		}

	}

	public void damage(Player player, int damage, DamageList damageList, ParticleList explosions, ParticleList slices, DropManager drops){
		if (hurtTimer >= invincibilityPeriod && timeSinceSpawn > 0.5){

			flash(1, 0, 0, 1, 5);
			slices.start(bounds.x - bounds.width/2, bounds.y - bounds.height, !player.facingLeft);
			damageList.start(damage, bounds.x, bounds.y, player.facingLeft, Damage.TYPE_DAMAGE);
			hurtTimer = 0;
			hurtEnemy.play();
			if (player.bounds.x > bounds.x){
				xVel = player.knockback/50 * -1;
			} else {
				xVel = player.knockback/50;
			}
			yVel = player.knockback/50;

			HP -= damage;
			if (HP <= 0){

				// kill off the enemy
				explosions.start(bounds.x + bounds.width/2 - 16, bounds.y - 16, false);
				kill.play();
				player.giveEXP(expVal);
				visible = false;

				// activate the drop
				drops.add(this);
			}
		}



	}

	// spawns a monster at location TILE COORDS X, Y
	public void spawn(int x, int y) {
		timeSinceSpawn = 0;
		bounds.x = x * Config.TILE_WIDTH;
		bounds.y = y * Config.TILE_WIDTH;
		xVel = 0;
		HP = maxHP;
		visible = true;
		flash(1, 0, 1, 0, 3);

	}

}