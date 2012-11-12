package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;

public class TypeHandler extends InputHandler {

	public boolean[] alphaTyped;
	public boolean[] alphaPressing;
	int key;
	public String name = "";
	
	public TypeHandler(AssetManager assets) {
		super(assets);
		
		readyForInput = false;
		
		alphaPressing = new boolean[200];
		alphaTyped = new boolean[200];
		for(int i = 0; i < alphaTyped.length; i++){
			alphaTyped[i] = false;
			alphaPressing[i] = false;
		}
	}
	
	public void update(){
		
		if (!readyForInput) {
			if (Gdx.input.isKeyPressed(Keys.X) || Gdx.input.isKeyPressed(Keys.ENTER)) {
				return;				
				
			} else {
				readyForInput = true;
			}
		}
		
		// do all the alphanumeric key presses
		for (int i = 29; i < 55; i ++){
			if (Gdx.input.isKeyPressed(i)){
				alphaTyped[i] = false;
				if (!alphaTyped[i] && !alphaPressing[i]){
					if (name.length() < 8) {
						if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)){
							name = name + (char) (i+36);
						} else {
							name = name + (char) (i+68);
						}
					}
					alphaTyped[i] = true;
				}
				alphaPressing[i] = true;
			} else {
				alphaPressing[i] = false;
			}
		}
		
		// check to erase
		if (Gdx.input.isKeyPressed(Keys.BACKSPACE)){
			alphaTyped[Keys.BACKSPACE] = false;
			if (!alphaTyped[Keys.BACKSPACE] && !alphaPressing[Keys.BACKSPACE]){
				if (name.length() <= 1) {
					name = "";
				} else {
					name = name.substring(0, name.length() - 1);
				}
				alphaTyped[Keys.BACKSPACE] = true;
			}
			alphaPressing[Keys.BACKSPACE] = true;
		} else {
			alphaPressing[Keys.BACKSPACE] = false;
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
			readyForInput = false;
		}
		
	}
	
	protected boolean alphaPressingAKey() {

		for (int i = 0; i < alphaPressing.length; i++) {
			if (alphaPressing[i] == true || alphaTyped[i] == true){
				return true;
			}
		}
		return false;
	}
	
//		if(Gdx.input.isKeyPressed(keyPress)) {
//			typed[keyPos] = false;
//			if (!typed[keyPos] && !pressing[keyPos]){
//				typed[keyPos] = true;
//			}
//			pressing[keyPos] = true;
//		} else {
//			pressing[keyPos] = false;
//		}
//	}


}
