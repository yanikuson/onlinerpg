package net.alcuria.online.client;

public class CollisionManager {

	Player player;
	Monster[] enemies;
	DropManager drops;

	public CollisionManager(Player player, Monster[] enemies, DropManager drops){
		this.player = player;
		this.enemies = enemies;
		this.drops = drops;
	}

	public void update(Map map, DamageList damageList, ParticleList explosions, ParticleList slices, ItemManager inventory){

		// update our refrential list of enemies. WE NEED TO DO THIS WHEN WE SWAP MAPS
		if (enemies[0] == null){
			this.enemies = map.spawner.monsterList;
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
