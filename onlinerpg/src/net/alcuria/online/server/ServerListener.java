package net.alcuria.online.server;


import net.alcuria.online.client.Config;
import net.alcuria.online.client.Map;
import net.alcuria.online.client.Actor;
import net.alcuria.online.client.Item;
import net.alcuria.online.client.Monster;
import net.alcuria.online.client.MonsterSpawner;
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
					} else {
						// add a new elem to sMonsters hash
						Log.info("[SERVER] adding new spawner/map elem");

						// ADD a map
						sMaps.put(sPlayers.get(i).currentMap, new Map(sPlayers.get(i).currentMap));

						// CREATE the monster spawner
						boolean toggler = false;
						MonsterSpawner spawner = new MonsterSpawner("maps/" + sPlayers.get(i).currentMap + ".spawn");
						for (int j = 0; j < MonsterSpawner.MAX_MONSTERS; j++) {

							spawner.addInitialMonsters(sPlayers.get(i).currentMap, toggler, f);
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

			// now update
			for (int i = 0; i < sPlayers.size; i++ ){	
				if (sMonsters.containsKey(sPlayers.get(i).currentMap)){
					if (!sMonsters.get(sPlayers.get(i).currentMap).updated){
						sMonsters.get(sPlayers.get(i).currentMap).serverUpdate(sPlayers.get(i).currentMap, sMaps.get(sPlayers.get(i).currentMap));
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
					sPlayers.get(i).networkCommand[Actor.MOVE_LEFT] =  ((Packet3SendPosition) o).MOVE_LEFT;
					sPlayers.get(i).networkCommand[Actor.MOVE_RIGHT] =  ((Packet3SendPosition) o).MOVE_RIGHT;
					sPlayers.get(i).networkCommand[Actor.MOVE_JUMP] =  ((Packet3SendPosition) o).MOVE_JUMP;
					sPlayers.get(i).networkCommand[Actor.MOVE_ATTACK] =  ((Packet3SendPosition) o).MOVE_ATTACK;

					if(sPlayers.get(i).weapon.id != ((Packet3SendPosition) o).wep){
						sPlayers.get(i).weapon = new Item(((Packet3SendPosition) o).wep);
					}
					if(sPlayers.get(i).armor.id != ((Packet3SendPosition) o).armor){
						sPlayers.get(i).armor = new Item(((Packet3SendPosition) o).armor);
					}
					if(sPlayers.get(i).helmet.id != ((Packet3SendPosition) o).helm){
						sPlayers.get(i).helmet = new Item(((Packet3SendPosition) o).helm);
					}

					sPlayers.get(i).currentMap =  ((Packet3SendPosition) o).currentMap;
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
					
				} else if (sPlayers.get(i).currentMap != null){
				
					// send off all players that are on the current map and of course not equal to the requesting player
					Packet3SendPosition position = new Packet3SendPosition();
					position.uid = (byte) i;
					position.bounds = sPlayers.get(i).bounds;
					
					position.MOVE_LEFT = sPlayers.get(i).networkCommand[Player.MOVE_LEFT];
					position.MOVE_RIGHT = sPlayers.get(i).networkCommand[Player.MOVE_RIGHT];
					position.MOVE_JUMP = sPlayers.get(i).networkCommand[Player.MOVE_JUMP];
					position.MOVE_ATTACK = sPlayers.get(i).networkCommand[Player.MOVE_ATTACK];

					position.wep = (byte) sPlayers.get(i).weapon.id;
					position.armor = (byte) sPlayers.get(i).armor.id;
					position.helm = (byte) sPlayers.get(i).helmet.id;

					//position.currentMap = sPlayers.get(i).currentMap;
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
						//curMonSpawner.monsterList[i].networkCommand[Monster.MOVE_JUMP] = false;
	
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
	}

	private byte getNextInt() {
		return nextID++;
	}


}
