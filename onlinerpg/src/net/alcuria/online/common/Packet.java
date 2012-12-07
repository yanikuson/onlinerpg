package net.alcuria.online.common;

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
		public int uid;
		public Rectangle bounds;
		public boolean facingLeft;
		public boolean onGround;
		public boolean moving;

	}
	
	// CLIENT sends this to SERVER to request all players positions
	public static class Packet4RequestPositions
	{
		public int uid;
	}
	

}
