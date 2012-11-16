package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SkillManager {
	
	public static final int WOUND = 0;
	public static final int SLAM = 1;
	public static final int THROW = 2;
	public static final int CHARGE = 3;
	
	public static final int FIREBALL = 4;
	public static final int FREEZE = 5;
	public static final int POISON = 6;
	public static final int BOLT = 7;
	
	public static final int HEAL = 8;
	public static final int BARRIER = 9;
	public static final int HASTE = 10;
	public static final int REGEN = 11;
	
	public static int hotkey1 = FREEZE;
	public static int hotkey2 = FIREBALL;
	public static int hotkey3 = FIREBALL;
	
	public int id;
	public int[] levels;
	public Rectangle area;
	public float duration;					// length the skill boundaries are displayed
	
	public float xVel, yVel;				// skill bounds' x and y velocity
	public int xOffset, yOffset;			// animation's x/y offsets from bounds
	public int activeSkill = 0;				// id of the active skill
	public boolean visible = false;			// is the skill active?
	public boolean harmful = false;			// does it harm the enemies?
	public boolean loop = false;			// does the skill animation loop?
	
	public Particle fireball;
	public Particle ice;
	public Particle swing;
	public Particle activeParticle;			// pointer to the active particle
	
	public Player p;
	public Sound cast;

	public Texture debugPoint;
	public AssetManager assets;
	
	public SkillManager(AssetManager assets, Player p){
		this.assets = assets;
		this.debugPoint = assets.get("sprites/point.png", Texture.class);
		this.area = new Rectangle(0, 0, 0, 0);
		this.p = p;
		
		levels = new int[12];
		for (int i=0; i < levels.length; i++){
			levels[i] = 0;
		}
	
		swing = new Particle("sprites/swing.png", 0, 0, 84, 84, 7, 3, false, assets);
		fireball = new Particle("sprites/fireball.png", 0, 0, 16, 16, 2, 3, false, assets);
	}
	
	public void start(int skillID){
		this.id = skillID;
	
		// determine which skill to play
		switch (skillID){
		
		case WOUND:
			
			if (p.facingLeft) {
				xOffset = -45;
			} else {
				xOffset = -25;
			}
			yOffset = -20;
			xVel = 5;
			yVel = 0;
			area.x = p.bounds.x;
			area.y = p.bounds.y;
			area.width = p.bounds.width;
			area.height = (float) (1.2 * p.bounds.height);
			visible = true;
			duration = 0.20f;
			harmful = true;
			loop = false;
			cast = assets.get("sounds/shoot_flame.wav", Sound.class);
			
			activeParticle = swing;
			activeParticle.loop = false;
			activeParticle.start(area.x, area.y, p.facingLeft);
			break;
			
		case FIREBALL:
			xOffset = 0;
			yOffset = 0;
			xVel = 4;
			yVel = 0;
			area.width = 16;
			area.height = 16;
			area.x = p.bounds.x;
			area.y = p.bounds.y;
			visible = true;
			duration = 1;
			harmful = true;
			loop = true;
			cast = assets.get("sounds/shoot_flame.wav", Sound.class);
			
			// start the particle
			activeParticle = fireball;
			activeParticle.loop = loop;
			activeParticle.start(area.x, area.y, p.facingLeft);
			break;
			
		case FREEZE:
			area.width = 60;
			area.height = 30;
			if (p.facingLeft) {
				area.x = p.bounds.x + p.bounds.width - area.width;
				area.y = p.bounds.y;
			} else {
				area.x = p.bounds.x;
				area.y = p.bounds.y;
			}
			cast = assets.get("sounds/shoot_flame.wav", Sound.class);
			visible = true;
			xVel = 0;
			yVel = 0;
			duration = 0.2f;
			activeParticle = null;
			harmful = true;
			break;
		}
		
		if (p.facingLeft) xVel *= -1;	
		cast.play(Config.sfxVol);
		
	}
	
	public void update() {
		
		if (visible){
			// update particle and bounds pos
			area.x += xVel;
			area.y += yVel;
			
			if (activeParticle != null)	activeParticle.update(area.x+xOffset, area.y+yOffset, true);
			
			// see if particle is done playing 
			duration -= Gdx.graphics.getDeltaTime();
			if (duration <= 0){
				area.x = 0;
				area.y = 0;
				area.width = 0;
				area.height = 0;
				visible = false;
			}
		}
	}
	
	public void render(SpriteBatch batch) {
		
		
		batch.draw(debugPoint, area.x, area.y);
		batch.draw(debugPoint, area.x+area.width, area.y);
		batch.draw(debugPoint, area.x, area.y+area.height);
		batch.draw(debugPoint, area.x+area.width, area.y+area.height);
		
		
		if (activeParticle != null && visible){
			activeParticle.render(batch);
		}
	}
	
}
