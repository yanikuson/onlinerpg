package net.alcuria.online.client.connection;

import net.alcuria.online.client.screens.Field;

public class ClientThread implements Runnable{

	private Field f;
	private int slot;

	public ClientThread(Field f, int slot){
		this.f = f;
		this.slot = slot;
	}

	public void run() {
		
		GameClient.f = f;
		GameClient.start();
		
		while (true){
			GameClient.sendPositionUpdate(f);
			pause(50);
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
