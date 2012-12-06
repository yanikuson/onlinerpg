package net.alcuria.online.common;

import net.alcuria.online.client.Player;

import com.badlogic.gdx.math.Rectangle;

public class Packet {

	
	public static class Packet0LoginRequest 
	{ 

	}

	public static class Packet1LoginAnswer 
	{ 
		public boolean accepted = false; 
		public int uid;
	}

	public static class Packet2Message 
	{ 
		public String message; 
	}
	
	public static class Packet3SendPosition
	{
		public Rectangle bounds;
		public float xVel;
		public float yVel;
	}
	
	public static class Packet4SendPlayer
	{
		public Player p;
	}

}
