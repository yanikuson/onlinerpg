package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

import net.alcuria.online.client.ui.Message;

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

	public int update(Message msgBox, CameraManager cameraManager, int commandIndex, Player p, NPC npc, Map m, AssetManager assets){

		switch (type){
		case TYPE_MSG:
			if (!startedCommand){
				msg = msg.replace("|name|", p.name);
				msgBox.startMessage(msg, (int)cameraManager.offsetX + Message.h_center, (int)cameraManager.offsetY + 200);
				startedCommand = true;
			} else {

				// test for a complete message
				if (!msgBox.visible){
					startedCommand = false;
					return commandIndex + 1;
				}
			}
			break;
		case TYPE_HEAL:

			p.effects.add(StatusEffects.HEAL, p.maxHP, 1);
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
			p.animation.reset();

			if (msg.equalsIgnoreCase("dead")){
				p.animation.frame = p.animation.dead;
			} else if (msg.equalsIgnoreCase("hurt")){
				p.animation.frame = p.animation.hurt;
			} else if (msg.equalsIgnoreCase("victory")){
				p.animation.frame = p.animation.victory;
			} else if (msg.equalsIgnoreCase("idle")){
				p.animation.frame = p.animation.idle[0];
			}
			p.animation.curWidth = p.animation.frame.getRegionWidth();
			p.animation.curHeight = p.animation.frame.getRegionHeight();
			return commandIndex + 1;

		case TYPE_BGM:
			m.bgm.stop();
			if (msg.equalsIgnoreCase("beach")){
				m.bgm = assets.get("music/beach.ogg", Music.class);
				m.bgm.setLooping(true);
			} else if (msg.equalsIgnoreCase("victory")){
				m.bgm = assets.get("music/victory.ogg", Music.class);
				m.bgm.setLooping(false);
			}
			m.bgm.play();
			return commandIndex + 1;
		
		case TYPE_REMOVE:
			npc.visible = false;
			return commandIndex + 1;
			
		case TYPE_FLAG:
			GlobalFlags.flags[Integer.parseInt(msg)] = true;
			return commandIndex + 1;
			
		case DEFAULT:
			return commandIndex + 1;
			
		}
		
		return commandIndex;
	}
}
