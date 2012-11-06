package net.alcuria.online.client.screens;

import net.alcuria.online.client.Background;
import net.alcuria.online.client.CameraManager;
import net.alcuria.online.client.CollisionManager;
import net.alcuria.online.client.DamageList;
import net.alcuria.online.client.DropManager;
import net.alcuria.online.client.Foreground;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.Item;
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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Field implements Screen {

	// uncomment if I ever need this?
	//private Game myGame;

	private SpriteBatch batch;
	private InputHandler inputs;
	private Player player;
	private DropManager drops;
	//private PacketHandler packetHandler;
	private CollisionManager collisions;
	private DamageList damageList;
	private ParticleList explosions;
	private ParticleList slices;
	private ParticleList burns;
	private Message msgBox;
	private Map map;
	private Background bg;
	private Foreground fg;
	private CameraManager cameraManager;
	private HUD hud;
	private Menu menu;
	public ItemManager items;
	public AssetManager assets;
	public Rectangle viewport;
	public NotificationList notifications;
	private Music bgm;

	float w;
	float h;
	float aspectRatio;
	long before, after;

	public Field(Game g, AssetManager assets)
	{
		this.assets = assets;
		//myGame = g;

	}

	@Override
	public void render(float delta) {

		if (assets.update()){
			
			//Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			batch.setProjectionMatrix(cameraManager.camera.combined);
			batch.begin();
					
			bg.render(batch, cameraManager);
			map.render(batch, true, cameraManager);
			//npc.render(batch);
			player.render(batch);
			map.render(batch, false, cameraManager);
			
			burns.render(batch);
			slices.render(batch);
			drops.render(batch);
			explosions.render(batch);

			fg.render(batch, cameraManager);
			damageList.render(batch);
			
			notifications.render(batch, cameraManager);
			hud.render(batch);
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
			msgBox.update(Gdx.graphics.getDeltaTime(), inputs.typed[InputHandler.ATTACK]);
			menu.update(inputs, cameraManager.offsetX, cameraManager.offsetY);
			notifications.update();

			// we only want to call update on the actors if a messagebox/menu isn't open
			if (!msgBox.visible && !menu.active){			

				// move all our actors: monsters, npcs, player
				drops.update(map);
				collisions.update(map, damageList, explosions, items);
				//npc.command(map, player);
				//npc.update(map);
				player.command(inputs);
				player.update(map);

				fg.update(Gdx.graphics.getDeltaTime());
				damageList.update();
				slices.update();
				burns.update();
				explosions.update();
				map.update(player, inputs);
				
				
				

			}

			// call our network handler
			// packetHandler.update(Gdx.graphics.getDeltaTime());
			
//			// TODO: put this in the collision manager
//			if (inputs.typed[InputHandler.ATTACK] && player.bounds.overlaps(npc.bounds)){
//				inputs.typed[InputHandler.ATTACK] = false;
//				msgBox.startMessage("Hello! You must be lost. Don't be afraid of the slimes.\nThey don't do much damage!", (int)cameraManager.offsetX + Message.h_center, (int)cameraManager.offsetY + 200);
//			}
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
		notifications.add("Welcome to Heroes of Umbra!");
		notifications.add("Z = Jump");
		notifications.add("X = Attack");
		notifications.add("Arrow Keys = Move");
		notifications.add("ESC = Menu");

		damageList = new DamageList();
		
		player = new Player("sprites/player.png", 160, 120, 14, 22, notifications, assets);
		player.playJump = true;
		player.effects.assignDamageList(damageList);
		//npc = new Monster("sprites/player.png", 14, 22, Config.MON_NPC, assets);
		//npc.spawn(300/16, 100/16);

		inputs = new InputHandler(assets);
		bg = new Background("backgrounds/forest.png", assets);
		fg = new Foreground("backgrounds/fog.png", assets);
		//packetHandler = new PacketHandler();
		
		// create our item manager
		items = new ItemManager();
		items.add(Item.ID_POTION);
		items.add(Item.ID_POTION);
		items.add(Item.ID_SPEED_PILL);
		items.add(Item.ID_WOOD_SWORD);
		drops = new DropManager(assets, notifications);
		
		msgBox = new Message(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), assets);
		menu = new Menu(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), assets, player, items, drops);
		map = new Map("tiles/forest.png", "forest1", assets);

		// create all the particles
		explosions = new ParticleList("sprites/kill.png",32, 32, 10, 2, false, assets);
		slices = new ParticleList("sprites/slice.png", 32, 32, 4, 2, false, assets);
		burns = new ParticleList("sprites/burn.png", 20, 20, 5, 3, false, assets);

		// create the manager for collisions
		collisions = new CollisionManager(player, map.spawner.monsterList, drops, slices, burns);
		
		// create our hud
		hud = new HUD(player);
		
		// play awesome bgm
		bgm = assets.get("music/forest.ogg", Music.class);
		bgm.setLooping(true);
		bgm.setVolume(0.8f);
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
		batch.dispose();
		player.dispose();

	}

}
