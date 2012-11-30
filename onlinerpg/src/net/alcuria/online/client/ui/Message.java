package net.alcuria.online.client.ui;


import net.alcuria.online.client.InputHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Message {
	
	public static int h_center = 208;
	public static int v_center = 120;

	private Texture msgBackground;
	private TextureRegion[] frame;
	private BitmapFont[] lines;
	private Sound talk;
	
	public boolean visible = false;
	public boolean play = false;
	
	private String[] messages;
	private String[] curMessages;
	private int x = 0;
	private int y = 0;
	private int numLines = 1;
	private int curLine = 0;
	private int curChar = 0;
	private int maxWidth = 0;
	
	private final int MAX_LINES = 5;
	private final int CHAR_HEIGHT = 12;


	public Message(Texture msgBackground, Texture msgBorder, AssetManager assetManager){

		// store our textures from the assetManager
		this.msgBackground = msgBackground;

		// create textureRegion containing message frame pieces:
		//
		// 		012
		//		3 4
		//		567
		frame = new TextureRegion[8];
		frame[0] = new TextureRegion(msgBorder, 0, 0, 5, 5);
		frame[1] = new TextureRegion(msgBorder, 5, 0, 5, 5);
		frame[2] = new TextureRegion(msgBorder, 10, 0, 5, 5);
		frame[3] = new TextureRegion(msgBorder, 0, 5, 5, 5);
		frame[4] = new TextureRegion(msgBorder, 10, 5, 5, 5);
		frame[5] = new TextureRegion(msgBorder, 0, 10, 5, 5);
		frame[6] = new TextureRegion(msgBorder, 5, 10, 5, 5);
		frame[7] = new TextureRegion(msgBorder, 10, 10, 5, 5);

		// load font
		lines = new BitmapFont[MAX_LINES];
		messages = new String[MAX_LINES];
		curMessages = new String[MAX_LINES];
		for (int i = 0; i < MAX_LINES; i++){
			lines[i] = new BitmapFont(Gdx.files.internal("fonts/message.fnt"), false);
			messages[i] = new String("");
			curMessages[i] = new String("");
		}

		talk = assetManager.get("sounds/talk.wav", Sound.class);


	}

	// render the message
	public void render(SpriteBatch batch){
		if (visible){
			batch.draw(msgBackground, x - 5, y - numLines*CHAR_HEIGHT-7, maxWidth+CHAR_HEIGHT, (numLines+1)*CHAR_HEIGHT);
			
			batch.draw(frame[1], x - 2, y + 2, maxWidth + 6, 5);
			batch.draw(frame[3], x - 5, y - numLines*CHAR_HEIGHT-7, 5, (numLines+1)*CHAR_HEIGHT-2);
			batch.draw(frame[4], x + maxWidth + 4, y - numLines*CHAR_HEIGHT-7, 5, (numLines+1)*CHAR_HEIGHT-2);
			batch.draw(frame[6], x - 2, y - numLines*CHAR_HEIGHT-10, maxWidth + 6, 5);
			batch.draw(frame[0], x - 5, y + 2, 5, 5);
			batch.draw(frame[2], x + maxWidth + 4, y + 2, 5, 5);
			batch.draw(frame[5], x - 5, y - numLines*CHAR_HEIGHT-10, 5, 5);
			batch.draw(frame[7], x + maxWidth + 4, y - numLines*CHAR_HEIGHT-10, 5, 5);

			for (int i = 0; i < numLines; i++){
				lines[i].draw(batch, curMessages[i], x, y - (CHAR_HEIGHT*i));
			}
		}
	}

	// update the message
	public void update(float deltaTime, InputHandler inputs){

		if (visible){
			if (curLine < numLines){
				if(curChar < messages[curLine].length()){
					curMessages[curLine] = curMessages[curLine] + messages[curLine].charAt(curChar);
					curChar++;
					play = !play; 
					if (play) talk.play();
				} else {
					curLine++;
					curChar = 0;
				}
			} else {
				
				// hide message on key press
				if(inputs.typed[InputHandler.ATTACK] || inputs.typed[InputHandler.SPACE] || inputs.typed[InputHandler.ENTER]){
					inputs.typed[InputHandler.ATTACK] = false;
					inputs.typed[InputHandler.SPACE] = false;
					inputs.typed[InputHandler.ENTER] = false;
					removeMessage();
				}
			}
			
		}


	}

	private void removeMessage() {
		for(int i = 0; i < numLines; i++){
			messages[i] = "";
			curMessages[i] = "";
		}
		maxWidth = 0;
		curLine = 0;
		numLines = 1;
		visible = false;
		
	}

	// call to display a message. Returns true on success, false otherwise.
	public boolean startMessage(String message, int x, int y){
	
		if (!visible){
			// TODO: assert we have no more than 5 lines

			// split the input string and calculate the widest line
			this.messages = message.split("<br>");
			this.numLines = message.split("<br>").length;

			maxWidth = 0;
			for (int i=0; i<numLines; i++){
				if (lines[i].getBounds(messages[i]).width > maxWidth){
					maxWidth = (int) lines[i].getBounds(messages[i]).width;
				}
			}

			// assign local variables
			this.x = x - maxWidth/2;
			this.y = y + numLines * CHAR_HEIGHT/2;
			visible = true;
			return true;
		}
		return false;
	}
}
