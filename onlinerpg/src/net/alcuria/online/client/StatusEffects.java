package net.alcuria.online.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StatusEffects {

	public static final int POISON = 1;
	public static final int REGEN = 2;
	public static final int HEAL = 3;

	public static final int MAX_EFFECTS = 4;
	public static final int MAX_DURATION = 5;
	public static final float EFFECT_FREQUENCY = 0.5f;

	public float[] timer;
	public float[] subTimer;
	public int[] severity;
	public float[] frequency;

	public DamageList damageList;
	public Particle healSparkle;
	public Sound heal;
	
	public StatusEffects(Actor actor, AssetManager assets) {

		timer = new float[MAX_EFFECTS];
		subTimer = new float[MAX_EFFECTS];
		severity = new int[MAX_EFFECTS];
		frequency = new float[MAX_EFFECTS];

		for (int i = 0; i < MAX_EFFECTS; i++) {
			timer[i] = 0;
			subTimer[i] = 0;
			severity[i] = 0;
			frequency[i] = 1f;
		}

		frequency[HEAL] = 0.2f;			// heal is instant!

		healSparkle = new Particle("sprites/sparkle.png", 0, 0, 25, 25, 5, 5, false, assets);
		heal = assets.get("sounds/heal.wav", Sound.class);
	}

	public void assignDamageList(DamageList damageList){
		this.damageList = damageList;
	}

	public void update(Actor actor){
		for (int i = 0; i < MAX_EFFECTS; i++) {

			// check if the effect is active
			if (timer[i] > 0) {
				timer[i] -= Gdx.graphics.getDeltaTime();
				subTimer[i]+= Gdx.graphics.getDeltaTime();
				if (subTimer[i] > frequency[i]) {
					doEffect(i, actor, damageList);
					subTimer[i] = 0;
				}

				if (timer[i] <= 0){
					remove(i);
				}
			}

		}

		healSparkle.update(actor.bounds.x-5, actor.bounds.y, true);

	}

	private void doEffect(int effectType, Actor actor, DamageList damageList) {
		switch (effectType) {
		case POISON:
			actor.flash(0, 0.7f, 0, 1, 1f);
			actor.HP -= severity[effectType];
			damageList.start(severity[effectType], actor.bounds.x, actor.bounds.y, actor.facingLeft, Damage.TYPE_HURT);
			break;

		case REGEN:
			break;

		case HEAL:
			heal.play(Config.sfxVol);
			healSparkle.start(actor.bounds.x, actor.bounds.y, false);
			actor.flash(1, 1, 0, 1, 1);
			actor.HP += severity[effectType];
			if (actor.HP > actor.maxHP) {
				actor.HP = actor.maxHP;
			}
			damageList.start(severity[effectType], actor.bounds.x, actor.bounds.y, actor.facingLeft, Damage.TYPE_HEAL);
			timer[effectType] = 0;
			break;
		}

	}

	public void render(SpriteBatch batch){
		healSparkle.render(batch);
	}

	public void remove(int effectType){
		switch (effectType) {
		case POISON:
			break;
		}

	}

	public void add(int effect, int severity){
		this.timer[effect] = MAX_DURATION;
		this.severity[effect] = severity;
	}
}
