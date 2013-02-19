package net.alcuria.online.server;


import net.alcuria.online.client.Config;
import net.alcuria.online.client.Map;
import net.alcuria.online.client.Actor;
import net.alcuria.online.client.Item;
import net.alcuria.online.client.Monster;
import net.alcuria.online.client.MonsterSpawner;
import net.alcuria.online.client.Platform;
import net.alcuria.online.client.Player;
import net.alcuria.online.client.screens.Field;
import net.alcuria.online.common.Packet.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;


public class ServerListener extends Listener {

	static Field f;
	static byte nextID = 0;
	static Array<Player> sPlayers;
	static ObjectMap<String, MonsterSpawner> sMonsters;
	static ObjectMap<String, Map> sMaps;

	static MonsterSpawner curMonSpawner;
	static Rectangle curPlayerRange;
	static boolean facingLeft;				// is the hero facing left? (for enemy kb)

	public static void init() {

		sPlayers = new Array<Player>(false, 10);
		sMonsters = new ObjectMap<String, MonsterSpawner>();
		sMaps = new ObjectMap<String, Map>();
		curPlayerRange = new Rectangle();

	}

	public static void update() {
		// update monsters
		if (sPlayers != null) {

			// first set all the updated flags to false
			for (int i = 0; i < sPlayers.size; i++ ){
				if (Gdx.files.internal("maps/" + sPlayers.get(i).currentMap + ".spawn").exists()){
					if (sMonsters.containsKey(sPlayers.get(i).currentMap)){
						sMonsters.get(sPlayers.get(i).currentMap).updated = false;
						sMaps.get(sPlayers.get(i).currentMap).updatedPlatforms = false;
					} else {
						// add a new elem to sMonsters hash
						Log.info("[SERVER] adding new spawner/map elem");

						// ADD a map
						sMaps.put(sPlayers.get(i).currentMap, new Map(sPlayers.get(i).currentMap));

						// CREATE the monster spawner
						boolean toggler = false;
						MonsterSpawner spawner = new MonsterSpawner("maps/" + sPlayers.get(i).currentMap + ".spawn");
						for (int j = 0; j < MonsterSpawner.MAX_MONSTERS; j++) {

							spawner.addInitialMonsters((byte)j, sPlayers.get(i).currentMap, toggler, f);
							toggler = !toggler;
						}
						spawner.doInitialServerSpawn(sPlayers.get(i).currentMap, sMaps.get(sPlayers.get(i).currentMap));
						sMonsters.put(sPlayers.get(i).currentMap, spawner);

						// END MONSTER SPAWNER CREATION
					}
				}

				// check for a disconnect
				sPlayers.get(i).lastPing++;
				if (sPlayers.get(i).lastPing > 30) {
					sPlayers.get(i).connected = false;
					Log.info("[SERVER] Player has disconnected: " + sPlayers.get(i).name);
					sPlayers.removeIndex(i);
				}

			}

			// now update (monsters + platforms)
			for (int i = 0; i < sPlayers.size; i++ ){	
				if (sMonsters.containsKey(sPlayers.get(i).currentMap)){
					if (!sMonsters.get(sPlayers.get(i).currentMap).updated){
						sMonsters.get(sPlayers.get(i).currentMap).serverUpdate(sPlayers.get(i).currentMap, sMaps.get(sPlayers.get(i).currentMap));
					}
					if (sMaps.get(sPlayers.get(i).currentMap) != null && !sMaps.get(sPlayers.get(i).currentMap).updatedPlatforms){
						sMaps.get(sPlayers.get(i).currentMap).updatePlatforms();
						sMaps.get(sPlayers.get(i).currentMap).updatedPlatforms = true;
					}
				}
			}
		}

	}


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
			loginAnswer.uid = getNextInt();
			c.sendTCP(loginAnswer);

			// create a server copy of the player that connected
			Player p = new Player("Name", Player.GENDER_MALE, Player.SKIN_PALE, 1, -20, -20, 14, 22, f);
			p.uid = loginAnswer.uid;
			p.name =((Packet0LoginRequest) o).name;
			p.skin = ((Packet0LoginRequest) o).skin;
			p.hair = ((Packet0LoginRequest) o).hair;
			p.gender = ((Packet0LoginRequest) o).gender;

			p.weapon = new Item(((Packet0LoginRequest) o).wep);
			p.armor = new Item(((Packet0LoginRequest) o).armor);
			p.accessory = new Item(((Packet0LoginRequest) o).acc);
			p.helmet = new Item(((Packet0LoginRequest) o).helm);

