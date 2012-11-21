package net.alcuria.online.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class VisualEquip {

	public static final int width = 26;
	public static final int height = 34;
	public static final int paddingX = 7;
	public static final int paddingY = 2;
	
	private Texture sheet;
	private TextureRegion region;
	
	private int newX, newY, newWidth;
	
	public VisualEquip(String filename, AssetManager assets) {
		
		sheet = assets.get("sprites/equips/hair/1.png", Texture.class);
		region = new TextureRegion(sheet, 0, 0, 0, 0);
	}
	
	public void render(SpriteBatch batch, float x, float y, boolean flipX){
	
		if (region != null){
			if (!flipX){

				batch.draw(region, x - paddingX, y - paddingY, newWidth, height);

			} else {

				// draw the frame flipped
				if (newWidth > width){
					batch.draw(region, x+width, y, 0-width*2, height);
				} else {
					batch.draw(region, x+width, y, 0-width, height);
				}
			}
		}
		
	}
	
	public void update(TextureRegion region){
		
		newX = region.getRegionX()/region.getRegionWidth()*width;
		newY = region.getRegionY()/region.getRegionHeight()*height;
		
		newWidth = (region.getRegionWidth()/14)*width;
		
		this.region.setRegion(newX, newY, newWidth, height);
		
	}

}
