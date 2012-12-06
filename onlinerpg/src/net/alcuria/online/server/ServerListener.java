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
	static Array<Player> sPlayers;
	
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
			
			// create a server copy of the player that connected
			Player p = new Player("Name", Player.GENDER_MALE, Player.SKIN_PALE, 1, -20, -20, 14, 22, f);
			p.uid = loginAnswer.uid;
			sPlayers.add(p);
			System.out.println("[SERVER] creating player object for newly-connected client");
		}
		
		if (o instanceof Packet2Message) {
			String message = ((Packet2Message) o).message;
			Log.info(message);
		}
		
		// server receives a position update from the client... we need to update the SERVER players array
		if (o instanceof Packet3SendPosition) {
			int index = ((Packet3SendPosition) o).uid;
			for (int i = 0; i < sPlayers.size; i++){
				if (sPlayers.get(i).uid == index){
					sPlayers.get(i).bounds =  ((Packet3SendPosition) o).bounds;
					sPlayers.get(i).xVel =  ((Packet3SendPosition) o).xVel;
					sPlayers.get(i).yVel =  ((Packet3SendPosition) o).yVel;
				}
			}
		}
		
		// Server gets a request for all relevant positions. Send back to client!
		if (o instanceof Packet4RequestPositions) {
			int requestersUid = ((Packet4RequestPositions) o).uid;
			for (int i = 0; i < sPlayers.size; i++){
				
				// only send this position if the uid isnt the requested users uid
				if (sPlayers.get(i).uid != requestersUid){
					Packet3SendPosition position = new Packet3SendPosition();
					position.uid = i;
					position.bounds = sPlayers.get(i).bounds;
					position.xVel = sPlayers.get(i).xVel;
					position.yVel = sPlayers.get(i).yVel;
					c.sendTCP(position);
				}
			}
		}
		
	}

	private int getNextInt() {
		return nextID++;
	}

}
