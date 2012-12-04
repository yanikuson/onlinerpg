package net.alcuria.online.server;
import java.io.IOException;

import net.alcuria.online.common.Packet.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;


public class GameServer {

	private Server server;
	
	public GameServer() throws IOException {
		server = new Server();
		registerPackets();
		server.addListener(new ServerListener());
		server.bind(54555, 54555);
		server.start();
		
	}
	

	private void registerPackets(){
		Kryo kryo = server.getKryo();
		kryo.register(Packet0LoginRequest.class);
		kryo.register(Packet1LoginAnswer.class);
		kryo.register(Packet2Message.class);
	}
	
	public static void main(String[] args){
		try {
			new GameServer();
			Log.set(Log.LEVEL_DEBUG);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
}
