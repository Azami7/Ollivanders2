package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Passive marker effect that increases the effectiveness of book learning for the affected player.
 *
 * <p>The IMPROVED_BOOK_LEARNING effect is a temporary marker effect that boosts the player's
 * learning rate when reading books to gain skill experience. This effect does not apply any
 * active modifications itself; instead, the skill system checks for this effect's presence and
 * applies a learning effectiveness multiplier when the player gains skill experience from reading
 * books. The effect is passive and only needs to be tracked until expiration.</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Passive marker effect - no active behavior during each tick</li>
 * <li>Book learning system applies effectiveness multiplier when this effect is present</li>
 * <li>Detectable by mind-reading spells (Legilimens)</li>
 * <li>Detection text: "feels more studious than usual"</li>
 * <li>Effect expires naturally when duration reaches zero</li>
 * </ul>
 *
 * @author Azami7
 * @see FAST_LEARNING for a similar passive boost effect for general skill experience gain
 * @see HIGHER_SKILL for a similar passive boost effect for effective skill level
 */
public class IMPROVED_BOOK_LEARNING extends O2Effect {
    /**
     * Constructor for creating an improved book learning passive marker effect.
     *
     * <p>Creates a temporary marker effect that boosts the player's book learning effectiveness.
     * This effect is passive and does not apply modifications directly; the skill system checks for
     * its presence to apply a learning effectiveness multiplier. Sets the detection text for
     * mind-reading spells to "feels more studious than usual".</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to grant improved book learning
     */
    public IMPROVED_BOOK_LEARNING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.IMPROVED_BOOK_LEARNING;
        checkDurationBounds();

        legilimensText = "feels more studious than usual";
    }

    /**
     * Age the improved book learning marker effect each game tick.
     *
     * <p>Called each game tick. This effect is passive and only needs to track its remaining
     * duration. The actual learning effectiveness boost is applied by the skill system when it
     * detects this effect's presence on a player reading books. When the duration reaches zero,
     * the effect is automatically killed and removed from the player.</p>
     */
    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Perform cleanup when the improved book learning effect is removed.
     *
     * <p>The default implementation does nothing, as the improved book learning effect is a passive
     * marker with no state to clean up. When removed, the player's book learning returns to normal
     * effectiveness.</p>
     */
    @Override
    public void doRemove() {
    }
}