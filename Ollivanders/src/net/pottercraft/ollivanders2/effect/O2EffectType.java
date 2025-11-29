package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;

/**
 * Enumeration of all available magical effects in Ollivanders2.
 *
 * <p>O2EffectType defines every magical effect that can be applied to players, from beneficial effects
 * (HEAL, LUCK, NIGHT_VISION) to harmful effects (POISON, HARM, WEAKNESS) to utility effects
 * (FLYING, PROTECTION). Each effect type maps to:</p>
 * <ul>
 * <li>An O2Effect implementation class used for instantiation via reflection</li>
 * <li>A MagicLevel indicating the spell's difficulty and for counter-spell matching</li>
 * <li>An enabled flag allowing runtime configuration of which effects are active</li>
 * </ul>
 *
 * <p>Effects are created by spells and divination, managed by O2Effects, and applied to players
 * through the effect system. Each effect type has a corresponding O2Effect subclass that implements
 * the specific behavior of that effect.</p>
 *
 * @author Azami7
 * @see O2Effect for the abstract base class of all effect implementations
 * @see O2Effects for the central effects management system
 */
public enum O2EffectType {
    /**
     * {@link AGGRESSION}
     */
    AGGRESSION(AGGRESSION.class, MagicLevel.OWL, 100, 100),
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
     * {@link BROOM_FLYING}
     */
    BROOM_FLYING(BROOM_FLYING.class, MagicLevel.EXPERT, 100, 100),
    /**
     * {@link BURNING}
     */
    BURNING(BURNING.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link CONFUSION}
     */
    CONFUSION(CONFUSION.class, MagicLevel.OWL, 2 * Ollivanders2Common.ticksPerMinute, 5 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link DANCING_FEET}
     */
    DANCING_FEET(DANCING_FEET.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link FAST_LEARNING}
     */
    FAST_LEARNING(FAST_LEARNING.class, MagicLevel.NEWT, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link FLAGRANTE_BURNING}
     */
    FLAGRANTE_BURNING(FLAGRANTE_BURNING.class, MagicLevel.EXPERT, 100, 100),
    /**
     * {@link FLYING}
     */
    FLYING(FLYING.class, MagicLevel.EXPERT, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link FUMOS}
     */
    FUMOS(FUMOS.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
    /**
     * {@link FUMOS_DUO}
     */
    FUMOS_DUO(FUMOS.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, 10 * Ollivanders2Common.ticksPerMinute),
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
    SLEEPING(SLEEPING.class, MagicLevel.BEGINNER, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
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
     * {@link SUSPENSION}
     */
    SUSPENSION(SUSPENSION.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
    /**
     * {@link PlayerChangeSizeSuper}
     */
    SWELLING(PlayerChangeSizeSuper.class, MagicLevel.OWL, 30 * Ollivanders2Common.ticksPerSecond, Ollivanders2Common.ticksPerHour),
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
     * The O2Effect implementation class for this effect type.
     * Used with reflection to dynamically instantiate the correct effect class
     * when the effect is applied to a player. See O2Effects.addEffect() for usage.
     */
    final private Class<?> className;

    /**
     * The magic level (difficulty) of this effect.
     * Used for counter-spell and antidote matching to ensure appropriate power levels.
     * For example, a high-level antidote cannot neutralize a low-level effect.
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
     * Runtime configuration flag indicating whether this effect type is currently enabled.
     * Disabled effects cannot be applied to players. Can be toggled via setEnabled() based
     * on server configuration (e.g., lycanthropy can be disabled in server config).
     */
    boolean enabled = true;

    /**
     * Constructor for creating an effect type enumeration constant.
     *
     * <p>Associates the effect type with its implementation class and magic level.
     * The className is used with reflection to instantiate O2Effect objects at runtime.</p>
     *
     * @param className   the O2Effect subclass corresponding to this effect type
     * @param level       the magic level (difficulty) of this effect
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
     * Get the O2Effect implementation class for this effect type.
     *
     * <p>The returned class is used with reflection to dynamically instantiate the correct
     * effect object when the effect is applied to a player. See O2Effects.addEffect() and
     * O2Prophecy.fulfill() for usage examples.</p>
     *
     * @return the O2Effect subclass for this effect type
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Get the magic level (difficulty) of this effect.
     *
     * <p>The magic level is used to match effects with appropriate counter-spells and antidotes.
     * For example, an antidote's level must be >= the effect's level to successfully neutralize it.</p>
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
     * Check whether this effect type is currently enabled.
     *
     * <p>Disabled effects cannot be applied to players. Effects can be disabled via server
     * configuration (e.g., setting enableLycanthropy to false in the config).</p>
     *
     * @return true if the effect is enabled and can be applied, false if disabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enable or disable this effect type at runtime.
     *
     * <p>Disabled effects cannot be applied to players via spells or divination.
     * This setting can be changed dynamically without restarting the server.</p>
     *
     * @param enabled true to enable the effect, false to disable it
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
