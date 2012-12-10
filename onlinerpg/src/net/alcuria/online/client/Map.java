package net.alcuria.online.client;

import net.alcuria.online.client.connection.GameClient;
import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Map {

	Field f;

	// Collision layer enums
	final int COLL_EMPTY 		= 0;
	final int COLL_BLOCKING 	= 1;		// All blocking tiles 
	final int COLL_SLOPE_N30A	= 2;
	final int COLL_SLOPE_N30B 	= 3;
	final int COLL_SLOPE_P30A	= 4;
	final int COLL_SLOPE_P30B 	= 5;
	final int COLL_SLOPE_N45	= 6;
	final int COLL_SLOPE_P45 	= 7;
	final int COLL_HALF			= 8;
	final int COLL_DOWNBLOCKING = 10;

	// for beach
	final int COLL_SLOPE_N15A	= 11;
	final int COLL_SLOPE_N15B	= 12;
	final int COLL_SLOPE_N15C	= 13;
	final int COLL_SLOPE_P15A	= 14;
	final int COLL_SLOPE_P15B	= 15;
	final int COLL_SLOPE_P15C	= 16;
	final int COLL_HALFP		= 17;
	final int COLL_HALFN		= 18;

	static int NUM_NPCS = 0;

	final int[] heightmapSlopeP30A	= { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7};
	final int[] heightmapSlopeP30B	= { 8, 8, 9, 9,10,10,11,11,12,12,13,13,14,14,15,15};
	final int[] heightmapSlopeN30A	= {15,15,14,14,13,13,12,12,11,11,10,10, 9, 9, 8, 8};
	final int[] heightmapSlopeN30B	= { 7, 7, 6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1, 0, 0};
	final int[] heightmapSlopeN45	= {16,15,14,13,12,11,10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
	final int[] heightmapSlopeP45	= { 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16};
	final int[] heightmapHalf		= { 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};

	final int[] heightmapSlopeP15A 	= { 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 6};
	final int[] heightmapSlopeP15B 	= { 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9,10,10,10,11,11};
	final int[] heightmapSlopeP15C 	= {11,12,12,12,12,13,13,13,14,14,14,15,15,15,16,16};
	final int[] heightmapSlopeN15A 	= {16,16,15,15,15,14,14,14,13,13,13,12,12,12,12,11};
	final int[] heightmapSlopeN15B 	= {11,11,10,10,10, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6};
	final int[] heightmapSlopeN15C 	= { 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 2, 2, 2, 1, 1, 1};
	final int[] heightmapSlopeP45H 	= { 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8};
	final int[] heightmapSlopeN45H 	= { 8, 8, 8, 8, 8, 8, 8, 8, 8, 7, 6, 5, 4, 3, 2, 1};



	int sheetWidth;							// width of the tileset (in tiles) (NOTE: must also be equal to the height)
	public int tileWidth;					// width of an individual tile (in px)
	public int width;						// width of map (in tiles)
	public int height; 						// height of map (in tiles)
	public boolean pause = false;			// do we pause the map updates (for NPC commands)?
	public String name = "";				// the name of the map tileset, with the file extension
	int mapIndex;			
	String[] line;

	public MonsterSpawner spawner;
	public NPC[] npcs;
	public TeleportManager teleports;
	public Platform[] platforms;
	AssetManager assets;
	DamageList damageList;
	CollisionManager collisions;
	Player p;

	int tileCoordX, tileCoordY = 0;			// coord x and y for getTileAtPoint() method
	Texture tileset;
	TextureRegion[] tiles;
	int[][] lowerLayer;
	int[][] upperLayer;
	int[][] collisionLayer;

	public Music bgm;
	public Background bg;
	public Foreground fg;
	public boolean containsEnemies = false;

	public Map(String mapfile, AssetManager assets, DamageList damageList, Field f){
		this.f = f;
		this.damageList = damageList;
		this.assets = assets;
		create(mapfile);		
	}

	public void render(SpriteBatch batch, boolean below, CameraManager camera) {

		if (below){
			for (int i = (int) (camera.offsetX/Config.TILE_WIDTH); i < camera.offsetX/Config.TILE_WIDTH+26; i++) {
				for (int j = (int) camera.offsetY/Config.TILE_WIDTH; j < camera.offsetY/Config.TILE_WIDTH+15; j++) { 
					if (height-1-j > 0 && i < width && lowerLayer[i][height-1-j] > 0 && lowerLayer[i][height-1-j] < 255){
						batch.draw(tiles[lowerLayer[i][height-1-j]], i*tileWidth, j*tileWidth);
					}
				}
			}

			// render all platforms
			if (platforms != null) {
				for (int i = 0; i < platforms.length; i++){
					if (platforms[i] != null){
						platforms[i].render(f);
					}
				}
			}

			if (spawner != null) spawner.render(batch);

			if (npcs != null){
				for (int i = 0; i < npcs.length; i++){
					if (npcs[i] != null) {
						npcs[i].render(batch);
					}
				}
			}

		} else {

			for (int i = (int) (camera.offsetX/Config.TILE_WIDTH); i < camera.offsetX/Config.TILE_WIDTH+26; i++) {
				for (int j = (int) camera.offsetY/Config.TILE_WIDTH; j < camera.offsetY/Config.TILE_WIDTH+15; j++) { 
					if (height-1-j > 0 && i < width && upperLayer[i][height-1-j] > 0 && upperLayer[i][height-1-j] < 255){
						batch.draw(tiles[upperLayer[i][height-1-j]], i*tileWidth, j*tileWidth);
					}
				}
			}

		}
	}

	public int getTileAtPoint(float x, float y) {

		// get the X and Y tile coordinates
		tileCoordX = (int) (x/Config.TILE_WIDTH);
		tileCoordY = (int) (height-y/Config.TILE_WIDTH);

		// ensure the tile is actually INSIDE the map
		if (tileCoordX >= 0 && tileCoordX < width){
			if (tileCoordY >= 0 && tileCoordY < height){
				return collisionLayer[tileCoordX][tileCoordY];
			}
		}

		// assume all space outside of map is AIR
		return COLL_EMPTY;
	}

	// this takes as input a pair of coordinates and returns the heightmap AT that value.
	public int getSubTileAtPoint(float x, float y, float yVel) {
		// get the X and Y tile coordinates
		tileCoordX = (int) (x/Config.TILE_WIDTH);
		tileCoordY = (int) (height-y/Config.TILE_WIDTH);


		// ensure the tile is actually INSIDE the map
		if (tileCoordX >= 0 && tileCoordX < width){
			if (tileCoordY >= 0 && tileCoordY < height){

				//TODO: use a 2d array				
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_BLOCKING){
					return (Config.TILE_WIDTH + getSubTileAtPoint(x, y+Config.TILE_WIDTH, yVel));						// oh ffffuuuuu--
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_P30A){
					return x >= 0 ? heightmapSlopeP30A[(int) (x % 16)] : 0;
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_P30B){
					return heightmapSlopeP30B[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_N30A){
					return heightmapSlopeN30A[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_N30B){
					return heightmapSlopeN30B[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_N45){
					return heightmapSlopeN45[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_P45){
					return heightmapSlopeP45[(int) (x % 16)];
				}

				// beach
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_N15A){
					return heightmapSlopeN15A[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_N15B){
					return heightmapSlopeN15B[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_N15C){
					return heightmapSlopeN15C[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_P15A){
					return heightmapSlopeP15A[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_P15B){
					return x >= 0 ? heightmapSlopeP15B[(int) (x % 16)] : 0;
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_P15C){
					return heightmapSlopeP15C[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_HALFP){
					return heightmapSlopeP45H[(int) (x % 16)];
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_HALFN){
					return heightmapSlopeN45H[(int) (x % 16)];
				}				


				if (collisionLayer[tileCoordX][tileCoordY] == COLL_HALF){
					return 8;
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_DOWNBLOCKING){
					return (16 + getSubTileAtPoint(x, y+16, yVel));
				}

			}
		}
		return COLL_EMPTY;
	}

	// creates a new map
	public void create(String mapfile){

		f.player.currentMap = mapfile;
		if (GameClient.client != null && GameClient.client.isConnected()){
			GameClient.sendMapChange(f);
		}
		
		// read in the map file into an array of strings
		FileHandle handle = Gdx.files.internal("maps/" + mapfile + ".cmf");
		String fileContent = handle.readString();
		String[] lines = fileContent.split("\\r?\\n");

		// Line 1: Tile Sheet Width (in tiles)
		sheetWidth = Integer.parseInt(lines[0]);

		// Line 2: Tile Width (in px)
		tileWidth = Integer.parseInt(lines[1]);

		// Line 3: Width (in tiles)
		width = Integer.parseInt(lines[2]);

		// Line 4: Height (in tiles)
		height = Integer.parseInt(lines[3]);

		// Line 5: Lower Data
		mapIndex = 0;
		lowerLayer = new int[width][height];
		String[] lineData = lines[4].split("\\s");
		for(int j = 0; j < height; j++){
			for(int k = 0; k < width; k++){
				lowerLayer[k][j] = Integer.parseInt(lineData[mapIndex]);
				mapIndex++;
			}
		}

		// Line 6: Upper Data
		mapIndex = 0;
		upperLayer = new int[width][height];
		lineData = lines[5].split("\\s");
		for(int j = 0; j < height; j++){
			for(int k = 0; k < width; k++){
				upperLayer[k][j] = Integer.parseInt(lineData[mapIndex]);
				mapIndex++;
			}
		}

		// Line 7: Collision Data
		mapIndex = 0;
		collisionLayer = new int[width][height];
		lineData = lines[6].split("\\s");
		for(int j = 0; j < height; j++){
			for(int k = 0; k < width; k++){
				collisionLayer[k][j] = Integer.parseInt(lineData[mapIndex]);
				mapIndex++;
			}
		}

		// Line 8: tileset name
		// load the map tileset (with file ext)
		tileset = new Texture(Gdx.files.internal("tiles/" + lines[7]));

		// if the map tileset name is different, we need to change the bgm to play a new one
		if (!name.equalsIgnoreCase(lines[7])){

			// first, stop the bgm if it's already initialized
			if (bgm != null) bgm.stop(); 

			// update the map's tileset name and determine the bgm to play based on that
			name = lines[7];
			if (name.equalsIgnoreCase("forest.png")){
				bgm = assets.get("music/forest.ogg", Music.class);
			} else if (name.equalsIgnoreCase("beach.png")){
				bgm = assets.get("music/beach.ogg", Music.class);
			} else if (name.equalsIgnoreCase("village.png")){
				bgm = assets.get("music/village.ogg", Music.class);
			}

			// start the new bgm
			bgm.setLooping(true);
			bgm.setVolume(Config.bgmVol);
			bgm.play();

		}

		// Now that the map data is created, split up the tileset into TextureRegions
		// initialize the texture regions of the tileset, first into a 2D spritesheet
		tiles = new TextureRegion[sheetWidth * sheetWidth];
		int index = 0;
		for (int i = 0; i < sheetWidth; i++){
			for (int j = 0; j < sheetWidth; j++){
				tiles[index++] = new TextureRegion(tileset, j*tileWidth, i*tileWidth, tileWidth, tileWidth);
			}
		}

		// CREATE the monster spawner
		if(Gdx.files.internal("maps/" + mapfile + ".spawn").exists()){
			containsEnemies = true;
			this.spawner = new MonsterSpawner("maps/" + mapfile + ".spawn", damageList);
			for (int i = 0; i < MonsterSpawner.MAX_MONSTERS; i++) {

				// WHICH MONSTER?
				if (mapfile.equals("beachroad")){

					// BEACH
					this.spawner.addMonster(new Monster("sprites/monsters/crab.png", 16, 16, Config.MON_CRAB, f));

				} else {

					// DEFAULT SLIMES/EYES
					if (Math.random() > 0.3){
						this.spawner.addMonster(new Monster("sprites/monsters/slime.png", 14, 16, Config.MON_SLIME, f));
					} else {
						this.spawner.addMonster(new Monster("sprites/monsters/eye.png", 14, 18, Config.MON_EYE, f));
					}
				}
			}
			this.spawner.doInitialSpawn();

		} else {
			containsEnemies = false;
			this.spawner = null;
		}

		// CREATE Teleporter!
		this.teleports = new TeleportManager(mapfile);

		// create NPCS HERE

		// wipe out old npcs
		if (npcs != null) {
			for (int i = 0; i < npcs.length; i++){
				npcs[i] = null;
			}
		}
		if(Gdx.files.internal("maps/" + mapfile + ".npc").exists()){
			handle = Gdx.files.internal("maps/" + mapfile + ".npc");
			fileContent = handle.readString();
			String[] npclist = fileContent.split("\\r?\\n");

			npcs = new NPC[npclist.length];
			// for each line, split and parse the x and y vals and add a spawn pt at that location
			for (int i = 0; i < npclist.length; i++){
				line = npclist[i].split("\\s");
				if (line.length == 4){

					// we dont create the intro npc if we've watched the intro
					if (line[1].equals("intro") && GlobalFlags.flags[GlobalFlags.INTRO]){
						break;
					}
					npcs[i] = new NPC(line[0], Integer.parseInt(line[2])*Config.TILE_WIDTH, Integer.parseInt(line[3])*Config.TILE_WIDTH, 14, 22, line[1], f);
				}
			}
		} 

		// create new backgrounds
		bg = new Background(this, assets);
		fg = new Foreground(this, assets);

		// create the collision manager
		if (containsEnemies){
			collisions = new CollisionManager(f.player, this.spawner.monsterList, f.drops, f.slices, f.burns, f.freezes, f.inputs);
		} else {
			collisions = new CollisionManager(f.player, null, f.drops, f.slices, f.burns, f.freezes, f.inputs);

		}

		// create the PLATFORMS
		if (platforms != null){
			for (int i = 0; i < platforms.length; i++){
				platforms[i] = null;
			}
		}

		if(Gdx.files.internal("maps/" + mapfile + ".plat").exists()){

			// create a filehandle, read in the string and split it by line
			handle = Gdx.files.internal("maps/" + mapfile + ".plat");
			String contents = handle.readString();
			String[] contentsSplit = contents.split("\\r?\\n");

			// init each element
			platforms = new Platform[Config.MAX_PLATFORMS];
			for (int i = 0; i < contentsSplit.length; i++){
				platforms[i] = new Platform(f, contentsSplit[i]);
			}

		}



	}

	public void update(){

		pause = false;
		if (npcs != null){
			for (int i = 0; i < npcs.length; i++){
				if (npcs[i] != null) {

					npcs[i].update(this, f.msgBox, f.cameraManager, f.player);
					if (npcs[i].startCommands) {
						f.inputs.typed[InputHandler.ATTACK] = false;
						pause = true;
					} else {

						// we only command the npc to move if there is no NPC Commands being executed
						npcs[i].command(this, p);
					}
				}
			}
		}
		teleports.update(f.player, this, f.inputs);
		if (spawner != null && containsEnemies) spawner.update();
		fg.update(Gdx.graphics.getDeltaTime());
		collisions.update(this, damageList, f.explosions, f.inventory);

		// update platforms
		if (platforms != null) {
			for (int i = 0; i < platforms.length; i++){
				if (platforms[i] != null){
					platforms[i].update(f);
				}
			}
		}


	}

	public void renderBG(SpriteBatch batch, CameraManager cameraManager) {
		bg.render(batch, cameraManager);

	}

	public void renderFG(SpriteBatch batch, CameraManager cameraManager) {
		fg.render(batch, cameraManager);

	}


}
