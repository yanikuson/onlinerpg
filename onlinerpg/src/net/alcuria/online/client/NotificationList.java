package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NotificationList {

	public final int MAX_NOTIFICATIONS = 8;
	public final float NOTIFICATION_DURATION = 7;

	public BitmapFont notificationFont;
	public String[] notifications;
	public float[] durations;
	public float[] x, y;
	public float deltaTime;
	private boolean doFlush = false;

	private int shiftAccumulator = 0;

	public NotificationList(){
		notificationFont = new BitmapFont(Gdx.files.internal("fonts/ui.fnt"), false);
		notifications = new String[MAX_NOTIFICATIONS];
		x = new float[MAX_NOTIFICATIONS];
		y = new float[MAX_NOTIFICATIONS];
		durations = new float[MAX_NOTIFICATIONS];

		for (int i = 0; i < MAX_NOTIFICATIONS; i++) {
			notifications[i] = "";
			x[i] = 10;
			y[i] = 0;
			durations[i] = 0;

		}

	}

	public void update(){
		// TODO: update all of the notifications
		// also check if a notice is invisible and clear it?
		deltaTime = Gdx.graphics.getDeltaTime();
		for (int i = 0; i < MAX_NOTIFICATIONS; i++){
			if (durations[i] > 0){
				durations[i] -= deltaTime;
			}
			if (shiftAccumulator > 0){
				y[i]+=2;	
			}
		}
		if (shiftAccumulator > 0){
			shiftAccumulator-=2;
		}

	}

	public void render(SpriteBatch batcher, CameraManager cam){
		// TODO: render the list of notices
		for (int i = 0; i < MAX_NOTIFICATIONS; i++) {
			if (durations[i] > 0){
				if (durations[i] < 1){
					doFlush = true;
					batcher.flush();
					notificationFont.setColor(1, 1, 1, durations[i]);
				}
				notificationFont.draw(batcher, notifications[i], x[i] + cam.offsetX, y[i] + cam.offsetY);
			}
			if (doFlush){
				batcher.flush();
				notificationFont.setColor(1, 1, 1, 1);
				doFlush = false;
			}
		}
	}

	public void add(String s){
		for (int i = MAX_NOTIFICATIONS - 1; i > 0; i--){
			notifications[i] = notifications[i - 1];
			x[i] = x[i-1];
			y[i] = y[i-1];
			durations[i] = durations[i-1];
		}
		notifications[0] = s;
		durations[0] = NOTIFICATION_DURATION;
		x[0] = 10;
		y[0] = 5 - shiftAccumulator;
		shiftAccumulator += 8;
	}

}
