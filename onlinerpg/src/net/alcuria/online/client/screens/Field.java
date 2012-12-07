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
import net.alcuria.online.client.SaveHandler;
import net.alcuria.online.client.SaveThread;
import net.alcuria.online.client.Transition;
import net.alcuria.online.client.connection.ClientThread;
import net.alcuria.online.client.ui.HUD;
import net.alcuria.online.client.ui.Message;
import net.alcuria.online.client.ui.Menu;
import net.alcuria.online.client.ui.ShopMenu;
import net.alcuria.online.server.GameServer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Field implements Screen {

	public Game g;
	public int slot;					// which slot the player has loaded

	public SaveThread t;
	public SpriteBatch batch;
	public InputHandler inputs;
	public Player player;
	public DropManager drops;
	public DamageList damageList;
	public ParticleList explosions;
	public ParticleList slices;
	public ParticleList freezes;
	public ParticleList burns;
	public Message msgBox;
	public Map map;

	public ShopMenu shop;
	public CameraManager cameraManager;
	public HUD hud;
	public Menu menu;
	public ItemManager inventory;
	public AssetManager assets;
	public NotificationList notifications;

	private static final int VIRTUAL_WIDTH = Config.WIDTH;
	private static final int VIRTUAL_HEIGHT = Config.HEIGHT;
	private static final float ASPECT_RATIO =
			(float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;
	private Rectangle viewport;

	public Array<Player> players;

	public Field(Game g, AssetManager assets, int loadedSlot)
	{
		this.slot = loadedSlot;
		this.assets = assets;
		this.g = g;
	}
	public Field(){

	}

	@Override
	public void render(float delta) {

		if (assets.update()){

			Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			batch.setProjectionMatrix(cameraManager.camera.combined);
			batch.begin();

			map.renderBG(batch, cameraManager);

			map.render(batch, true, cameraManager);
			for (int i = 0; i < players.size; i++){
				players.get(i).render(batch);
			}
			player.render(batch);
			map.render(batch, false, cameraManager);

			freezes.render(batch);
			burns.render(batch);
			slices.render(batch);
			drops.render(batch);
			explosions.render(batch);

			map.renderFG(batch, cameraManager);
			damageList.render(batch);

			NotificationList.render(batch, cameraManager);
			if (GlobalFlags.flags[GlobalFlags.INTRO]){
				hud.render(batch);
			}

			msgBox.render(batch);
			shop.render(batch);
			menu.render(batch);
			inputs.render(batch);

			Transition.render(batch, cameraManager);

			batch.end();

			//-------------------------------------------------------------

			// update the camera state FIRST
			cameraManager.update(player.bounds.x, player.bounds.y, map.width*map.tileWidth, map.height*map.tileWidth);

			// call the input handler to poll the keyboard's state
			inputs.update(player, map, cameraManager);

			// update our UI
			hud.update(cameraManager.offsetX, cameraManager.offsetY);
			msgBox.update(Gdx.graphics.getDeltaTime(), inputs);
			NotificationList.update();
			Transition.update(map.bgm);
			if (!Config.npcCommand){
				menu.update(inputs, cameraManager.offsetX, cameraManager.offsetY, this);
			}

			// we only want to call update on the actors if a messagebox/menu isn't open
			if (!msgBox.visible && !menu.active && Transition.finished){

				// before we step anything, check if it's on a platform
				player.checkIfOnMovingPlatform(map);

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

				for (int i = 0; i < players.size; i++){
					players.get(i).networkUpdate();
					players.get(i).update(map);
				}


			}
			player.updateEquips();

		}

	}

	@Override
	public void resize(int width, int height) {

		// calculate new viewport
		float aspectRatio = (float)width/(float)height;
		float scale = 1f;
		Vector2 crop = new Vector2(0f, 0f);

		if(aspectRatio > ASPECT_RATIO)
		{
			scale = (float)height/(float)VIRTUAL_HEIGHT;
			crop.x = (width - VIRTUAL_WIDTH*scale)/2f;
		}
		else if(aspectRatio < ASPECT_RATIO)
		{
			scale = (float)width/(float)VIRTUAL_WIDTH;
			crop.y = (height - VIRTUAL_HEIGHT*scale)/2f;
		}
		else
		{
			scale = (float)width/(float)VIRTUAL_WIDTH;
		}

		float w = (float)VIRTUAL_WIDTH*scale;
		float h = (float)VIRTUAL_HEIGHT*scale;
		viewport = new Rectangle(crop.x, crop.y, w, h);
	}

	@Override
	public void show() {


		// create all the particles
		explosions = new ParticleList("sprites/kill.png",32, 32, 10, 2, false, assets);
		slices = new ParticleList("sprites/slice.png", 32, 32, 4, 2, false, assets);
		burns = new ParticleList("sprites/burn.png", 20, 20, 5, 3, false, assets);
		freezes = new ParticleList("sprites/ice.png", 24, 24, 21, 2, false, assets);

		notifications = new NotificationList();
		//notifications.add("Welcome to Heroes of Umbra!");

		player = SaveHandler.loadPlayer(slot, this);
		inventory = SaveHandler.loadItems(slot);
		SaveHandler.loadFlags(slot);

		player.animation.assignPlayer(player);
		player.resetVisualEquips();

		cameraManager = new CameraManager();		

		batch = new SpriteBatch();

		// create notification handler

		damageList = new DamageList();

		player.playJump = true;
		player.effects.assignDamageList(damageList);

		inputs = new InputHandler(assets);


		// create our item manager
		drops = new DropManager(this, notifications);

		msgBox = new Message(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), assets);
		menu = new Menu(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), assets, player, inventory, drops);
		menu.saveSlot = slot;
		map = new Map(player.currentMap, assets, damageList, this);

		if (!GlobalFlags.flags[GlobalFlags.INTRO]){
			player.bounds.x = 9 * Config.TILE_WIDTH;
			player.bounds.y = 3 * Config.TILE_WIDTH;
			map.npcs[0].start();
		}



		// create our hud
		hud = new HUD(player);

		// create a default shop
		shop = new ShopMenu(this, new ItemManager());
		shop.active = false;

		player.resetVisualEquips();

		players = new Array<Player>();

		// launch our save thread
		(new Thread(new SaveThread(this, slot))).start();
		(new Thread(new ClientThread(this, slot))).start();

		if (Config.IP.equals("127.0.0.1")){
			GameServer.f = this;
			GameServer.start();
		}

		Transition.fadeIn(1.0f);

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
