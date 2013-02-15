package net.alcuria.online.server;
import java.io.IOException;

import net.alcuria.online.common.Packet.*;
import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;


public class GameServer {

	public static Server server;
	public static Field f;
	public static boolean running = false;

	public static void start() {

		// here we initialize all objects the server needs to sync
		ServerListener.f = f;
		ServerListener.init();

		// now we start up the server
		server = new Server();
		registerPackets();
		server.addListener(new ServerListener());
		try {
			running = true;
			server.bind(54555, 54555);
			server.start();

		} catch (IOException e) {
			Log.error("error binding server to port");
			server.stop();
			running = false;
		}

	}

	public static void update() {

		ServerListener.update();

	}


	// register all packets: required by the kryonet library 
	private static void registerPackets(){
		Kryo kryo = server.getKryo();
		kryo.register(Rectangle.class);
		kryo.register(Packet0LoginRequest.class);
		kryo.register(Packet1LoginAnswer.class);
		kryo.register(Packet2Message.class);
		kryo.register(Packet3SendPosition.class);
		kryo.register(Packet4RequestPositions.class);
		kryo.register(Packet5SendMap.class);
		kryo.register(Packet6SendMonsterPosition.class);
		kryo.register(Packet7SendDamageNotification.class);
		kryo.register(Packet8SendEnemySpawnNotification.class);
		kryo.register(Packet9RequestPlayerData.class);
		kryo.register(Packet10SendPlayerData.class);
		kryo.register(Packet11SendPlatformState.class);
	}

	public static void main(String[] args){

		Log.set(Log.LEVEL_DEBUG);
		start();

	}
}
