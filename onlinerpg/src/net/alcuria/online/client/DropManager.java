package net.alcuria.online.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DropManager {

	public static final int MAX_DROPS = 40;

	public Drop[] dropList;				// list of drops. we reuse this.
	public int index = 0;				// index into the droplist, when finding an available drop object to use for the spawn

	public Sound drop;
	public Sound pickup;

	public DropManager(AssetManager assets){

		dropList = new Drop[MAX_DROPS];
		for (int i = 0; i < MAX_DROPS; i++){
			dropList[i] = new Drop("sprites/drops.png", -40, -40, 14, 11, assets);
		}

		pickup = assets.get("sounds/pickup.wav", Sound.class);
		drop = assets.get("sounds/drop.wav", Sound.class);

	}

	// tosses item i out onto the field
	public void toss(Item i, Player p){

		// get an available drop reference
		for (index = 0; index < dropList.length; index++){
			if (!dropList[index].visible){
				break;
			}
		}

		// set the values based on the passed in item
		dropList[index].particleID = i.icon; 
		dropList[index].value = i.id;

		// finally set its position and flag it as visible so it gets render/update calls
		dropList[index].start(p);
		drop.play(Config.sfxVol);

	}

	public void add(Monster m){

		// get the first available drop reference
		for (index = 0; index < dropList.length; index++){
			if (!dropList[index].visible){
				break;
			}
		}

		//TODO: this needs to determine what to drop based on the id and manipulate particle data
		switch (m.type) {

		case Config.MON_SLIME:
			dropList[index].particleID = 0;
			dropList[index].value = 50;
			break;

		case Config.MON_EYE:
			dropList[index].particleID = 1;
			dropList[index].value = Item.ID_POTION;
			break;

		default:

		}

		// finally set its position and flag it as visible so it gets render/update calls
		dropList[index].start(m);
		drop.play(Config.sfxVol);
	}

	public void update(Map map){
		for (int i = 0; i < dropList.length; i++) {
			dropList[i].update(map);
		}
	}

	public void render(SpriteBatch batch){
		for (int i = 0; i < dropList.length; i++) {
			dropList[i].render(batch);
		}
	}

	public void remove(Drop drop){
		drop.visible = false;
	}

	public void collect(int i, ItemManager inventory) {
		dropList[i].collect(inventory);
		pickup.play(Config.sfxVol);
	}

}
