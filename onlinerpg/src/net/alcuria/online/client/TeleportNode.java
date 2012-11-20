package net.alcuria.online.client;

import com.badlogic.gdx.scenes.scene2d.actions.FadeOut;

public class TeleportNode {

	public static final int EDGE_NORTH = 0;
	public static final int EDGE_SOUTH = 1;
	public static final int EDGE_WEST = 2;
	public static final int EDGE_EAST = 3;

	String destination;					// name of the destination map (without .cmf)
	int srcX, srcY, destX, destY;		// source and destination teleport tiles
	int edge;
	boolean startedFade = false;

	// constructor for cardinal teleport nodes
	public TeleportNode(int edge, String destination, int destX, int destY){
		this.srcX = -1;
		this.srcY = -1;
		this.edge = edge;
		this.destination = destination;
		this.destX = destX;
		this.destY = destY;
	}

	// constructor for absolute teleport nodes
	public TeleportNode(int srcX, int srcY, String destination, int destX, int destY){
		this.edge = -1;
		this.srcX = srcX;
		this.srcY = srcY;
		this.destination = destination;
		this.destX = destX;
		this.destY = destY;
	}

	public void update(Player p, Map m, InputHandler input){

		// check if it's a cardinal-activated or key-activated teleport
		switch (edge) {
		case EDGE_NORTH:
			break;

		case EDGE_SOUTH:
			break;

		case EDGE_WEST:
			if (((int)p.bounds.x + p.bounds.width - 4)/Config.TILE_WIDTH < 0){

				if (!startedFade){
					startedFade = true;
					Transition.fadeOut(1.0f);					
				}
				if (Transition.finished) { 
					changeMap(m, p);
					Transition.fadeIn(1.0f);
					startedFade = false;
				}

			}
			break;

		case EDGE_EAST:
			if (((int)p.bounds.x + 4)/Config.TILE_WIDTH > m.width){
				changeMap(m, p);

			}
			break;

		default:
			// key-activated -- check if player is overlapping it and pressing up
			if (((int)p.bounds.x + 10)/Config.TILE_WIDTH == srcX && ((int)p.bounds.y + 4)/Config.TILE_WIDTH == srcY && input.typed[InputHandler.UP]){
				input.typed[InputHandler.UP] = false;
				changeMap(m, p);

			}
			break;
		}


	}

	public void changeMap(Map m, Player p){

		if (m.spawner != null) m.spawner.removeAllMonsters();
		m.create(destination);
		p.bounds.x = destX * 16;
		p.bounds.y = destY * 16;

	}

}
