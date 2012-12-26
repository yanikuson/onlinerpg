package net.alcuria.online.client.screens;

import net.alcuria.online.client.Actor;
import net.alcuria.online.client.Animator;
import net.alcuria.online.client.Config;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.SaveHandler;
import net.alcuria.online.client.VisualEquip;
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
	private Actor a;
	
	float x, y;

	String[] names;
	int[] dungeons;
	int[] levels;
	String[] times;
	public Animator[] skins;
	public VisualEquip[] hairs;
	public VisualEquip[] armors;
	public VisualEquip[] weapons;
	public VisualEquip[] helmets;

	SelectMenu menu;
	InputHandler inputs;

	public Select(Game g, AssetManager assets)
	{
		a = new Actor("sprites/player.png", 0, 0, 16, 22, null);
		a.facingLeft = false;
		a.onGround = true;
		a.moving = true;
		a.attackSpeed = 0.1f;
		
		this.assets = assets;
		myGame = g;



	}

	public void render(float delta) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.draw(titleRegion, 0, 0);
		menu.render(batch);

		for (int i = 0; i < 3; i++){
			x = 125;
			y = 177 - 65*i;

			if (menu.selection[0] != i){
				batch.flush();
				batch.setColor(0.8f, 0.8f, 0.8f, 0.8f);
			}
			skins[i].render(batch, x, y);
			armors[i].render(batch, x, y, false);
			weapons[i].render(batch, x, y, false);
			hairs[i].render(batch, x, y, false);
			helmets[i].render(batch, x, y, false);
			if (menu.selection[0] != i){
				batch.flush();
				batch.setColor(1, 1, 1, 1);
			}
		}

		batch.end();

		// ------------------------- 
		inputs.update(null, null, null);
		menu.update(inputs, 0, 0);
		if (menu.slot == 0){
			bgm.stop();
			myGame.setScreen(new Title(myGame, assets));

		} else if (menu.slot > 0){
			if (SaveHandler.fileExists(menu.slot)){


				bgm.stop();
				myGame.setScreen(new Field(myGame, assets, menu.slot));
			} else {
				myGame.setScreen(new Create(myGame, assets, menu.slot));
			}
		}

		for (int i = 0; i < 3; i++){
			// update to walk or run pose
			if (menu.selection[0] == i){
				a.moving = true;
			} else {
				a.moving = false;
			}
			skins[i].update(a);
			hairs[i].update(skins[i].frame);
			weapons[i].update(skins[i].frame);
			armors[i].update(skins[i].frame);
			helmets[i].update(skins[i].frame);
			
			

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

		skins = new Animator[3];
		hairs = new VisualEquip[3];
		armors = new VisualEquip[3];
		weapons = new VisualEquip[3];
		helmets = new VisualEquip[3];

		for (int i = 0; i < 3; i++){

			names[i] = SaveHandler.getPlayerName(i+1);
			dungeons[i] = 0;
			levels[i] = SaveHandler.getPlayerLevel(i+1);
			times[i] = "00:00";

			skins[i] = new Animator(SaveHandler.getPlayerSkinFilename(i+1), 14, 22, assets);
			hairs[i] = new VisualEquip(SaveHandler.getPlayerHairFilename(i+1), assets);
			armors[i] = new VisualEquip(SaveHandler.getPlayerArmorFilename(i+1), assets);
			weapons[i] = new VisualEquip(SaveHandler.getPlayerWeaponFilename(i+1), assets);
			helmets[i] = new VisualEquip(SaveHandler.getPlayerHelmetFilename(i+1), assets);

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
