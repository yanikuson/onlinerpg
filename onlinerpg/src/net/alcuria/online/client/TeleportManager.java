package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class TeleportManager {

	public static final int MAX_TELEPORTS = 10;
	public TeleportNode[] locations;
	String line[];

	public TeleportManager(String mapname){

		locations = new TeleportNode[MAX_TELEPORTS];

		// read in the spawn file into an array of strings

		if(Gdx.files.internal("maps/" + mapname + ".tele").exists()){
			FileHandle handle = Gdx.files.internal("maps/" + mapname + ".tele");
			String fileContent = handle.readString();
			String[] lines = fileContent.split("\\r?\\n");

			// for each line, split and parse the x and y vals and add a spawn pt at that location
			for (int i = 0; i < lines.length; i++){
				if (i < MAX_TELEPORTS) {
					line = lines[i].split("\\s");
					if (line[0].equals("E")) {
						locations[i] = new TeleportNode(TeleportNode.EDGE_EAST, line[1], Integer.parseInt(line[2]), Integer.parseInt(line[3]));

					} else if (line[0].equals("W")) {
						locations[i] = new TeleportNode(TeleportNode.EDGE_WEST, line[1], Integer.parseInt(line[2]), Integer.parseInt(line[3]));

					}
					// TODO: do other cardinal directions + manual teleport

				}
			}
		}
	}

	public void update(Player p, Map m, InputHandler input){
		for (int i=0; i < MAX_TELEPORTS; i++){
			if (locations[i] != null){
				locations[i].update(p, m, input);
			}
		}
	}
}
