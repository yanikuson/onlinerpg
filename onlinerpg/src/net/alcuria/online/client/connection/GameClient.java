package net.alcuria.online.client.connection;

import java.io.IOException;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.Player;
import net.alcuria.online.client.screens.Field;
import net.alcuria.online.common.Packet.*;

import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

public class GameClient {
	
	public static Client client;
	public static Field f;
	
	public static void start(){
		
		client = new Client();
		register();
	
		// create a listener to accept incoming packets from the server
		ClientListener cl = new ClientListener();
		cl.init(client, f);
		client.addListener(cl);
		
		client.start();
		try {
			client.connect(10000, Config.IP, 54555, 54555);			
		} catch (IOException e) {
			e.printStackTrace();
			client.stop();
		}
	}
	
	

	
	private static void register(){
		
		Kryo kryo = client.getKryo();
		kryo.register(Rectangle.class);
		kryo.register(Packet0LoginRequest.class);
		kryo.register(Packet1LoginAnswer.class);
		kryo.register(Packet2Message.class);
		kryo.register(Packet3SendPosition.class);
		kryo.register(Packet4RequestPositions.class);
	}

	public static void sendPositionUpdate(Field f){
		
		Packet3SendPosition pos = new Packet3SendPosition();
		pos.uid = f.player.uid;
		pos.bounds = f.player.bounds;
		pos.MOVE_LEFT = f.player.networkCommand[Player.MOVE_LEFT];
		pos.MOVE_RIGHT = f.player.networkCommand[Player.MOVE_RIGHT];
		pos.MOVE_JUMP = f.player.networkCommand[Player.MOVE_JUMP];
		if (f.player.networkCommand[Player.MOVE_JUMP]) {
			f.player.networkCommand[Player.MOVE_JUMP] = false; // STOP jumping if we are going to send the packet!!
		}
		client.sendTCP(pos);
	}
	
	public static void requestPositions(Field f) {
		Packet4RequestPositions req = new Packet4RequestPositions();
		req.uid = f.player.uid;
		client.sendTCP(req);
		
	}

}
