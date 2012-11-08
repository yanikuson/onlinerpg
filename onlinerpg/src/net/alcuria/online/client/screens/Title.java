package net.alcuria.online.client.screens;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.ui.Menu;
import net.alcuria.online.client.ui.TitleMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Title implements Screen {


	public AssetManager assets;
	private SpriteBatch batch;
	private Game myGame;
	private OrthographicCamera camera;
	
	private Texture title;
	private TextureRegion titleRegion;
	private Music bgm;
	
	TitleMenu menu;
	InputHandler inputs;
	
	public Title(Game g, AssetManager assets)
	{
		this.assets = assets;
		myGame = g;
	}
	
	@Override
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

		
		if (menu.single){
			bgm.stop();
			myGame.setScreen(new Field(myGame, assets));
			
		}
		/*if(Gdx.input.justTouched() || Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.ENTER)){
			bgm.stop();
			myGame.setScreen(new Field(myGame, assets));
			dispose();
		}
		*/

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
		
		menu = new TitleMenu(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), assets);
		menu.active = true;
		menu.drawDimmer = false;
		inputs = new InputHandler(assets);
		
		bgm = assets.get("music/title.ogg", Music.class);
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
		batch.dispose();

	}

}
