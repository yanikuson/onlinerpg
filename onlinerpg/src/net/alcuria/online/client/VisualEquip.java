package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class VisualEquip {

	public static final int width = 26+6;
	public static final int height = 34+2;
	public static final int paddingX = 13;
	public static final int paddingY = 4;

	private Texture sheet;
	private TextureRegion region;

	private int newX, newY, newWidth;
	private AssetManager assets;

	public VisualEquip(String filename, AssetManager assets) {
		this.assets = assets;
		
		if(Gdx.files.internal(filename).exists()){
			sheet = assets.get(filename, Texture.class);
		} else {
			sheet = assets.get("sprites/equips/empty.png", Texture.class);
		}
		region = new TextureRegion(sheet, 0, 0, 0, 0);

	}

	public void changeTexture(String filename){

		if(Gdx.files.internal(filename).exists()){
			sheet = assets.get(filename, Texture.class);
		} else {
			System.out.println("texture not found: " + filename);
		}
		region = new TextureRegion(sheet, 0, 0, 0, 0);


	}

	public void render(SpriteBatch batch, float x, float y, boolean flipX){

		if (region != null){
			if (!flipX){

				batch.draw(region, x - paddingX + 6, y - paddingY, newWidth, height);

			} else {

				// draw the frame flipped
				if (newWidth > width){
					batch.draw(region, x + width - paddingX + 2, y - paddingY, 0-width*2, height);
				} else {
					batch.draw(region, x + width - paddingX + 2, y - paddingY, 0-width, height);
				}
			}
		}

	}

	public void update(TextureRegion region){


		if (region != null) {
			newX = region.getRegionX()/14*width;
			if (region.getRegionX() > 0 && region.getRegionY() > 0){
				newX += 5;
			}
			newY = region.getRegionY()/region.getRegionHeight()*height;

			newWidth = (region.getRegionWidth()/14)*width;

			this.region.setRegion(newX, newY, newWidth, height);
		}
	}

}
