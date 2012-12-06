package net.alcuria.online.client;

import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DropManager {

	public static final int MAX_DROPS = 40;

	public Drop[] dropList;				// list of drops. we reuse this.
	public int index = 0;				// index into the droplist, when finding an available drop object to use for the spawn

	public Sound drop;
	public Sound pickup;
	public NotificationList notifications;

	public DropManager(Field f, NotificationList notifications){

		dropList = new Drop[MAX_DROPS];
		for (int i = 0; i < MAX_DROPS; i++){
			dropList[i] = new Drop("sprites/drops.png", -40, -40, 14, 11, f);
		}

		pickup = f.assets.get("sounds/pickup.wav", Sound.class);
		drop = f.assets.get("sounds/drop.wav", Sound.class);
		this.notifications = notifications;

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

	public void add(Monster m, Item loot){

		// get the first available drop reference
		for (index = 0; index < dropList.length; index++){
			if (!dropList[index].visible){
				break;
			}
		}
		 
		if (loot == null) {
			// define money drop as null item
			dropList[index].particleID = 0;
			dropList[index].value = (int) (m.money * (1 + (Math.random() - 0.5)));
		} else {
			// otherwise drop the enemy's loot
			dropList[index].particleID = loot.icon;
			dropList[index].value = loot.id;	
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
