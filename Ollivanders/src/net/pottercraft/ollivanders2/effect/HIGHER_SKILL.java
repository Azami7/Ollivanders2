package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Passive marker effect that boosts effective skill level for the affected player.
 *
 * <p>The HIGHER_SKILL effect is a temporary marker effect that increases the player's
 * effective experience level in skills, making them appear more skilled than their actual
 * training progress. This effect does not apply any active modifications itself; instead,
 * the skill system checks for this effect's presence and applies an effective level boost
 * when calculating skill-based actions or checks. The effect is passive and only needs to
 * be tracked until expiration.</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Passive marker effect - no active behavior during each tick</li>
 * <li>Skill system applies effective level multiplier when this effect is present</li>
 * <li>Detectable by mind-reading spells (Legilimens)</li>
 * <li>Detection text: "feels more skillful than usual"</li>
 * <li>Effect expires naturally when duration reaches zero</li>
 * </ul>
 *
 * @author Azami7
 * @see FAST_LEARNING for a similar passive boost effect for skill experience gain
 */
public class HIGHER_SKILL extends O2Effect {
    /**
     * Constructor for creating a higher skill passive marker effect.
     *
     * <p>Creates a temporary marker effect that boosts the player's effective skill level.
     * This effect is passive and does not apply modifications directly; the skill system checks
     * for its presence to apply an effective level boost. Sets the detection text for mind-reading
     * spells to "feels more skillful than usual".</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to grant higher skill
     */
    public HIGHER_SKILL(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.HIGHER_SKILL;
        legilimensText = "feels more skillful than usual";
    }

    /**
     * Age the higher skill marker effect each game tick.
     *
     * <p>Called each game tick. This effect is passive and only needs to track its remaining
     * duration. The actual skill level boost is applied by the skill system when it detects
     * this effect's presence on a player. When the duration reaches zero, the effect is
     * automatically killed and removed from the player.</p>
     */
    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Perform cleanup when the higher skill effect is removed.
     *
     * <p>The default implementation does nothing, as the higher skill effect is a passive marker
     * with no state to clean up. When removed, the player's effective skill level returns to
     * normal based on their actual experience.</p>
     */
    @Override
    public void doRemove() {
    }
}