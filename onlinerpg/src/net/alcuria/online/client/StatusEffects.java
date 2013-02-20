package net.alcuria.online.client;

import net.alcuria.online.client.connection.GameClient;
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
	public static final int RAGE = 6;
	public static final int TELE = 7;

	public static final int MAX_EFFECTS = 10;
	public static final int MAX_DURATION = 5;
	public static final float EFFECT_FREQUENCY = 0.5f;

	// whether or not this effect was added from the server
	// if it is sent from the server, we dont want to do things like actually
	// reduce HP because the origin client handles that
	public boolean networkStatusEffect = false;

	public float[] timer;
	public float[] subTimer;
	public int[] severity;
	public float[] frequency;

	public Field f;
	public Particle healSparkle;
	public Sound heal;

	public Actor actor;

	public short hpOffset = 0;
	public short atkOffset = 0;
	public short defOffset = 0;
	public short matkOffset = 0;
	public short mdefOffset = 0;
	public short speedOffset = 0;
	public short jumpOffset = 0;
	public short kbOffset = 0;

	public short netID = 0;
	public boolean netIsMonster = false;
	// for casting
	public Monster netMonster;
	public Player netPlayer;
	public boolean sendEffect = false;

	public boolean rageFlip = false;

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
		frequency[RAGE] = 0.25f;

		healSparkle = new Particle("sprites/sparkle.png", 0, 0, 25, 25, 5, 5, false, f.assets);
		heal = f.assets.get("sounds/heal.wav", Sound.class);

		this.f = f;

	}


	public void update(Actor actor){
		for (int i = 0; i < MAX_EFFECTS; i++) {

			// check if the effect is active
			if (timer[i] > 0) {
				timer[i] -= Gdx.graphics.getDeltaTime();
				subTimer[i]+= Gdx.graphics.getDeltaTime();
				if (subTimer[i] > frequency[i]) {
					doEffect(i, actor, this.f.damageList);
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

			// only do hp reduction for clients
			if (!networkStatusEffect){
				actor.HP -= severity[effectType];
				if (actor.HP < 1) {
					actor.HP = 1;
					timer[effectType] = 0;
					subTimer[effectType] = 0;
				}

				damageList.start(this.severity[effectType], actor.bounds.x, actor.bounds.y, actor.facingLeft, Damage.TYPE_DAMAGE);
			}
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

		case RAGE:
			rageFlip = !rageFlip;
			if (rageFlip){
				actor.flash(1f, 0.5f, 0.5f, 1f, 1f);
			} else {
				actor.flash(1f, 1f, 0.5f, 1f, 1f);
			}
			break;

		case TELE:
			if (!networkStatusEffect){
				TeleportNode node = new TeleportNode(TeleportNode.EDGE_NORTH, "beacharena", (int) (7 + Math.random()*25), 20);
				node.changeMap(f.map, f.player);
				node = null;
				Transition.fadeIn(0.35f);
				remove(TELE);
				timer[TELE] = 0;
				subTimer[TELE] = 0;
				severity[TELE] = 0;
			}
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
			speedOffset -= severity[effectType];

		case RAGE:
			defOffset += severity[effectType];
			atkOffset -= severity[effectType];

		}

	}

	// called ONCE to add an effect (at the start) -- client only
	public void add(int effect, int severity, int duration){

		// we only apply the stat effect if the player is visible (ie hasn't been recently damaged)
		if (actor.hurtTimer >= actor.invincibilityPeriod){
			sendEffect = false;
			applyEffect(effect, severity, duration);
			if (sendEffect){
				GameClient.sendStatusEffect(netID, netIsMonster, effect, severity, duration);
			}
		}
	}

	// this is called just to apply the effect, doesn't do a client call to notify the server
	public void applyEffect(int effect, int severity, int duration) {
		switch (effect) {
		case POISON:
			sendEffect = true;
			this.timer[effect] = duration;
			this.severity[effect] = severity;
			break;
		case FREEZE:
			sendEffect = true;
			this.timer[effect] = duration;
			this.severity[effect] = severity;
			break;
		case HEAL:
			sendEffect = true;
			heal.play(Config.sfxVol);
			healSparkle.start(actor.bounds.x, actor.bounds.y, false);
			actor.flash(1, 1, 0, 1, 1);
			if (!networkStatusEffect){
				this.timer[effect] = duration;
				this.severity[effect] = severity;
				actor.HP = Math.min(this.severity[effect] + actor.HP, actor.maxHP);
				f.damageList.start(this.severity[effect], actor.bounds.x, actor.bounds.y, actor.facingLeft, Damage.TYPE_HEAL);
				this.timer[effect] = 0;
			}
			break;
		case SPEED:
			sendEffect = true;
			// if speed is already applied we reset the duration
			if (timer[SPEED] > 0) {
				timer[SPEED] = duration;
			} else {
				// else apply walking speed increase
				this.timer[effect] = duration;
				this.severity[effect] = severity;
				this.speedOffset += this.severity[effect];

			}
			break;

		case RAGE:
			sendEffect = true;
			if (timer[RAGE] > 0) {
				timer[RAGE] = duration;
			} else {
				this.timer[effect] = duration;
				this.severity[effect] += severity;
				this.atkOffset += this.severity[effect];
				this.defOffset -= this.severity[effect];

			}
			break;

		case TELE:
			sendEffect = false;
			this.frequency[effect] = 0.5f;
			this.timer[effect] = 100;
			Transition.fadeOut(0.35f);
		}

		// check actor's subclass and assign the correct ID/monval
		if (actor instanceof Monster){
			netMonster = (Monster)actor;
			netID = netMonster.id;
			netIsMonster = true;
		} else if (actor instanceof Player){
			netPlayer = (Player)actor;
			netID = netPlayer.uid;
			netIsMonster = false;
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
