package net.alcuria.online.client.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Window {
	
	private Texture background;
	private TextureRegion borderComponent[];	// each of the edges of the window, aka the frame
	
	public TextElement[] text;
	private int curText = 0;
	public final static int MAX_TEXT_ELEMS = 20;
	
	private boolean visible = true;				// do we render this window??
	private int x, y, width, height;			// location and dimensions of our window 
	private int offsetX, offsetY;
	
	public Window(int x, int y, int width, int height, int offsetX, int offsetY, Texture background, Texture border){
		// store our textures from the assetManager
		this.background = background;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		// create textureRegion containing message frame pieces:
		//
		// 		012
		//		3 4
		//		567
		borderComponent = new TextureRegion[8];
		borderComponent[0] = new TextureRegion(border, 0, 0, 5, 5);
		borderComponent[1] = new TextureRegion(border, 5, 0, 5, 5);
		borderComponent[2] = new TextureRegion(border, 10, 0, 5, 5);
		borderComponent[3] = new TextureRegion(border, 0, 5, 5, 5);
		borderComponent[4] = new TextureRegion(border, 10, 5, 5, 5);
		borderComponent[5] = new TextureRegion(border, 0, 10, 5, 5);
		borderComponent[6] = new TextureRegion(border, 5, 10, 5, 5);
		borderComponent[7] = new TextureRegion(border, 10, 10, 5, 5);	
		
		// create text elements
		text = new TextElement[MAX_TEXT_ELEMS];
	}

	// remove all references to the window sub-elements
	public void dispose() {
		for (int i=0; i<curText; i++){
			text[i].dispose();
			text[i] = null;
		}
		curText = 0;
		
	}

	public void render(SpriteBatch batch, BitmapFont font) {
		if (visible){
			batch.draw(background, x, y, width, height);
			
			batch.draw(borderComponent[1], x, y + height - 2, width, 5);
			batch.draw(borderComponent[6], x, y - 3, width, 5);
			batch.draw(borderComponent[3], x - 3, y, 5, height);
			batch.draw(borderComponent[4], x + width - 2, y, 5, height);
			
			batch.draw(borderComponent[0], x - 3, y + height - 2, 5, 5);
			batch.draw(borderComponent[2], x + width - 2, y + height - 2, 5, 5);
			batch.draw(borderComponent[5], x - 3, y - 3, 5, 5);
			batch.draw(borderComponent[7], x + width - 2, y - 3, 5, 5);

			// draw text elements
			for (int i=0; i < curText; i++){
				text[i].render(batch, font);
			}
		}
		
	}

	public void update(int offsetX, int offsetY) {
		
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		for (int i=0; i < curText; i++) {
			text[i].update(offsetX, offsetY);
		}
		
	}
	
	public void addText(int x, int y, int width, int height, String textContents, int iconID){
		if (curText < MAX_TEXT_ELEMS){
			text[curText] = new TextElement(x+offsetX, y+offsetY, width, height, textContents, iconID);
			curText++;
		}
	}
	
	public void addText(int x, int y, int width, int height, String textContents){
		addText(x, y, width, height, textContents, -1);
	}
}
