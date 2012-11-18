package net.alcuria.online.client;

public class GlobalFlags {

	
	public static boolean flags[];
	
	public static final int INTRO = 0;
	
	public static void init(){
		
		flags = new boolean[20];
		for (int i=0; i < flags.length; i++) {
			flags[i] = false;
		}
		
	}
}
