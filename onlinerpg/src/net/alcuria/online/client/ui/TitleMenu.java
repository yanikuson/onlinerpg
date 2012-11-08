package net.alcuria.online.client.ui;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.InputHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class TitleMenu extends Menu {

	public boolean single = false;
	public boolean multi = false;
	
	public TitleMenu(Texture background, Texture border, AssetManager assets) {
		super(background, border, assets, null, null, null);

		addWindow(155, 10, 100, 35);
		cursorFacingRight[0] = true;
		windows[curWindow-1].addText(163, Config.HEIGHT - 195, 80, 70, "Single Player\nMultiplayer\nQuit");
		selection[0] = 0;
		cursorY[0] = Config.HEIGHT - 205 + 11*-selection[0];
		cursorX[0] = 150;
		
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
			if (input.typed[InputHandler.ATTACK] || input.typed[InputHandler.SPACE]){
				input.typed[InputHandler.ATTACK] = false;
				input.typed[InputHandler.SPACE] = false;

				select.play(Config.sfxVol);
				switch (selection[0]){
				case 0: // single player
					single = true;
					
					break;
				case 1: // multiplayer
					multi = true;

					break;
				case 2: // quit
					Gdx.app.exit();
					break;
				}

			}

			// update the cursor position
			cursorY[0] = Config.HEIGHT - 205 + 11*-selection[0];
			cursorX[0] = 150;
		}
	}


}
