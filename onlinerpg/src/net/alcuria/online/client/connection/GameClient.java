package net.alcuria.online.client.connection;

import java.io.IOException;
import java.util.Scanner;

import net.alcuria.online.common.Packet.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

public class GameClient {
	
	public static Client client;
	public static Scanner scanner;
	
	public static void start(){
		scanner = new Scanner(System.in);
		client = new Client();
		register();
		
		ClientListener cl = new ClientListener();
		cl.init(client);
		client.addListener(cl);
		
		client.start();
		try {
			Log.info("[CLIENT] Attepmpting to connect to localhost");
			client.connect(20000, "127.0.0.1", 54555, 54555);
		} catch (IOException e) {
			e.printStackTrace();
			client.stop();
		}
	}
	
	private static void register(){
		Kryo kryo = client.getKryo();
		kryo.register(Packet0LoginRequest.class);
		kryo.register(Packet1LoginAnswer.class);
		kryo.register(Packet2Message.class);
	}


}
