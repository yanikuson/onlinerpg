package net.alcuria.online.server;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.screens.Field;

public class ServerThread implements Runnable{

	ServerPanel sp;
	
	// the server thread has two jobs: 1, start up the server (in yet another thread) and 2. update the server when appropriate
	public ServerThread(Field f){
				
		if (Config.IP.equals("127.0.0.1")){
			
			GameServer.f = f;
			GameServer.start();
		}
		
		
	}

	public void run() {
		// launch the gui
		//sp = new ServerPanel();
		//sp.create();
		
		while (GameServer.running){
			GameServer.update();
			pause(16);
		}

	}


	// pauses the thread for i ms
	private void pause(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			return;
		}		
	}


}
