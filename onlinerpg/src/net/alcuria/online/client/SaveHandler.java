package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

public class SaveHandler {

	public static FileHandle file;
	public static final int MAX_SAVES = 3;
	
	
	
	public static void save(Player p, int slot){
		
		file = Gdx.files.local("save.dat");
		
		String playerData = new String();
		playerData = playerData + p.lvl + ",";
		playerData = playerData + p.maxHP + ",";
		
		file.writeString(playerData, false);
	}
	
	
	public static Player load(int slot, NotificationList notifications, AssetManager assets){
		
		file = Gdx.files.local("save.dat");
		Player p = new Player("sprites/player.png", 160, 120, 14, 22, notifications, assets);
	
		String savedata = Gdx.files.local("save.dat").readString();
		String[] subdata = savedata.split(",");
		
		if (subdata.length < 2){
			return p;
		}
		p.lvl = Integer.parseInt(subdata[0]);
		p.maxHP = Integer.parseInt(subdata[1]);
		
		p.HP = p.maxHP;
		return p;
	}
	
	
}
