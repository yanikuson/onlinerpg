package net.alcuria.online.client.screens;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.ItemManager;
import net.alcuria.online.client.NotificationList;
import net.alcuria.online.client.Player;
import net.alcuria.online.client.SaveHandler;
import net.alcuria.online.client.ui.SelectMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Select implements Screen {

	public AssetManager assets;
	private SpriteBatch batch;
	private Game myGame;
	private OrthographicCamera camera;
	
	private Texture title;
	private TextureRegion titleRegion;
	private Music bgm;
	
	String[] names;
	int[] dungeons;
	int[] levels;
	String[] times;
	
	SelectMenu menu;
	InputHandler inputs;

	public Select(Game g, AssetManager assets)
	{
		this.assets = assets;
		myGame = g;
		

		
	}
	
	public void render(float delta) {
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		batch.draw(titleRegion, 0, 0);
		menu.render(batch);
		
		batch.end();
		
		// ------------------------- 
		inputs.update(null, null, null);

		menu.update(inputs, 0, 0);
		
		if (menu.slot == 0){
			bgm.stop();
			myGame.setScreen(new Title(myGame, assets));

		} else if (menu.slot > 0){
			if (SaveHandler.fileExists(menu.slot)){
				
				// load all the data
				Player p = SaveHandler.loadPlayer(menu.slot, new NotificationList(), assets);
				ItemManager items = SaveHandler.loadItems(menu.slot);
				SaveHandler.loadFlags(menu.slot);
				
				bgm.stop();
				myGame.setScreen(new Field(myGame, assets, p, items, menu.slot));
			} else {
				myGame.setScreen(new Create(myGame, assets, menu.slot));
			}
		}
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 416, 240);
		
		batch = new SpriteBatch();
		title = new Texture(Gdx.files.internal("backgrounds/title.png"));
		titleRegion = new TextureRegion(title, 0, 0, Config.WIDTH, Config.HEIGHT);
		
		// get the data to display on the screens
		names = new String[3];
		dungeons = new int[3];
		times = new String[3];
		levels = new int[3];
		for (int i = 0; i < 3; i++){
			names[i] = SaveHandler.getPlayerName(i+1);
			dungeons[i] = 0;
			levels[i] = SaveHandler.getPlayerLevel(i+1);
			times[i] = "00:00";
		}
		
		menu = new SelectMenu(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), assets, names, levels, dungeons, times);
		menu.active = true;
		menu.drawDimmer = false;
		menu.slot = -1;
		inputs = new InputHandler(assets);
		
		
		bgm = assets.get("music/select.ogg", Music.class);
		bgm.setLooping(true);
		bgm.setVolume(Config.bgmVol);
		bgm.play();
		

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
