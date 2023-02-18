package net.pottercraft.ollivanders2.effect;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.common.MagicLevel;

/**
 * All effect types
 */
public enum O2EffectType {
	AGGRESSION(AGGRESSION.class, MagicLevel.OWL), ANIMAGUS_EFFECT(ANIMAGUS_EFFECT.class, MagicLevel.EXPERT),
	ANIMAGUS_INCANTATION(ANIMAGUS_INCANTATION.class, MagicLevel.EXPERT), AWAKE(AWAKE.class, MagicLevel.BEGINNER),
	AWAKE_ANTIDOTE_LESSER(AWAKE_ANTIDOTE_LESSER.class, MagicLevel.BEGINNER),
	BABBLING(BABBLING.class, MagicLevel.BEGINNER),
	BABBLING_ANTIDOTE_LESSER(BABBLING_ANTIDOTE_LESSER.class, MagicLevel.BEGINNER),
	BLINDNESS(BLINDNESS.class, MagicLevel.OWL), BROOM_FLYING(BROOM_FLYING.class, MagicLevel.EXPERT),
	BURNING(BURNING.class, MagicLevel.NEWT), CONFUSION(CONFUSION.class, MagicLevel.OWL),
	FAST_LEARNING(FAST_LEARNING.class, MagicLevel.NEWT), FLAGRANTE_BURNING(FLAGRANTE_BURNING.class, MagicLevel.EXPERT),
	FLYING(FLYING.class, MagicLevel.EXPERT), HARM(HARM.class, MagicLevel.OWL),
	HARM_ANTIDOTE_LESSER(HARM_ANTIDOTE_LESSER.class, MagicLevel.OWL), HEAL(HEAL.class, MagicLevel.OWL),
	HEALTH_BOOST(HEALTH_BOOST.class, MagicLevel.NEWT), HUNGER(HUNGER.class, MagicLevel.BEGINNER),
	HIGHER_SKILL(HIGHER_SKILL.class, MagicLevel.NEWT), IMMOBILIZE(IMMOBILIZE.class, MagicLevel.BEGINNER),
	IMPROVED_BOOK_LEARNING(IMPROVED_BOOK_LEARNING.class, MagicLevel.NEWT), LUCK(LUCK.class, MagicLevel.BEGINNER),
	LYCANTHROPY(LYCANTHROPY.class, MagicLevel.EXPERT), LYCANTHROPY_SPEECH(LYCANTHROPY_SPEECH.class, MagicLevel.EXPERT),
	LYCANTHROPY_RELIEF(LYCANTHROPY_RELIEF.class, MagicLevel.EXPERT), MUCUS(MUCUS.class, MagicLevel.BEGINNER),
	MUTED_SPEECH(MUTED_SPEECH.class, MagicLevel.OWL), NIGHT_VISION(NIGHT_VISION.class, MagicLevel.BEGINNER),
	POISON(POISON.class, MagicLevel.OWL), POISON_ANTIDOTE_LESSER(POISON_ANTIDOTE_LESSER.class, MagicLevel.OWL),
	SLEEP_SPEECH(SLEEP_SPEECH.class, MagicLevel.BEGINNER), SLEEPING(SLEEPING.class, MagicLevel.BEGINNER),
	SPEED(SPEED.class, MagicLevel.BEGINNER), SPEED_SPEEDIER(SPEED_SPEEDIER.class, MagicLevel.OWL),
	SPEED_SPEEDIEST(SPEED_SPEEDIEST.class, MagicLevel.NEWT), SLOWNESS(SLOWNESS.class, MagicLevel.BEGINNER),
	SUSPENSION(SUSPENSION.class, MagicLevel.OWL), UNLUCK(UNLUCK.class, MagicLevel.BEGINNER),
	UNLUCK_ANTIDOTE_LESSER(UNLUCK_ANTIDOTE_LESSER.class, MagicLevel.BEGINNER),
	WATER_BREATHING(WATER_BREATHING.class, MagicLevel.NEWT), WEAKNESS(WEAKNESS.class, MagicLevel.OWL),
	WEALTH(WEALTH.class, MagicLevel.OWL),;

	/**
	 * The class of effect to create
	 */
	final private Class<?> className;

	/**
	 * The level of magic this effect is, for use in counter-spells and antidotes
	 */
	final private MagicLevel level;

	/**
	 * Is this effect type enabled
	 */
	boolean enabled = true;

	/**
	 * Enum constructor.
	 *
	 * @param className the name of the effect class this type represents.
	 * @param level     the level of this effect
	 */
	O2EffectType(@NotNull Class<?> className, @NotNull MagicLevel level) {
		this.className = className;
		this.level = level;
	}

	/**
	 * Get the class name for the O2Effect class that this type represents.
	 *
	 * @return the effect class
	 */
	@NotNull
	public Class<?> getClassName() {
		return className;
	}

	/**
	 * Get the level of this effect.
	 *
	 * @return the level of this effect
	 */
	@NotNull
	public MagicLevel getLevel() {
		return level;
	}

	/**
	 * Is this effect currently enabled
	 *
	 * @return true if this effect is enabled, false otherwise
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Set whether this effect is currently enabled
	 *
	 * @param enabled true to enable this effect, false to disable
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
