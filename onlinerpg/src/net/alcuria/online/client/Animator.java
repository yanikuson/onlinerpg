package net.alcuria.online.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animator {

	int width;
	int height;
	int attackIndex = 0;
	public int framesSinceAttack = 0;
	boolean flipX;
	boolean swingPose = false;
	boolean stabPose = false;
	boolean readyPose = false;

	float animationTimer = 0f;
	float attackTimer = 0f;
	float readyTimer = 0f;
	float idleTimer = 0f;

	Texture sheet;									// entire graphical sprite sheet
	TextureRegion frame;							// cell within the sprite sheet (this gets drawn)

	TextureRegion[] idle;
	TextureRegion[] walking;
	TextureRegion falling;
	TextureRegion[] attacking;
	TextureRegion[] stabbing;
	TextureRegion[] ready;

	public Animator(String filename, int celWidth, int celHeight, AssetManager assets){

		// create new texture and region
		this.sheet = assets.get(filename, Texture.class);
		this.frame = new TextureRegion(sheet, 0, 0, celWidth, celHeight);

		// create individual pose regions
		this.idle = new TextureRegion[2];
		this.idle[0] = new TextureRegion(sheet, 0, 0, celWidth, celHeight);
		this.idle[1] = new TextureRegion(sheet, celWidth, 0, celWidth, celHeight);

		this.walking = new TextureRegion[4];
		this.walking[0] = new TextureRegion(sheet, 0, 0, celWidth, celHeight);
		this.walking[1] = new TextureRegion(sheet, celWidth*2, 0, celWidth, celHeight);
		this.walking[2] = new TextureRegion(sheet, 0, 0, celWidth, celHeight);
		this.walking[3] = new TextureRegion(sheet, celWidth*3, 0, celWidth, celHeight);

		this.falling = new TextureRegion(sheet, celWidth*4, 0, celWidth, celHeight);

		this.attacking = new TextureRegion[3];
		this.attacking[0] = new TextureRegion(sheet, 0, celHeight, celWidth*2, celHeight);
		this.attacking[1] = new TextureRegion(sheet, celWidth*2, celHeight, celWidth*2, celHeight);
		this.attacking[2] = new TextureRegion(sheet, celWidth*4, celHeight, celWidth*2, celHeight);

		this.stabbing = new TextureRegion[3];
		this.stabbing[0] = new TextureRegion(sheet, 0, celHeight*2, celWidth*2, celHeight);
		this.stabbing[1] = new TextureRegion(sheet, celWidth*2, celHeight*2, celWidth*2, celHeight);
		this.stabbing[2] = new TextureRegion(sheet, celWidth*4, celHeight*2, celWidth*2, celHeight);

		this.ready = new TextureRegion[2];
		this.ready[0] = new TextureRegion(sheet, celWidth*5, 0, celWidth, celHeight);
		this.ready[1] = new TextureRegion(sheet, celWidth*6, 0, celWidth, celHeight);

		// set width/height, for convenience later
		width = frame.getRegionWidth();
		height = frame.getRegionHeight();

	}

	public void render(SpriteBatch batch, float x, float y){

		if (!flipX){

			batch.draw(frame, x, y);
		} else {

			// draw the frame flipped
			if (swingPose || stabPose){
				batch.draw(frame, x+width, y, 0-width*2, height);
			} else {
				batch.draw(frame, x+width, y, 0-width, height);
			}
		}

	}

	public void update(boolean isFacingLeft, boolean onGround, boolean moving, float timestep, float attackSpeed){

		flipX = isFacingLeft;

		if (!swingPose && !stabPose){
			if (onGround){
				if (moving){
					framesSinceAttack = 100;
					animationTimer += timestep;
					frame = walking[(int) ((animationTimer*10 + 1) % walking.length)];
				} else {
					animationTimer = 0;
					if (!readyPose){
						idleTimer += timestep;
						frame = idle[(int) ((idleTimer*3) % idle.length)];

					} else {
						readyTimer += timestep;
						frame = ready[(int) ((readyTimer*4) % ready.length)];
					}
				}
			} else {
				idleTimer = 0;
				animationTimer = 0;
				frame = falling;

			}
		} else {
			// process an attack animation
			attackIndex = (int) ((attackTimer*attackSpeed));
			if (swingPose) {
				framesSinceAttack = 0;
				if (attackIndex >= attacking.length){
					attackTimer = 0;
					swingPose = false;
					readyPose = true;
					frame = attacking[attacking.length-1];
					
				} else {
					attackTimer += timestep;
					frame = attacking[attackIndex];
				}

			} else {
				if (attackIndex >= attacking.length){
					attackTimer = 0;
					stabPose = false;
					readyPose = true;
					frame = stabbing[attacking.length-1];
				} else {
					attackTimer += timestep;
					frame = stabbing[attackIndex];
				}
			}
		}

		framesSinceAttack++;

		// reset ready timer if 5 seconds pass
		if (readyTimer > 5){
			readyPose = false;
			readyTimer = 0;
		}
	}


	public void dispose(){

		sheet.dispose();

	}

	public void setReady(){
		readyTimer = 0;
		readyPose = true;
	}

	public void startAttack(boolean facingLeft) {
		readyTimer = 0;
		attackTimer = 0f;
		if (framesSinceAttack < 20){
			stabPose = true;
		} else {
			swingPose = true;
		}
	}
}
