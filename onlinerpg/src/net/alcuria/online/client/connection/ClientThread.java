package net.alcuria.online.client.connection;

import net.alcuria.online.client.screens.Field;

public class ClientThread implements Runnable{

	private Field f;
	private byte infrequentUpdateCounter = 0;

	public ClientThread(Field f){
		this.f = f;

	}

	// client thread to fire off packets to the server
	public void run() {
		
		GameClient.f = f;
		GameClient.start();
		
		while (true){
			// every 50ms, send out critical updates
			GameClient.sendPositionUpdate(f);
			GameClient.requestPositions(f);
			pause(50);
			
			// every 2000ms we send an infrequent update
			infrequentUpdateCounter++;
			if (infrequentUpdateCounter > 40){
				
				// send full player data
				GameClient.sendFullUpdate(f);
				GameClient.requestFullUpdate(f);
				
				// request full player data
				infrequentUpdateCounter = 0;
				
			}
			
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
