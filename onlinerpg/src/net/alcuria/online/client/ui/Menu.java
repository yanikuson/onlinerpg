package net.alcuria.online.client.ui;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.DropManager;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.Item;
import net.alcuria.online.client.ItemManager;
import net.alcuria.online.client.Player;
import net.alcuria.online.client.SaveHandler;
import net.alcuria.online.client.StatusEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Menu {

	public Sound move, open, cancel, select, addstat, equip;

	public Texture background;
	public Texture border;
	public TextureRegion downArrow;
	public TextureRegion rightArrow;
	public TextureRegion leftTriangle;
	public TextureRegion rightTriangle;
	public TextureRegion dimmer;
	public AssetManager assets;

	public BitmapFont font;							// the font to be displayed everywhere.
	public Player p;								// a pointer to our player, for showing stats
	public ItemManager inventory;					// a pointer to the inventory, for the item screens
	public Item swapItem;							// a pointer to save off removed gear while new gear gets equipped
	public DropManager dropManager;					// a pointer to dropmanager to toss items

	public Window[] windows;						// a menu has many windows. a window has many text elements.
	public int curWindow = 0;						// current window counter, for adding additional windows and tracking it

	public int depth = 0;							// depth = number of times player pressed confirm.
	public int[] selection = {0,0,0,0,0};			// selection = similar to depth but for movement across menu selections. depth index for multiple cursor display and better backtracking
	public int[] cursorX = {0,0,0,0,0};
	public int[] cursorY = {0,0,0,0,0};
	public final static int MAX_WINDOWS = 20;

	public boolean[] cursorFacingRight = {false, false, false, false, false};		// used to determine which region to draw
	public boolean active = false;													// is the entire menu active?
	public boolean changeScreen = false;											// if true, the appropriate sub-windows will be re-drawn on our update() function
	float offsetX, offsetY;															// offset from the camera

	public boolean drawDimmer = false;
	public int oldHP = 0;
	public String itemList;
	public boolean refreshText = false;
	public int[] selectionStats = {0, 0, 0, 0, 0, 0, 0};
	public int[] calculatedStats = {0, 0, 0, 0, 0, 0, 0};

	public int saveSlot;					// the slot to which we are saving

	public Menu(Texture background, Texture border, AssetManager assets, Player p, ItemManager inventory, DropManager dropManager){
		this.background = background;
		this.border = border;
		this.assets = assets;
		this.p = p;
		this.inventory = inventory;
		this.dropManager = dropManager;

		font = new BitmapFont(Gdx.files.internal("fonts/message.fnt"), false);
		rightArrow = new TextureRegion(border, 16, 0, 13, 10);
		downArrow = new TextureRegion(border, 32, 0, 10, 13);
		leftTriangle = new TextureRegion(border, 43, 0, 5, 7);
		rightTriangle = new TextureRegion(border, 59, 0, 5, 7);
		dimmer = new TextureRegion(background, 0, 0, 1, 1);
		windows = new Window[MAX_WINDOWS];

		move = assets.get("sounds/cursor_move.wav", Sound.class);
		open = assets.get("sounds/cursor_open.wav", Sound.class);
		cancel = assets.get("sounds/cursor_cancel.wav", Sound.class);
		select = assets.get("sounds/cursor_select.wav", Sound.class);
		addstat = assets.get("sounds/addstat.wav", Sound.class);
		equip = assets.get("sounds/equip.wav", Sound.class);

	}

	public void addWindow(int x, int y, int width, int height){
		addWindow(x, y, (int) offsetX, (int) offsetY, width, height);

	}
	public void addWindow(int x, int y, int offsetX, int offsetY, int width, int height){

		if (curWindow < MAX_WINDOWS){
			windows[curWindow] = new Window((int)(x+offsetX), (int)(y+offsetY), width, height, (int)offsetX, (int)offsetY, background, border);
			curWindow++;
		}

	}

	public void update(InputHandler input, float offsetX, float offsetY, Screen g){

		// update the offset due to camera shifts
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		// update the offset of the submenu items
		for (int i=0; i < curWindow; i++){
			windows[i].update((int) offsetX, (int) offsetY);
		}

		// handle input
		if (active){

			// different depths require different processes
			switch (depth){

			case 0: /* top menu!*/

				// process any key presses				
				if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
					// remove the menu and resume gameplay
					hideMenu(input);
				}
				if (input.typed[InputHandler.LEFT]){
					input.typed[InputHandler.LEFT] = false;

					move.play(Config.sfxVol);
					changeScreen = true;
					selection[depth]--;
					if (selection[depth] < 0){
						selection[depth] = 5; 
					}
				}
				if (input.typed[InputHandler.RIGHT]){
					input.typed[InputHandler.RIGHT] = false;

					move.play(Config.sfxVol);
					changeScreen = true;
					selection[depth]++;
					if (selection[depth] > 5){
						selection[depth] = 0; 
					}
				}
				if (input.typed[InputHandler.ATTACK]){
					input.typed[InputHandler.ATTACK] = false;

					select.play(Config.sfxVol);
					switch (selection[0]){
					case 0: // stats
						depth++;
						cursorX[1] = 134;
						cursorY[1] = Config.HEIGHT - 141;
						cursorFacingRight[1] = true;
						selection[1] = 0;
						break;
					case 1: // items
						depth++;
						selection[1] = 0;
						cursorX[1] = 76 + 100 * (selection[1]%2);
						cursorY[1] = Config.HEIGHT - 84 - selection[1]/2 * 11;
						cursorFacingRight[1] = true;
						dispose();
						createItemScreen();
						break;
					case 2: // selected equip
						depth++;
						selection[1] = 0;
						cursorX[1] = 76;
						cursorY[1] = Config.HEIGHT - 82;
						cursorFacingRight[1] = true;
						dispose();
						createEquipScreen();
						break;
					}

				}

				// update the cursor position
				cursorY[0] = Config.HEIGHT - 25;
				cursorX[0] = 75 + 50*selection[0];
				cursorFacingRight[0] = false;

				// see if we need to create a new window
				if (changeScreen){
					drawNewScreen();
					changeScreen = false;
				}

				break;

			case 1: /* a sub-menu! */

				// determine which update to call based on the position of selection[0]
				switch (selection[0]){
				case 0:
					updateStatSubmenu(input);
					break;
				case 1:
					updateItemSubmenu(input);
					break;
				case 2:
					updateEquipSubmenu(input);
					break;
				}

				break;

			case 2: /* yo dawg, I heard u like submenus...? */

				// determine which update to call based on the position of selection[0]
				switch (selection[0]){
				case 1:
					// handle item submenu submenu eventually...
					updateItemUseSubmenu(input);
					break;
				case 2:
					updateEquipChangeGearSubmenu(input);
					break;
				}

			}

		} else {

			// check if the menu is INACTIVE and we press ESCAPE. if so we need to CREATE OUR MENU~~~!
			if (input.typed[InputHandler.ESCAPE]){
				input.typed[InputHandler.ESCAPE] = false;
				open.play(Config.sfxVol);
				// activate our menu
				active = true;
				depth = 0;
				selection[0] = 0;
				selection[1] = -1;
				cursorY[0] = Config.HEIGHT - 25;
				cursorX[0] = 75;
				cursorFacingRight[0] = false;

				// render our stat screen
				createStatScreen();
				SaveHandler.savePlayer(p, saveSlot);
				SaveHandler.saveItems(saveSlot, inventory);
				SaveHandler.saveFlags(saveSlot);
			}

		}

	}

	protected void hideMenu(InputHandler input) {
		SaveHandler.savePlayer(p, saveSlot);
		SaveHandler.saveItems(saveSlot, inventory);
		SaveHandler.saveFlags(saveSlot);
		cancel.play(Config.sfxVol);
		input.typed[InputHandler.JUMP] = false;
		input.typed[InputHandler.ESCAPE] = false;
		active = false;
		dispose();
	}

	// user is IN item menu and selecting whether to USE or TOSS an item
	public void updateItemUseSubmenu(InputHandler input) {

		// handle the keypresses
		if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
			input.typed[InputHandler.ESCAPE] = false;
			input.typed[InputHandler.JUMP] = false;

			cancel.play(Config.sfxVol);
			depth--;
			refreshText = true;
		}

		if (input.typed[InputHandler.UP]){
			input.typed[InputHandler.UP] = false;

			move.play(Config.sfxVol);
			selection[depth]--;
			if (selection[depth] < 0){
				selection[depth] = 1; 
			}
			refreshText = true;
		}
		if (input.typed[InputHandler.DOWN]){
			input.typed[InputHandler.DOWN] = false;

			move.play(Config.sfxVol);
			selection[depth]++;
			if (selection[depth] > 1){
				selection[depth] = 0; 
			}
			refreshText = true;
		}
		if (input.typed[InputHandler.ATTACK]){
			input.typed[InputHandler.ATTACK] = false;
			if (selection[depth] == 0){
				// if selection type = consumable, do the effect -- close menu
				if (inventory.getItem(selection[1]).type == Item.TYPE_CONSUMABLE){

					// do the effect
					switch (inventory.getItem(selection[1]).id) {
					case Item.ID_POTION:
						p.effects.add(StatusEffects.HEAL, 50, 1);
						break;
					case Item.ID_SPEED_PILL:
						p.effects.add(StatusEffects.SPEED, 20, 60);
						break;
					}

					// remove from inventory and close the menu
					inventory.removeIndex(selection[1]);
					hideMenu(input);

					// update the player's pose
					p.animation.itemPose = true;

				} else {
					cancel.play(Config.sfxVol);
				}
			} else if (selection[depth] == 1){
				// call dropmanager to add a new drop. the item passed in is the selected item.
				dropManager.toss(inventory.removeIndex(selection[1]), p);
				hideMenu(input);

			}

		}

		if (refreshText){
			dispose();
			createItemScreen();
			refreshText = false;
		}

		// update cursor position
		cursorX[2] = 273;
		cursorY[2] = Config.HEIGHT - 119 - selection[2]*11;

	}

	// this time... the user is IN equip and is also selecting an EQUIP to change!
	public void updateEquipChangeGearSubmenu(InputHandler input){

		// process any key presses				
		if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
			input.typed[InputHandler.ESCAPE] = false;
			input.typed[InputHandler.JUMP] = false;


			cancel.play(Config.sfxVol);
			depth--;
			refreshText = true;
		}
		if (input.typed[InputHandler.UP]){
			input.typed[InputHandler.UP] = false;

			move.play(Config.sfxVol);
			selection[depth]--;
			if (selection[depth] < 0){
				selection[depth] = 8; 
			}
			refreshText = true;
		}
		if (input.typed[InputHandler.DOWN]){
			input.typed[InputHandler.DOWN] = false;

			move.play(Config.sfxVol);
			selection[depth]++;
			if (selection[depth] > 8){
				selection[depth] = 0; 
			}
			refreshText = true;
		}
		if (input.typed[InputHandler.ATTACK]){
			input.typed[InputHandler.ATTACK] = false;

			// swap the gear...

			// first, save a copy of the gear to remove
			if (selection[1] == 0){
				swapItem = p.weapon;
				p.weapon = getEquipItem(selection[1]+2, selection[2]);
			} else if (selection[1] == 1){
				swapItem = p.armor;
				p.armor = getEquipItem(selection[1]+2, selection[2]);
			} else if (selection[1] == 2){
				swapItem = p.helmet;
				p.helmet = getEquipItem(selection[1]+2, selection[2]);
			} else if (selection[1] == 3){
				swapItem = p.accessory;
				p.accessory = getEquipItem(selection[1]+2, selection[2]);
			}

			// add it back to the inventory iff it's not blank
			if (swapItem.id != Item.ID_BLANK){
				inventory.addItem(swapItem);
			}

			// update stats
			p.atk = calculatedStats[0];
			p.def = calculatedStats[1];
			p.matk = calculatedStats[2];
			p.mdef = calculatedStats[3];
			p.knockback = calculatedStats[4];
			p.walkSpeed = calculatedStats[5];
			p.jumpPower = calculatedStats[6];

			// change visual effects
			p.resetVisualEquips();
			
			equip.play(Config.sfxVol);
			refreshText = true;
			depth--;

		}

		if (refreshText){
			dispose();
			createEquipScreen();
			refreshText = false;
		}

		// update cursor position
		cursorX[2] = 76;
		cursorY[2] = Config.HEIGHT - 137 - selection[2]*11;


	}

	// this is to process key input if the cursor is IN the EQUIP submenu
	public void updateEquipSubmenu(InputHandler input){

		// process any key presses				
		if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
			input.typed[InputHandler.ESCAPE] = false;
			input.typed[InputHandler.JUMP] = false;

			cancel.play(Config.sfxVol);
			depth--;
			refreshText = true;
		}
		if (input.typed[InputHandler.UP]){
			input.typed[InputHandler.UP] = false;

			move.play(Config.sfxVol);
			selection[depth]--;
			if (selection[depth] < 0){
				selection[depth] = 3; 
			}
			refreshText = true;
		}
		if (input.typed[InputHandler.DOWN]){
			input.typed[InputHandler.DOWN] = false;

			move.play(Config.sfxVol);
			selection[depth]++;
			if (selection[depth] > 3){
				selection[depth] = 0; 
			}
			refreshText = true;
		}
		if (input.typed[InputHandler.ATTACK]){
			input.typed[InputHandler.ATTACK] = false;

			// go into the submenu to select a new gear
			select.play(Config.sfxVol);
			depth++;
			refreshText = true;
			cursorFacingRight[depth] = true;
			selection[depth] = 0;
			cursorX[depth] = 76;
			cursorY[depth] = Config.HEIGHT - 137 - selection[depth]*11;

		}

		if (refreshText){
			dispose();
			createEquipScreen();
			refreshText = false;
		}

		// update cursor position
		cursorX[1] = 76;
		cursorY[1] = Config.HEIGHT - 82 - selection[1]*11;

	}

	public void updateItemSubmenu(InputHandler input){

		if (input.typed[InputHandler.ATTACK]){
			input.typed[InputHandler.ATTACK] = false;


			// go into the submenu to USE/TOSS iff the item actually exists
			if (inventory.getSize() > selection[depth]){
				select.play(Config.sfxVol);
				depth++;
				refreshText = true;
				cursorFacingRight[depth] = true;
				selection[depth] = 0;
				// update cursor position
				cursorX[depth] = 273;
				cursorY[depth] = Config.HEIGHT - 119 - selection[2]*11;
			} else {
				cancel.play(Config.sfxVol);
			}

		}
		if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
			input.typed[InputHandler.ESCAPE] = false;
			input.typed[InputHandler.JUMP] = false;

			cancel.play(Config.sfxVol);
			depth--;
			selection[1] = -1;
			refreshText = true;
		}
		
		// can only update cursor pos if we have enough items
		if (inventory.getSize() > 1) {
			if (input.typed[InputHandler.UP]){
				input.typed[InputHandler.UP] = false;


				move.play(Config.sfxVol);
				selection[depth]-=2;
				if (selection[depth] < 0){
					selection[depth] += inventory.getSize(); 
				}
				refreshText = true;

			}

			if (input.typed[InputHandler.DOWN]){
				input.typed[InputHandler.DOWN] = false;

				move.play(Config.sfxVol);
				selection[depth]+=2;
				if (selection[depth] >= inventory.getSize()){
					selection[depth] -= inventory.getSize(); 
				}
				refreshText = true;
			}

			if (input.typed[InputHandler.LEFT] || input.typed[InputHandler.RIGHT]){
				input.typed[InputHandler.LEFT] = false;
				input.typed[InputHandler.RIGHT] = false;

				move.play(Config.sfxVol);
				if (selection[depth] % 2 == 0){
					selection[depth]++; 
					if (selection[depth] >= inventory.getSize()) selection[depth]-= 2;
				} else {
					selection[depth]--;
				}
				refreshText = true;
			}
		}

		// update cursor POS
		cursorX[1] = 76 + 100 * (selection[1]%2);
		cursorY[1] = Config.HEIGHT - 84 - selection[1]/2 * 11;

		cursorFacingRight[1] = true;

		// if we've moved the cursor, we need to refresh the item list again to update the description
		if (refreshText){
			dispose();
			createItemScreen();
			refreshText = false;
		}

	}

	// this is to process key input if the cursor is IN the STAT submenu
	public void updateStatSubmenu(InputHandler input){

		// process any key presses				
		if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
			input.typed[InputHandler.ESCAPE] = false;
			input.typed[InputHandler.JUMP] = false;

			cancel.play(Config.sfxVol);
			depth--;
		}
		if (input.typed[InputHandler.UP]){
			input.typed[InputHandler.UP] = false;

			move.play(Config.sfxVol);
			selection[depth]--;
			if (selection[depth] < 0){
				selection[depth] = 2; 
			}
		}
		if (input.typed[InputHandler.DOWN]){
			input.typed[InputHandler.DOWN] = false;

			move.play(Config.sfxVol);
			selection[depth]++;
			if (selection[depth] > 2){
				selection[depth] = 0; 
			}
		}
		if (input.typed[InputHandler.ATTACK]){
			input.typed[InputHandler.ATTACK] = false;

			// check to see if we can add a stat point to the selected stat
			if (p.allocateStatPoint(selection[depth])){
				addstat.play(Config.sfxVol);
			} else {
				cancel.play(Config.sfxVol);
			}
			dispose();
			createStatScreen();
		}

		// update cursor position
		cursorX[1] = 134;
		cursorY[1] = Config.HEIGHT - 141 - selection[1]*10;

		cursorFacingRight[0] = false;
	}

	public void render(SpriteBatch batch){

		// render all of the windows
		if (active){

			// draw the semi-transparent dimmer
			if (drawDimmer){
				batch.flush();
				batch.setColor(1, 1, 1, 0.5f);
				batch.draw(dimmer, offsetX, offsetY, Config.WIDTH, Config.HEIGHT);
				batch.flush();
				batch.setColor(1, 1, 1, 1);
			}

			// draw menu elements
			for (int i=0; i < curWindow; i++){
				windows[i].render(batch, font);
			}

			// draw our cursor. position was updated in the ... UPDATE METHOD :D
			for (int i = 0; i <= depth; i++){

				// change to a faded arrow if we can!
				if (i < depth){
					batch.flush();
					batch.setColor(1, 1, 1, 0.5f);
				}

				// draw the arrow
				if(cursorFacingRight[i]){
					batch.draw(rightArrow, cursorX[i]+offsetX, cursorY[i]+offsetY);
				} else {
					batch.draw(downArrow, cursorX[i]+offsetX, cursorY[i]+offsetY);
				}

				// aanndd... change our cursor back
				if (i < depth){
					batch.flush();
					batch.setColor(1, 1, 1, 1);
				}
			}

		}

	}

	public void dispose(){

		// deletes all window references
		for (int i=0; i<curWindow; i++){
			windows[i].dispose();
			windows[i] = null;
		}
		curWindow = 0;

	}

	// -------------------------------------------------------
	//			predefined menu screen create methods
	// -------------------------------------------------------

	public void drawNewScreen(){

		// remove old screen
		dispose();

		// determine which new screen to draw
		switch (selection[0]){
		case 0: /* stats */
			createStatScreen();
			break;

		case 1: /* items */
			createItemScreen();
			break;

		case 2: /* equip */
			createEquipScreen();
			break;

		case 3: /* skills */
			createSkillsScreen();
			break;

		case 4: /* keys */
			createKeysScreen();
			break;

		case 5: /* options */
			createOptionScreen();
			break;
		}
	}

	public void createHeader(){

		// create the six header windows: STAT | ITEM | EQP | SKILL | HOTKEY | OPTIONS
		for (int i = 0; i < 6; i ++){
			addWindow(60 + 50*i, Config.HEIGHT - 40, 40, 15);

			switch (i){
			case 0:
				windows[i].addText(65 + 50*i, Config.HEIGHT - 26, 40, 15, "Stats");
				break;
			case 1:
				windows[i].addText(65 + 50*i, Config.HEIGHT - 26, 40, 15, "Items");
				break;
			case 2:
				windows[i].addText(65 + 50*i, Config.HEIGHT - 26, 40, 15, "Equip");
				break;
			case 3:
				windows[i].addText(65 + 50*i, Config.HEIGHT - 26, 40, 15, "Skills");
				break;
			case 4:
				windows[i].addText(65 + 50*i, Config.HEIGHT - 26, 40, 15, "Keys");
				break;
			case 5:
				windows[i].addText(65 + 50*i, Config.HEIGHT - 26, 40, 15, "Option");
				break;
			default:

			}

		}
	}

	public void createStatScreen(){
		createHeader();

		// top left status guy
		addWindow(85, Config.HEIGHT - 98, 50, 50);

		// level, hp, etc
		addWindow(144, Config.HEIGHT - 118, 80, 70);
		windows[curWindow-1].addText(148, Config.HEIGHT - 48, 80, 70, "Lv:\nHP:\nEP:\n\nNext Level:\n   " + (Config.getNextLvl(p.lvl) - p.curEXP) + " EXP");
		windows[curWindow-1].addText(173, Config.HEIGHT - 48, 80, 70, "" + p.lvl + "\n" + p.HP + "/" + p.maxHP + "\n" + p.EP + "/" + p.maxEP);

		// core stats (str etc)
		addWindow(144, Config.HEIGHT - 167, 80, 40);
		windows[curWindow-1].addText(148, Config.HEIGHT - 129, 80, 70, "Power:\nStamina:\nWisdom:");
		windows[curWindow-1].addText(210, Config.HEIGHT - 129, 80, 70, "" + p.power + "\n" + p.stamina + "\n" + p.wisdom);


		// sub stats (attack, def, etc)
		addWindow(233, Config.HEIGHT - 140, 90, 92);
		windows[curWindow-1].addText(237, Config.HEIGHT - 48, 90, 70, "Attack:\nDefense:\nM.Attack:\nM.Defense:\n\nKnockback:\nSpeed:\nJump:");
		windows[curWindow-1].addText(303, Config.HEIGHT - 48, 90, 70, "" + p.atk + "\n" + p.def + "\n" + p.matk + "\n" + p.mdef + "\n\n" + (int)p.knockback + "\n" + (int)p.walkSpeed + "\n" + (int)p.jumpPower);

		// gold held
		addWindow(233, Config.HEIGHT - 167, 90, 18);
		windows[curWindow-1].addText(237, Config.HEIGHT - 152, 90, 11, "Flips: " + inventory.money);

		// points remaining
		addWindow(85, Config.HEIGHT - 167, 50, 30);
		windows[curWindow-1].addText(90, Config.HEIGHT - 139, 90, 30, "Stat Pts:\n    " + p.statPts);

	}

	public void createItemScreen(){
		createHeader();

		// description window
		addWindow(85, Config.HEIGHT - 64, 238, 16);
		if (depth >= 1) {
			windows[curWindow-1].addText(89, Config.HEIGHT - 50, 238, 16, inventory.getItemDesc(selection[1]));
		}
		// item list
		addWindow(85, Config.HEIGHT - 206, 191, 134);
		windows[curWindow-1].addText(89, Config.HEIGHT - 72, 100, 200, getItemList(true));
		windows[curWindow-1].addText(189, Config.HEIGHT - 72, 100, 200, getItemList(false));

		// sort order
		addWindow(284, Config.HEIGHT - 97, 39, 25);
		windows[curWindow-1].addText(288, Config.HEIGHT - 72, 30, 30, " Sort:\n NEW");

		// render submenu if depth = 2 that contains the USE / TOSS selection
		if (depth >= 2){
			addWindow(284, Config.HEIGHT - 131, 34, 25);
			windows[curWindow-1].addText(288, Config.HEIGHT - 107, 30, 30, "Use\nToss");
		}


	}

	public void createEquipScreen(){
		createHeader();

		// description window
		addWindow(85, Config.HEIGHT - 64, 238, 16);
		if (depth == 1){

			// if we are only one screen in, we return the description of the equipped gear
			if (selection[1] == 0) windows[curWindow-1].addText(89, Config.HEIGHT - 50, 238, 16, p.weapon.description);
			if (selection[1] == 1) windows[curWindow-1].addText(89, Config.HEIGHT - 50, 238, 16, p.armor.description);
			if (selection[1] == 2) windows[curWindow-1].addText(89, Config.HEIGHT - 50, 238, 16, p.helmet.description);
			if (selection[1] == 3) windows[curWindow-1].addText(89, Config.HEIGHT - 50, 238, 16, p.accessory.description);

		} else if (depth == 2){

			// if we are deeper, we go to the itemlist to get our description -- we pass in which TYPE we are highlighting and which CURSOR position as well
			windows[curWindow-1].addText(89, Config.HEIGHT - 50, 238, 16, getEquipDescription(selection[1]+2, selection[2]));
		}

		// current equipment pane:
		addWindow(85, Config.HEIGHT - 116, 140, 44);
		windows[curWindow-1].addText(89, Config.HEIGHT - 71, 100, 60, "Weapon:\nArmor:\nHelm:\nOther:");
		windows[curWindow-1].addText(135, Config.HEIGHT - 71, 100, 60, p.weapon.name + "\n" + p.armor.name + "\n" + p.helmet.name + "\n" + p.accessory.name);

		// sub stats (attack, def, etc)
		addWindow(233, Config.HEIGHT - 167, 90, 95);
		windows[curWindow-1].addText(237, Config.HEIGHT - 71, 90, 70, "Atk:\nDef:\nM.Atk:\nM.Def:\n\nKB:\nSpd:\nJmp:");
		windows[curWindow-1].addText(278, Config.HEIGHT - 71, 90, 70, "" + p.atk + "\n" + p.def + "\n" + p.matk + "\n" + p.mdef + "\n\n" + (int)p.knockback + "\n" + (int)p.walkSpeed + "\n" + (int)p.jumpPower);
		if (depth > 1){

			// calculate stats
			getEquipStats(selection[1]+2, selection[2]);

			// depending on the selected equip type we are changing, we calculate new stats slightly differently
			if (selection[1]+2 == Item.TYPE_WEAPON){
				calculatedStats[0] = p.atk - p.weapon.atk + selectionStats[0];
				calculatedStats[1] = p.def - p.weapon.def + selectionStats[1];
				calculatedStats[2] = p.matk - p.weapon.matk + selectionStats[2];
				calculatedStats[3] = p.mdef - p.weapon.mdef + selectionStats[3];
				calculatedStats[4] = (int) (p.knockback - p.weapon.kb + selectionStats[4]);
				calculatedStats[5] = (int) (p.walkSpeed - p.weapon.speed + selectionStats[5]);
				calculatedStats[6] = (int) (p.jumpPower - p.weapon.jump + selectionStats[6]);
			} else if (selection[1]+2 == Item.TYPE_ARMOR){
				calculatedStats[0] = p.atk - p.armor.atk + selectionStats[0];
				calculatedStats[1] = p.def - p.armor.def + selectionStats[1];
				calculatedStats[2] = p.matk - p.armor.matk + selectionStats[2];
				calculatedStats[3] = p.mdef - p.armor.mdef + selectionStats[3];
				calculatedStats[4] = (int) (p.knockback - p.armor.kb + selectionStats[4]);
				calculatedStats[5] = (int) (p.walkSpeed - p.armor.speed + selectionStats[5]);
				calculatedStats[6] = (int) (p.jumpPower - p.armor.jump + selectionStats[6]);
			} else if (selection[1]+2 == Item.TYPE_HELM){
				calculatedStats[0] = p.atk - p.helmet.atk + selectionStats[0];
				calculatedStats[1] = p.def - p.helmet.def + selectionStats[1];
				calculatedStats[2] = p.matk - p.helmet.matk + selectionStats[2];
				calculatedStats[3] = p.mdef - p.helmet.mdef + selectionStats[3];
				calculatedStats[4] = (int) (p.knockback - p.helmet.kb + selectionStats[4]);
				calculatedStats[5] = (int) (p.walkSpeed - p.helmet.speed + selectionStats[5]);
				calculatedStats[6] = (int) (p.jumpPower - p.helmet.jump + selectionStats[6]);
			} else if (selection[1]+2 == Item.TYPE_OTHER){
				calculatedStats[0] = p.atk - p.accessory.atk + selectionStats[0];
				calculatedStats[1] = p.def - p.accessory.def + selectionStats[1];
				calculatedStats[2] = p.matk - p.accessory.matk + selectionStats[2];
				calculatedStats[3] = p.mdef - p.accessory.mdef + selectionStats[3];
				calculatedStats[4] = (int) (p.knockback - p.accessory.kb + selectionStats[4]);
				calculatedStats[5] = (int) (p.walkSpeed - p.accessory.speed + selectionStats[5]);
				calculatedStats[6] = (int) (p.jumpPower - p.accessory.jump + selectionStats[6]);
			}
			// display them!
			windows[curWindow-1].addText(303, Config.HEIGHT - 71, 90, 70, "" + calculatedStats[0] + "\n" + calculatedStats[1] + "\n" + calculatedStats[2] + "\n" + calculatedStats[3] + "\n\n" + calculatedStats[4] + "\n" + calculatedStats[5] + "\n" + calculatedStats[6]);
		}
		// available gear window -- dynamic ishly selects the correct text to display depending on what's highlighted
		if (depth > 0) {
			addWindow(85, Config.HEIGHT - 225, 140, 101);
			windows[curWindow-1].addText(88, Config.HEIGHT - 125, 90, 70, getEquipList(selection[1]+2));
		}
	}

	public void createSkillsScreen(){
		createHeader();
	}

	public void createKeysScreen(){
		createHeader();
	}

	public void createOptionScreen(){
		createHeader();
	}

	public String getItemList(boolean leftSide){
		itemList = "";
		if (leftSide){
			for (int i = 0; i < inventory.getSize(); i = i+2){
				itemList += inventory.getItemName(i) + "\n";
			}
		} else {
			for (int i = 1; i < inventory.getSize(); i = i+2){
				itemList += inventory.getItemName(i) + "\n";
			}
		}
		return itemList;
	}

	public String getEquipList(int type){
		itemList = "";
		for (int i = 0; i < inventory.getSize(); i++){
			if (inventory.getItemType(i) == type){
				itemList += inventory.getItemName(i) + "\n";
			}
		}
		return itemList;
	}

	public String getEquipDescription(int type, int cursorPos){
		int cursorIndex = 0;

		// iterate through the whole inventory
		for (int i = 0; i < inventory.getSize(); i++){

			// if we encounter an item type that matches the passed-in type, we check if this is the cursor pos we want
			if (inventory.getItemType(i) == type){
				if (cursorIndex == cursorPos){
					return inventory.getItemDesc(i);
				} else {
					cursorIndex++;
				}
			}
		}
		return "";
	}

	// gets the stat values of a highlighted equip
	public void getEquipStats(int type, int cursorPos){
		int cursorIndex = 0;

		// iterate through the whole inventory
		for (int i = 0; i < inventory.getSize(); i++){

			// if we encounter an item type that matches the passed-in type, we check if this is the cursor pos we want
			if (inventory.getItemType(i) == type){
				if (cursorIndex == cursorPos){
					selectionStats = inventory.getItemStats(i);
					return;
				} else {
					cursorIndex++;
				}
			}
		}

		// reset the values if nothing was found
		for (int i=0; i<selectionStats.length; i++){
			selectionStats[i] = 0;
		}
	}


	// returns an item object of whatever is highlighted
	public Item getEquipItem(int type, int cursorPos){
		int cursorIndex = 0;

		// iterate through the whole inventory
		for (int i = 0; i < inventory.getSize(); i++){

			// if we encounter an item type that matches the passed-in type, we check if this is the cursor pos we want
			if (inventory.getItemType(i) == type){
				if (cursorIndex == cursorPos){
					return inventory.removeIndex(i);
				} else {
					cursorIndex++;
				}
			}
		}
		return new Item(Item.ID_BLANK);
	}



}
