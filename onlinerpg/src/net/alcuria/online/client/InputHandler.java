package net.alcuria.online.client;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class InputHandler {

	// GUI joypad textures
	Texture buttons;
	TextureRegion up;
	TextureRegion down;
	TextureRegion left;
	TextureRegion right;
	TextureRegion confirm;
	TextureRegion cancel;

	int joypadX = -100;
	int joypadY = -100;
	int keysX = -100;
	int keysY = -100;
	float offsetX;
	float offsetY;
	int touchPointX;
	int touchPointY;

	boolean touchedThisFrame = false;

	OrthographicCamera camera;

	// keycodes referencing boolean input indices
	public final static int SPACE = 0;
	public final static int JUMP = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	public final static int ATTACK = 4;
	public final static int INSPECT = 5;
	public final static int ESCAPE = 6;
	public final static int UP = 7;
	public final static int DOWN = 8;
	public final static int SKILL_1 = 9;
	public final static int SKILL_2 = 10;
	public final static int SKILL_3 = 11;


	public boolean[] pressing;			// true while a key is held down, false otherwise
	public boolean[] typed;				// true ONLY the frame the key is pressed
	public Rectangle[] virtualButtons;	// 1-to-1 mapping of pressed keys to on-screen 

	public InputHandler(AssetManager assets) {

		// create boolean input arrays
		pressing = new boolean[12];
		typed = new boolean[12];
		virtualButtons = new Rectangle[12];
		for (int i=0; i<typed.length; i++){
			pressing[i] = false;
			typed[i] = false;
		}

		// load texture/create region for joypad if on Android
		if (Gdx.app.getType() == ApplicationType.Android) {

			buttons = assets.get("ui/buttons.png", Texture.class);
			up = 		new TextureRegion(buttons, 30, 0, 22, 27);
			down = 		new TextureRegion(buttons, 30, 55, 22, 27);
			left = 		new TextureRegion(buttons, 0, 30, 27, 22);
			right = 	new TextureRegion(buttons, 55, 30, 27, 22);
			cancel = 	new TextureRegion(buttons, 84, 0, 38, 35);
			confirm = 	new TextureRegion(buttons, 84, 47, 38, 35);

			// create the rectangles
			virtualButtons[UP] = new Rectangle(-100, -100, 22, 27);
			virtualButtons[DOWN] = new Rectangle(-100, -100, 22, 27);
			virtualButtons[LEFT] = new Rectangle(-100, -100, 27, 22);
			virtualButtons[RIGHT] = new Rectangle(-100, -100, 27, 22);
			virtualButtons[JUMP] = new Rectangle(-100, -100, 38, 35);
			virtualButtons[ATTACK] = new Rectangle(-100, -100, 38, 35);
			virtualButtons[ESCAPE] = new Rectangle(-100, -100, 50, 30);

			camera = new OrthographicCamera();
		}

	}

	public float convertTouchPoint(float touchPoint, boolean yAxis) {

		if (yAxis) {
			return Config.HEIGHT - ((float)Config.WIDTH/(float)Gdx.graphics.getWidth() * touchPoint) + offsetY ;
		}
		return (float)Config.WIDTH/(float)Gdx.graphics.getWidth() * touchPoint + offsetX;

	}

	public void setInput(int keyPos, int keyPress){

		// pulls input and sets boolean flags
		if (Gdx.app.getType() == ApplicationType.Android) {
			// android

			pressing[keyPos] = false;


			for (int i=0; i<3; i++) {
				if (Gdx.input.isTouched(i)){

					touchedThisFrame = true;

					touchPointX = (int) convertTouchPoint(Gdx.input.getX(i), false);
					touchPointY = (int) convertTouchPoint(Gdx.input.getY(i), true);

					// iterate through each of the button rectangles and see if the touch point is within bounds 
					if (virtualButtons[keyPos] != null && virtualButtons[keyPos].contains(touchPointX, touchPointY)) {
						typed[keyPos] = true;
						pressing[keyPos] = true;
					} 

				}

			}


		} else {

			// desktop+html5
			if(Gdx.input.isKeyPressed(keyPress)) {
				typed[keyPos] = false;
				if (!typed[keyPos] && !pressing[keyPos]){
					typed[keyPos] = true;
				}
				pressing[keyPos] = true;
			} else {
				pressing[keyPos] = false;
			}
		}





	}
	// handles all input updates.
	public void update(Player player, Map map, CameraManager cameraManager){

		// get fresh inputs
		setInput(LEFT, Keys.LEFT);
		setInput(RIGHT, Keys.RIGHT);
		setInput(UP, Keys.UP);
		setInput(DOWN, Keys.DOWN);
		setInput(JUMP, Keys.Z);
		setInput(ATTACK, Keys.X);
		setInput(SPACE, Keys.SPACE);
		setInput(ESCAPE, Keys.ESCAPE);
		setInput(SKILL_1, Keys.SHIFT_LEFT);

		if(cameraManager != null){
			offsetX = cameraManager.offsetX;
			offsetY = cameraManager.offsetY;
			joypadX = (int) (cameraManager.offsetX + 45);
			joypadY = (int) (cameraManager.offsetY + Config.HEIGHT - 169);
			keysX = (int) cameraManager.offsetX + Config.WIDTH - 48;
			keysY = (int) cameraManager.offsetY + Config.HEIGHT - 195;
		}
		if (Gdx.app.getType() == ApplicationType.Android) {

			virtualButtons[ESCAPE].x = offsetX;
			virtualButtons[ESCAPE].y = offsetY + Config.HEIGHT - 20;

			virtualButtons[UP].x = joypadX;
			virtualButtons[UP].y = joypadY;

			virtualButtons[DOWN].x = joypadX;
			virtualButtons[DOWN].y = joypadY - 56;

			virtualButtons[LEFT].x = joypadX - 31;
			virtualButtons[LEFT].y = joypadY - 26;

			virtualButtons[RIGHT].x = joypadX + 31;
			virtualButtons[RIGHT].y = joypadY - 26;

			virtualButtons[ATTACK].x = keysX;
			virtualButtons[ATTACK].y = keysY;

			virtualButtons[JUMP].x = keysX - 61;
			virtualButtons[JUMP].y = keysY - 37;	

		}

	}

	public void render(SpriteBatch batch){

		// render the controller overlay if we're on ANDROID
		if (Gdx.app.getType() == ApplicationType.Android) {

			batch.flush();
			batch.setColor(1,1,1,Config.androidControlsOpacity);

			batch.draw(up, joypadX, joypadY);
			batch.draw(down, joypadX, joypadY - 56);
			batch.draw(left, joypadX-31, joypadY - 26);
			batch.draw(right, joypadX+31, joypadY - 26);

			batch.draw(confirm, keysX, keysY);
			batch.draw(cancel, keysX-61, keysY-37);

			batch.flush();
			batch.setColor(1,1,1,1);

		}

	}

}