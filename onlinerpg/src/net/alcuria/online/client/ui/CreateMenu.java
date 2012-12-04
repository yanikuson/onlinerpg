package net.alcuria.online.client.ui;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.TypeHandler;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CreateMenu extends Menu {

	// we set these flags to true when we want to update the screen or enter a subscreen

	public static final int MODE_CHOOSE_COMPONENT = 0;			// choose a component, eg. name, gender, etc
	public static final int MODE_CHANGE_COMPONENT = 1;			// change a component, eg enter name, male vs female, etc

	public boolean leaveMenu = false;
	public boolean enterName = false;
	public boolean createFile = false;

	public int mode = MODE_CHOOSE_COMPONENT;
	public int triangleX = 0;
	public int triangleY = 0;

	public String values = "";
	public String name = "";
	public int skin = 0;
	public int gender = 0;
	public int hair = 0;

	public CreateMenu(Texture background, Texture border, AssetManager assets) {
		super(background, border, assets, null, null, null);

		cursorFacingRight[0] = true;

		createScreen();

		selection[0] = 0;
		cursorY[0] = Config.HEIGHT - 80 + 22*-selection[0];
		cursorX[0] = 105;
		cursorY[1] = -30;
		cursorX[1] = -30;
		cursorY[2] = -30;
		cursorX[2] = -30;

	}


	private void createScreen() {
		// 			values = name + "\n\nMale\n\nPale\n\nStyle 1\n\n";

		// set strings
		if (mode == MODE_CHANGE_COMPONENT && selection[0] == 0 && name.length() < 8){
			values = name + "_\n\n";
		} else {
			values = name + "\n\n";
		}
			
		switch (gender) {
		case 0:
			values = values + "Male\n\n";
			break;
		case 1:
			values = values + "Female\n\n";
			break;
		}
		
		switch (skin) {
		case 0:
			values = values + "Pale\n\n";
			break;
		case 1:
			values = values + "Tan\n\n";
			break;
		case 2:
			values = values + "Dark\n\n";
			break;
		}
		
		values = values + "Style " + (hair + 1) + "\n\n";
	
		
		// update menu
		dispose();
		
		addWindow(117, Config.HEIGHT - 42, Config.WIDTH-200, 20);
		windows[curWindow-1].addText(150, Config.HEIGHT-25, 300, 50, "Please create your character");

		addWindow(117, 79, 100, 92);
		windows[curWindow-1].addText(123, 170, 160, 50, "Name:\n\nGender:\n\nSkin:\n\nHair:\n\n");
		
		windows[curWindow-1].addText(145, 159, 160, 50, values);

		// character preview
		addWindow(250, 98, 50, 50);

		// create confirmation
		addWindow(117, Config.HEIGHT - 195, 50, 20);
		windows[curWindow-1].addText(123, Config.HEIGHT-178, 30, 50, "Create");
		
	}


	public void render(SpriteBatch batch) {
		super.render(batch);
		if (depth == 1 	&& selection[0] != 0) {
			batch.draw(rightTriangle, triangleX, triangleY);
			batch.draw(leftTriangle, triangleX + 80, triangleY);

		}
	}

	public void update(InputHandler input, TypeHandler type, float offsetX, float offsetY){
		// update the offset due to camera shifts
		this.offsetX = offsetX;
		this.offsetY = offsetY;


		// update the offset of the submenu items
		for (int i=0; i < curWindow; i++){
			windows[i].update((int) offsetX, (int) offsetY);
		}

		// handle input
		if (active){

			switch (mode) {

			case MODE_CHOOSE_COMPONENT:

				if (input.typed[InputHandler.UP]){
					input.typed[InputHandler.UP] = false;

					move.play(Config.sfxVol);
					changeScreen = true;
					selection[depth]--;
					if (selection[depth] < 0){
						selection[depth] = 4; 
					}
				}
				if (input.typed[InputHandler.DOWN]){
					input.typed[InputHandler.DOWN] = false;

					move.play(Config.sfxVol);
					changeScreen = true;
					selection[depth]++;
					if (selection[depth] > 4){
						selection[depth] = 0; 
					}

				}
				if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
					input.typed[InputHandler.ESCAPE] = false;
					input.typed[InputHandler.JUMP] = false;


					cancel.play(Config.sfxVol);
					leaveMenu = true;
				}
				if (input.typed[InputHandler.ATTACK] || input.typed[InputHandler.SPACE] || input.typed[InputHandler.ENTER]){
					input.typed[InputHandler.ATTACK] = false;
					input.typed[InputHandler.SPACE] = false;
					input.typed[InputHandler.ENTER] = false;
					refreshText = true;

					if (selection[depth] != 4){
						select.play(Config.sfxVol);
						triangleX = cursorX[depth] + 20;
						triangleY = cursorY[depth] - 11;
						depth++;
						mode = MODE_CHANGE_COMPONENT;
						break;
					} else {
						// flag our Create Screen to create the file
						if (name.length() > 0) {
							select.play(Config.sfxVol);
							createFile = true;
						} else {
							cancel.play(Config.sfxVol);
						}
					}
				}			

				// update the cursor position
				if (selection[depth] == 4) {
					cursorY[depth] = 50;
				} else {
					cursorY[depth] = Config.HEIGHT - 80 + 22*-selection[depth];
				}
				cursorX[depth] = 105;
				break;

			case MODE_CHANGE_COMPONENT:


				if (selection[0] != 0) {
					
					// make changes for any non-name entry component
					if (input.typed[InputHandler.LEFT]){
						input.typed[InputHandler.LEFT] = false;
						if (selection[0] == 1) {
							gender-= 1;
							if (gender <0) gender = 1;
						} else if (selection[0] == 2){
							skin -= 1;
							if (skin <0) skin = 2;
						} else if (selection[0] == 3){
							hair -= 1;
							if (hair < 0) hair = 3;
						}
						move.play(Config.sfxVol);
						refreshText = true;
					}

					if (input.typed[InputHandler.RIGHT]){
						input.typed[InputHandler.RIGHT] = false;
						if (selection[0] == 1) {
							gender+= 1;
							gender %= 2;
						} else if (selection[0] == 2){
							skin += 1;
							skin %= 3;
						} else if (selection[0] == 3){
							hair += 1;
							hair %= 4;
						}
						move.play(Config.sfxVol);
						refreshText = true;
					}
					
					if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.JUMP]){
						input.typed[InputHandler.ESCAPE] = false;
						input.typed[InputHandler.JUMP] = false;
						cancel.play(Config.sfxVol);
						depth--;
						mode = MODE_CHOOSE_COMPONENT;
					}					
					
				} else {
					
					// handle the name entry component
					if (this.name != type.name) {
						name = type.name;
						refreshText = true;
					}
					
					if (input.typed[InputHandler.ESCAPE] || input.typed[InputHandler.ENTER]){
						input.typed[InputHandler.ESCAPE] = false;
						input.typed[InputHandler.ENTER] = false;
						
						cancel.play(Config.sfxVol);
						depth--;
						mode = MODE_CHOOSE_COMPONENT;
						refreshText = true;
					}
				}
				break;
			}
		}


		if (refreshText) {
			createScreen();
			refreshText = false;
		}
		
	}
}



