package net.alcuria.online.client;

import net.alcuria.online.client.screens.Field;

public class SaveThread implements Runnable{

	private Field f;
	private int slot;

	public SaveThread(Field f, int slot){
		this.f = f;
		this.slot = slot;
	}

	public void run() {

		while (true){
			SaveHandler.saveFlags(slot);
			SaveHandler.saveItems(slot, f.inventory);
			SaveHandler.savePlayer(f.player, slot);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}


}
