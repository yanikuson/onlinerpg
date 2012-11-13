package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;

import net.alcuria.online.client.ui.Message;

public class NPCCommand {

	public static final int TYPE_MSG = 0;
	public static final int TYPE_HEAL = 1;
	public static final int TYPE_WAIT = 2;
	
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

	public int update(Message msgBox, CameraManager cameraManager, int commandIndex, Player p){

		switch (type){
		case TYPE_MSG:
			if (!startedCommand){
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
		}		
		
		return commandIndex;
	}

}