			p.currentMap = ((Packet0LoginRequest) o).currentMap;

			sPlayers.add(p);
			Log.info("[SERVER] creating player object for newly-connected client.");
		}

		if (o instanceof Packet2Message) {
			String message = ((Packet2Message) o).message;
			Log.info(message);
		}

		// server receives a position update from the client... we need to update the SERVER players array
		if (o instanceof Packet3SendPosition) {
			int index = ((Packet3SendPosition) o).uid;
			for (int i = 0; i < sPlayers.size; i++){
				if (sPlayers.get(i).uid == index){
					sPlayers.get(i).bounds =  ((Packet3SendPosition) o).bounds;

					// update FACING flag
					if (((Packet3SendPosition) o).MOVE_LEFT){
						sPlayers.get(i).facingLeft = true;
					} else if (((Packet3SendPosition) o).MOVE_RIGHT){
						sPlayers.get(i).facingLeft = false;
					}
					sPlayers.get(i).networkCommand[Actor.MOVE_LEFT] =  ((Packet3SendPosition) o).MOVE_LEFT;
					sPlayers.get(i).networkCommand[Actor.MOVE_RIGHT] =  ((Packet3SendPosition) o).MOVE_RIGHT;
					sPlayers.get(i).networkCommand[Actor.MOVE_JUMP] =  ((Packet3SendPosition) o).MOVE_JUMP;
					sPlayers.get(i).networkCommand[Actor.MOVE_ATTACK] =  ((Packet3SendPosition) o).MOVE_ATTACK;
					sPlayers.get(i).networkFacingLeft =  ((Packet3SendPosition) o).facingLeft;

					sPlayers.get(i).networkSkillID =  ((Packet3SendPosition) o).skillID;

					sPlayers.get(i).currentMap =  ((Packet3SendPosition) o).currentMap;
					sPlayers.get(i).HP = ((Packet3SendPosition) o).HP;
					sPlayers.get(i).maxHP = ((Packet3SendPosition) o).maxHP;
					sPlayers.get(i).lastPing = 0;


					break;

				}
			}
		}

		// Server gets a request for all relevant positions. Send back to client!
		if (o instanceof Packet4RequestPositions) {
			int requestersUid = ((Packet4RequestPositions) o).uid;
			String requestersMap = ((Packet4RequestPositions) o).currentMap;

			// send off all players
			for (int i = 0; i < sPlayers.size; i++){
				// only send this position if the uid isnt the requested users uid & maps are same
				if (sPlayers.get(i).uid == requestersUid) {
					// save off a copy of the player's bounds for later sending the enemy positions
					curPlayerRange.x = sPlayers.get(i).bounds.x - Config.WIDTH;
					curPlayerRange.y = sPlayers.get(i).bounds.y - Config.HEIGHT;
					curPlayerRange.width = Config.WIDTH * 2;
					curPlayerRange.height = Config.HEIGHT * 2;

					// send off any possible damage queues for this player
					while(sPlayers.get(i).damageQueue.size > 0){
						c.sendTCP(sPlayers.get(i).damageQueue.pop());
					}
					
					// send off any possible status effects
					while(sPlayers.get(i).statusEffectQueue.size > 0){
						c.sendTCP(sPlayers.get(i).statusEffectQueue.pop());
					}

				} else if (sPlayers.get(i).currentMap != null && sPlayers.get(i).currentMap.equals(requestersMap)){

					// send off all players that are on the current map and of course not equal to the requesting player
					Packet3SendPosition position = new Packet3SendPosition();
					position.uid = (byte) i;
					position.bounds = sPlayers.get(i).bounds;

					position.MOVE_LEFT = sPlayers.get(i).networkCommand[Player.MOVE_LEFT];
					position.MOVE_RIGHT = sPlayers.get(i).networkCommand[Player.MOVE_RIGHT];
					position.MOVE_JUMP = sPlayers.get(i).networkCommand[Player.MOVE_JUMP];
					position.MOVE_ATTACK = sPlayers.get(i).networkCommand[Player.MOVE_ATTACK];
					position.skillID = sPlayers.get(i).networkSkillID;
					position.facingLeft = sPlayers.get(i).networkFacingLeft;

					position.currentMap = sPlayers.get(i).currentMap;
					position.HP = (short) sPlayers.get(i).HP;
					position.maxHP = (short) sPlayers.get(i).maxHP;

					position.connected = sPlayers.get(i).connected;

					c.sendTCP(position);
				}
			}

			// SEND any monster positions out
			if (sMonsters.containsKey(requestersMap)){

				curMonSpawner = sMonsters.get(requestersMap);
				for (int i = 0; i < curMonSpawner.monsterList.length; i++){
					if (curMonSpawner.monsterList[i] != null && curMonSpawner.monsterList[i].visible && curMonSpawner.monsterList[i].HP > 0 && curMonSpawner.monsterList[i].bounds.overlaps(curPlayerRange)){
						Packet6SendMonsterPosition monPosition = new Packet6SendMonsterPosition();
						monPosition.id = (byte) i;
						monPosition.bounds = curMonSpawner.monsterList[i].bounds;
						monPosition.MOVE_LEFT = curMonSpawner.monsterList[i].networkCommand[Monster.MOVE_LEFT];
						monPosition.MOVE_RIGHT = curMonSpawner.monsterList[i].networkCommand[Monster.MOVE_RIGHT];
						monPosition.MOVE_JUMP =  curMonSpawner.monsterList[i].networkCommand[Monster.MOVE_JUMP];
						monPosition.MOVE_ATTACK =  curMonSpawner.monsterList[i].networkCommand[Monster.MOVE_ATTACK];
						monPosition.HP = (short) curMonSpawner.monsterList[i].HP;
						c.sendTCP(monPosition);
						curMonSpawner.monsterList[i].networkCommand[Monster.MOVE_JUMP] = false;

					}
				}
			}

		}

		// server receives a packet containing a map change. update server's player list element's current map
		if (o instanceof Packet5SendMap) {
			Log.info("server received map change request \"" + ((Packet5SendMap) o).currentMap + "\" from client.");

			int index = ((Packet5SendMap) o).uid;
			for (int i = 0; i < sPlayers.size; i++){
				if (sPlayers.get(i).uid == index){
					sPlayers.get(i).currentMap =  ((Packet5SendMap) o).currentMap;
					break;
				}
			}
		}

		// Server gets a request for all relevant positions. Send back to client!
		if (o instanceof Packet7SendDamageNotification) {

			int sendersUid;
			if (((Packet7SendDamageNotification) o).hittingEnemy){
				sendersUid = ((Packet7SendDamageNotification) o).attackerID;
			} else {
				sendersUid = ((Packet7SendDamageNotification) o).defenderID;
			}
			String sendersMap = ((Packet7SendDamageNotification) o).currentMap;

			// iterate through the sPlayers array and add to a queue of damages to display
			for (int i = 0; i < sPlayers.size; i++){
				// only send this position if the uid isnt the requested users uid & maps are same
				// TODO: we can optimize this by only adding it to the queue if the players are near each other
				if (sPlayers.get(i).uid == sendersUid){
					// reduce the server-side HP value
					if (!((Packet7SendDamageNotification) o).hittingEnemy){
						sPlayers.get(i).HP -= ((Packet7SendDamageNotification) o).damage;
						if (sPlayers.get(i).HP <= 0){
							sPlayers.get(i).HP = sPlayers.get(i).maxHP;
						}
						sPlayers.get(i).damageQueue.add((Packet7SendDamageNotification) o);

					} else {				
						facingLeft = sPlayers.get(i).facingLeft;
					}
				} else if (sPlayers.get(i).currentMap.equals(sendersMap)){
					// add to the damage queue...
					sPlayers.get(i).damageQueue.add((Packet7SendDamageNotification) o);
				}
			}

			// update the server's copy of the enemy's/player's HP
			if (((Packet7SendDamageNotification) o).hittingEnemy){
				sMonsters.get(sendersMap).monsterList[((Packet7SendDamageNotification) o).defenderID].HP -=  ((Packet7SendDamageNotification) o).damage;
				sMonsters.get(sendersMap).monsterList[((Packet7SendDamageNotification) o).defenderID].knockback(facingLeft, 2);
			} 

			//TODO: if the enemy has <= 0 HP, hand out some loot
		}


		//*****************************************************************************

		if (o instanceof Packet9RequestPlayerData) {
			int requestersUid = ((Packet9RequestPlayerData) o).requesterUid;
			String requestersMap = ((Packet9RequestPlayerData) o).currentMap;

			// send off all players
			for (int i = 0; i < sPlayers.size; i++){

				final Player p = sPlayers.get(i);
				// only send this position if the uid isnt the requested users uid & maps are same
				if (requestersUid != p.uid && p.currentMap != null && p.currentMap.equals(requestersMap)){
					// send back to the player details to client
					Packet10SendPlayerData pack = new Packet10SendPlayerData();
					pack.uid = (byte) p.uid;

					pack.name = p.name;

					pack.gender = p.gender;
					pack.hair = p.hair;
					pack.skin = p.skin;

					pack.armor = (byte) p.armor.id;
					pack.helm = (byte) p.helmet.id;
					pack.wep = (byte) p.weapon.id;

					c.sendTCP(pack);

				}
			}

			// send off the infrequent platform updates
			// TODO: assumption is platforms exist on monsters with maps. fix this.
			if (sMonsters != null && sMonsters.containsKey(requestersMap) && sMaps.get(requestersMap) != null && sMaps.get(requestersMap).platforms != null){

				final Platform[] curPlatforms = sMaps.get(requestersMap).platforms;
				for (int i = 0; i < curPlatforms.length; i++){
					if (curPlatforms[i] != null && curPlatforms[i].bounds.overlaps(curPlayerRange)){

						// create the platform packet to send off
						Packet11SendPlatformState plat = new Packet11SendPlatformState();
						plat.id = (byte) i;
						plat.counter = curPlatforms[i].counter;

						//TODO: fix platforms
						c.sendTCP(plat);

					}
				}
			}
		}

		// Server gets a request for all relevant positions. Send back to client!
		if (o instanceof Packet12SendStatusEffect) {

			int sendersUid = ((Packet12SendStatusEffect) o).originID;
			String sendersMap = ((Packet12SendStatusEffect) o).currentMap;
			int targetUid = ((Packet12SendStatusEffect) o).targetID;
			byte effect = ((Packet12SendStatusEffect) o).effect;
			byte severity = ((Packet12SendStatusEffect) o).severity;
			byte duration = ((Packet12SendStatusEffect) o).duration;
			
			// iterate through the sPlayers array and add to a queue of damages to display
			for (int i = 0; i < sPlayers.size; i++){
				
				// only send this position if the uid isnt the requested users uid & maps are same
				if (sPlayers.get(i).uid == sendersUid){
					// update the server's copy and add it to the queue to send off to client
					if (((Packet12SendStatusEffect) o).targetingEnemy){
						// add the stat effect to the monster
						sMonsters.get(sendersMap).monsterList[targetUid].effects.applyEffect(effect, severity, duration);
						System.out.println("Server added effect to a server-side monster");
					} else {
						// add the stat effect to the player
						sPlayers.get(targetUid).effects.applyEffect(effect, severity, duration);
						System.out.println("Server added effect to server-side player: " + sPlayers.get(i).name);
					}
				} else if (sPlayers.get(i).currentMap.equals(sendersMap)){
					// add to the status effect queue...
					sPlayers.get(i).statusEffectQueue.add((Packet12SendStatusEffect) o);
					System.out.println("Appending a stat effect to damage queue of " + sPlayers.get(i).name);
				}
			}

		}



		// server receives the full/infreq updates from the client
		if (o instanceof Packet10SendPlayerData) {
			int index = ((Packet10SendPlayerData) o).uid;

			// find the server element to update
			for (int i = 0; i < sPlayers.size; i++){
				if (sPlayers.get(i).uid == index){

					// update local server copy
					sPlayers.get(i).name =  ((Packet10SendPlayerData) o).name;

					sPlayers.get(i).skin = (byte) ((Packet10SendPlayerData) o).skin;
					sPlayers.get(i).gender = (byte) ((Packet10SendPlayerData) o).gender;					
					sPlayers.get(i).hair = (byte) ((Packet10SendPlayerData) o).hair;					

					if(sPlayers.get(i).weapon.id != ((Packet10SendPlayerData) o).wep){
						sPlayers.get(i).weapon = new Item(((Packet10SendPlayerData) o).wep);
					}
					if(sPlayers.get(i).armor.id != ((Packet10SendPlayerData) o).armor){
						sPlayers.get(i).armor = new Item(((Packet10SendPlayerData) o).armor);
					}
					if(sPlayers.get(i).helmet.id != ((Packet10SendPlayerData) o).helm){
						sPlayers.get(i).helmet = new Item(((Packet10SendPlayerData) o).helm);
					}
					break;

				}
			}
		}



	}



	private byte getNextInt() {
		return nextID++;
	}


}
