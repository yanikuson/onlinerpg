package net.alcuria.online.client.connection;
import net.alcuria.online.common.Packet.*;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;


public class ClientListener extends Listener {
	
	public boolean connected = false;
	public int uid;
	private Client client;

	public void init(Client client) {
		this.client = client;
	}

	public void connected(Connection arg0) {
		Log.info("[CLIENT] You have connected.");
		client.sendTCP(new Packet0LoginRequest());
	}

	public void disconnected(Connection arg0) {
		Log.info("[CLIENT] You have disconnected.");
	}

	public void received(Connection c, Object o) {

		if (o instanceof Packet1LoginAnswer){
			
			this.connected = ((Packet1LoginAnswer) o).accepted;
			
			if (connected) {
				
				// assign the client a unique user ID
				this.uid = ((Packet1LoginAnswer) o).uid;

				// send off a response to the server... perhaps a player obj
				Packet2Message mpacket = new Packet2Message();
				mpacket.message = "thanks for assigning me id " + uid;
				client.sendTCP(mpacket);
				
			} else {
				c.close();
			}
		}


	}
}
