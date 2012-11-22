package net.alcuria.online.client;

public class Config {

	// graphics settings
	public static final int WIDTH = 416;
	public static final int HEIGHT = 240;

	// map settings
	public static final int TILE_WIDTH = 16;

	// audio settings
	public static float sfxVol = 1.0f;
	public static float bgmVol = 0.8f;

	// gameplay-related
	public static final float lowHPFlashFrequency = 0.6F;
	public static final float androidControlsOpacity = 0.8f;

	// monster types -- see Monster.java's constructor
	public static final int MON_NPC = 0;
	public static final int MON_SLIME = 1;
	public static final int MON_EYE = 2;

	// NPC identifiers -- see NPC.java
	public static final int NPC_WELCOME = 0;
	public static final int NPC_SHOP = 1;

	// int for damage calculating
	private static int damage = 0;

	// show notifications for?
	public static boolean notifExp = true;
	public static boolean notifItem = true;
	public static boolean notifMoney = true;
	public static boolean notifLevel = true;

	// is an NPC command in progress
	public static boolean npcCommand = false;

	// all formulae are here so they can be changed with ease without screwing with several references scattered about
	public static int getNextLvl(int lvl){
		return (int) (10 * (Math.pow(1.2, lvl)));
	}

	public static int getMaxHP(int lvl, int vit){
		return 20 + 2 * lvl + (vit * vit)/200 + vit * 2;
	}

	public static int getDamageDone(int attackerAtk, int attackerPow, int defenderDef, int defenderStam) {
		damage = (int) ((((float)attackerAtk/25 * attackerPow*2) - ((float)defenderDef/50 * defenderStam/2)) * (1 + (Math.random() - 0.5) / 5));
		if (damage > 0){
			return damage;
		}
		return 1;
	}

	public static float smoothstep(float x){
		return x*x*(3.0f - 2.0f*x);
	}

}
