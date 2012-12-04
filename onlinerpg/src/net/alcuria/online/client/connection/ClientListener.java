package net.alcuria.online.client.connection;
import net.alcuria.online.common.Packet.*;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;


public class ClientListener extends Listener {
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
			boolean answer = ((Packet1LoginAnswer) o).accepted;

			if (answer) {

				// send off packets to the server
				while (true) {

					Packet2Message mpacket = new Packet2Message();
					mpacket.message = "hello server from client!";
					client.sendTCP(mpacket);
					Log.info("Sent packet to server");
					
					pause(5000);

				}
			} else {
				c.close();
			}
		}


	}

	private void pause(int ms) {

		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}



}
