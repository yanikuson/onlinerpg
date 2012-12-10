package net.alcuria.online.client;

import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class SaveHandler {

	public static final int NUM_SAVE_ELEMS = 25;
	public static FileHandle file;
	public static int slotSelected = 0;
	public static final int MAX_SAVES = 3;

	public static void createPlayer(int slot, String name, int gender, int skin, int hair){


		file = Gdx.files.local("data/player" + slot + ".dat");

		String playerData = new String();
		playerData = playerData + 1 + ",";
		playerData = playerData + Config.getMaxHP(1, 5) + ",";
		playerData = playerData + 0 + ",";
		playerData = playerData + 5 + ",";
		playerData = playerData + 5 + ",";
		playerData = playerData + 5 + ",";
		playerData = playerData + 0 + ",";
		playerData = playerData + 2 + ",";
		playerData = playerData + 0 + ",";
		playerData = playerData + 0 + ",";
		playerData = playerData + 100 + ",";
		playerData = playerData + 100 + ",";
		playerData = playerData + 100 + ",";
		playerData = playerData + 0 + ",";

		// weapon armor helm acc
		playerData = playerData + 0 + ",";
		playerData = playerData + 0 + ",";
		playerData = playerData + Item.ID_LEATHER_VEST + ",";
		playerData = playerData + 0 + ",";

		playerData = playerData + name + ",";

		// location, x, y (in tiles)
		playerData = playerData + "beach,";
		playerData = playerData + 0 + ",";
		playerData = playerData + 0 + ",";

		// gender, skin, and hair values (internal, need to be converted to file location + 1)
		playerData = playerData + gender + ",";
		playerData = playerData + skin + ",";
		playerData = playerData + hair + ",";

		file.writeString(playerData, false);

	}


	public static void savePlayer(Player p, int slot){

		file = Gdx.files.local("data/player" + slot + ".dat");

		String playerData = new String();
		playerData = playerData + p.lvl + ",";
		playerData = playerData + p.maxHP + ",";
		playerData = playerData + p.statPts + ",";
		playerData = playerData + p.power + ",";
		playerData = playerData + p.stamina + ",";
		playerData = playerData + p.wisdom + ",";
		playerData = playerData + p.atk + ",";
		playerData = playerData + p.def + ",";
		playerData = playerData + p.matk + ",";
		playerData = playerData + p.mdef + ",";
		playerData = playerData + p.walkSpeed + ",";
		playerData = playerData + p.jumpPower + ",";
		playerData = playerData + p.knockback + ",";
		playerData = playerData + p.curEXP + ",";

		playerData = playerData + p.weapon.id + ",";
		playerData = playerData + p.helmet.id + ",";
		playerData = playerData + p.armor.id + ",";
		playerData = playerData + p.accessory.id + ",";

		playerData = playerData + p.name + ",";

		playerData = playerData + p.currentMap + ",";
		playerData = playerData + (int)(p.bounds.x/16) + ",";
		playerData = playerData + (int)(p.bounds.y/16) + ",";

		playerData = playerData + p.gender + ",";
		playerData = playerData + p.skin + ",";
		playerData = playerData + p.hair + ",";

		file.writeString(playerData, false);
	}


	public static Player loadPlayer(int slot, Field f){

		Player p = new Player("", 0, 0, 0, 160, 120, 14, 22, f);

		//return if a file doesnt exist
		if(!Gdx.files.local("data/player" + slot + ".dat").exists()){
			return p;
		}

		// get the length of the save data. if it's too short we also return
		String savedata = Gdx.files.local("data/player" + slot + ".dat").readString();
		String[] subdata = savedata.split(",");
		if (subdata.length < NUM_SAVE_ELEMS){
			return p;
		}

		// assign our player object the values loaded from the file
		p.lvl = Integer.parseInt(subdata[0]);
		p.maxHP = Integer.parseInt(subdata[1]);
		p.statPts = Integer.parseInt(subdata[2]);
		p.power = Integer.parseInt(subdata[3]);
		p.stamina = Integer.parseInt(subdata[4]);
		p.wisdom = Integer.parseInt(subdata[5]);
		p.atk = Integer.parseInt(subdata[6]);
		p.def = Integer.parseInt(subdata[7]);
		p.matk = Integer.parseInt(subdata[8]);
		p.mdef = Integer.parseInt(subdata[9]);
		p.walkSpeed = Float.parseFloat(subdata[10]);
		p.jumpPower = Float.parseFloat(subdata[11]);
		p.knockback = Float.parseFloat(subdata[12]);
		p.curEXP = Integer.parseInt(subdata[13]);

		p.weapon = new Item(Integer.parseInt(subdata[14]));
		p.helmet = new Item(Integer.parseInt(subdata[15]));
		p.armor = new Item(Integer.parseInt(subdata[16]));
		p.accessory = new Item(Integer.parseInt(subdata[17]));

		p.name = subdata[18];
		p.currentMap = subdata[19];

		p.bounds.x = Float.parseFloat(subdata[20]) * 16;
		p.bounds.y = Float.parseFloat(subdata[21]) * 16;

		p.gender = Byte.parseByte(subdata[22]);
		p.skin = Byte.parseByte(subdata[23]);
		p.hair = Byte.parseByte(subdata[24]);

		// ---------

		p.animation = new Animator(("sprites/equips/skin/" + (p.skin+1) + ".png"), 14, 22, f.assets);
		p.animation.assignPlayer(p);
		p.neededEXP = Config.getNextLvl(p.lvl);
		p.HP = p.maxHP;

		return p;
	}

	public static void saveItems(int slot, ItemManager items){

		file = Gdx.files.local("data/item" + slot + ".dat");

		String itemData = new String();
		itemData = itemData + items.money + ",";
		for (int i = 0; i < items.getSize(); i++) {
			itemData = itemData + items.getItem(i).id + ",";
		}
		file.writeString(itemData, false);
	}

	public static void saveFlags(int slot){

		// create a reference to the flagsN.dat file
		file = Gdx.files.local("data/flags" + slot + ".dat");

		// create string containing all the flags in this array
		String flagData = new String();
		for (int i = 0; i < GlobalFlags.flags.length; i++) {
			flagData = flagData + GlobalFlags.flags[i] + ",";
		}

		// and write it to the file
		file.writeString(flagData, false);
	}

	public static void loadFlags(int slot){
		GlobalFlags.init();

		if(Gdx.files.local("data/flags" + slot + ".dat").exists()){

			// create a string with the contents of the file and split it
			String savedata = Gdx.files.local("data/flags" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");

			// update all flags
			for (int i = 0; i < subdata.length; i++){
				GlobalFlags.flags[i] = Boolean.parseBoolean(subdata[i]);
			}
		}
	}

	public static ItemManager loadItems(int slot) {

		ItemManager items = new ItemManager();
		if(!Gdx.files.local("data/item" + slot + ".dat").exists()){
			items.add(Item.ID_POTION);
			items.add(Item.ID_SPEED_PILL);
			items.add(Item.ID_WOOD_SWORD);
			items.add(Item.ID_WIZARD_HAT);
			return items;
		}

		// create a string with the contents of the file and split it
		String savedata = Gdx.files.local("data/item" + slot + ".dat").readString();
		String[] subdata = savedata.split(",");

		// add all items in our data file to the item manager
		items.money = Integer.parseInt(subdata[0]);
		for (int i = 1; i < subdata.length; i++){
			items.add(Integer.parseInt(subdata[i]));
		}

		return items;

	}


	public static boolean fileExists(int slot){

		return Gdx.files.local("data/player" + slot + ".dat").exists();

	}


	public static String getPlayerName(int slot) {

		if (fileExists(slot)){

			String savedata = Gdx.files.local("data/player" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			if (subdata.length < NUM_SAVE_ELEMS){
				return "";
			}

			// assign our player object the values loaded from the file
			return subdata[18];
		}
		return "";

	}


	public static int getPlayerLevel(int slot) {
		if (fileExists(slot)){

			String savedata = Gdx.files.local("data/player" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			if (subdata.length < NUM_SAVE_ELEMS){
				return 0;
			}

			// assign our player object the values loaded from the file
			return Integer.parseInt(subdata[0]);
		}
		return 0;
	}


	public static String getPlayerSkinFilename(int slot) {
		
		if (fileExists(slot)){
			String savedata = Gdx.files.local("data/player" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			if (subdata.length < NUM_SAVE_ELEMS){
				return "sprites/equips/empty.png";
			}

			// assign our player object the values loaded from the file
			return ("sprites/equips/skin/" + (Integer.parseInt(subdata[23])+1) + ".png");
		}
		return "sprites/equips/empty.png";
	}


	public static String getPlayerHairFilename(int slot) {
		
		if (fileExists(slot)){
			String savedata = Gdx.files.local("data/player" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			if (subdata.length < NUM_SAVE_ELEMS){
				return "sprites/equips/empty.png";
			}

			// assign our player object the values loaded from the file
			return ("sprites/equips/hair/" + (Integer.parseInt(subdata[24])+1) + ".png");
		}
		return "sprites/equips/empty.png";
	}


	public static String getPlayerArmorFilename(int slot) {
		if (fileExists(slot)){
			String savedata = Gdx.files.local("data/player" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			if (subdata.length < NUM_SAVE_ELEMS){
				return "sprites/equips/empty.png";
			}

			// assign our player object the values loaded from the file
			return (Item.getVisualFilename(Integer.parseInt(subdata[16])));
		}
		return "sprites/equips/empty.png";
	}


	public static String getPlayerWeaponFilename(int slot) {
		if (fileExists(slot)){
			String savedata = Gdx.files.local("data/player" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			if (subdata.length < NUM_SAVE_ELEMS){
				return "sprites/equips/empty.png";
			}

			// assign our player object the values loaded from the file
			return (Item.getVisualFilename(Integer.parseInt(subdata[14])));
		}
		return "sprites/equips/empty.png";
	}

	public static String getPlayerHelmetFilename(int slot) {
		if (fileExists(slot)){
			String savedata = Gdx.files.local("data/player" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			if (subdata.length < NUM_SAVE_ELEMS){
				return "sprites/equips/empty.png";
			}

			// assign our player object the values loaded from the file
			return (Item.getVisualFilename(Integer.parseInt(subdata[15])));
		}
		return "sprites/equips/empty.png";
	}

}
