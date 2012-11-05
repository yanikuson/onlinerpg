package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Map {

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


	final int[] heightmapSlopeP30A	= { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7};
	final int[] heightmapSlopeP30B	= { 8, 8, 9, 9,10,10,11,11,12,12,13,13,14,14,15,15};
	final int[] heightmapSlopeN30A	= {15,15,14,14,13,13,12,12,11,11,10,10, 9, 9, 8, 8};
	final int[] heightmapSlopeN30B	= { 7, 7, 6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1, 0, 0};
	final int[] heightmapSlopeN45	= {16,15,14,13,12,11,10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
	final int[] heightmapSlopeP45	= { 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16};
	final int[] heightmapHalf		= { 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};

	int sheetWidth;							// width of the tileset (in tiles) (NOTE: must also be equal to the height)
	public int tileWidth;					// width of an individual tile (in px)
	public int width;						// width of map (in tiles)
	public int height; 						// height of map (in tiles)
	int mapIndex;			

	int tileCoordX, tileCoordY = 0;			// coord x and y for getTileAtPoint() method

	Texture tileset;
	TextureRegion[] tiles;
	public MonsterSpawner spawner;
	public TeleportManager teleports;
	AssetManager assets;

	int[][] lowerLayer;
	int[][] upperLayer;
	int[][] collisionLayer;

	public Map(String tilesetLocation, String mapfile, AssetManager assets){

		this.assets = assets;
		teleport(tilesetLocation, mapfile);

	}

	public void render(SpriteBatch batch, boolean below, CameraManager camera) {

		if (below){
			for (int i = (int) (camera.offsetX/Config.TILE_WIDTH); i < camera.offsetX/Config.TILE_WIDTH+26; i++) {
				for (int j = (int) camera.offsetY/Config.TILE_WIDTH; j < camera.offsetY/Config.TILE_WIDTH+15; j++) { 
					if (height-1-j > 0 && i < width && lowerLayer[i][height-1-j] > 0){
						batch.draw(tiles[lowerLayer[i][height-1-j]], i*tileWidth, j*tileWidth);
					}
				}
			}
			spawner.render(batch);
		} else {

			for (int i = (int) (camera.offsetX/Config.TILE_WIDTH); i < camera.offsetX/Config.TILE_WIDTH+26; i++) {
				for (int j = (int) camera.offsetY/Config.TILE_WIDTH; j < camera.offsetY/Config.TILE_WIDTH+15; j++) { 
					if (height-1-j > 0 && i < width && upperLayer[i][height-1-j] > 0){
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

				if (collisionLayer[tileCoordX][tileCoordY] == COLL_BLOCKING){
					return (Config.TILE_WIDTH + getSubTileAtPoint(x, y+Config.TILE_WIDTH, yVel));						// oh ffffuuuuu--
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_SLOPE_P30A){
					return heightmapSlopeP30A[(int) (x % 16)];
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
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_HALF){
					return 8;
				}
				if (collisionLayer[tileCoordX][tileCoordY] == COLL_DOWNBLOCKING && yVel <= 0){
					return (16 + getSubTileAtPoint(x, y+16, yVel));
				}

			}
		}
		return COLL_EMPTY;
	}

	public void teleport(String tilesetLocation, String mapfile){
		// load the map tileset
		tileset = new Texture(Gdx.files.internal(tilesetLocation));

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
		// set spawn points
		System.out.println(mapfile);
		this.spawner = new MonsterSpawner("maps/" + mapfile + ".spawn");
		for (int i = 0; i < MonsterSpawner.MAX_MONSTERS; i++) {
			System.out.println("adding m");
			if (Math.random() > 0.3){
				this.spawner.addMonster(new Monster("sprites/slime.png", 14, 16, Config.MON_SLIME, assets));
			} else {
				this.spawner.addMonster(new Monster("sprites/eye.png", 14, 18, Config.MON_EYE, assets));
			}
		}
		
		this.spawner.doInitialSpawn();
		
		// CREATE Teleporter!
		this.teleports = new TeleportManager(mapfile);
	}

	public void update(Player p, InputHandler input) {
		teleports.update(p, this, input);
		spawner.update();
		
	}

}
