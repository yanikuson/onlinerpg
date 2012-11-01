package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Particle {

	private boolean flip = false;		// do we flip along the Y axis?
	public boolean playAnimation = false;	// set to true to start rendering
	public boolean loop = false;		// set to true to loop
	private int width;					// width of an individual frame
	private int height;					// height of the frame
	private int x;						// x coord of anim
	private int y;						// y coord of anim
	private int totalFrames;			// number of frames to display
	private int currentFrame = 0;		// the current frame to display
	private int frameDuration = 1;		// number of game frames to display each animation

	public int row = 0;					// the row in the spritesheet to be used
	public boolean rotate = false;		// true rotates the sprite
	public boolean clockwise = false;	// determines rotation 
	float rotation = 0;					// counter for rotation amount

	Texture sheet;
	TextureRegion[] frames;

	public Particle(String filename, int x, int y, int width, int height, int totalFrames, int frameDuration, boolean flip, AssetManager assets) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.totalFrames = totalFrames;
		this.flip = flip;
		this. frameDuration = frameDuration;

		// load all our textures/regions
		sheet = assets.get(filename, Texture.class);
		frames = new TextureRegion[totalFrames];
		for (int i = 0; i < totalFrames; i++) {
			frames[i] = new TextureRegion(sheet, width*i, height*row, width, height);
		}


	}

	public void start(float x, float y, boolean facingLeft) {
		this.currentFrame = 0;
		this.x = (int) x;
		this.y = (int) y; 
		this.flip = facingLeft;
		this.playAnimation = true;
		
		// reset the frame animation type based on row
		for (int i=0; i< totalFrames; i++){
			frames[i].setRegion(width*i, height*row, width, height);
		}

	}
	public void update(float x, float y, boolean follow){


		// if the animation has started, update our frame count
		if (playAnimation){

			// update the animation's location if follow is set
			if (follow){
				this.x = (int) x;
				this.y = (int) y;
			}
			currentFrame++;

			// check if the animation is over
			if (currentFrame/frameDuration >= totalFrames){

				if (loop) {
					currentFrame = 0;
				} else {
					playAnimation = false;
				}

			}
			if (rotate){
				if (clockwise){
					rotation+=Gdx.graphics.getDeltaTime()*-800;
				} else {
					rotation+=Gdx.graphics.getDeltaTime()*800;

				}
			}

		} else {
			// otherwise, continuously update
			currentFrame = 0;
		}
	}

	public void render(SpriteBatch batch){

		if (playAnimation){

			if (currentFrame/frameDuration < totalFrames){

				if (rotate) {
					batch.draw(frames[currentFrame/frameDuration], x, y, width/2, height/2, width, height, 1, 1, rotation);
				} else {

					if (!flip){
						batch.draw(frames[currentFrame/frameDuration], x, y, width, height);
					} else {
						batch.draw(frames[currentFrame/frameDuration], x+width, y, 0-width, height);
					}

				}
			} 

		}
	}

	public void dispose() {
		// TODO: dispose stuff

	}





}