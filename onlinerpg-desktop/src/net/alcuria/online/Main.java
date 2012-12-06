package net.alcuria.online;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.OnlineRPG;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "onlinerpg";
		cfg.useGL20 = true;
		
		cfg.resizable = false;
		cfg.fullscreen = false;
			
		for (int i = 1; i < 7; i++){
			cfg.width = Config.WIDTH*(i-1);
			cfg.height = Config.HEIGHT*(i-1);
			
			if (LwjglApplicationConfiguration.getDesktopDisplayMode().width < Config.WIDTH*i){
				break;
			}
			
		}
		
		cfg.width = Config.WIDTH;
		cfg.height = Config.HEIGHT;
		
		new LwjglApplication(new OnlineRPG(), cfg);
	}
}
