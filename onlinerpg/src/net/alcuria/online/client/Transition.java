package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Transition {

	private static Texture t;
	private static TextureRegion fade;

	private static float duration = 0;
	private static float curFade = 0;
	private static float targetFade = 0;
	private static boolean initialized = false;

	private static boolean fadeOut = false;
	private static boolean fadeIn = false;
	public static boolean finished = true;
	public static boolean started = false;

	public static void init() {

		if (!initialized) {
			t = new Texture(Gdx.files.internal("ui/fade.png"));
			fade = new TextureRegion(t, 0, 0, 1, 1);
			initialized = true;
		}
	}

	public static void fadeOut(float time){

		if (!fadeOut) {
			if (!initialized) init();
			fadeIn = false;
			finished = false;
			targetFade = time;
			curFade = 0; 
			duration = time;
			started = true;
			fadeOut = true;
		}

	}

	public static void fadeIn(float time){

		if (!fadeIn){
			if (!initialized) init();
			fadeOut = false;
			started = true;
			finished = false;
			targetFade = 0;
			curFade = time;
			duration = time;
			fadeIn = true;
		}
	}

	public static void update(){

		if (fadeOut){
			System.out.println("fade out");
			curFade += Gdx.graphics.getDeltaTime();
			if (curFade >= targetFade) {
				curFade = targetFade;
				finished = true;
				System.out.println("Finished fade out");
			}
		} else if (fadeIn){
			System.out.println("fade in");
			curFade -= Gdx.graphics.getDeltaTime();
			if (curFade <= 0) {
				curFade = 0;
				finished = true;
				System.out.println("Finished fade in");
			}
		}


	}
	public static void render(SpriteBatch batch, CameraManager camera){

		// draw the semi-transparent dimmer
		if (initialized && (fadeIn || fadeOut) && duration != 0) {


			batch.flush();
			System.out.println(curFade/duration);
			//batch.setBlendFunction(GL11.GL_ZERO, GL11.GL_SRC_COLOR);
			batch.setColor(1, 1, 1, curFade/duration);

			batch.draw(fade, camera.offsetX, camera.offsetY, Config.WIDTH, Config.HEIGHT);
			batch.flush();


		}

	}

}
