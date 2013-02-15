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

public class GameClient {
	
	public static Client client;
	public static Field f;
	public static byte jumpNoticesSent = -1; 	// going to try sending the jump notice three times
	
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
		kryo.register(Packet8SendEnemySpawnNotification.class);
		kryo.register(Packet9RequestPlayerData.class);
		kryo.register(Packet10SendPlayerData.class);
		kryo.register(Packet11SendPlatformState.class);

	}

	public static void sendMapChange(Field f){
		
		Packet5SendMap map = new Packet5SendMap();
		map.uid = f.player.uid;
		map.currentMap = f.player.currentMap;
		
		client.sendTCP(map);
		
	}
	
	public static void sendFullUpdate(Field f){
		
		Packet10SendPlayerData full = new Packet10SendPlayerData();
		
		full.uid = f.player.uid;
		full.name = f.player.name;
		
		full.armor = (byte) f.player.armor.id;
		full.wep = (byte) f.player.weapon.id;
		full.helm = (byte) f.player.helmet.id;
		
		full.skin = f.player.skin;
		full.hair = f.player.hair;
		full.gender = f.player.gender;
		
		client.sendTCP(full);
	}
	
	public static void sendPositionUpdate(Field f){
		
		Packet3SendPosition pos = new Packet3SendPosition();
		pos.uid = f.player.uid;
		pos.bounds = f.player.bounds;
		pos.MOVE_LEFT = f.player.networkCommand[Player.MOVE_LEFT];
		pos.MOVE_RIGHT = f.player.networkCommand[Player.MOVE_RIGHT];
		
		// we want to check if we are sending a jump packet. if so, we send it 3 times
		if (f.player.networkCommand[Player.MOVE_JUMP]){
			jumpNoticesSent = 0; 
		}
		if (jumpNoticesSent >= 0 && jumpNoticesSent < 3){
			pos.MOVE_JUMP = true;
			jumpNoticesSent++;
		} else {
			pos.MOVE_JUMP = false;
			jumpNoticesSent = -1;
		}
		pos.MOVE_ATTACK = f.player.networkCommand[Player.MOVE_ATTACK];
		pos.facingLeft = f.player.facingLeft;
		
		if (f.player.networkCommand[Player.MOVE_JUMP]) {
			f.player.networkCommand[Player.MOVE_JUMP] = false; // STOP jumping if we are going to send the packet!!
		}
		if (f.player.networkCommand[Player.MOVE_ATTACK]) {
			f.player.networkCommand[Player.MOVE_ATTACK] = false; // STOP jumping if we are going to send the packet!!
		}
		
		// assign the skill id
		pos.skillID = f.player.networkSkillID;
		f.player.networkSkillID = -1;			// we set this to -1 so the skill id doesn't register twice. only need to send the first one.
		
		// TODO: don't need to send the map every time. try only on map change
		pos.currentMap = f.player.currentMap;
		pos.HP = (short) f.player.HP;
		pos.maxHP = (short) f.player.maxHP;
		
		client.sendTCP(pos);
	}
	
	// client sends server a request for a critical player update
	//TODO: because the server already knows what map the player is on
	// 		I think sending currentMap is redundant
	public static void requestPositions(Field f) {
		
		Packet4RequestPositions req = new Packet4RequestPositions();
		req.uid = f.player.uid;
		req.currentMap = f.player.currentMap;
		client.sendTCP(req);
		
	}

	// client sends server a request for a full player update
	//TODO: because the server already knows what map the player is on
	// 		I think sending currentMap is redundant
	public static void requestFullUpdate(Field f) {

		Packet9RequestPlayerData fullReq = new Packet9RequestPlayerData();
		fullReq.requesterUid = f.player.uid;
		fullReq.currentMap = f.player.currentMap;
		client.sendTCP(fullReq);
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
		
	}



}
