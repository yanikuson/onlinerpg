package net.alcuria.online.client.screens;

import net.alcuria.online.client.CameraManager;
import net.alcuria.online.client.Config;
import net.alcuria.online.client.DamageList;
import net.alcuria.online.client.DropManager;
import net.alcuria.online.client.GlobalFlags;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.ItemManager;
import net.alcuria.online.client.Map;
import net.alcuria.online.client.NotificationList;
import net.alcuria.online.client.ParticleList;
import net.alcuria.online.client.Player;
import net.alcuria.online.client.ui.HUD;
import net.alcuria.online.client.ui.Message;
import net.alcuria.online.client.ui.Menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Field implements Screen {

	public Game g;
	public int slot;					// which slot the player has loaded

	private SpriteBatch batch;
	public InputHandler inputs;
	public Player player;
	public DropManager drops;
	private DamageList damageList;
	public ParticleList explosions;
	public ParticleList slices;
	public ParticleList freezes;
	public ParticleList burns;
	public Message msgBox;
	private Map map;

	public CameraManager cameraManager;
	private HUD hud;
	private Menu menu;
	public ItemManager items;
	public AssetManager assets;
	public Rectangle viewport;
	public NotificationList notifications;

	float w;
	float h;
	float aspectRatio;
	long before, after;

	public Field(Game g, AssetManager assets, Player player, ItemManager items, int loadedSlot)
	{
		this.slot = loadedSlot;
		this.player = player;
		this.items = items;
		this.assets = assets;
		this.g = g;
	}

	@Override
	public void render(float delta) {
		if (assets.update()){

			//Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			batch.setProjectionMatrix(cameraManager.camera.combined);
			batch.begin();

			map.renderBG(batch, cameraManager);
			map.render(batch, true, cameraManager);
			player.render(batch);
			map.render(batch, false, cameraManager);

			freezes.render(batch);
			burns.render(batch);
			slices.render(batch);
			drops.render(batch);
			explosions.render(batch);

			map.renderFG(batch, cameraManager);
			damageList.render(batch);

			notifications.render(batch, cameraManager);
			if (GlobalFlags.flags[GlobalFlags.INTRO]){
				hud.render(batch);
			}
			msgBox.render(batch);
			menu.render(batch);
			inputs.render(batch);

			batch.end();

			//-------------------------------------------------------------

			// update the camera state FIRST
			cameraManager.update(player.bounds.x, player.bounds.y, map.width*map.tileWidth, map.height*map.tileWidth);

			// call the input handler to poll the keyboard's state
			inputs.update(player, map, cameraManager);

			// update for our message system
			hud.update(cameraManager.offsetX, cameraManager.offsetY);
			msgBox.update(Gdx.graphics.getDeltaTime(), inputs.typed[InputHandler.SPACE]);
			notifications.update();
			
			// we only want to call update on the actors if a messagebox/menu isn't open
			if (!msgBox.visible && !menu.active){
				
				if (!Config.npcCommand){
					menu.update(inputs, cameraManager.offsetX, cameraManager.offsetY, this);
				}
				
				// move all our actors: monsters, npcs, player
				drops.update(map);

				damageList.update();
				slices.update();
				burns.update();
				freezes.update();
				explosions.update();
				map.update();

				if (!map.pause){ 
					player.command(inputs);
					player.update(map);
				}


			}

		}

	}

	@Override
	public void resize(int width, int height) {


	}

	@Override
	public void show() {

		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		aspectRatio = w/h;

		cameraManager = new CameraManager();		

		batch = new SpriteBatch();

		// create notification handler
		notifications = new NotificationList();
		//notifications.add("Welcome to Heroes of Umbra!");

		damageList = new DamageList();

		player.playJump = true;
		player.effects.assignDamageList(damageList);

		inputs = new InputHandler(assets);


		// create our item manager
		drops = new DropManager(assets, notifications);

		msgBox = new Message(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), assets);
		menu = new Menu(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), assets, player, items, drops);
		menu.saveSlot = slot;
		map = new Map("beach", assets, damageList, this);

		if (!GlobalFlags.flags[GlobalFlags.INTRO]){
			player.bounds.x = 9 * Config.TILE_WIDTH;
			player.bounds.y = 3 * Config.TILE_WIDTH;
			map.npcs[0].start();
		}
		
		// create all the particles
		explosions = new ParticleList("sprites/kill.png",32, 32, 10, 2, false, assets);
		slices = new ParticleList("sprites/slice.png", 32, 32, 4, 2, false, assets);
		burns = new ParticleList("sprites/burn.png", 20, 20, 5, 3, false, assets);
		freezes = new ParticleList("sprites/ice.png", 24, 24, 21, 2, false, assets);

		// create our hud
		hud = new HUD(player);
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
		batch.dispose();
		player.dispose();

	}

}
