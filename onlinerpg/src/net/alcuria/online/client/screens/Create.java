package net.alcuria.online.client.screens;

import net.alcuria.online.client.Actor;
import net.alcuria.online.client.Animator;
import net.alcuria.online.client.Config;
import net.alcuria.online.client.InputHandler;
import net.alcuria.online.client.SaveHandler;
import net.alcuria.online.client.TypeHandler;
import net.alcuria.online.client.VisualEquip;
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

	public int skin = 0;
	public int gender = 0;
	public int hair = 0;
	
	public Actor a;
	public Animator[] sprites;
	public VisualEquip hairVisual;
	public VisualEquip armorVisual;

	public Create(Game g, AssetManager assets, int id)
	{
		a = new Actor("sprites/player.png", 0, 0, 16, 22, null);
		a.facingLeft = false;
		a.onGround = true;
		a.moving = true;
		a.attackSpeed = 0.1f;
		
		this.id = id;
		this.assets = assets;
		myGame = g;

		sprites = new Animator[3];
		for (int i = 0; i < sprites.length; i++){
			sprites[i] = new Animator(("sprites/equips/skin/" + (i+1) + ".png"), 14, 22, assets);
		}
		
		hairVisual = new VisualEquip("sprites/equips/hair/1.png", assets);
		armorVisual = new VisualEquip("sprites/equips/armor/1.png", assets);


	}

	public void render(float delta) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.draw(titleRegion, 0, 0);
		menu.render(batch);
		sprites[skin].render(batch, 268, 112);
		armorVisual.render(batch, 268, 112, false);
		hairVisual.render(batch, 268, 112, false);
		
		batch.end();

		// ------------------------- 
		inputs.update(null, null, null);
		if (menu.depth == 1 && menu.selection[0] == 0){
			type.update();
		}
		menu.update(inputs, type, 0, 0);
		if (skin != menu.skin) {
			skin = menu.skin;
		}
		if (gender != menu.gender){
			gender = menu.gender;
		} 
		if (hair != menu.hair){
			hair = menu.hair;
			hairVisual.changeTexture("sprites/equips/hair/" + (menu.hair+1) + ".png");
		}
		
		sprites[skin].update(a);
		hairVisual.update(sprites[skin].frame);
		armorVisual.update(sprites[skin].frame);

		if(menu.leaveMenu){
			myGame.setScreen(new Select(myGame, assets));
		} else if (menu.createFile){

			SaveHandler.createPlayer(id, menu.name, menu.gender, menu.skin, menu.hair);
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
