package net.alcuria.online.server;

import net.alcuria.online.common.Packet.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ServerListener extends Listener {
	
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
			c.sendTCP(loginAnswer);
		}
		
		if (o instanceof Packet2Message) {
			String message = ((Packet2Message) o).message;
			Log.info(message);
		}
		
	}

}
