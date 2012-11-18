package net.alcuria.online.client.screens;

import net.alcuria.online.client.Config;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Loading implements Screen
{

	private Sound coin;
	private SpriteBatch batch;
	private Texture splash;
	private TextureRegion[] splashFrames;
	private TextureRegion loadingBar;
	
	private Game myGame;
	private OrthographicCamera camera;
	public AssetManager assets;
	
	private int frameCounter = 0;

	
	public Loading(Game g)
	{
		myGame = g;
	}


	@Override
	public void render(float delta)
	{
		// RENDER FUNCTIONS
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		// check if we should draw the animation sequence or just stick to the last frame in the animation (while loading)
		if (frameCounter/2 < splashFrames.length){
			batch.draw(splashFrames[frameCounter/2], Config.WIDTH/2 - splashFrames[0].getRegionWidth()/2, Config.HEIGHT/2-splashFrames[0].getRegionHeight()/2);
		} else if (frameCounter < 80 || assets.getProgress() < 1){
			batch.draw(splashFrames[9], Config.WIDTH/2 - splashFrames[0].getRegionWidth()/2, Config.HEIGHT/2-splashFrames[0].getRegionHeight()/2);
		}
		
		// if we are done loading, go to the title screen
		if (assets.update() && frameCounter > 80){
			batch.end();
			myGame.setScreen(new Title(myGame, assets));
			
		} else {
			// else draw the loading bar
			batch.draw(loadingBar, 0, 0, assets.getProgress()*Config.WIDTH+1, 1);
			batch.end();
		}
		
		frameCounter++;
		if (frameCounter == 14){
			coin.play(Config.sfxVol);
		}

	}

	@Override
	public void show()
	{

		// initialize our tools for viewing the loading screen
		assets = new AssetManager();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Config.WIDTH, Config.HEIGHT);
		batch = new SpriteBatch();
		
		// create the splash screen components
		coin = Gdx.audio.newSound(Gdx.files.internal("sounds/coin.wav"));
		splash = new Texture(Gdx.files.internal("sprites/logo.png"));
		splashFrames = new TextureRegion[10];
		for (int i = 0; i < splashFrames.length; i++){
			splashFrames[i] = new TextureRegion(splash, 0, 8*i, 50, 8);
		}
		loadingBar = new TextureRegion(splash,0,80,1,1);
		
		
		// queue up assets to load

		// SOUND EFFECTS
		assets.load("sounds/talk.wav", Sound.class);
		assets.load("sounds/collect.wav", Sound.class);
		assets.load("sounds/equip.wav", Sound.class);
		assets.load("sounds/hurt.wav", Sound.class);
		assets.load("sounds/hurt_enemy.wav", Sound.class);
		assets.load("sounds/jump.wav", Sound.class);
		assets.load("sounds/kill.wav", Sound.class);
		assets.load("sounds/swing.wav", Sound.class);
		assets.load("sounds/levelup.wav", Sound.class);
		assets.load("sounds/shoot.wav", Sound.class);
		assets.load("sounds/cast.wav", Sound.class);
		assets.load("sounds/cursor_move.wav", Sound.class);
		assets.load("sounds/cursor_open.wav", Sound.class);
		assets.load("sounds/cursor_cancel.wav", Sound.class);
		assets.load("sounds/cursor_select.wav", Sound.class);
		assets.load("sounds/addstat.wav", Sound.class);
		assets.load("sounds/pickup.wav", Sound.class);
		assets.load("sounds/drop.wav", Sound.class);
		assets.load("sounds/heal.wav", Sound.class);
		assets.load("sounds/shoot_flame.wav", Sound.class);
		assets.load("sounds/shoot_ice.wav", Sound.class);
		
		// BGM
		assets.load("music/forest.ogg", Music.class);
		assets.load("music/title.ogg", Music.class);
		assets.load("music/select.ogg", Music.class);
		assets.load("music/beach.ogg", Music.class);
		assets.load("music/victory.ogg", Music.class);

		
		// SPRITES
		assets.load("sprites/burn.png", Texture.class);
		assets.load("sprites/drops.png", Texture.class);
		assets.load("sprites/fireball.png", Texture.class);
		assets.load("sprites/eye.png", Texture.class);
		assets.load("sprites/kill.png", Texture.class);
		assets.load("sprites/levelup.png", Texture.class);
		assets.load("sprites/logo.png", Texture.class);
		assets.load("sprites/player.png", Texture.class);
		assets.load("sprites/point.png", Texture.class);
		assets.load("sprites/projectile.png", Texture.class);
		assets.load("sprites/slice.png", Texture.class);
		assets.load("sprites/slime.png", Texture.class);
		assets.load("sprites/swing.png", Texture.class);
		assets.load("sprites/sparkle.png", Texture.class);
		assets.load("sprites/cast.png", Texture.class);
		assets.load("sprites/ice.png", Texture.class);


		
		// BACKGROUNDS
		assets.load("backgrounds/sky.png", Texture.class);
		assets.load("backgrounds/fog.png", Texture.class);
		assets.load("backgrounds/forest.png", Texture.class);
		assets.load("backgrounds/title.png", Texture.class);
		
		// TILES
		assets.load("tiles/forest.png", Texture.class);
		
		// UI
		assets.load("ui/buttons.png", Texture.class);
		assets.load("ui/hud-player.png", Texture.class);
		assets.load("ui/msg-bg.png", Texture.class);
		assets.load("ui/msg-border.png", Texture.class);
	}

	@Override
	public void dispose() {
		batch.dispose();
		splash.dispose();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
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


}
