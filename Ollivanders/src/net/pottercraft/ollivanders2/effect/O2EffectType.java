package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.common.MagicLevel;
import org.jetbrains.annotations.NotNull;

/**
 * All effect types
 */
public enum O2EffectType {
    /**
     * {@link AGGRESSION}
     */
    AGGRESSION(AGGRESSION.class, MagicLevel.OWL),
    /**
     * {@link ANIMAGUS_EFFECT}
     */
    ANIMAGUS_EFFECT(ANIMAGUS_EFFECT.class, MagicLevel.EXPERT),
    /**
     * {@link ANIMAGUS_INCANTATION}
     */
    ANIMAGUS_INCANTATION(ANIMAGUS_INCANTATION.class, MagicLevel.EXPERT),
    /**
     * {@link AWAKE}
     */
    AWAKE(AWAKE.class, MagicLevel.BEGINNER),
    /**
     * {@link AWAKE_ANTIDOTE_LESSER}
     */
    AWAKE_ANTIDOTE_LESSER(AWAKE_ANTIDOTE_LESSER.class, MagicLevel.BEGINNER),
    /**
     * {@link BABBLING}
     */
    BABBLING(BABBLING.class, MagicLevel.BEGINNER),
    /**
     * {@link BABBLING_ANTIDOTE_LESSER}
     */
    BABBLING_ANTIDOTE_LESSER(BABBLING_ANTIDOTE_LESSER.class, MagicLevel.BEGINNER),
    /**
     * {@link BLINDNESS}
     */
    BLINDNESS(BLINDNESS.class, MagicLevel.OWL),
    /**
     * {@link BROOM_FLYING}
     */
    BROOM_FLYING(BROOM_FLYING.class, MagicLevel.EXPERT),
    /**
     * {@link BURNING}
     */
    BURNING(BURNING.class, MagicLevel.NEWT),
    /**
     * {@link CONFUSION}
     */
    CONFUSION(CONFUSION.class, MagicLevel.OWL),
    /**
     * {@link DANCING_FEET}
     */
    DANCING_FEET(DANCING_FEET.class, MagicLevel.BEGINNER),
    /**
     * {@link FAST_LEARNING}
     */
    FAST_LEARNING(FAST_LEARNING.class, MagicLevel.NEWT),
    /**
     * {@link FLAGRANTE_BURNING}
     */
    FLAGRANTE_BURNING(FLAGRANTE_BURNING.class, MagicLevel.EXPERT),
    /**
     * {@link FUMOS}
     */
    FUMOS(FUMOS.class, MagicLevel.BEGINNER),
    /**
     * {@link FUMOS_DUO}
     */
    FUMOS_DUO(FUMOS.class, MagicLevel.OWL),
    /**
     * {@link FLYING}
     */
    FLYING(FLYING.class, MagicLevel.EXPERT),
    /**
     * {@link HARM}
     */
    HARM(HARM.class, MagicLevel.OWL),
    /**
     * {@link HARM_ANTIDOTE_LESSER}
     */
    HARM_ANTIDOTE_LESSER(HARM_ANTIDOTE_LESSER.class, MagicLevel.OWL),
    /**
     * {@link HEAL}
     */
    HEAL(HEAL.class, MagicLevel.OWL),
    /**
     * {@link HEALTH_BOOST}
     */
    HEALTH_BOOST(HEALTH_BOOST.class, MagicLevel.NEWT),
    /**
     * {@link HUNGER}
     */
    HUNGER(HUNGER.class, MagicLevel.BEGINNER),
    /**
     * {@link HIGHER_SKILL}
     */
    HIGHER_SKILL(HIGHER_SKILL.class, MagicLevel.NEWT),
    /**
     * {@link IMMOBILIZE}
     */
    IMMOBILIZE(IMMOBILIZE.class, MagicLevel.BEGINNER),
    /**
     * {@link IMPROVED_BOOK_LEARNING}
     */
    IMPROVED_BOOK_LEARNING(IMPROVED_BOOK_LEARNING.class, MagicLevel.NEWT),
    /**
     * {@link LUCK}
     */
    LUCK(LUCK.class, MagicLevel.BEGINNER),
    /**
     * {@link LYCANTHROPY}
     */
    LYCANTHROPY(LYCANTHROPY.class, MagicLevel.EXPERT),
    /**
     * {@link LYCANTHROPY_RELIEF}
     */
    LYCANTHROPY_RELIEF(LYCANTHROPY_RELIEF.class, MagicLevel.EXPERT),
    /**
     * {@link LYCANTHROPY_SPEECH}
     */
    LYCANTHROPY_SPEECH(LYCANTHROPY_SPEECH.class, MagicLevel.EXPERT),
    /**
     * {@link MUCUS}
     */
    MUCUS(MUCUS.class, MagicLevel.BEGINNER),
    /**
     * {@link MUTED_SPEECH}
     */
    MUTED_SPEECH(MUTED_SPEECH.class, MagicLevel.OWL),
    /**
     * {@link NIGHT_VISION}
     */
    NIGHT_VISION(NIGHT_VISION.class, MagicLevel.BEGINNER),
    /**
     * {@link POISON}
     */
    POISON(POISON.class, MagicLevel.OWL),
    /**
     * {@link POISON_ANTIDOTE_LESSER}
     */
    POISON_ANTIDOTE_LESSER(POISON_ANTIDOTE_LESSER.class, MagicLevel.OWL),
    /**
     * {@link PROTEGO}
     */
    PROTEGO(PROTEGO.class, MagicLevel.OWL),
    /**
     * {@link SLEEP_SPEECH}
     */
    SLEEP_SPEECH(SLEEP_SPEECH.class, MagicLevel.BEGINNER),
    /**
     * {@link SLEEPING}
     */
    SLEEPING(SLEEPING.class, MagicLevel.BEGINNER),
    /**
     * {@link SPEED}
     */
    SPEED(SPEED.class, MagicLevel.BEGINNER),
    /**
     * {@link SPEED_SPEEDIER}
     */
    SPEED_SPEEDIER(SPEED_SPEEDIER.class, MagicLevel.OWL),
    /**
     * {@link SPEED_SPEEDIEST}
     */
    SPEED_SPEEDIEST(SPEED_SPEEDIEST.class, MagicLevel.NEWT),
    /**
     * {@link SLOWNESS}
     */
    SLOWNESS(SLOWNESS.class, MagicLevel.BEGINNER),
    /**
     * {@link SUSPENSION}
     */
    SUSPENSION(SUSPENSION.class, MagicLevel.OWL),
    /**
     * {@link UNLUCK}
     */
    UNLUCK(UNLUCK.class, MagicLevel.BEGINNER),
    /**
     * {@link UNLUCK_ANTIDOTE_LESSER}
     */
    UNLUCK_ANTIDOTE_LESSER(UNLUCK_ANTIDOTE_LESSER.class, MagicLevel.BEGINNER),
    /**
     * {@link WATER_BREATHING}
     */
    WATER_BREATHING(WATER_BREATHING.class, MagicLevel.NEWT),
    /**
     * {@link WEAKNESS}
     */
    WEAKNESS(WEAKNESS.class, MagicLevel.OWL),
    /**
     * {@link WEALTH}
     */
    WEALTH(WEALTH.class, MagicLevel.OWL),
    ;

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
