package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;

/**
 * The available magical effects that can be applied to players. Each type carries its {@link O2Effect} implementation
 * class (instantiated via reflection), a {@link MagicLevel} used for counter-spell and antidote matching, its duration
 * bounds, and a runtime enabled flag.
 *
 * @author Azami7
 * @see O2Effect
 * @see O2Effects
 */
public enum O2EffectType {
    /**
     * {@link AGGRESSION}
     */
    AGGRESSION(AGGRESSION.class, MagicLevel.OWL, 100, 100),
    /**
     * {@link AGNIFORS_MAXIMA}
     */
    AGNIFORS_MAXIMA(AGNIFORS_MAXIMA.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link ANIMAGUS_EFFECT}
     */
    ANIMAGUS_EFFECT(ANIMAGUS_EFFECT.class, MagicLevel.EXPERT, 100, 100),
    /**
     * {@link ANIMAGUS_INCANTATION}
     */
    ANIMAGUS_INCANTATION(ANIMAGUS_INCANTATION.class, MagicLevel.EXPERT, 5 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link AWAKE}
     */
    AWAKE(AWAKE.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link AWAKE_ANTIDOTE_LESSER}
     */
    AWAKE_ANTIDOTE_LESSER(AWAKE_ANTIDOTE_LESSER.class, MagicLevel.BEGINNER, 100, 100),
    /**
     * {@link BABBLING}
     */
    BABBLING(BABBLING.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link BABBLING_ANTIDOTE_LESSER}
     */
    BABBLING_ANTIDOTE_LESSER(BABBLING_ANTIDOTE_LESSER.class, MagicLevel.BEGINNER, 100, 100),
    /**
     * {@link BLINDNESS}
     */
    BLINDNESS(BLINDNESS.class, MagicLevel.OWL, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link BOVIFORS_MAXIMA}
     */
    BOVIFORS_MAXIMA(BOVIFORS_MAXIMA.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link BROOM_FLYING}
     */
    BROOM_FLYING(BROOM_FLYING.class, MagicLevel.EXPERT, 100, 100),
    /**
     * {@link BURNING}
     */
    BURNING(BURNING.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link CANIFORS_MAXIMA}
     */
    CANIFORS_MAXIMA(CANIFORS_MAXIMA.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link CONFUSION}
     */
    CONFUSION(CONFUSION.class, MagicLevel.OWL, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link DANCING_FEET}
     */
    DANCING_FEET(DANCING_FEET.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link DUCKLIFORS_MAXIMA}
     */
    DUCKLIFORS_MAXIMA(DUCKLIFORS_MAXIMA.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link EQUIFORS_MAXIMA}
     */
    EQUIFORS_MAXIMA(EQUIFORS_MAXIMA.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link FAST_LEARNING}
     */
    FAST_LEARNING(FAST_LEARNING.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link FELIFORS_MAXIMA}
     */
    FELIFORS_MAXIMA(FELIFORS_MAXIMA.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link FIRE_RESISTANCE}
     */
    FIRE_RESISTANCE(FIRE_RESISTANCE.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link FLAGRANTE_BURNING}
     */
    FLAGRANTE_BURNING(FLAGRANTE_BURNING.class, MagicLevel.EXPERT, 100, 100),
    /**
     * {@link FLYING}
     */
    FLYING(FLYING.class, MagicLevel.EXPERT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link FULL_IMMOBILIZE}
     */
    FULL_IMMOBILIZE(FULL_IMMOBILIZE.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link FUMOS}
     */
    FUMOS(FUMOS.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link FUMOS_DUO}
     */
    FUMOS_DUO(FUMOS_DUO.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link HARM}
     */
    HARM(HARM.class, MagicLevel.OWL, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link HARM_ANTIDOTE_LESSER}
     */
    HARM_ANTIDOTE_LESSER(HARM_ANTIDOTE_LESSER.class, MagicLevel.OWL, 100, 100),
    /**
     * {@link HEAL}
     */
    HEAL(HEAL.class, MagicLevel.OWL, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link HEALTH_BOOST}
     */
    HEALTH_BOOST(HEALTH_BOOST.class, MagicLevel.NEWT, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link HIGHER_SKILL}
     */
    HIGHER_SKILL(HIGHER_SKILL.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link HUNGER}
     */
    HUNGER(HUNGER.class, MagicLevel.BEGINNER, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link IMMOBILIZE}
     */
    IMMOBILIZE(IMMOBILIZE.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link IMPROVED_BOOK_LEARNING}
     */
    IMPROVED_BOOK_LEARNING(IMPROVED_BOOK_LEARNING.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link INVISIBILITY}
     */
    INVISIBILITY(INVISIBILITY.class, MagicLevel.EXPERT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link LAPIFORS_MAXIMA}
     */
    LAPIFORS_MAXIMA(LAPIFORS_MAXIMA.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link LAUGHING}
     */
    LAUGHING(LAUGHING.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link LUCK}
     */
    LUCK(LUCK.class, MagicLevel.BEGINNER, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link LYCANTHROPY}
     */
    LYCANTHROPY(LYCANTHROPY.class, MagicLevel.EXPERT, 100, 100),
    /**
     * {@link LYCANTHROPY_RELIEF}
     */
    LYCANTHROPY_RELIEF(LYCANTHROPY_RELIEF.class, MagicLevel.EXPERT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link LYCANTHROPY_SPEECH}
     */
    LYCANTHROPY_SPEECH(LYCANTHROPY_SPEECH.class, MagicLevel.EXPERT, 100, 100),
    /**
     * {@link MUCUS}
     */
    MUCUS(MUCUS.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link MUTED_SPEECH}
     */
    MUTED_SPEECH(MUTED_SPEECH.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link NIGHT_VISION}
     */
    NIGHT_VISION(NIGHT_VISION.class, MagicLevel.BEGINNER, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link POISON}
     */
    POISON(POISON.class, MagicLevel.OWL, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link POISON_ANTIDOTE_LESSER}
     */
    POISON_ANTIDOTE_LESSER(POISON_ANTIDOTE_LESSER.class, MagicLevel.OWL, 100, 100),
    /**
     * {@link PROTEGO}
     */
    PROTEGO(PROTEGO.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link REGENERATION}
     */
    REGENERATION(REGENERATION.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link SATIATION}
     */
    SATIATION(SATIATION.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link SHRINKING}
     */
    SHRINKING(SHRINKING.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link SLEEP_SPEECH}
     */
    SLEEP_SPEECH(SLEEP_SPEECH.class, MagicLevel.BEGINNER, 100, 100),
    /**
     * {@link SLEEPING}
     */
    SLEEPING(SLEEPING.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link SLOWNESS}
     */
    SLOWNESS(SLOWNESS.class, MagicLevel.BEGINNER, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link SPEED}
     */
    SPEED(SPEED.class, MagicLevel.BEGINNER, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link SPEED_SPEEDIER}
     */
    SPEED_SPEEDIER(SPEED_SPEEDIER.class, MagicLevel.OWL, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link SPEED_SPEEDIEST}
     */
    SPEED_SPEEDIEST(SPEED_SPEEDIEST.class, MagicLevel.NEWT, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link STRENGTH}
     */
    STRENGTH(STRENGTH.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link SUIFORS_MAXIMA}
     */
    SUIFORS_MAXIMA(SUIFORS_MAXIMA.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link SUSPENSION}
     */
    SUSPENSION(SUSPENSION.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link PlayerChangeSize}
     */
    SWELLING(PlayerChangeSize.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link TICKLING}
     */
    TICKLING(TICKLING.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link UNLUCK}
     */
    UNLUCK(UNLUCK.class, MagicLevel.BEGINNER, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link UNLUCK_ANTIDOTE_LESSER}
     */
    UNLUCK_ANTIDOTE_LESSER(UNLUCK_ANTIDOTE_LESSER.class, MagicLevel.BEGINNER, 100, 100),
    /**
     * {@link URSIFORS_MAXIMA}
     */
    URSIFORS_MAXIMA(URSIFORS_MAXIMA.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link WATER_BREATHING}
     */
    WATER_BREATHING(WATER_BREATHING.class, MagicLevel.NEWT, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link WEAKNESS}
     */
    WEAKNESS(WEAKNESS.class, MagicLevel.OWL, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link WEALTH}
     */
    WEALTH(WEALTH.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    ;

    /**
     * The {@link O2Effect} implementation class for this effect type, instantiated via reflection when applied.
     */
    final private Class<?> className;

    /**
     * The magic level of this effect, used to match it against counter-spells and antidotes of sufficient level.
     */
    final private MagicLevel level;

    /**
     * The minimum duration, in ticks, for this effect type
     */
    final private int minDuration;

    /**
     * The maximum duration, in ticks, for this effect type
     */
    final private int maxDuration;

    /**
     * Whether this effect type may currently be applied; disabled types are rejected. Toggled from server config.
     */
    boolean enabled = true;

    /**
     * Constructor
     *
     * @param className   the {@link O2Effect} subclass for this effect type
     * @param level       the magic level of this effect
     * @param minDuration the minimum duration, in ticks, for this effect type
     * @param maxDuration the maximum duration, in ticks, for this effect type
     */
    O2EffectType(@NotNull Class<?> className, @NotNull MagicLevel level, int minDuration, int maxDuration) {
        this.className = className;
        this.level = level;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    /**
     * Get the {@link O2Effect} implementation class for this effect type, used to instantiate the effect via reflection.
     *
     * @return the effect's implementation class
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Get the magic level of this effect, used to match it against counter-spells and antidotes.
     *
     * @return the magic level of this effect
     */
    @NotNull
    public MagicLevel getLevel() {
        return level;
    }

    /**
     * Get the minimum duration, in ticks, for this effect type
     *
     * @return the minimum duration
     */
    public int getMinDuration() {
        return minDuration;
    }

    /**
     * Get the maximum duration, in ticks, for this effect type
     *
     * @return the maximum duration
     */
    public int getMaxDuration() {
        return maxDuration;
    }

    /**
     * Check whether this effect type is currently enabled and may be applied.
     *
     * @return true if enabled, false if disabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enable or disable this effect type at runtime.
     *
     * @param enabled true to enable the effect, false to disable it
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
