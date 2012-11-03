package net.alcuria.online.client.screens;

import net.alcuria.online.client.Config;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
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
		
		batch.end();

		if(Gdx.input.justTouched() || Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.ENTER)){
			myGame.setScreen(new Field(myGame, assets));
			dispose();
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
