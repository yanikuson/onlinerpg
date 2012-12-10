package net.alcuria.online.server;

import javax.swing.JList;

public class PlayerList extends JList<String> {
	
	private static final long serialVersionUID = 1L;

	public PlayerList(){
		String[] players = {"1", "2", "3" };
		this.setListData(players);
	}
	
	
	

}
