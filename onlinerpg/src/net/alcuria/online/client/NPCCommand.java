package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import net.alcuria.online.client.screens.Field;
import net.alcuria.online.client.ui.Message;
import net.alcuria.online.client.ui.ShopMenu;

public class NPCCommand {

	public static final int DEFAULT = -1;
	public static final int TYPE_MSG = 0;
	public static final int TYPE_HEAL = 1;
	public static final int TYPE_WAIT = 2;
	public static final int TYPE_MOVE = 3;
	public static final int TYPE_STOP = 4;
	public static final int TYPE_HERO = 5;
	public static final int TYPE_BGM = 6;
	public static final int TYPE_REMOVE = 7;
	public static final int TYPE_FLAG = 8;
	public static final int TYPE_SHOP = 9;
	public static final int TYPE_EQUIP = 10;

	public float waitCount = 0;
	public float waitDuration = 0;
	public int type;
	public String msg;
	public boolean startedCommand = false;

	public NPCCommand(int type){
		this.type = type;
	}

	public NPCCommand(int type, String msg){
		this.type = type;
		this.msg = msg;
	}

	public int update(Field f, NPC npc, int commandIndex){

		switch (type){
		case TYPE_MSG:
			if (!startedCommand){
				msg = msg.replace("|name|", f.player.name);
				f.msgBox.startMessage(msg, (int)f.cameraManager.offsetX + Message.h_center, (int)f.cameraManager.offsetY + 200);
				startedCommand = true;
			} else {

				// test for a complete message
				if (!f.msgBox.visible){
					startedCommand = false;
					return commandIndex + 1;
				}
			}
			break;
		case TYPE_HEAL:

			f.player.effects.add(StatusEffects.HEAL, f.player.maxHP, 1);
			return commandIndex + 1;

		case TYPE_WAIT:
			waitCount += Gdx.graphics.getDeltaTime();
			if (waitCount > waitDuration){
				waitCount = 0;
				return commandIndex + 1;
			}
			break;

		case TYPE_MOVE:
			if (msg.equalsIgnoreCase("left")){
				npc.moveCommand[Actor.MOVE_LEFT] = true;
			} else if (msg.equalsIgnoreCase("right")){
				npc.moveCommand[Actor.MOVE_RIGHT] = true;
			} else if (msg.equalsIgnoreCase("jump")){
				npc.moveCommand[Actor.MOVE_JUMP] = true;
			}
			return commandIndex + 1;

		case TYPE_STOP:
			npc.stopMovement();
			return commandIndex + 1;

		case TYPE_HERO:
			f.player.animation.reset();

			if (msg.equalsIgnoreCase("dead")){
				f.player.animation.frame = f.player.animation.dead;
			} else if (msg.equalsIgnoreCase("hurt")){
				f.player.animation.frame = f.player.animation.hurt;
			} else if (msg.equalsIgnoreCase("victory")){
				f.player.animation.frame = f.player.animation.victory;
			} else if (msg.equalsIgnoreCase("idle")){
				f.player.animation.frame = f.player.animation.idle[0];
			}
			f.player.animation.curWidth = f.player.animation.frame.getRegionWidth();
			f.player.animation.curHeight = f.player.animation.frame.getRegionHeight();
			return commandIndex + 1;

		case TYPE_BGM:
			f.map.bgm.stop();
			if (msg.equalsIgnoreCase("beach")){
				f.map.bgm = f.assets.get("music/beach.ogg", Music.class);
				f.map.bgm.setLooping(true);
			} else if (msg.equalsIgnoreCase("victory")){
				f.map.bgm = f.assets.get("music/victory.ogg", Music.class);
				f.map.bgm.setLooping(false);
			}
			f.map.bgm.play();
			return commandIndex + 1;

		case TYPE_REMOVE:
			npc.visible = false;
			return commandIndex + 1;

		case TYPE_FLAG:
			GlobalFlags.flags[Integer.parseInt(msg)] = true;
			return commandIndex + 1;

		case TYPE_SHOP:
			if (!startedCommand){
				ItemManager items = new ItemManager();
				items.add(Item.ID_POTION);
				items.add(Item.ID_SPEED_PILL);
				f.shop = new ShopMenu(f, items);
				f.map.pause = true;
				startedCommand = true;
			} else {
				// test for a completed shop
				f.shop.update(f.inputs, f.cameraManager.offsetX, f.cameraManager.offsetY);
				if (!f.shop.active){
					f.map.pause = false;
					startedCommand = false;
					return commandIndex + 1;
				}
			}
			break;
		case TYPE_EQUIP:
			int ID = Item.getType(Integer.parseInt(msg));
			System.out.println("id = " + ID);
			// switch out the equipment based on the passed in ID
			switch (ID) {
			case Item.TYPE_WEAPON:
				System.out.println("weapon");
				if (!f.player.weapon.name.equalsIgnoreCase("")){
					f.inventory.addItem(f.player.weapon);
				}
				f.player.weapon = new Item(Integer.parseInt(msg));
				break;

			case Item.TYPE_HELM:
				f.inventory.addItem(f.player.helmet);
				f.player.helmet = new Item(Integer.parseInt(msg));
				break;

			case Item.TYPE_ARMOR:
				f.inventory.addItem(f.player.armor);
				f.player.armor = new Item(Integer.parseInt(msg));
				break;

			case Item.TYPE_OTHER:
				f.inventory.addItem(f.player.accessory);
				f.player.accessory = new Item(Integer.parseInt(msg));
				break;

			default:
				break;
			}
			f.player.updateEquips();
			return commandIndex + 1;

		case DEFAULT:
			return commandIndex + 1;

		}

		return commandIndex;
	}
}
