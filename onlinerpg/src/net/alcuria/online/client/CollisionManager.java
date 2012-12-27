package net.alcuria.online.client;

import net.alcuria.online.client.screens.Field;

public class CollisionManager {

	Field f;
	Player player;
	Monster[] enemies;
	DropManager drops;
	ParticleList slices;
	ParticleList burns;
	ParticleList freezes;

	InputHandler inputs;

	public CollisionManager(Field f, Monster[] enemies){

		this.f = f;
		this.player = f.player;
		this.enemies = enemies;
		this.drops = f.drops;
		this.inputs = f.inputs;

		this.slices = f.slices;
		this.burns = f.burns;
		this.freezes = f.freezes;
	}

	public void update(Map map, DamageList damageList, ParticleList explosions, ItemManager inventory){

		// npc talk?
		if (map.npcs != null){
			for (int i = 0; i < map.npcs.length; i++){
				if (map.npcs[i] != null && inputs.typed[InputHandler.SPACE] && player.bounds.overlaps(map.npcs[i].bounds) && !map.npcs[i].startCommands){
					inputs.typed[InputHandler.SPACE] = false;
					map.npcs[i].start();
				}
			}
		}

		// check for a player vs player collision
		if (Config.PvpEnabled){
			for (int i = 0; i < f.players.size; i++){
				if (f.player.swingBounds.overlaps(f.players.get(i).bounds)){
					f.players.get(i).damage(player.bounds.x, Config.getDamageDone(f.player.atk, f.player.power, f.players.get(i).def, f.players.get(i).stamina), damageList);
					f.players.get(i).animation.setReady();
				}
				
				if (player.skills.visible && player.skills.harmful && player.skills.area.overlaps(f.players.get(i).bounds)){
					// switch to handle all different types of damage
					switch (player.skills.id) {
					case SkillManager.WOUND:
						f.players.get(i).damage(f.player.bounds.x, Config.getDamageDone(player.matk, player.wisdom, f.players.get(i).mdef, f.players.get(i).wisdom), f.damageList);
						break;
					case SkillManager.FIREBALL:
						f.players.get(i).damage(f.player.bounds.x, Config.getDamageDone(player.matk + 10, player.wisdom, f.players.get(i).mdef, f.players.get(i).wisdom), f.damageList);
						break;
					case SkillManager.FREEZE:
						f.players.get(i).damage(f.player.bounds.x, Config.getDamageDone(player.matk, player.wisdom, f.players.get(i).mdef, f.players.get(i).wisdom), f.damageList);
						f.players.get(i).effects.add(StatusEffects.FREEZE, 10, 5);
						break;
					case SkillManager.BOLT:
						f.players.get(i).damage(f.player.bounds.x, Config.getDamageDone(player.matk+25, player.wisdom, f.players.get(i).mdef, f.players.get(i).wisdom), f.damageList);
						break;
					case SkillManager.POISON:
						f.players.get(i).effects.add(StatusEffects.POISON, player.wisdom/4 + 1, 10);
						break;
					}

				}
			}
		}
		
		// enemy collisions
		if (map.containsEnemies){

			// update our refrential list of enemies. WE NEED TO DO THIS WHEN WE SWAP MAPS
			if (enemies[0] == null){
				this.enemies = map.spawner.monsterList;
			}
			
			// check for a monster collision
			for (int i = 0; i < MonsterSpawner.MAX_MONSTERS; i++) {

				if (enemies != null && enemies[i] != null) {

					enemies[i].update(map);

					// hurt a player
					if (enemies[i].timeSinceSpawn > 0.5){
						if ((enemies[i].bounds.overlaps(player.bounds) || (enemies[i].projectile != null && enemies[i].projectile.bounds.overlaps(player.bounds))) && enemies[i].HP > 0 && enemies[i].effects.timer[StatusEffects.FREEZE] <= 0){
							player.damage(enemies[i].bounds.x, Config.getDamageDone(enemies[i].atk, enemies[i].power, player.def, player.stamina), damageList);
							player.animation.setReady();
						}
					}


					// hurt an enemy
					if (enemies[i].bounds.overlaps(player.swingBounds) && enemies[i].HP > 0){
						enemies[i].damage(player, Config.getDamageDone(player.atk, player.power, enemies[i].def, enemies[i].stamina), damageList, explosions, slices, drops, player.facingLeft, true);
					}
					if (player.skills.visible && player.skills.harmful && player.skills.area.overlaps(enemies[i].bounds)){

						// switch to handle all different types of damage
						switch (player.skills.id) {
						case SkillManager.WOUND:
							enemies[i].damage(player, Config.getDamageDone((int)(player.atk*1.2), player.power, enemies[i].def, enemies[i].stamina), damageList, explosions, slices, drops, player.facingLeft, true);
							break;
						case SkillManager.FIREBALL:
							enemies[i].damage(player, Config.getDamageDone(player.matk + 10, player.wisdom, enemies[i].mdef, enemies[i].wisdom), damageList, explosions, burns, drops, player.facingLeft, true);
							break;
						case SkillManager.FREEZE:
							enemies[i].damage(player, Config.getDamageDone(player.matk, player.wisdom, enemies[i].mdef, enemies[i].wisdom), damageList, explosions, freezes, drops, player.facingLeft, true);
							enemies[i].effects.add(StatusEffects.FREEZE, 10, 5);
							break;
						case SkillManager.BOLT:
							enemies[i].damage(player, Config.getDamageDone(player.matk + 25, player.wisdom, enemies[i].mdef, enemies[i].wisdom), damageList, explosions, burns, drops, player.facingLeft, true);
							break;
						case SkillManager.POISON:
							enemies[i].effects.add(StatusEffects.POISON, player.wisdom/4 + 1, 10);
							break;
						}

					}

				}

			}
		}
		

		
		// check for a collision with loot
		if (player != null) {

			for (int i = 0; i < DropManager.MAX_DROPS; i++){
				if (drops.dropList[i].bounds.overlaps(player.bounds) && drops.dropList[i].visible){
					drops.collect(i, inventory);
				}
			}

		}


	}

}
