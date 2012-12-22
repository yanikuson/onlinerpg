package net.alcuria.online.client;

import net.alcuria.online.client.screens.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StatusEffects {

	public static final int POISON = 1;
	public static final int REGEN = 2;
	public static final int HEAL = 3;
	public static final int SPEED = 4;
	public static final int FREEZE = 5;

	public static final int MAX_EFFECTS = 10;
	public static final int MAX_DURATION = 5;
	public static final float EFFECT_FREQUENCY = 0.5f;


	public float[] timer;
	public float[] subTimer;
	public int[] severity;
	public float[] frequency;

	public DamageList damageList;
	public Particle healSparkle;
	public Sound heal;

	public Actor actor;

	public StatusEffects(Actor actor, Field f) {

		this.actor = actor;

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
		frequency[SPEED] = 1.0f;
		frequency[FREEZE] = 0.0f;

		healSparkle = new Particle("sprites/sparkle.png", 0, 0, 25, 25, 5, 5, false, f.assets);
		heal = f.assets.get("sounds/heal.wav", Sound.class);
		
		this.damageList = f.damageList;

	}


	public void update(Actor actor){
		for (int i = 0; i < MAX_EFFECTS; i++) {

			// check if the effect is active
			if (timer[i] > 0) {
				timer[i] -= Gdx.graphics.getDeltaTime();
				subTimer[i]+= Gdx.graphics.getDeltaTime();
				if (subTimer[i] > frequency[i]) {
					doEffect(i, actor, this.damageList);
					subTimer[i] = 0;
				}

				if (timer[i] <= 0){
					remove(i);
				}
			}

		}

		healSparkle.update(actor.bounds.x-5, actor.bounds.y, true);

	}

	// this is called every few ticks, so things like psn damage, etc go here
	private void doEffect(int effectType, Actor actor, DamageList damageList) {
		switch (effectType) {
		case POISON:
			actor.flash(0, 0.7f, 0, 1, 1f);
			actor.HP -= severity[effectType];
			if (actor.HP < 1) {
				actor.HP = 1;
				timer[effectType] = 0;
				subTimer[effectType] = 0;
			}

			damageList.start(this.severity[effectType], actor.bounds.x, actor.bounds.y, actor.facingLeft, Damage.TYPE_DAMAGE);

			break;

		case REGEN:
			break;

		case FREEZE:
			actor.flash(0, 0.9f, 1, 1, 1f);
			break;			
		case HEAL:
			break;

		case SPEED:
			actor.flash(0.5f, 1, 1, 1, 1f);
			break;

		}

	}

	public void render(SpriteBatch batch){
		healSparkle.render(batch);
	}

	// called once at the end to REMOVE
	public void remove(int effectType){
		switch (effectType) {
		case POISON:
			break;

		case SPEED:
			actor.walkSpeed -= severity[effectType];

		}

	}

	// called ONCE to add an effect (at the start)
	public void add(int effect, int severity, int duration){


		switch (effect) {
		case POISON:
			this.timer[effect] = duration;
			this.severity[effect] = severity;
			break;
		case FREEZE:
			this.timer[effect] = duration;
			this.severity[effect] = severity;
			break;
		case HEAL:
			this.timer[effect] = duration;
			this.severity[effect] = severity;
			heal.play(Config.sfxVol);
			healSparkle.start(actor.bounds.x, actor.bounds.y, false);
			actor.flash(1, 1, 0, 1, 1);
			actor.HP += this.severity[effect];
			if (actor.HP > actor.maxHP) {
				actor.HP = actor.maxHP;
			}
			damageList.start(this.severity[effect], actor.bounds.x, actor.bounds.y, actor.facingLeft, Damage.TYPE_HEAL);
			this.timer[effect] = 0;
			break;

		case SPEED:
			// if speed is already applied we reset the duration
			if (timer[SPEED] > 0) {
				timer[SPEED] = duration;
			} else {
				// else apply walking speed increase
				this.timer[effect] = duration;
				this.severity[effect] = severity;
				actor.walkSpeed += this.severity[effect];

			}
			break;
		}
	}

	// removes all effects from the actor
	public void removeAll() {
		for (int i = 0; i < MAX_EFFECTS; i++) {
			if (timer[i] > 0){
				remove(i);
				timer[i] = 0;
				subTimer[i] = 0;
				severity[i] = 0;
			}
		}

	}
}
