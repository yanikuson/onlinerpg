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
		super(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), f.assets, f.player, f.inventory, f.drops);

		this.f = f;
		this.items = items;
		this.offsetX = f.cameraManager.offsetX;
		this.offsetY = f.cameraManager.offsetY;

		active = true;
		depth = 0;
		selection[0] = 0;
		selection[1] = -1;
		cursorY[0] = -20;
		cursorX[0] = -20;
		cursorFacingRight[0] = false;
		createBuyScreen();

	}

	public void update(InputHandler input, float offsetX, float offsetY){

		// update the offset due to camera shifts
		this.offsetX = f.cameraManager.offsetX;
		this.offsetY = f.cameraManager.offsetY;

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
					drawNewScreen(selection[depth]);
				}
				if (input.typed[InputHandler.RIGHT]){
					input.typed[InputHandler.RIGHT] = false;

					move.play(Config.sfxVol);
					changeScreen = true;
					selection[depth]++;
					if (selection[depth] > 2){
						selection[depth] = 0; 
					}
					drawNewScreen(selection[depth]);
				}
				if (input.typed[InputHandler.ATTACK] || input.typed[InputHandler.SPACE] || input.typed[InputHandler.ENTER]){
					input.typed[InputHandler.ATTACK] = false;
					input.typed[InputHandler.SPACE] = false;
					input.typed[InputHandler.ENTER] = false;

					switch (selection[0]){
					case 0: // buy
					case 1: // sell

						if (selection[0] == 1 && f.inventory.getSize() == 0){
							// if we are trying to sell and have no items play an error sound
							cancel.play(Config.sfxVol);

						} else {

							// else we can enter the submenu
							select.play(Config.sfxVol);
							depth++;
							refreshText = true;
							cursorFacingRight[depth] = true;
							selection[depth] = 0;

							// set initial cursor pos
							if (selection[0] == 0) {
								cursorX[1] = 76;
								cursorY[1] = Config.HEIGHT - 82 - selection[1]*11;
							}
							if (selection[0] == 1) {
								cursorX[1] = 76 + 100 * (selection[depth]%2);
								cursorY[1] = Config.HEIGHT - 84 - selection[depth]/2 * 11;
							}
						}

						break;
					case 2: // quit
						hideMenu(input);
						break;
					}

				}

				// if user pressed escape, we can close the shop menu
				if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
					input.typed[InputHandler.ESCAPE] = false;
					input.typed[InputHandler.JUMP] = false;
					hideMenu(input);
					break;
				}

				// update the cursor position
				cursorY[0] = Config.HEIGHT - 20;
				cursorX[0] = 143 + selection[0] * 50;

				break;

			case 1:
				// BUY UPDATES

				if (selection[0] == 0){
					// process any key presses				
					if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
						input.typed[InputHandler.ESCAPE] = false;
						input.typed[InputHandler.JUMP] = false;

						cancel.play(Config.sfxVol);
						selection[depth] = 0;
						cursorX[depth] = - 20;
						cursorY[depth] = - 20;
						depth--;
						refreshText = true;
					}
					if (input.typed[InputHandler.UP]){
						input.typed[InputHandler.UP] = false;

						move.play(Config.sfxVol);
						selection[depth]--;
						if (selection[depth] < 0){
							selection[depth] = items.getSize()-1; 
						}
						refreshText = true;
					}
					if (input.typed[InputHandler.DOWN]){
						input.typed[InputHandler.DOWN] = false;

						move.play(Config.sfxVol);
						selection[depth]++;
						if (selection[depth] > items.getSize()-1){
							selection[depth] = 0; 
						}
						refreshText = true;
					}
					if (input.typed[InputHandler.ATTACK]){
						input.typed[InputHandler.ATTACK] = false;

						// check if player has enough money to buy the selected item
						if (f.inventory.money >= items.getItemCost(selection[depth])){

							// remove money/add to inventory
							f.drops.pickup.play(Config.sfxVol);
							f.inventory.money -= items.getItemCost(selection[depth]);
							f.inventory.addItem(items.getItem(selection[depth]));
							refreshText = true;

						} else {

							cancel.play(Config.sfxVol);
						}

					}

					// update cursor position
					cursorX[1] = 76;
					cursorY[1] = Config.HEIGHT - 82 - selection[1]*11;

					if (refreshText){
						dispose();
						createBuyScreen();
						refreshText = false;
					}



				} else {

					// SELL UPDATES
					if (input.typed[InputHandler.ATTACK]){

						if (f.inventory.getItem(selection[depth]).id > 0){
							f.drops.pickup.play(Config.sfxVol);
							f.inventory.removeIndex(selection[depth]);
							f.inventory.money += f.inventory.getItemCost(selection[depth])/2;
							if (selection[depth] >= f.inventory.getSize()){
								selection[depth]--;
							}

							refreshText = true;

						}

						if (f.inventory.getSize() <= 0){
							cursorX[depth] = - 20;
							cursorY[depth] = - 20;
							depth--;
						}




					}
					if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
						input.typed[InputHandler.ESCAPE] = false;
						input.typed[InputHandler.JUMP] = false;

						cancel.play(Config.sfxVol);
						selection[depth] = -1;
						cursorX[depth] = - 20;
						cursorY[depth] = - 20;
						depth--;
						refreshText = true;
					}

					if (input.typed[InputHandler.UP]){
						input.typed[InputHandler.UP] = false;

						move.play(Config.sfxVol);
						selection[depth]-=2;
						if (selection[depth] < 0){
							selection[depth] += f.inventory.getSize(); 
						}
						refreshText = true;
					}

					if (input.typed[InputHandler.DOWN]){
						input.typed[InputHandler.DOWN] = false;

						move.play(Config.sfxVol);
						selection[depth]+=2;
						if (selection[depth] >= f.inventory.getSize()){
							selection[depth] -= f.inventory.getSize(); 
						}
						refreshText = true;
					}

					if (input.typed[InputHandler.LEFT] || input.typed[InputHandler.RIGHT]){
						input.typed[InputHandler.LEFT] = false;
						input.typed[InputHandler.RIGHT] = false;

						move.play(Config.sfxVol);
						if (selection[depth] % 2 == 0){
							selection[depth]++; 
							if (selection[depth] >= f.inventory.getSize()) selection[depth]-= 2;
						} else {
							selection[depth]--;
						}
						refreshText = true;
					}

					// update cursor POS
					cursorX[1] = 76 + 100 * (selection[depth]%2);
					cursorY[1] = Config.HEIGHT - 84 - selection[depth]/2 * 11;

					cursorFacingRight[1] = true;

					// if we've moved the cursor, we need to refresh the item list again to update the description
					if (refreshText){
						dispose();
						createSellScreen();
						refreshText = false;
					}
				}


				break;
			}
		}
	}

	//###################################################################################################################################################################################################

	public void drawNewScreen(int selection){
		dispose();
		if (selection == 0) {
			createBuyScreen();
		} else if (selection == 1){
			createSellScreen();
		} else if (selection == 2){
			createMainShopScreen();
		}
	}
	public void createMainShopScreen() {

		// create the three header windows BUY | SELL | LEAVE
		for (int i = 0; i < 3; i ++){
			addWindow(135 + 50*i, Config.HEIGHT - 40, 40, 15);

			switch (i){
			case 0:
				windows[i].addText(138 + 50*i, Config.HEIGHT - 26, 40, 15, "Buy");
				break;
			case 1:
				windows[i].addText(138 + 50*i, Config.HEIGHT - 26, 40, 15, "Sell");
				break;
			case 2:
				windows[i].addText(138 + 50*i, Config.HEIGHT - 26, 40, 15, "Leave");
				break;
			}

		}

	}

	public void createBuyScreen() {
		createMainShopScreen();

		// description
		addWindow(85, Config.HEIGHT - 64, 238, 16);
		if (depth >= 1) {
			windows[curWindow-1].addText(89, Config.HEIGHT - 50, 238, 16, items.getItemDesc(selection[1]));
		}

		// item list
		addWindow(85, Config.HEIGHT - 206, 141, 134);
		for (int i = 0; i < items.getSize(); i++){
			windows[curWindow-1].addText(89, Config.HEIGHT - 72 - 11*i, 100, 200, items.getItemName(i));
			windows[curWindow-1].addText(199, Config.HEIGHT - 72 - 11*i, 100, 200, String.valueOf(items.getItemCost(i)));
		}

		// total $$$
		addWindow(234, Config.HEIGHT - 97, 39, 25);
		windows[curWindow-1].addText(238, Config.HEIGHT - 72, 30, 30, (" Flips:\n"+f.inventory.money));

	}

	public void createSellScreen() {
		createMainShopScreen();

		// description window
		addWindow(85, Config.HEIGHT - 64, 238, 16);
		if (depth >= 1) {
			windows[curWindow-1].addText(89, Config.HEIGHT - 50, 238, 16, f.inventory.getItemDesc(selection[1]));
		}
		// item list
		addWindow(85, Config.HEIGHT - 206, 191, 134);
		windows[curWindow-1].addText(89, Config.HEIGHT - 72, 100, 200, getItemList(true));
		windows[curWindow-1].addText(189, Config.HEIGHT - 72, 100, 200, getItemList(false));

		// total $$$
		addWindow(284, Config.HEIGHT - 97, 39, 25);
		windows[curWindow-1].addText(288, Config.HEIGHT - 72, 30, 30, (" Flips:\n"+f.inventory.money));

		// render submenu that contains the sell price of the item if we're selecting them
		if (depth >= 1){
			addWindow(284, Config.HEIGHT - 131, 39, 25);
			windows[curWindow-1].addText(288, Config.HEIGHT - 107, 30, 30, ("Value:\n"+ f.inventory.getItemCost(selection[1])/2));
		}

	}

}
