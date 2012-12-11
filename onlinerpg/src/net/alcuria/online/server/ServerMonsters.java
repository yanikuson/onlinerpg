package net.alcuria.online.server;

import com.badlogic.gdx.utils.Array;
import net.alcuria.online.client.Monster;

public class ServerMonsters {

	public Array<Monster> monsters;
	
	public int timer = 0;
	public int numEnemies = 0;
	public int respawnTimer = 0;
	public boolean updated = false;
	
	public ServerMonsters(){
		
		monsters = new Array<Monster>(20);
		
		for (int i = 0; i < monsters.size; i++){
			monsters.add(new Monster(1));
		}
	}
	
	public void update(String mapfile){
		
		// check for a respawn
		timer++;
		if (numEnemies < monsters.size && timer > 0){
			System.out.println("spawning");
			timer = 0;
			numEnemies++;
		}
		
		// command the monsters
		for (int i = 0; i < monsters.size; i++){
			monsters.get(i).serverUpdate();
		}
			
		updated = true;
	}
	
	public void spawn(){
		
	}
}
