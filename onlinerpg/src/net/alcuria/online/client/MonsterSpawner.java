package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MonsterSpawner {

	public static final int MAX_MONSTERS = 40;
	public static final int MAX_SPAWN_PTS = 20;
	public static final int SPAWN_TIMER = 8;
	public static int INITIAL_SPAWN_NUM = 8;

	public int initialSpawnCtr=0;		// a counter to spawn INITIAL_SPAWN_NUM monsters on the map immediately
	public boolean initialSpawn=false;

	public Monster[] monsterList;
	public int[] spawnPointX;
	public int[] spawnPointY;

	public int monsterListIndex = 0;
	public int spawnPointIndex = 0;

	public int activeSpawnPoints = 0;
	public int activeMonsters = 0;

	public float spawnCounter = 0;
	
	public DamageList damageList;

	public MonsterSpawner(String spawnfile, DamageList damageList){

		activeMonsters = 0;
		monsterList = new Monster[MAX_MONSTERS];
		spawnPointX = new int[MAX_SPAWN_PTS];
		spawnPointY = new int[MAX_SPAWN_PTS];
		removeAllSpawnPoints();

		// read in the spawn file into an array of strings
		FileHandle handle = Gdx.files.internal(spawnfile);
		String fileContent = handle.readString();
		String[] lines = fileContent.split("\\r?\\n");
		
		// for each line, split and parse the x and y vals and add a spawn pt at that location
		for (int i = 0; i < lines.length; i++){
			addSpawnPoint(Integer.parseInt(lines[i].split("\\s")[0]), Integer.parseInt(lines[i].split("\\s")[1]));
		}
		
		this.damageList = damageList;

	}

	public void update(){

		spawnCounter += Gdx.graphics.getDeltaTime();
		if (spawnCounter > SPAWN_TIMER || initialSpawn){
			
			// look for the next available (inactive) monster in the monster list	
			monsterListIndex = 0;
			while (monsterList[monsterListIndex].visible){
				monsterListIndex++;
				// if we iterate through the whole array, the map is full
				if (monsterListIndex >= activeMonsters){
					spawnCounter = 0;
					System.out.println("map is full");
					return;
				}
			}

			// look for a random spawn point
			spawnPointIndex = (int) (Math.random() * activeSpawnPoints);

			// spawn a monster on a map there
			if (monsterList[monsterListIndex] != null){
				monsterList[monsterListIndex].spawn(spawnPointX[spawnPointIndex], spawnPointY[spawnPointIndex]);
			} else {
				System.out.println("Warning: attempting to add null monster in MonsterSpawner.update()");
			}
			spawnCounter = 0;
		}
	}
	
	// spawns a set number of monsters on map load
	public void doInitialSpawn(){
		initialSpawn = true;
		for (int i = 0; i < INITIAL_SPAWN_NUM; i++){
			update();
		}
		initialSpawn = false;		
	}

	// renders all enabled monsters on the field. (TODO: this could be optimized to only call render if they are on the visible screen!!)
	public void render(SpriteBatch batch) {
		for (int i = 0; i < MonsterSpawner.MAX_MONSTERS; i++) {
			if (monsterList[i] != null) {
				monsterList[i].render(batch);
				if (monsterList[i].projectile != null){
					monsterList[i].projectile.render(batch);	
				}
			}
		}

	}

	// just adds a spawn point to the map
	public void addSpawnPoint(int x, int y){

		if (activeSpawnPoints < MAX_SPAWN_PTS){
			spawnPointX[activeSpawnPoints] = x;
			spawnPointY[activeSpawnPoints] = y;
		}
		activeSpawnPoints++;
	}

	public void addMonster(Monster m){
		monsterList[activeMonsters] = m;
		m.visible = false;
		m.effects.assignDamageList(damageList);
		activeMonsters++;

	}

	public void removeAllSpawnPoints(){
		for (int i=0; i<MAX_SPAWN_PTS; i++){
			spawnPointX[i] = -1;
			spawnPointY[i] = -1;
		}
	}

	public void removeAllMonsters(){

		for (int i=0; i<MAX_MONSTERS; i++){
			//monsterList[i].dispose();
			monsterList[i] = null;
		}
		activeMonsters = 0;
		monsterListIndex = 0;
	}



}
