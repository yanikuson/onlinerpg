package net.alcuria.online.server;

import javax.swing.JList;

public class PlayerList extends JList<String> {

	private static final long serialVersionUID = 1L;

	public PlayerList(){
		if (ServerListener.sPlayers != null){
			String[] players = new String[ServerListener.sPlayers.size];
			for (int i = 0; i < players.length; i++){
				players[i] = ServerListener.sPlayers.get(i).name;
			}
			this.setListData(players);
		} else {
			String[] players = {"Awaiting"};
			this.setListData(players);
		}
	

	}




}
