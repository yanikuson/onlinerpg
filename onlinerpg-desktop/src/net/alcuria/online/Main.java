package net.alcuria.online;

import net.alcuria.online.client.OnlineRPG;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "onlinerpg";
		cfg.useGL20 = true;
		cfg.resizable = true;
		cfg.width = 416*3;
		cfg.height = 240*3;
		new LwjglApplication(new OnlineRPG(), cfg);
	}
}
