package net.alcuria.online.client.ui;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.ItemManager;
import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class ShopMenu extends Menu {

	Field f;					// reference to the game world
	ItemManager items; 			// list of items the shop will sell
	
	public ShopMenu(Field f, ItemManager items) {
		// TODO: fix null ptr
		super(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), f.assets, f.player, f.items, f.drops);

		this.f = f;
		this.items = items;
		
		active = true;
		depth = 0;
		selection[0] = 0;
		selection[1] = -1;
		cursorY[0] = Config.HEIGHT - 25;
		cursorX[0] = 75;
		cursorFacingRight[0] = false;
		createMainShopScreen();

	}

	public void update(InputHandler input, float offsetX, float offsetY){
		System.out.println(depth);
		// update the offset due to camera shifts
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		// update the offset of the submenu items
		for (int i=0; i < curWindow; i++){
			windows[i].update((int) this.offsetX, (int) this.offsetY);
		}

		// handle input
		if (active){

			switch (depth) {

			case 0:
				if (input.typed[InputHandler.LEFT]){
					input.typed[InputHandler.LEFT] = false;

					move.play(Config.sfxVol);
					changeScreen = true;
					selection[depth]--;
					if (selection[depth] < 0){
						selection[depth] = 2; 
					}
				}
				if (input.typed[InputHandler.RIGHT]){
					input.typed[InputHandler.RIGHT] = false;

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
					case 0: // buy


						break;
					case 1: // sell


						break;
					case 2: // quit
						hideMenu(input);
						break;
					}

				}
				
				// if user pressed escape, we can close the shop menu
				if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
					cancel.play(Config.sfxVol);
					input.typed[InputHandler.ESCAPE] = false;
					input.typed[InputHandler.JUMP] = false;
					hideMenu(input);
					break;
				}

				// update the cursor position
				cursorY[0] = Config.HEIGHT - 205 + 11*-selection[0];
				cursorX[0] = 190;
				break;

			case 1:
				// BUY OR SELL SUBSCREENS

				break;
			}
		}
	}

	public void createMainShopScreen() {
		
		addWindow(195, 10, 60, 35);
		cursorFacingRight[0] = true;
		windows[curWindow-1].addText(203, Config.HEIGHT - 195, 70, 70, "Buy\nSell\nLeave");
		selection[0] = 0;
		cursorY[0] = Config.HEIGHT - 205 + 11*-selection[0];
		cursorX[0] = 190;
		
	}
	
	public void createBuyScreen() {
		
	}
	
	public void createSellScreen() {
		
	}

}
