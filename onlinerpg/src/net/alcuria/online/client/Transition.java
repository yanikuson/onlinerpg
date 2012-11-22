package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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

			fadeOut = true;
		}

	}

	public static void fadeIn(float time){

		if (!fadeIn){
			if (!initialized) init();
			fadeOut = false;

			finished = false;
			targetFade = 0;
			curFade = time;
			duration = time;
			fadeIn = true;
		}
	}

	public static void update(Music bgm){

		if (fadeOut){
			curFade += Gdx.graphics.getDeltaTime();
			if (curFade >= targetFade) {
				curFade = targetFade;
				finished = true;
			}
		} else if (fadeIn){
			curFade -= Gdx.graphics.getDeltaTime();
			if (curFade <= 0) {
				curFade = 0;
				finished = true;
			}
		}
		
		// update sound vol
		if (!finished){
			bgm.setVolume(Config.bgmVol * (1 - curFade/duration));
		}


	}
	public static void render(SpriteBatch batch, CameraManager camera){

		// draw the screen dimming effect
		if (initialized && (fadeIn || fadeOut) && duration != 0) {

			batch.flush();
			batch.setColor(1, 1, 1, Config.smoothstep(curFade/duration));
			batch.draw(fade, camera.offsetX, camera.offsetY, Config.WIDTH, Config.HEIGHT);

			batch.setColor(1, 1, 1, 1);

		}

	}

}
