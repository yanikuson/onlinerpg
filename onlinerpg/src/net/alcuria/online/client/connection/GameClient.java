package net.alcuria.online.client.connection;

import java.io.IOException;

import net.alcuria.online.client.screens.Field;
import net.alcuria.online.common.Packet.*;

import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

public class GameClient {
	
	public static Client client;
	public static Field f;
	
	public static void start(){
		
		client = new Client();
		register();
	
		// create a listener to accept incoming packets from the server
		ClientListener cl = new ClientListener();
		cl.init(client);
		client.addListener(cl);
		
		client.start();
		try {
			client.connect(10000, "127.0.0.1", 54555, 54555);			
		} catch (IOException e) {
			e.printStackTrace();
			client.stop();
		}
	}
	
	
	public static void sendPositionUpdate(Field f){
		
		Packet3SendPosition pos = new Packet3SendPosition();
		pos.bounds = f.player.bounds;
		pos.xVel = f.player.xVel;
		pos.yVel = f.player.yVel;
		client.sendTCP(pos);
	}
	
	private static void register(){
		
		Kryo kryo = client.getKryo();
		kryo.register(Rectangle.class);
		kryo.register(Packet0LoginRequest.class);
		kryo.register(Packet1LoginAnswer.class);
		kryo.register(Packet2Message.class);
		kryo.register(Packet3SendPosition.class);
	}

}
