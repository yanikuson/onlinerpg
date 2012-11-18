package net.alcuria.online.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ParticleList {

	public Particle[] particles;
	public int index = 0;
	public final int MAX_ELEMS = 30;
	
	public int width, height;				// w/h of a SINGLE particle
	
	public ParticleList(String filename, int width, int height, int totalFrames, int duration, boolean mirror, AssetManager assets) {

		particles = new Particle[MAX_ELEMS];
		for (int i = 0; i < particles.length; i++){
			particles[i] = new Particle(filename, 0, 0, width, height, totalFrames, duration, mirror, assets);
		}
		this.width = width;
		this.height = height;
		
	}
	
	public void update() {
	
		for (int i = 0; i < particles.length; i++){
			particles[i].update(0, 0, false);
		}
	}
	
	public void start(float x, float y, boolean flip) {
		
		// start a new instance of the damage object and increment our index
		particles[index].start(x, y + 10, flip);
		index =  (index + 1) % MAX_ELEMS;
		
	}
	
	public void render(SpriteBatch batcher) {
		for (int i = 0; i < particles.length; i++){
			particles[i].render(batcher);
		}
	}
	
}
