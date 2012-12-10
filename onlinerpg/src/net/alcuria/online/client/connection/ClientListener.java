package net.alcuria.online.client.connection;
import net.alcuria.online.client.Item;
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
	public boolean updateVisual = false;

	public Field f;
	private Client client;

	public void init(Client client, Field f) {
		this.client = client;
		this.f = f;
	}

	public void connected(Connection arg0) {
		Log.info("[CLIENT] You have connected.");

		// create a login req
		Packet0LoginRequest req = new Packet0LoginRequest();
		req.currentMap = f.player.currentMap;

		req.wep = f.player.weapon.id;
		req.acc = f.player.accessory.id;
		req.armor = f.player.armor.id;
		req.helm = f.player.helmet.id;

		req.hair = f.player.hair;
		req.skin = f.player.skin;
		req.gender = f.player.gender;

		System.out.println("login request has skin: " + req.skin);
		client.sendTCP(req);
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
			byte index = ((Packet3SendPosition) o).uid;
			for (int i = 0; i < f.players.size; i++){
				if (f.players.get(i).uid == index){
					f.players.get(i).desiredBounds = ((Packet3SendPosition) o).bounds;
					f.players.get(i).networkCommand[Player.MOVE_LEFT] = ((Packet3SendPosition) o).MOVE_LEFT;
					f.players.get(i).networkCommand[Player.MOVE_RIGHT] = ((Packet3SendPosition) o).MOVE_RIGHT;
					f.players.get(i).networkCommand[Player.MOVE_JUMP] = ((Packet3SendPosition) o).MOVE_JUMP;
					f.players.get(i).networkCommand[Player.MOVE_ATTACK] = ((Packet3SendPosition) o).MOVE_ATTACK;

					f.players.get(i).hair = ((Packet3SendPosition) o).hair;
					f.players.get(i).skin = ((Packet3SendPosition) o).skin;
					f.players.get(i).gender = ((Packet3SendPosition) o).gender;

					// check to see if this player object will need a visual equip refresh
					if(f.players.get(i).weapon.id != ((Packet3SendPosition) o).wep || f.players.get(i).armor.id != ((Packet3SendPosition) o).armor || f.players.get(i).helmet.id != ((Packet3SendPosition) o).helm){
						
						// assign latest weps/armors to client player element
						f.players.get(i).weapon = new Item(((Packet3SendPosition) o).wep);
						f.players.get(i).armor = new Item(((Packet3SendPosition) o).armor);
						f.players.get(i).helmet = new Item(((Packet3SendPosition) o).helm);
						
						f.players.get(i).resetVisualEquips();
						Log.info("updating equips.");
					}

					f.players.get(i).currentMap = ((Packet3SendPosition) o).currentMap;

					updated = true;
					break;
				}
			}
			// if we iterate thru the whole list and dont update a player element, we can safely add it now
			if (!updated){
				Log.info("[CLIENT] creating new local player element");
				Player p = new Player("New", ((Packet3SendPosition) o).gender, ((Packet3SendPosition) o).skin, ((Packet3SendPosition) o).hair, -20, -20, 14, 22, f);
				p.uid = index;
				p.resetVisualEquips();
				f.players.add(p);

			}

		}


	}
}
