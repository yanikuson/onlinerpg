package net.alcuria.online.client.screens;

import net.alcuria.online.client.Config;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.SaveHandler;
import net.alcuria.online.client.TypeHandler;
import net.alcuria.online.client.ui.CreateMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Create implements Screen {

	public int id;
	public AssetManager assets;
	private SpriteBatch batch;
	private Game myGame;
	private OrthographicCamera camera;
	
	private Texture title;
	private TextureRegion titleRegion;
	
	CreateMenu menu;
	InputHandler inputs;
	TypeHandler type;

	public Create(Game g, AssetManager assets, int id)
	{
		this.id = id;
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
		if (menu.depth == 1 && menu.selection[0] == 0){
			type.update();
		}
		menu.update(inputs, type, 0, 0);
		
		if(menu.leaveMenu){
			myGame.setScreen(new Select(myGame, assets));
		} else if (menu.createFile){

			SaveHandler.createPlayer(id, menu.name);
			myGame.setScreen(new Select(myGame, assets));
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
		
		menu = new CreateMenu(new Texture(Gdx.files.internal("ui/msg-bg.png")), new Texture(Gdx.files.internal("ui/msg-border.png")), assets);
		menu.active = true;
		menu.drawDimmer = false;
		inputs = new InputHandler(assets);
		type = new TypeHandler(assets);
		
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
