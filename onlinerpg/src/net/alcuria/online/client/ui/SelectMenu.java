package net.alcuria.online.client.ui;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.SaveHandler;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class SelectMenu extends Menu {
	public String[] names;
	
	public int slot = -1;

	public SelectMenu(Texture background, Texture border, AssetManager assets, String[] names, int[] levels, int[] dungeons, String[] times) {
		super(background, border, assets, null, null, null);

		this.names = names;
		
		cursorFacingRight[0] = true;
		int file = 1;
		for (int i = 2; i >= 0; i--){
			addWindow(107, 40 + i*65, 200, 50);

			if (SaveHandler.fileExists(file)){
				windows[curWindow-1].addText(110, 88 + i * 65, 160, 50, names[file-1]);
				windows[curWindow-1].addText(180, 88 + i * 65, 160, 50, ("Level: " + levels[file-1]));
				windows[curWindow-1].addText(180, 72 + i * 65, 160, 50, "Dungeons Cleared: 1");
				windows[curWindow-1].addText(180, 56 + i * 65, 160, 50, "Time Played: 00:00");
			} else {
				windows[curWindow-1].addText(182, 73 + i * 65, 160, 50, "New Game");
			}
			file++;
		}

		selection[0] = 0;
		cursorY[0] = Config.HEIGHT - 33 + 65*-selection[0];
		cursorX[0] = 95;

	}

	public void update(InputHandler input, float offsetX, float offsetY){
		// update the offset due to camera shifts
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		// update the offset of the submenu items
		for (int i=0; i < curWindow; i++){
			windows[i].update((int) offsetX, (int) offsetY);
		}

		// handle input
		if (active){

			if (input.typed[InputHandler.UP]){
				input.typed[InputHandler.UP] = false;

				move.play(Config.sfxVol);
				changeScreen = true;
				selection[depth]--;
				if (selection[depth] < 0){
					selection[depth] = 2; 
				}
			}
			if (input.typed[InputHandler.DOWN]){
				input.typed[InputHandler.DOWN] = false;

				move.play(Config.sfxVol);
				changeScreen = true;
				selection[depth]++;
				if (selection[depth] > 2){
					selection[depth] = 0; 
				}
			}
			if (input.typed[InputHandler.ESCAPE]){
				input.typed[InputHandler.ESCAPE] = false;

				cancel.play(Config.sfxVol);
				slot = 0;
			}
			if (input.typed[InputHandler.ATTACK] || input.typed[InputHandler.SPACE]){
				input.typed[InputHandler.ATTACK] = false;
				input.typed[InputHandler.SPACE] = false;

				switch (selection[0]){
				case 0: // slot1
					slot = 1;

					break;
				case 1: // options
					slot = 2;

					break;
				case 2: // quit
					slot = 3;
					break;
				}

			}

			// update the cursor position
			cursorY[0] = Config.HEIGHT - 33 + 65*-selection[0];
			cursorX[0] = 95;
		}
	}


}
