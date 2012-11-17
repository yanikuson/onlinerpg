package net.alcuria.online.client.ui;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HUD {

	public Player player;
	public Texture fullHUD;
	public TextureRegion hud;
	public TextureRegion hp;
	public TextureRegion ep;
	public TextureRegion exp;

	public TextureRegion numbers[];
	public int[] levelArray;
	public int[] hpArray;
	public int multiplier = 0;
	public int index = 0;

	public int x = -100;
	public int y = -100;

	public HUD(Player player){

		this.player = player;

		// load the full texture
		fullHUD = new Texture(Gdx.files.internal("ui/hud-player.png"));

		// create subcomponents
		hud = new TextureRegion(fullHUD, 0, 0, 96, 32);
		hp = new TextureRegion(fullHUD, 0, 32, 1, 5);
		ep = new TextureRegion(fullHUD, 16, 32, 2, 2);
		exp = new TextureRegion(fullHUD, 32, 32, 1, 1);
		
		// hacky numbers
		numbers = new TextureRegion[11];
		for (int i=0; i<numbers.length; i++){
			numbers[i] = new TextureRegion(fullHUD, 5*i, 48, 4, 6);
		}

		// display arrays
		levelArray = new int[2];
		hpArray = new int[9];

		for (int i = 0; i < levelArray.length; i++) {
			levelArray[i] = -1;
		}
		for (int i = 0; i < hpArray.length; i++) {
			hpArray[i] = -1;
		}

	}

	public void update(float camX, float camY){
		// update HUD coords
		x = (int) camX;
		y = (int) (camY + Config.HEIGHT - 32);

		// update scrolling HP
		
		// update arrays
		if (player.lvl > 9){
			levelArray[0] = player.lvl/10;
		}
		levelArray[1] = player.lvl%10;

		for (int i=0; i<hpArray.length; i++){
			hpArray[i] = -1;
		}
		multiplier = 1000;
		index = 0;
		while(multiplier > 0){
			if (player.HP >= multiplier){
				hpArray[index] = player.HP/multiplier%10;
				index++;
			}
			multiplier /= 10;
		}
		hpArray[index] = 10;
		index++;
		multiplier = 1000;
		while(multiplier > 0){
			if (player.maxHP >= multiplier){
				hpArray[index] = player.maxHP/multiplier%10;
				index++;
			}
			multiplier /= 10;
		}
	}

	public void render(SpriteBatch batch){
		
		batch.draw(hud, x, y);
		batch.draw(hp, x+24, y+16, (player.HP*58)/player.maxHP, 5);
		for (int i=0; i<player.EP; i++){
			batch.draw(ep, x+49+i*3, y+9, 2, 2);
		}
		batch.draw(exp, x+24, y+4, player.curEXP*55/player.neededEXP, 1);
		
		for (int i=0; i<levelArray.length; i++){
			if (levelArray[i] > -1 && levelArray[i] < numbers.length){
				batch.draw(numbers[levelArray[i]], x + 14 + 4*i, y + 2);
			}
		}
		
		for (int i=0; i<hpArray.length; i++){
			if (hpArray[i] > -1 && hpArray[i] < numbers.length){
				batch.draw(numbers[hpArray[i]], x + 36 + 4*i, y + 23);
			}
		}

	}

}
