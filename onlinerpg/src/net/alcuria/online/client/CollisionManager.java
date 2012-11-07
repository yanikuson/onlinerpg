package net.alcuria.online.client;

public class CollisionManager {

	Player player;
	Monster[] enemies;
	DropManager drops;
	ParticleList slices;
	ParticleList burns;
	InputHandler inputs;

	public CollisionManager(Player player, Monster[] enemies, DropManager drops, ParticleList slices, ParticleList burns, InputHandler inputs){
		this.player = player;
		this.enemies = enemies;
		this.drops = drops;
		this.inputs = inputs;

		this.slices = slices;
		this.burns = burns;
	}

	public void update(Map map, DamageList damageList, ParticleList explosions, ItemManager inventory){

		// update our refrential list of enemies. WE NEED TO DO THIS WHEN WE SWAP MAPS
		if (enemies[0] == null){
			this.enemies = map.spawner.monsterList;
		}

		for (int i = 0; i < map.npcs.length; i++){
			if (inputs.typed[InputHandler.ATTACK] && player.bounds.overlaps(map.npcs[i].bounds) && !map.npcs[i].startCommands){
				inputs.typed[InputHandler.ATTACK] = false;
				map.npcs[i].start();
			}
		}
		for (int i = 0; i < MonsterSpawner.MAX_MONSTERS; i++) {

			if (enemies != null && enemies[i] != null) {

				enemies[i].command(map, player);
				enemies[i].update(map);

				// hurt a player
				if (enemies[i].timeSinceSpawn > 0.5){
					if ((enemies[i].bounds.overlaps(player.bounds) || (enemies[i].projectile != null && enemies[i].projectile.bounds.overlaps(player.bounds))) && enemies[i].HP > 0){
						player.damage(enemies[i].bounds.x, Config.getDamageDone(enemies[i].atk, enemies[i].power, player.def, player.stamina), damageList);
						player.animation.setReady();
					}
				}


				// hurt an enemy
				if (enemies[i].bounds.overlaps(player.swingBounds) && enemies[i].HP > 0){
					enemies[i].damage(player, Config.getDamageDone(player.atk, player.power, enemies[i].def, enemies[i].stamina), damageList, explosions, slices, drops);
				}
				if (player.skills.visible && player.skills.harmful && player.skills.area.overlaps(enemies[i].bounds)){

					// switch to handle all different types of damage
					switch (player.skills.id) {
					case SkillManager.FIREBALL:
						enemies[i].damage(player, Config.getDamageDone(player.matk, player.wisdom, enemies[i].mdef, enemies[i].wisdom), damageList, explosions, burns, drops);
						break;
					case SkillManager.FREEZE:
						enemies[i].damage(player, Config.getDamageDone(player.matk, player.wisdom, enemies[i].mdef, enemies[i].wisdom), damageList, explosions, burns, drops);
						enemies[i].effects.add(StatusEffects.POISON, 3, 30);
						break;
					}

				}

			}

		}

		// check for a collision with loot
		for (int i = 0; i < DropManager.MAX_DROPS; i++){
			if (drops.dropList[i].bounds.overlaps(player.bounds) && drops.dropList[i].visible){
				drops.collect(i, inventory);
			}
		}

	}

}
