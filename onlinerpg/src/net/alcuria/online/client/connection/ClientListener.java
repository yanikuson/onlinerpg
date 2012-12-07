package net.alcuria.online.client.connection;
import net.alcuria.online.client.Player;
import net.alcuria.online.client.screens.Field;
import net.alcuria.online.common.Packet.*;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;


public class ClientListener extends Listener {
	public boolean updated = false;
	public boolean connected = false;
	
	public Field f;
	private Client client;

	public void init(Client client, Field f) {
		this.client = client;
		this.f = f;
	}

	public void connected(Connection arg0) {
		Log.info("[CLIENT] You have connected.");
		client.sendTCP(new Packet0LoginRequest());
	}

	public void disconnected(Connection arg0) {
		Log.info("[CLIENT] You have disconnected.");
	}

	public void received(Connection c, Object o) {

		// if the client receives a response to the login request, we know that it contains a new uid, which we assign to the player
		if (o instanceof Packet1LoginAnswer){
			
			this.connected = ((Packet1LoginAnswer) o).accepted;
			
			if (connected) {
				
				// assign the client a unique user ID
				f.player.uid = ((Packet1LoginAnswer) o).uid;

				// send off a response to the server... perhaps a player obj
				Packet2Message mpacket = new Packet2Message();
				mpacket.message = "thanks for assigning me id " + f.player.uid;
				client.sendTCP(mpacket);
				
			} else {
				c.close();
			}
		}
		
		// if client receives a position update, we know it came from the server
		// so we update that UID in the players array with the position and velocity from the packet
		if (o instanceof Packet3SendPosition){
			updated = false;
			int index = ((Packet3SendPosition) o).uid;
			for (int i = 0; i < f.players.size; i++){
				if (f.players.get(i).uid == index){
					f.players.get(i).desiredBounds = ((Packet3SendPosition) o).bounds;
					f.players.get(i).moving = ((Packet3SendPosition) o).moving;
					f.players.get(i).facingLeft = ((Packet3SendPosition) o).facingLeft;
					f.players.get(i).onGround = ((Packet3SendPosition) o).onGround; 
					updated = true;
				}
			}
			// if we iterate thru the whole list and dont update a player element, we can safely add it now
			if (!updated){
				Log.info("[CLIENT] creating new local player element");
				Player p = new Player("New", Player.GENDER_MALE, Player.SKIN_DARK, 1, -20, -20, 14, 22, f);
				p.uid = index;
				f.players.add(p);
				
			}
			
		}


	}
}
