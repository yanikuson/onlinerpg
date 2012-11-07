package net.alcuria.online.client;

import net.alcuria.online.client.ui.Message;

public class NPCCommand {

	public static final int TYPE_MSG = 0;
	public static final int TYPE_HEAL = 1;

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

		if (type == TYPE_MSG) {
			if (!startedCommand){
				msgBox.startMessage(msg, (int)cameraManager.offsetX + Message.h_center, (int)cameraManager.offsetY + 200);
				startedCommand = true;
			} else {

				// test for a complete message
				if (!msgBox.visible){
					startedCommand = false;
					return commandIndex+1;
				}
			}
		} else if (type == TYPE_HEAL){
			p.effects.add(StatusEffects.HEAL, p.maxHP, 1);
			return commandIndex + 1;
		}
		
		
		return commandIndex;
	}

}
