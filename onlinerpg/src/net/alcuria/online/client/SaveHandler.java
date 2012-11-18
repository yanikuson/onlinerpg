package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

public class SaveHandler {

	public static FileHandle file;
	public static int slotSelected = 0;
	public static final int MAX_SAVES = 3;
	
	public static void savePlayer(Player p, int slot){
		
		file = Gdx.files.local("player" + slot + ".dat");
		
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
		
		file.writeString(playerData, false);
	}
	
	
	public static Player loadPlayer(int slot, NotificationList notifications, AssetManager assets){
		
		Player p = new Player("sprites/player.png", "", 160, 120, 14, 22, notifications, assets);
	
		//return if a file doesnt exist
		if(!Gdx.files.local("player" + slot + ".dat").exists()){
			return p;
		}
		
		// get the length of the save data. if it's too short we also return
		String savedata = Gdx.files.local("player" + slot + ".dat").readString();
		String[] subdata = savedata.split(",");
		if (subdata.length < 18){
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
		
		p.neededEXP = Config.getNextLvl(p.lvl);
		p.HP = p.maxHP;
		
		return p;
	}

	public static void saveItems(int slot, ItemManager items){
		
		file = Gdx.files.local("item" + slot + ".dat");
		
		String itemData = new String();
		itemData = itemData + items.money + ",";
		for (int i = 0; i < items.getSize(); i++) {
			itemData = itemData + items.getItem(i).id + ",";
		}
		file.writeString(itemData, false);
	}
	
	public static void saveFlags(int slot){
		
		// create a reference to the flagsN.dat file
		file = Gdx.files.local("flags" + slot + ".dat");

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
		
		if(Gdx.files.local("flags" + slot + ".dat").exists()){
			
			// create a string with the contents of the file and split it
			String savedata = Gdx.files.local("flags" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			
			// update all flags
			for (int i = 0; i < subdata.length; i++){
				GlobalFlags.flags[i] = Boolean.parseBoolean(subdata[i]);
			}
		}
	}
	
	public static ItemManager loadItems(int slot) {
		
		ItemManager items = new ItemManager();
		if(!Gdx.files.local("item" + slot + ".dat").exists()){
			items.add(Item.ID_POTION);
			items.add(Item.ID_SPEED_PILL);
			items.add(Item.ID_WOOD_SWORD);
			items.add(Item.ID_WIZARD_HAT);
			return items;
		}
		
		// create a string with the contents of the file and split it
		String savedata = Gdx.files.local("item" + slot + ".dat").readString();
		String[] subdata = savedata.split(",");
		
		// add all items in our data file to the item manager
		items.money = Integer.parseInt(subdata[0]);
		for (int i = 1; i < subdata.length; i++){
			items.add(Integer.parseInt(subdata[i]));
		}
		
		return items;
		
	}
	
	
	public static boolean fileExists(int slot){
		
		return Gdx.files.local("player" + slot + ".dat").exists();

	}


	public static String getPlayerName(int slot) {
		
		if (fileExists(slot)){
			
			String savedata = Gdx.files.local("player" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			if (subdata.length < 19){
				return "";
			}
			
			// assign our player object the values loaded from the file
			return subdata[18];
		}
		return "";
		
	}


	public static int getPlayerLevel(int slot) {
		if (fileExists(slot)){
			
			String savedata = Gdx.files.local("player" + slot + ".dat").readString();
			String[] subdata = savedata.split(",");
			if (subdata.length < 19){
				return 0;
			}
			
			// assign our player object the values loaded from the file
			return Integer.parseInt(subdata[0]);
		}
		return 0;
	}
	
	
}
