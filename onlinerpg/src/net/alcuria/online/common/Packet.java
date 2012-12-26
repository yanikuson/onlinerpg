package net.alcuria.online.common;

import com.badlogic.gdx.math.Rectangle;

public class Packet {

	
	public static class Packet0LoginRequest 
	{ 
		public String currentMap;
		public byte skin;
		public byte hair;
		public byte gender;
		
		public int wep;
		public int armor;
		public int helm;
		public int acc;
	}

	public static class Packet1LoginAnswer 
	{ 
		public boolean accepted = false; 
		public byte uid;
	}

	public static class Packet2Message 
	{ 
		public String message; 
	}
	
	public static class Packet3SendPosition
	{
		public byte uid;
		public Rectangle bounds;
		public boolean MOVE_LEFT;
		public boolean MOVE_RIGHT;
		public boolean MOVE_JUMP;
		public boolean MOVE_ATTACK;
		
		public byte skillID;
		
		public byte skin;
		public byte hair;
		public byte gender;
		
		public byte wep;
		public byte armor;
		public byte helm;
		
		public short HP;
		public short maxHP;
		public String currentMap;
		public boolean connected;
		

	}
	
	// CLIENT sends this to SERVER to request all players positions
	public static class Packet4RequestPositions
	{
		public byte uid;
		public String currentMap;
	}
	
	// client sends this on map change
	public static class Packet5SendMap
	{
		public byte uid;
		public String currentMap;
	}
	
	// Server sends MANY of these to client when a monster is visible!
	public static class Packet6SendMonsterPosition
	{
		public byte id;
		public Rectangle bounds;
		public boolean MOVE_LEFT;
		public boolean MOVE_RIGHT;
		public boolean MOVE_JUMP;
		public boolean MOVE_ATTACK;
		public short HP;
	}

	// these are passed around to show on-screen damage and update enemy HP
	public static class Packet7SendDamageNotification
	{
		public boolean hittingEnemy;
		public byte attackerID;
		public byte defenderID;
		public short damage;
		public boolean facingLeft;
		public byte animationID;
		public String currentMap;
	}
	
	// SERVER sends to relevant CLIENT to refresh HP with servers copy (send this out when the server spawns a new enemy after init spawn)
	public static class Packet8SendEnemySpawnNotification
	{
		public byte enemyID;
		public int HP;
	}
	
}
