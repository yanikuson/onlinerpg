package net.alcuria.online.server;

import net.alcuria.online.client.Player;
import net.alcuria.online.client.screens.Field;
import net.alcuria.online.common.Packet.*;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ServerListener extends Listener {
	
	static Field f;
	static int nextID = 0;
	static Array<Player> players;
	
	public void connected(Connection arg0) {
		Log.info("[SERVER] Client has connected.");
	}

	public void disconnected(Connection arg0) {
		Log.info("[SERVER] Client has disconnected.");
	}

	public void received(Connection c, Object o) {
		
		if (o instanceof Packet0LoginRequest){
			Packet1LoginAnswer loginAnswer = new Packet1LoginAnswer();
			loginAnswer.accepted = true;
			loginAnswer.uid = getNextInt();
			c.sendTCP(loginAnswer);
			
			players.add(new Player("Name", Player.GENDER_MALE, Player.SKIN_PALE, 1, -20, -20, 14, 22, f));
		}
		
		if (o instanceof Packet2Message) {
			String message = ((Packet2Message) o).message;
			Log.info(message);
		}
		
		if (o instanceof Packet3SendPosition) {
			float position = ((Packet3SendPosition) o).bounds.x;
			System.out.println("xpos: " + position);
			Log.info("X POS: " + position);
		}
		
	}

	private int getNextInt() {
		return nextID++;
	}

}
