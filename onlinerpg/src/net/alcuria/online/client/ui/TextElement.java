package net.alcuria.online.client.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextElement {
	
	private int x, y;
	public String contents;
	
	public TextElement(int x, int y, int width, int height, String textContents, int iconID){
		this.x = x;
		this.y = y;
		this.contents = textContents;
		
	}
	
	public TextElement(int x, int y, int width, int height, String textContents){
		this(x, y, width, height, textContents, -1);
	}

	public void dispose() {
		
		
	}
	
	public void update(int offsetX, int offsetY){

	}
	
	public void render(SpriteBatch batch, BitmapFont font){
		font.drawMultiLine(batch, contents, x, y);	
	}
}
