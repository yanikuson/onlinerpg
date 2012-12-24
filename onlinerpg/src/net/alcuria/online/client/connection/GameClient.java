package net.alcuria.online.client.connection;

import java.io.IOException;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.Monster;
import net.alcuria.online.client.Player;
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
		kryo.register(Packet5SendMap.class);
		kryo.register(Packet6SendMonsterPosition.class);
		kryo.register(Packet7SendDamageNotification.class);

	}

	public static void sendMapChange(Field f){
		
		Packet5SendMap map = new Packet5SendMap();
		map.uid = f.player.uid;
		map.currentMap = f.player.currentMap;
		
		client.sendTCP(map);
		
	}
	
	public static void sendPositionUpdate(Field f){
		
		Packet3SendPosition pos = new Packet3SendPosition();
		pos.uid = f.player.uid;
		pos.bounds = f.player.bounds;
		pos.MOVE_LEFT = f.player.networkCommand[Player.MOVE_LEFT];
		pos.MOVE_RIGHT = f.player.networkCommand[Player.MOVE_RIGHT];
		pos.MOVE_JUMP = f.player.networkCommand[Player.MOVE_JUMP];
		pos.MOVE_ATTACK = f.player.networkCommand[Player.MOVE_ATTACK];
		if (f.player.networkCommand[Player.MOVE_JUMP]) {
			f.player.networkCommand[Player.MOVE_JUMP] = false; // STOP jumping if we are going to send the packet!!
		}
		if (f.player.networkCommand[Player.MOVE_ATTACK]) {
			f.player.networkCommand[Player.MOVE_ATTACK] = false; // STOP jumping if we are going to send the packet!!
		}
		
		pos.wep = (byte) f.player.weapon.id;
		pos.armor = (byte) f.player.armor.id;
		pos.helm = (byte) f.player.helmet.id;
		
		pos.currentMap = f.player.currentMap;
		
		client.sendTCP(pos);
	}
	
	public static void requestPositions(Field f) {
		Packet4RequestPositions req = new Packet4RequestPositions();
		req.uid = f.player.uid;
		req.currentMap = f.player.currentMap;
		client.sendTCP(req);
		
	}

	public static void sendDamage(Player p, Monster m, short damage, boolean hittingEnemy) {
		Packet7SendDamageNotification dmg = new Packet7SendDamageNotification();
		
		if (hittingEnemy){
			dmg.attackerID = p.uid;
			if (m != null) dmg.defenderID = m.id;
		} else {
			dmg.attackerID = 0;
			dmg.defenderID = p.uid;
		}
		dmg.damage = damage;
		dmg.facingLeft = p.facingLeft;
		dmg.animationID = 0;
		dmg.currentMap = p.currentMap;
		dmg.hittingEnemy = hittingEnemy;
		client.sendTCP(dmg);
		Log.info("Sending a damage packet");
		
	}

}
